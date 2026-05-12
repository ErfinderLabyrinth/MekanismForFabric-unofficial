package mekanism.common.content.network;

import mekanism.api.FluidStack;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.fluid.IMekanismFluidHandler;
import mekanism.api.math.MathUtils;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.content.network.distribution.FluidHandlerTarget;
import mekanism.common.content.network.distribution.FluidTransmitterSaveTarget;
import mekanism.common.content.network.transmitter.MechanicalPipe;
import mekanism.common.lib.transmitter.DynamicBufferedNetwork;
import mekanism.common.util.EmitUtils;
import mekanism.common.util.FluidUtils;
import mekanism.common.util.MekanismUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FluidNetwork extends DynamicBufferedNetwork<Storage<FluidVariant>, FluidNetwork, FluidStack, MechanicalPipe> implements IMekanismFluidHandler {

    private final List<IExtendedFluidTank> fluidTanks;
    public final VariableCapacityFluidTank fluidTank;
    @NotNull
    public FluidStack lastFluid = FluidStack.EMPTY;
    private long prevTransferAmount;

    //TODO: Make fluid storage support storing as longs?
    private int intCapacity;

    public FluidNetwork(UUID networkID) {
        super(networkID);
        fluidTank = VariableCapacityFluidTank.create(this::getCapacityAsInt, BasicFluidTank.alwaysTrueBi, BasicFluidTank.alwaysTrueBi, BasicFluidTank.alwaysTrue, this);
        fluidTanks = Collections.singletonList(fluidTank);
    }

    public FluidNetwork(Collection<FluidNetwork> networks) {
        this(UUID.randomUUID());
        adoptAllAndRegister(networks);
    }

    @Override
    protected void forceScaleUpdate() {
        if (!fluidTank.isEmpty() && fluidTank.getCapacity() > 0) {
            currentScale = Math.min(1, (float) fluidTank.getAmount() / fluidTank.getCapacity());
        } else {
            currentScale = 0;
        }
    }

    @Override
    public List<MechanicalPipe> adoptTransmittersAndAcceptorsFrom(FluidNetwork net) {
        float oldScale = currentScale;
        long oldCapacity = getCapacity();
        List<MechanicalPipe> transmittersToUpdate = super.adoptTransmittersAndAcceptorsFrom(net);
        //Merge the fluid scales
        long capacity = getCapacity();
        currentScale = Math.min(1, capacity == 0 ? 0 : (currentScale * oldCapacity + net.currentScale * net.capacity) / capacity);
        if (isRemote()) {
            if (fluidTank.isEmpty() && !net.fluidTank.isEmpty()) {
                fluidTank.setStack(net.getBuffer());
                net.fluidTank.setEmpty();
            }
        } else {
            if (!net.fluidTank.isEmpty()) {
                if (fluidTank.isEmpty()) {
                    fluidTank.setStack(net.getBuffer());
                } else if (fluidTank.isFluidEqual(net.fluidTank.getFluid())) {
                    long amount = net.fluidTank.getAmount();
                    MekanismUtils.logMismatchedStackSize(fluidTank.growStack(amount), amount);
                } else {
                    Mekanism.logger.error("Incompatible fluid networks merged.");
                }
                net.fluidTank.setEmpty();
            }
            if (oldScale != currentScale) {
                //We want to make sure we update to the scale change
                needsUpdate = true;
            }
        }
        return transmittersToUpdate;
    }

    @NotNull
    @Override
    public FluidStack getBuffer() {
        return fluidTank.getFluid().copy();
    }

    @Override
    public void absorbBuffer(MechanicalPipe transmitter) {
        FluidStack fluid = transmitter.releaseShare();
        if (!fluid.isEmpty()) {
            if (fluidTank.isEmpty()) {
                fluidTank.setStack(fluid.copy());
            } else if (fluidTank.isFluidEqual(fluid)) {
                long amount = fluid.amount();
                MekanismUtils.logMismatchedStackSize(fluidTank.growStack(amount), amount);
            }
        }
    }

    @Override
    public void clampBuffer() {
        if (!fluidTank.isEmpty()) {
            int capacity = getCapacityAsInt();
            if (fluidTank.getAmount() > capacity) {
                MekanismUtils.logMismatchedStackSize(fluidTank.setStackSize(capacity), capacity);
            }
        }
    }

    @Override
    protected synchronized void updateCapacity(MechanicalPipe transmitter) {
        super.updateCapacity(transmitter);
        intCapacity = MathUtils.clampToInt(getCapacity());
    }

    @Override
    public synchronized void updateCapacity() {
        super.updateCapacity();
        intCapacity = MathUtils.clampToInt(getCapacity());
    }

    public int getCapacityAsInt() {
        return intCapacity;
    }

    @Override
    protected void updateSaveShares(@Nullable MechanicalPipe triggerTransmitter) {
        super.updateSaveShares(triggerTransmitter);
        if (!isEmpty()) {
            FluidStack fluidType = fluidTank.getFluid();
            FluidTransmitterSaveTarget saveTarget = new FluidTransmitterSaveTarget(fluidType, transmitters);
            EmitUtils.sendToAcceptors(saveTarget, fluidType.amount(), fluidType);
            saveTarget.saveShare();
        }
    }

    private long tickEmit(@NotNull FluidStack fluidToSend) {
        Collection<Map<Direction, Optional<Storage<FluidVariant>>>> acceptorValues = acceptorCache.getAcceptorValues();
        FluidHandlerTarget target = new FluidHandlerTarget(fluidToSend, acceptorValues.size() * 2);
        for (Map<Direction, Optional<Storage<FluidVariant>>> acceptors : acceptorValues) {
            for (Optional<Storage<FluidVariant>> acceptorOpt : acceptors.values()) {
                acceptorOpt.ifPresent(acceptor -> {
                    if (FluidUtils.canFill(acceptor, fluidToSend)) {
                        target.addHandler(acceptor);
                    }
                });
            }
        }
        return EmitUtils.sendToAcceptors(target, fluidToSend.amount(), fluidToSend);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (needsUpdate) {
            Mekanism.instance.onLiquidTransferred(new FluidTransferEvent(this, lastFluid));
            needsUpdate = false;
        }
        if (fluidTank.isEmpty()) {
            prevTransferAmount = 0;
        } else {
            prevTransferAmount = tickEmit(fluidTank.getFluid());
            MekanismUtils.logMismatchedStackSize(fluidTank.shrinkStack(prevTransferAmount), prevTransferAmount);
        }
    }

    @Override
    protected float computeContentScale() {
        float scale = fluidTank.getAmount() / (float) fluidTank.getCapacity();
        float ret = Math.max(currentScale, scale);
        if (prevTransferAmount > 0 && ret < 1) {
            ret = Math.min(1, ret + 0.02F);
        } else if (prevTransferAmount <= 0 && ret > 0) {
            ret = Math.max(scale, ret - 0.02F);
        }
        return ret;
    }

    public long getPrevTransferAmount() {
        return prevTransferAmount;
    }

    @Override
    public String toString() {
        return "[FluidNetwork] " + transmittersSize() + " transmitters, " + getAcceptorCount() + " acceptors.";
    }

    @Override
    public Component getNeededInfo() {
        return MekanismLang.FLUID_NETWORK_NEEDED.translate(fluidTank.getNeeded() / 1_000F);
    }

    @Override
    public Component getStoredInfo() {
        if (fluidTank.isEmpty()) {
            return MekanismLang.NONE.translate();
        }
        return MekanismLang.NETWORK_MB_STORED.translate(fluidTank.getFluid(), fluidTank.getAmount());
    }

    @Override
    public Component getFlowInfo() {
        return MekanismLang.NETWORK_MB_PER_TICK.translate(prevTransferAmount);
    }

    @Override
    public boolean isCompatibleWith(FluidNetwork other) {
        return super.isCompatibleWith(other) && (this.fluidTank.isEmpty() || other.fluidTank.isEmpty() || this.fluidTank.isFluidEqual(other.fluidTank.getFluid()));
    }

    @NotNull
    @Override
    public Component getTextComponent() {
        return MekanismLang.NETWORK_DESCRIPTION.translate(MekanismLang.FLUID_NETWORK, transmittersSize(), getAcceptorCount());
    }

    @Override
    public Storage<FluidVariant> getFluidTanks(@Nullable Direction side) {
        return new CombinedStorage<>(fluidTanks);
    }

    @Override
    public void onContentsChanged() {
        markDirty();
        FluidStack type = fluidTank.getFluid();
        if (!lastFluid.equals(type)) {
            //If the fluid type does not match update it, and mark that we need an update
            if (!type.isEmpty()) {
                lastFluid = new FluidStack(type, 1);
            }
            needsUpdate = true;
        }
    }

    public void setLastFluid(@NotNull FluidStack fluid) {
        if (fluid.isEmpty()) {
            fluidTank.setEmpty();
        } else {
            lastFluid = fluid;
            fluidTank.setStack(new FluidStack(fluid, 1));
        }
    }

//    @Override
//    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
//        return 0;
//    }
//
//    @Override
//    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
//        return 0;
//    }
//
//    @Override
//    public Iterator<StorageView<FluidVariant>> iterator() {
//        return null;
//    }

    public static class FluidTransferEvent extends TransferEvent<FluidNetwork> {

        public final FluidStack fluidType;

        public FluidTransferEvent(FluidNetwork network, @NotNull FluidStack type) {
            super(network);
            fluidType = type;
        }
    }
}
