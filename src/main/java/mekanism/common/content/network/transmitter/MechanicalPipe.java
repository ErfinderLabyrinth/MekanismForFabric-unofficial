package mekanism.common.content.network.transmitter;

import mekanism.api.FluidStack;
import mekanism.api.NBTConstants;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.fluid.IMekanismFluidHandler;
import mekanism.api.math.MathUtils;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.content.network.FluidNetwork;
import mekanism.common.lib.transmitter.CompatibleTransmitterValidator;
import mekanism.common.lib.transmitter.CompatibleTransmitterValidator.CompatibleFluidTransmitterValidator;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.lib.transmitter.acceptor.AcceptorCache;
import mekanism.common.tier.PipeTier;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.upgrade.transmitter.MechanicalPipeUpgradeData;
import mekanism.common.upgrade.transmitter.TransmitterUpgradeData;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MechanicalPipe extends BufferedTransmitter<Storage<FluidVariant>, FluidNetwork, FluidStack, MechanicalPipe> implements IMekanismFluidHandler,
      IUpgradeableTransmitter<MechanicalPipeUpgradeData> {

    public final PipeTier tier;
    @NotNull
    public FluidStack saveShare = FluidStack.EMPTY;
    private final List<IExtendedFluidTank> tanks;
    public final BasicFluidTank buffer;

    public MechanicalPipe(IBlockProvider blockProvider, TileEntityTransmitter tile) {
        super(tile, TransmissionType.FLUID);
        this.tier = Attribute.getTier(blockProvider, PipeTier.class);
        //TODO: If we make fluids support longs then adjust this
        buffer = BasicFluidTank.create(MathUtils.clampToInt(getCapacity()), BasicFluidTank.alwaysFalse, BasicFluidTank.alwaysTrue, this);
        tanks = Collections.singletonList(buffer);
    }

    @Override
    protected BlockApiLookup<Storage<FluidVariant>, Direction> getAcceptorCacheLookup() {
        return FluidStorage.SIDED;
    }

    @Override
    public AcceptorCache<Storage<FluidVariant>> getAcceptorCache() {
        //Cast it here to make things a bit easier, as we know createAcceptorCache by default returns an object of type AcceptorCache
        return (AcceptorCache<Storage<FluidVariant>>) super.getAcceptorCache();
    }

    @Override
    public PipeTier getTier() {
        return tier;
    }

    @Override
    public void pullFromAcceptors() {
        Set<Direction> connections = getConnections(ConnectionType.PULL);
        if (!connections.isEmpty()) {
            for (Storage<FluidVariant> connectedAcceptor : getAcceptorCache().getConnectedAcceptors(connections)) {
                FluidStack received = FluidStack.EMPTY;
                //Note: We recheck the buffer each time in case we ended up accepting fluid somewhere
                // and our buffer changed and is no longer empty
                FluidStack bufferWithFallback = getBufferWithFallback();
                if (bufferWithFallback.isEmpty()) {
                    //If we don't have a fluid stored try pulling as much as we are able to
                    for (StorageView<FluidVariant> view : connectedAcceptor) {
                        if (!view.isResourceBlank() && view.getAmount() > 0) {
                            try(Transaction t = Transaction.openOuter()) {
                                long amount = view.extract(view.getResource(), getAvailablePull(), t);
                                received = new FluidStack(view.getResource(), amount);
                            }
                            break;
                        }
                    }
                } else {
                    //Otherwise, try draining the same type of fluid we have stored requesting up to as much as we are able to pull
                    // We do this to better support multiple tanks in case the fluid we have stored we could pull out of a block's
                    // second tank but just asking to drain a specific amount
                    try(Transaction t = Transaction.openOuter()) {
                        long amount = connectedAcceptor.extract(bufferWithFallback.variant(), getAvailablePull(), t);
                        received = new FluidStack(bufferWithFallback.variant(), amount);
                    }
                }
                long amountCanTake;
                try (Transaction t = Transaction.openOuter()) {
                    amountCanTake = takeFluid(received, t);
                }
                if (!received.isEmpty() && amountCanTake != 0) {
                    //If we received some fluid and are able to insert it all, then actually extract it and insert it into our thing.
                    // Note: We extract first after simulating ourselves because if the target gave a faulty simulation value, we want to handle it properly
                    // and not accidentally dupe anything, and we know our simulation we just performed on taking it is valid
                    long amount = 0;
                    try(Transaction t = Transaction.openOuter()) {
                        amount = connectedAcceptor.extract(received.variant(), received.amount(), t);
                        takeFluid(new FluidStack(received.variant(), amount), t);
                        t.commit();
                    }
                }
            }
        }
    }

    private int getAvailablePull() {
        if (hasTransmitterNetwork()) {
            return (int)Math.min(tier.getPipePullAmount(), getTransmitterNetwork().fluidTank.getNeeded());
        }
        return (int)Math.min(tier.getPipePullAmount(), buffer.getNeeded());
    }

    @Nullable
    @Override
    public MechanicalPipeUpgradeData getUpgradeData() {
        return new MechanicalPipeUpgradeData(redstoneReactive, getConnectionTypesRaw(), getShare());
    }

    @Override
    public boolean dataTypeMatches(@NotNull TransmitterUpgradeData data) {
        return data instanceof MechanicalPipeUpgradeData;
    }

    @Override
    public void parseUpgradeData(@NotNull MechanicalPipeUpgradeData data) {
        redstoneReactive = data.redstoneReactive;
        setConnectionTypesRaw(data.connectionTypes);
        try(Transaction t=Transaction.openOuter()) {
            takeFluid(data.contents, t);
            t.commit();
        }
    }

    @Override
    public void read(@NotNull CompoundTag nbtTags) {
        super.read(nbtTags);
        if (nbtTags.contains(NBTConstants.FLUID_STORED, Tag.TAG_COMPOUND)) {
            saveShare = FluidStack.loadFluidStackFromNBT(nbtTags.getCompound(NBTConstants.FLUID_STORED));
        } else {
            saveShare = FluidStack.EMPTY;
        }
        buffer.setStack(saveShare);
    }

    @NotNull
    @Override
    public CompoundTag write(@NotNull CompoundTag nbtTags) {
        super.write(nbtTags);
        if (hasTransmitterNetwork()) {
            getTransmitterNetwork().validateSaveShares(this);
        }
        if (saveShare.isEmpty()) {
            nbtTags.remove(NBTConstants.FLUID_STORED);
        } else {
            nbtTags.put(NBTConstants.FLUID_STORED, saveShare.writeToNBT(new CompoundTag()));
        }
        return nbtTags;
    }

    @Override
    public boolean isValidAcceptor(Level level, BlockPos pos, Direction side) {
        return super.isValidAcceptor(level, pos, side) && getAcceptorCache().isAcceptorAndListen(level, pos, side, FluidStorage.SIDED);
    }

    @Override
    public CompatibleTransmitterValidator<Storage<FluidVariant>, FluidNetwork, MechanicalPipe> getNewOrphanValidator() {
        return new CompatibleFluidTransmitterValidator(this);
    }

    @Override
    public boolean isValidTransmitter(TileEntityTransmitter transmitter, Direction side) {
        if (super.isValidTransmitter(transmitter, side) && transmitter.getTransmitter() instanceof MechanicalPipe other) {
            FluidStack buffer = getBufferWithFallback();
            if (buffer.isEmpty() && hasTransmitterNetwork() && getTransmitterNetwork().getPrevTransferAmount() > 0) {
                buffer = getTransmitterNetwork().lastFluid;
            }
            FluidStack otherBuffer = other.getBufferWithFallback();
            if (otherBuffer.isEmpty() && other.hasTransmitterNetwork() && other.getTransmitterNetwork().getPrevTransferAmount() > 0) {
                otherBuffer = other.getTransmitterNetwork().lastFluid;
            }
            return buffer.isEmpty() || otherBuffer.isEmpty() || buffer.equals(otherBuffer);
        }
        return false;
    }

    @Override
    public FluidNetwork createEmptyNetworkWithID(UUID networkID) {
        return new FluidNetwork(networkID, getTileWorld());
    }

    @Override
    public FluidNetwork createNetworkByMerging(Collection<FluidNetwork> networks) {
        return new FluidNetwork(networks, getTileWorld());
    }

    @Override
    protected boolean canHaveIncompatibleNetworks() {
        return true;
    }

    @Override
    public long getCapacity() {
        return tier.getPipeCapacity();
    }

    @NotNull
    @Override
    public FluidStack releaseShare() {
        FluidStack ret = buffer.getFluid();
        buffer.setEmpty();
        return ret;
    }

    @Override
    public boolean noBufferOrFallback() {
        return getBufferWithFallback().isEmpty();
    }

    @NotNull
    @Override
    public FluidStack getBufferWithFallback() {
        FluidStack buffer = getShare();
        //If we don't have a buffer try falling back to the network's buffer
        if (buffer.isEmpty() && hasTransmitterNetwork()) {
            return getTransmitterNetwork().getBuffer();
        }
        return buffer;
    }

    @NotNull
    @Override
    public FluidStack getShare() {
        return buffer.getFluid();
    }

    @Override
    public void takeShare() {
        if (hasTransmitterNetwork()) {
            FluidNetwork network = getTransmitterNetwork();
            if (!network.fluidTank.isEmpty() && !saveShare.isEmpty()) {
                long amount = saveShare.amount();
                MekanismUtils.logMismatchedStackSize(network.fluidTank.shrinkStack(amount), amount);
                buffer.setStack(saveShare);
            }
        }
    }

    @Override
    public Storage<FluidVariant> getFluidTanks(@Nullable Direction side) {
        if (hasTransmitterNetwork()) {
            return getTransmitterNetwork().getFluidTanks(side);
        }
        return new CombinedStorage<>(tanks);
    }

    @Override
    public void onContentsChanged() {
        getTransmitterTile().setChanged();
    }

    /**
     * @return remainder
     */
    @NotNull
    public long takeFluid(@NotNull FluidStack fluid, Transaction t) {
        if (hasTransmitterNetwork()) {
            return getTransmitterNetwork().fluidTank.insert(fluid.variant(), fluid.amount(), t);
        }
        return buffer.insert(fluid.variant(), fluid.amount(), t);
    }

    @Override
    protected void handleContentsUpdateTag(@NotNull FluidNetwork network, @NotNull CompoundTag tag) {
        super.handleContentsUpdateTag(network, tag);
        NBTUtils.setFluidStackIfPresent(tag, NBTConstants.FLUID_STORED, network::setLastFluid);
        NBTUtils.setFloatIfPresent(tag, NBTConstants.SCALE, scale -> network.currentScale = scale);
    }
}