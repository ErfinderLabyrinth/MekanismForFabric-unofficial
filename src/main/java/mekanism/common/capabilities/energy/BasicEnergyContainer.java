package mekanism.common.capabilities.energy;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.math.FloatingLong;
import mekanism.common.util.NBTUtils;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

@NothingNullByDefault
public class BasicEnergyContainer extends SnapshotParticipant<Long> implements IEnergyContainer {
    public static final Predicate<@NotNull AutomationType> alwaysTrue = ConstantPredicates.alwaysTrue();
    public static final Predicate<@NotNull AutomationType> alwaysFalse = ConstantPredicates.alwaysFalse();
    public static final Predicate<@NotNull AutomationType> internalOnly = automationType -> automationType == AutomationType.INTERNAL;
    public static final Predicate<@NotNull AutomationType> manualOnly = automationType -> automationType == AutomationType.MANUAL;
    public static final Predicate<@NotNull AutomationType> notExternal = automationType -> automationType != AutomationType.EXTERNAL;

    public static BasicEnergyContainer create(long maxEnergy, @Nullable IContentsListener listener) {
        //Objects.requireNonNull(maxEnergy, "Max energy cannot be null");
        return new BasicEnergyContainer(maxEnergy, alwaysTrue, alwaysTrue, listener);
    }

    public static BasicEnergyContainer input(long maxEnergy, @Nullable IContentsListener listener) {
        //Objects.requireNonNull(maxEnergy, "Max energy cannot be null");
        return new BasicEnergyContainer(maxEnergy, notExternal, alwaysTrue, listener);
    }

    public static BasicEnergyContainer output(long maxEnergy, @Nullable IContentsListener listener) {
        //Objects.requireNonNull(maxEnergy, "Max energy cannot be null");
        return new BasicEnergyContainer(maxEnergy, alwaysTrue, internalOnly, listener);
    }

    public static BasicEnergyContainer create(long maxEnergy, Predicate<@NotNull AutomationType> canExtract, Predicate<@NotNull AutomationType> canInsert,
          @Nullable IContentsListener listener) {
        //Objects.requireNonNull(maxEnergy, "Max energy cannot be null");
        Objects.requireNonNull(canExtract, "Extraction validity check cannot be null");
        Objects.requireNonNull(canInsert, "Insertion validity check cannot be null");
        return new BasicEnergyContainer(maxEnergy, canExtract, canInsert, listener);
    }

    private long stored = 0;
    protected final Predicate<@NotNull AutomationType> canExtract;
    protected final Predicate<@NotNull AutomationType> canInsert;
    private final long maxEnergy;
    @Nullable
    private final IContentsListener listener;

    protected BasicEnergyContainer(long maxEnergy, Predicate<@NotNull AutomationType> canExtract, Predicate<@NotNull AutomationType> canInsert,
          @Nullable IContentsListener listener) {
        this.maxEnergy = maxEnergy;
        this.canExtract = canExtract;
        this.canInsert = canInsert;
        this.listener = listener;
    }

    @Override
    public void onContentsChanged() {
        if (listener != null) {
            listener.onContentsChanged();
        }
    }

    @Override
    public long getEnergy() {
        return stored;
    }

    @Override
    public void setEnergy(long energy, TransactionContext t) {
        if (stored != energy) {
            updateSnapshots(t);
            stored = energy;
        }
    }

    @Override
    public void setEnergy(long energy) {
        if (stored != energy) {
            stored = energy;
        }
    }

    /**
     * Helper method to allow easily setting a rate at which this {@link BasicEnergyContainer} can insert/extract energy.
     *
     * @param automationType The automation type to limit the rate by or null if we don't have access to an automation type.
     *
     * @return The rate this tank can insert/extract at.
     *
     * @implNote By default, this returns {@link FloatingLong#MAX_VALUE} to not actually limit the container's rate. By default, this is also ignored for direct setting
     * of the stack/stack size
     */
    protected long getRate(@Nullable AutomationType automationType) {
        //TODO: Decide if we want to split this into a rate for inserting and a rate for extracting.
        return Long.MAX_VALUE;
    }

    @Override
    public long insert(long amount, TransactionContext t) {
        if (amount == 0 || !canInsert.test(null)) {
            return 0;
        }
        long needed = Long.min(getRate(null), getNeeded());
        if (needed == 0) {
            //Fail if we are a full container or our rate is zero
            return 0;
        }
        long toAdd = Long.min(amount, needed);
        if (toAdd != 0) {
            updateSnapshots(t);
            //If we want to actually insert the energy, then update the current energy
            // Note: this also will mark that the contents changed
            stored += toAdd;
        }
        return toAdd;
    }

    @Override
    public long extract(long amount, TransactionContext t) {
        if (isEmpty() || amount == 0 || !canExtract.test(null)) {
            return 0;
        }
        long ret = Long.min(Long.min(getRate(null), getEnergy()), amount);
        if (ret != 0) {
            updateSnapshots(t);
            //Note: this also will mark that the contents changed
            stored -= ret;
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Overwritten so that if we decide to change to returning a cached/copy of our value in {@link #getEnergy()}, we can optimize out the copying.
     */
    @Override
    public boolean isEmpty() {
        return stored == 0;
    }

    @Override
    public long getMaxEnergy() {
        return maxEnergy;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Overwritten so that if we decide to change to returning a cached/copy of our value in {@link #getEnergy()}, we can optimize out the copying.
     */
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (!isEmpty()) {
            nbt.putLong(NBTConstants.STORED, stored);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        NBTUtils.setLongIfPresent(nbt, NBTConstants.STORED, this::setEnergy);
    }

    @Override
    protected Long createSnapshot() {
        return stored;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        stored = snapshot;
    }

    @Override
    protected void onFinalCommit() {
        listener.onContentsChanged();
    }
}