package mekanism.common.content.matrix;

import com.google.common.math.LongMath;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tier.InductionProviderTier;
import mekanism.common.tile.multiblock.TileEntityInductionCell;
import mekanism.common.tile.multiblock.TileEntityInductionProvider;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.Map;
import java.util.Set;

@NothingNullByDefault
public class MatrixEnergyContainer extends SnapshotParticipant<Pair<Long, Long>> implements IEnergyContainer {

    private final Map<BlockPos, InductionProviderTier> providers = new Object2ObjectOpenHashMap<>();
    private final Map<BlockPos, IEnergyContainer> cells = new Object2ObjectOpenHashMap<>();
    private final Set<BlockPos> invalidPositions = new ObjectOpenHashSet<>();

    //TODO: Eventually we could look into extending FloatingLong to have a "BigInt" styled implementation that is used by the class
    // at the very least for keeping track of the cached values and rates
    private long queuedOutput = 0;
    private long queuedInput = 0;
    private long lastOutput = 0;
    private long lastInput = 0;

    private long cachedTotal = 0;
    private long transferCap = 0;
    private long storageCap = 0;

    private final MatrixMultiblockData multiblock;

    public MatrixEnergyContainer(MatrixMultiblockData multiblock) {
        this.multiblock = multiblock;
    }

    public void addCell(BlockPos pos, TileEntityInductionCell cell) {
        //As we already have the two different variables just pass them instead of accessing world to get tile again
        MachineEnergyContainer<TileEntityInductionCell> energyContainer = cell.getEnergyContainer();
        cells.put(pos, energyContainer);
        storageCap = LongMath.saturatedAdd(storageCap, energyContainer.getMaxEnergy());
        cachedTotal = LongMath.saturatedAdd(cachedTotal, energyContainer.getEnergy());
    }

    public void addProvider(BlockPos pos, TileEntityInductionProvider provider) {
        providers.put(pos, provider.tier);
        transferCap = LongMath.saturatedAdd(transferCap, provider.tier.getOutput());
    }

    //TODO: I believe this is needed or at least will be after we eventually rewrite some of the multiblock system
    // currently I think it may just be rechecking the entire structure when something changes internally
    // We need to validate that does properly happen even if the cell is floating in the middle and not touching any walls
    // We may also want to make cells and providers extend TileEntityInternalMultiblock
    public void removeInternal(BlockPos pos) {
        if (invalidPositions.add(pos)) {
            if (providers.containsKey(pos)) {
                //It is a provider
                transferCap = Math.max(transferCap - providers.get(pos).getOutput(), 0);
            } else if (cells.containsKey(pos)) {
                //It is a cell
                //TODO: Handle this better, as I believe we *technically* could have this cause the cached total to become negative
                // It may work better if we just flush the buffer writing immediately, and then recalculate the cached totals/caps
                IEnergyContainer cellContainer = cells.get(pos);
                storageCap = LongMath.saturatedAdd(storageCap, cellContainer.getMaxEnergy());
                cachedTotal = Math.max(cachedTotal - cellContainer.getEnergy(), 0);
            }
        }
    }

    public void invalidate() {
        //Force save
        tick();
        //And reset everything
        cells.clear();
        providers.clear();
        queuedOutput = 0;
        queuedInput = 0;
        lastOutput = 0;
        lastInput = 0;
        cachedTotal = 0;
        transferCap = 0;
        storageCap = 0;
    }

    public void tick() {
        if (!invalidPositions.isEmpty()) {
            for (BlockPos invalidPosition : invalidPositions) {
                cells.remove(invalidPosition);
                providers.remove(invalidPosition);
            }
            invalidPositions.clear();
        }
        int compare = Long.compare(queuedInput, queuedOutput);
        if (compare < 0) {
            //queuedInput is smaller - we are removing energy
            removeEnergy(queuedOutput - queuedInput);
        } else if (compare > 0) {
            //queuedInput is larger - we are adding energy
            addEnergy(queuedInput - queuedOutput);
        }
        lastInput = queuedInput;
        lastOutput = queuedOutput;
        queuedInput = 0;
        queuedOutput = 0;
    }

    private void addEnergy(long energy) {
        cachedTotal = LongMath.saturatedAdd(cachedTotal, energy);
        for (IEnergyContainer container : cells.values()) {
            //Note: inserting into the cell's energy container handles marking the cell for saving if it changes
            long remainder = energy;
            try(Transaction t = Transaction.openOuter()) {
                remainder -= container.insert(energy, t);
                t.commit();
            }
            if (remainder < energy) {
                //Our cell accepted at least some energy
                if (remainder == 0) {
                    //Check less than equal rather than just equal in case something went wrong
                    // and break if we don't have any energy left to add
                    break;
                }
                energy = remainder;
            }
        }
    }

    private void removeEnergy(long energy) {
        cachedTotal = Math.max(cachedTotal - energy, 0);
        for (IEnergyContainer container : cells.values()) {
            //Note: extracting from the cell's energy container handles marking the cell for saving if it changes
            long extracted;
            try(Transaction t = Transaction.openOuter()) {
                extracted = container.extract(energy, t);
            }
            if (extracted != 0) {
                energy = Math.max(energy - extracted, 0);
                if (energy == 0) {
                    //Check less than equal rather than just equal in case something went wrong
                    // and break if we don't need to remove any more energy
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return The energy post queue when this container next actually updates/saves to disk
     */
    @Override
    public long getEnergy() {
        return cachedTotal + queuedInput - queuedOutput;
    }

    @Override
    public void setEnergy(long energy, TransactionContext t) {
        //Throws a RuntimeException as specified is allowed when something unexpected happens
        // As setEnergy is more meant to be used as an internal method
        throw new RuntimeException("Unexpected call to setEnergy. The matrix energy container does not support directly setting the energy.");
    }

    @Override
    public void setEnergy(long energy) {
        throw new RuntimeException("Unexpected call to directSetEnergy. The matrix energy container does not support directly setting the energy.");
    }

    @Override
    public long insert(long amount, TransactionContext t) {
        if (amount == 0 || !multiblock.isFormed()) {
            return 0;
        }
        long toAdd = Math.min(Math.min(amount, getRemainingInput()), getNeeded());
        if (toAdd == 0) {
            //Exit if we don't actually have anything to add, either due to how much we need
            // or due to the remaining rate limit
            return 0;
        }

        updateSnapshots(t);
        //Increase how much we are inputting
        queuedInput = LongMath.saturatedAdd(queuedInput, toAdd);
        return toAdd;
    }

    @Override
    public long extract(long amount, TransactionContext t) {
        if (isEmpty() || amount == 0 || !multiblock.isFormed()) {
            return 0;
        }
        //We limit it overall by the amount we can extract plus how much energy we have
        // as we want to be as accurate as possible with the values we return
        // It is possible that the energy we have stored is a lot less than the amount we
        // can output at once such as if the matrix is almost empty.
        amount = Math.min(Math.min(amount, getRemainingOutput()), getEnergy());
        if (amount != 0) {
            updateSnapshots(t);
            //Increase how much we are outputting by the amount we accepted
            queuedOutput = LongMath.saturatedAdd(queuedOutput, amount);
        }
        return amount;
    }

    @Override
    public long getMaxEnergy() {
        return storageCap;
    }

    @Override
    public void onContentsChanged() {
        //Unused
    }

    @Override
    public CompoundTag serializeNBT() {
        //Note: We don't actually have any specific serialization
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }

    private long getRemainingInput() {
        return transferCap - queuedInput;
    }

    private long getRemainingOutput() {
        return transferCap - queuedOutput;
    }

    public long getMaxTransfer() {
        return transferCap;
    }

    public long getLastInput() {
        return lastInput;
    }

    public long getLastOutput() {
        return lastOutput;
    }

    public int getCells() {
        return cells.size();
    }

    public int getProviders() {
        return providers.size();
    }

    @Override
    protected Pair<Long, Long> createSnapshot() {
        return new Pair<>(queuedInput, queuedOutput);
    }

    @Override
    protected void readSnapshot(Pair<Long, Long> snapshot) {
        queuedInput = snapshot.getFirst();
        queuedOutput = snapshot.getSecond();
    }
}