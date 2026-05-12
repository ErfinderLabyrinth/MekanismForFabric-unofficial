package mekanism.common.content.network;

import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.IMekanismStrictEnergyHandler;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.VariableCapacityEnergyContainer;
import mekanism.common.content.network.distribution.EnergyAcceptorTarget;
import mekanism.common.content.network.distribution.EnergyTransmitterSaveTarget;
import mekanism.common.content.network.transmitter.UniversalCable;
import mekanism.common.lib.transmitter.DynamicBufferedNetwork;
import mekanism.common.util.EmitUtils;
import mekanism.common.util.text.EnergyDisplay;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

public class EnergyNetwork extends DynamicBufferedNetwork<IStrictEnergyHandler, EnergyNetwork, Long, UniversalCable> implements IMekanismStrictEnergyHandler {

    private final List<IEnergyContainer> energyContainers;
    public final VariableCapacityEnergyContainer energyContainer;
    private FloatingLong prevTransferAmount = FloatingLong.ZERO;
    private FloatingLong floatingLongCapacity = FloatingLong.ZERO;

    public EnergyNetwork(UUID networkID) {
        super(networkID);
        energyContainer = VariableCapacityEnergyContainer.create(this::getCapacity, BasicEnergyContainer.alwaysTrue, BasicEnergyContainer.alwaysTrue, this);
        energyContainers = Collections.singletonList(energyContainer);
    }

    public EnergyNetwork(Collection<EnergyNetwork> networks) {
        this(UUID.randomUUID());
        adoptAllAndRegister(networks);
    }

    @Override
    protected void forceScaleUpdate() {
        if (!energyContainer.isEmpty() && energyContainer.getMaxEnergy() != 0) {
            currentScale = Math.min(1, (float)(energyContainer.getEnergy() / energyContainer.getMaxEnergy()));
        } else {
            currentScale = 0;
        }
    }

    @Override
    public List<UniversalCable> adoptTransmittersAndAcceptorsFrom(EnergyNetwork net) {
        FloatingLong oldCapacity = getCapacityAsFloatingLong();
        List<UniversalCable> transmittersToUpdate = super.adoptTransmittersAndAcceptorsFrom(net);
        //Merge the energy scales
        FloatingLong ourScale = currentScale == 0 ? FloatingLong.ZERO : oldCapacity.multiply(currentScale);
        FloatingLong theirScale = net.currentScale == 0 ? FloatingLong.ZERO : net.getCapacityAsFloatingLong().multiply(net.currentScale);
        FloatingLong capacity = getCapacityAsFloatingLong();
        currentScale = Math.min(1, capacity.isZero() ? 0 : ourScale.add(theirScale).divide(getCapacityAsFloatingLong()).floatValue());
        if (!isRemote() && !net.energyContainer.isEmpty()) {
            energyContainer.setEnergy(energyContainer.getEnergy() + net.getBuffer());
            net.energyContainer.setEnergy(0);
        }
        return transmittersToUpdate;
    }

    @NotNull
    @Override
    public Long getBuffer() {
        return energyContainer.getEnergy();
    }

    @Override
    public void absorbBuffer(UniversalCable transmitter) {
        long energy = transmitter.releaseShare();
        if (energy != 0) {
            energyContainer.setEnergy(energyContainer.getEnergy() + energy);
        }
    }

    @Override
    public void clampBuffer() {
        if (!energyContainer.isEmpty()) {
            long capacity = getCapacity();
            if (energyContainer.getEnergy() > capacity) {
                energyContainer.setEnergy(capacity);
            }
        }
    }

    @Override
    protected synchronized void updateCapacity(UniversalCable transmitter) {
        floatingLongCapacity = floatingLongCapacity.plusEqual(transmitter.getCapacityAsFloatingLong());
        capacity = floatingLongCapacity.longValue();
    }

    @Override
    public synchronized void updateCapacity() {
        FloatingLong sum = FloatingLong.ZERO;
        for (UniversalCable transmitter : transmitters) {
            sum = sum.plusEqual(transmitter.getCapacityAsFloatingLong());
        }
        if (!floatingLongCapacity.equals(sum)) {
            floatingLongCapacity = sum;
            capacity = floatingLongCapacity.longValue();
        }
    }

    @NotNull
    public FloatingLong getCapacityAsFloatingLong() {
        return floatingLongCapacity;
    }

    @Override
    protected void updateSaveShares(@Nullable UniversalCable triggerTransmitter) {
        super.updateSaveShares(triggerTransmitter);
        if (!isEmpty()) {
            EnergyTransmitterSaveTarget saveTarget = new EnergyTransmitterSaveTarget(transmitters);
            EmitUtils.sendToAcceptors(saveTarget, FloatingLong.create(energyContainer.getEnergy()));
            saveTarget.saveShare();
        }
    }

    private FloatingLong tickEmit(FloatingLong energyToSend) {
        Collection<Map<Direction, Optional<IStrictEnergyHandler>>> acceptorValues = acceptorCache.getAcceptorValues();
        EnergyAcceptorTarget target = new EnergyAcceptorTarget(acceptorValues.size() * 2);
        for (Map<Direction, Optional<IStrictEnergyHandler>> acceptors : acceptorValues) {
            for (Optional<IStrictEnergyHandler> lazyAcceptor : acceptors.values()) {
                lazyAcceptor.ifPresent(acceptor -> {
                    boolean shouldAdd;
                    try(Transaction t = Transaction.openOuter()) {
                        shouldAdd = acceptor.insertEnergy(energyToSend, t).smallerThan(energyToSend);
                    }
                    if (shouldAdd) {
                        target.addHandler(acceptor);
                    }
                });
            }
        }
        return EmitUtils.sendToAcceptors(target, energyToSend.copy());
    }

    @Override
    public String toString() {
        return "[EnergyNetwork] " + transmittersSize() + " transmitters, " + getAcceptorCount() + " acceptors.";
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (needsUpdate) {
            Mekanism.instance.onEnergyTransferred(new EnergyTransferEvent(this));
            needsUpdate = false;
        }
        if (energyContainer.isEmpty()) {
            prevTransferAmount = FloatingLong.ZERO;
        } else {
            prevTransferAmount = tickEmit(FloatingLong.create(energyContainer.getEnergy()));
            try(Transaction t = Transaction.openOuter()) {
                energyContainer.extract(prevTransferAmount.longValue(), t);
            }
        }
    }

    @Override
    protected float computeContentScale() {
        float scale = (float) ((double)energyContainer.getEnergy() / energyContainer.getMaxEnergy());
        float ret = Math.max(currentScale, scale);
        if (!prevTransferAmount.isZero() && ret < 1) {
            ret = Math.min(1, ret + 0.02F);
        } else if (prevTransferAmount.isZero() && ret > 0) {
            ret = Math.max(scale, ret - 0.02F);
        }
        return ret;
    }

    @Override
    public Component getNeededInfo() {
        return EnergyDisplay.of(energyContainer.getNeeded()).getTextComponent();
    }

    @Override
    public Component getStoredInfo() {
        return EnergyDisplay.of(energyContainer.getEnergy()).getTextComponent();
    }

    @Override
    public Component getFlowInfo() {
        return MekanismLang.GENERIC_PER_TICK.translate(EnergyDisplay.of(prevTransferAmount));
    }

    @Override
    public Object getNetworkReaderCapacity() {
        return getCapacityAsFloatingLong();
    }

    @NotNull
    @Override
    public Component getTextComponent() {
        return MekanismLang.NETWORK_DESCRIPTION.translate(MekanismLang.ENERGY_NETWORK, transmittersSize(), getAcceptorCount());
    }

    @Override
    public EnergyStorage getEnergyContainer(@Nullable Direction side) {
        return energyContainers.get(0);
    }

    @Override
    public void onContentsChanged() {
        markDirty();
    }

    public static class EnergyTransferEvent extends TransferEvent<EnergyNetwork> {

        public EnergyTransferEvent(EnergyNetwork network) {
            super(network);
        }
    }
}