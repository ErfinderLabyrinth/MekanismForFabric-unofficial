package mekanism.common.content.network.transmitter;

import mekanism.api.NBTConstants;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.IMekanismStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.content.network.EnergyNetwork;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.lib.transmitter.acceptor.EnergyAcceptorCache;
import mekanism.common.tier.CableTier;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.upgrade.transmitter.TransmitterUpgradeData;
import mekanism.common.upgrade.transmitter.UniversalCableUpgradeData;
import mekanism.common.util.NBTUtils;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

public class UniversalCable extends BufferedTransmitter<EnergyStorage, EnergyNetwork, Long, UniversalCable> implements IMekanismStrictEnergyHandler,
      IUpgradeableTransmitter<UniversalCableUpgradeData> {

    public final CableTier tier;

    private final List<IEnergyContainer> energyContainers;
    public final BasicEnergyContainer buffer;
    public long lastWrite = 0;

    public UniversalCable(IBlockProvider blockProvider, TileEntityTransmitter tile) {
        super(tile, TransmissionType.ENERGY);
        this.tier = Attribute.getTier(blockProvider, CableTier.class);
        buffer = BasicEnergyContainer.create(getCapacity(), BasicEnergyContainer.alwaysFalse, BasicEnergyContainer.alwaysTrue, this);
        energyContainers = Collections.singletonList(buffer);
    }

    public List<IEnergyContainer> getEnergyContainers() {
        return energyContainers;
    }

    @Override
    protected BlockApiLookup<EnergyStorage, Direction> getAcceptorCacheLookup() {
        return null;
    }

    @Override
    protected EnergyAcceptorCache createAcceptorCache() {
        return new EnergyAcceptorCache(this, getTransmitterTile());
    }

    @Override
    public EnergyAcceptorCache getAcceptorCache() {
        return (EnergyAcceptorCache) super.getAcceptorCache();
    }

    @Override
    public CableTier getTier() {
        return tier;
    }

    @Override
    public void pullFromAcceptors() {
        Set<Direction> connections = getConnections(ConnectionType.PULL);
        if (!connections.isEmpty()) {
            for (EnergyStorage connectedAcceptor : getAcceptorCache().getConnectedAcceptors(connections)) {
                long received;
                try(Transaction t=Transaction.openOuter()) {
                    received = connectedAcceptor.extract(getAvailablePull(), t);
                }
                try(Transaction t = Transaction.openOuter()) {
                    if (received != 0 && takeEnergy(received, t) == received) {
                        //If we received some energy and are able to insert it all
                        connectedAcceptor.extract(received, t);
                        t.commit();
                    }
                }
            }
        }
    }

    private long getAvailablePull() {
        if (hasTransmitterNetwork()) {
            return Long.min(getCapacity(), getTransmitterNetwork().energyContainer.getNeeded());
        }
        return Long.min(getCapacity(), buffer.getNeeded());
    }

    @Override
    public EnergyStorage getEnergyContainer(@Nullable Direction side) {
        if (hasTransmitterNetwork()) {
            return getTransmitterNetwork().getEnergyContainer(side);
        }
        return energyContainers.get(0);
    }

    @Override
    public void onContentsChanged() {
        getTransmitterTile().setChanged();
    }

    @Nullable
    @Override
    public UniversalCableUpgradeData getUpgradeData() {
        return new UniversalCableUpgradeData(redstoneReactive, getConnectionTypesRaw(), buffer);
    }

    @Override
    public boolean dataTypeMatches(@NotNull TransmitterUpgradeData data) {
        return data instanceof UniversalCableUpgradeData;
    }

    @Override
    public void parseUpgradeData(@NotNull UniversalCableUpgradeData data) {
        redstoneReactive = data.redstoneReactive;
        setConnectionTypesRaw(data.connectionTypes);
        buffer.setEnergy(data.buffer.getEnergy());
    }

    @Override
    public void read(@NotNull CompoundTag nbtTags) {
        super.read(nbtTags);
        if (nbtTags.contains(NBTConstants.ENERGY_STORED, Tag.TAG_STRING)) {
            try {
                lastWrite = nbtTags.getLong(NBTConstants.ENERGY_STORED);
            } catch (NumberFormatException e) {
                lastWrite = 0;
            }
        } else {
            lastWrite = 0;
        }
        buffer.setEnergy(lastWrite);
    }

    @NotNull
    @Override
    public CompoundTag write(@NotNull CompoundTag nbtTags) {
        super.write(nbtTags);
        if (hasTransmitterNetwork()) {
            getTransmitterNetwork().validateSaveShares(this);
        }
        if (lastWrite == 0) {
            nbtTags.remove(NBTConstants.ENERGY_STORED);
        } else {
            nbtTags.putLong(NBTConstants.ENERGY_STORED, lastWrite);
        }
        return nbtTags;
    }

    @Override
    public EnergyNetwork createNetworkByMerging(Collection<EnergyNetwork> networks) {
        return new EnergyNetwork(networks, getTileWorld());
    }

    @Override
    public boolean isValidAcceptor(Level level, BlockPos pos, Direction side) {
        return super.isValidAcceptor(level, pos, side) && getAcceptorCache().hasStrictEnergyHandlerAndListen(level, pos, side);
    }

    @Override
    public EnergyNetwork createEmptyNetworkWithID(UUID networkID) {
        return new EnergyNetwork(networkID, getTileWorld());
    }

    @NotNull
    @Override
    public Long releaseShare() {
        long energy = buffer.getEnergy();
        buffer.setEmpty();
        return energy;
    }

    @NotNull
    @Override
    public Long getShare() {
        return buffer.getEnergy();
    }

    @Override
    public boolean noBufferOrFallback() {
        return getBufferWithFallback() == 0;
    }

    @NotNull
    @Override
    public Long getBufferWithFallback() {
        long buffer = getShare();
        //If we don't have a buffer try falling back to the network's buffer
        if (buffer == 0 && hasTransmitterNetwork()) {
            return getTransmitterNetwork().getBuffer();
        }
        return buffer;
    }

    @Override
    public void takeShare() {
        if (hasTransmitterNetwork()) {
            EnergyNetwork transmitterNetwork = getTransmitterNetwork();
            if (!transmitterNetwork.energyContainer.isEmpty() && lastWrite != 0) {
                transmitterNetwork.energyContainer.setEnergy(transmitterNetwork.energyContainer.getEnergy() - lastWrite);
                buffer.setEnergy(lastWrite);
            }
        }
    }

    @NotNull
    public FloatingLong getCapacityAsFloatingLong() {
        return tier.getCableCapacity();
    }

    @Override
    public long getCapacity() {
        return getCapacityAsFloatingLong().longValue();
    }

    /**
     * @return remainder
     */
    private long takeEnergy(long amount, Transaction t) {
        if (hasTransmitterNetwork()) {
            return getTransmitterNetwork().energyContainer.insert(amount, t);
        }
        return buffer.insert(amount, t);
    }

    @Override
    protected void handleContentsUpdateTag(@NotNull EnergyNetwork network, @NotNull CompoundTag tag) {
        super.handleContentsUpdateTag(network, tag);
        NBTUtils.setLongIfPresent(tag, NBTConstants.ENERGY_STORED, network.energyContainer::setEnergy);
        NBTUtils.setFloatIfPresent(tag, NBTConstants.SCALE, scale -> network.currentScale = scale);
    }
}