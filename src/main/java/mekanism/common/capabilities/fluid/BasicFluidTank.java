package mekanism.common.capabilities.fluid;

import mekanism.api.AutomationType;
import mekanism.api.FluidStack;
import mekanism.api.IContentsListener;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.RegistryUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@NothingNullByDefault
public class BasicFluidTank extends SnapshotParticipant<FluidStack> implements IExtendedFluidTank {

    public static final Predicate<@NotNull FluidStack> alwaysTrue = ConstantPredicates.alwaysTrue();
    public static final Predicate<@NotNull FluidStack> alwaysFalse = ConstantPredicates.alwaysFalse();
    public static final BiPredicate<@NotNull FluidStack, @NotNull AutomationType> alwaysTrueBi = ConstantPredicates.alwaysTrueBi();
    public static final BiPredicate<@NotNull FluidStack, @NotNull AutomationType> internalOnly = ConstantPredicates.internalOnly();
    public static final BiPredicate<@NotNull FluidStack, @NotNull AutomationType> notExternal = ConstantPredicates.notExternal();

    public static BasicFluidTank create(int capacity, @Nullable IContentsListener listener) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be at least zero");
        }
        return new BasicFluidTank(capacity, alwaysTrueBi, alwaysTrueBi, alwaysTrue, listener);
    }

    public static BasicFluidTank create(int capacity, Predicate<@NotNull FluidStack> validator, @Nullable IContentsListener listener) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be at least zero");
        }
        Objects.requireNonNull(validator, "Fluid validity check cannot be null");
        return new BasicFluidTank(capacity, alwaysTrueBi, alwaysTrueBi, validator, listener);
    }

    public static BasicFluidTank create(int capacity, Predicate<@NotNull FluidStack> canExtract, Predicate<@NotNull FluidStack> canInsert,
          @Nullable IContentsListener listener) {
        return create(capacity, canExtract, canInsert, alwaysTrue, listener);
    }

    public static BasicFluidTank input(int capacity, Predicate<@NotNull FluidStack> validator, @Nullable IContentsListener listener) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be at least zero");
        }
        Objects.requireNonNull(validator, "Fluid validity check cannot be null");
        return new BasicFluidTank(capacity, notExternal, alwaysTrueBi, validator, listener);
    }

    public static BasicFluidTank input(int capacity, Predicate<@NotNull FluidStack> canInsert, Predicate<@NotNull FluidStack> validator, @Nullable IContentsListener listener) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be at least zero");
        }
        Objects.requireNonNull(canInsert, "Insertion validity check cannot be null");
        Objects.requireNonNull(validator, "Fluid validity check cannot be null");
        return new BasicFluidTank(capacity, notExternal, (stack, automationType) -> canInsert.test(stack), validator, listener);
    }

    public static BasicFluidTank output(int capacity, @Nullable IContentsListener listener) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be at least zero");
        }
        return new BasicFluidTank(capacity, alwaysTrueBi, internalOnly, alwaysTrue, listener);
    }

    public static BasicFluidTank create(int capacity, Predicate<@NotNull FluidStack> canExtract, Predicate<@NotNull FluidStack> canInsert,
          Predicate<@NotNull FluidStack> validator, @Nullable IContentsListener listener) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be at least zero");
        }
        Objects.requireNonNull(canExtract, "Extraction validity check cannot be null");
        Objects.requireNonNull(canInsert, "Insertion validity check cannot be null");
        Objects.requireNonNull(validator, "Fluid validity check cannot be null");
        return new BasicFluidTank(capacity, canExtract, canInsert, validator, listener);
    }

    public static BasicFluidTank create(int capacity, BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canExtract,
          BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canInsert, Predicate<@NotNull FluidStack> validator, @Nullable IContentsListener listener) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be at least zero");
        }
        Objects.requireNonNull(canExtract, "Extraction validity check cannot be null");
        Objects.requireNonNull(canInsert, "Insertion validity check cannot be null");
        Objects.requireNonNull(validator, "Fluid validity check cannot be null");
        return new BasicFluidTank(capacity, canExtract, canInsert, validator, listener);
    }

    /**
     * @apiNote This is only protected for direct querying access. To modify this stack the external methods or {@link #setStackUnchecked(FluidStack)} should be used
     * instead.
     */
    protected FluidStack stored = FluidStack.EMPTY;
    private final Predicate<@NotNull FluidStack> validator;
    protected final BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canExtract;
    protected final BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canInsert;
    private final long capacity;
    @Nullable
    private final IContentsListener listener;

    protected BasicFluidTank(long capacity, Predicate<@NotNull FluidStack> canExtract, Predicate<@NotNull FluidStack> canInsert, Predicate<@NotNull FluidStack> validator,
          @Nullable IContentsListener listener) {
        this(capacity, (stack, automationType) -> automationType == AutomationType.MANUAL || canExtract.test(stack), (stack, automationType) -> canInsert.test(stack),
              validator, listener);
    }

    protected BasicFluidTank(long capacity, BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canExtract,
          BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canInsert, Predicate<@NotNull FluidStack> validator, @Nullable IContentsListener listener) {
        this.capacity = capacity * 81; //81 Einheiten pro mB
        this.canExtract = canExtract;
        this.canInsert = canInsert;
        this.validator = validator;
        this.listener = listener;
    }

    @Override
    public void onContentsChanged() {
        if (listener != null) {
            listener.onContentsChanged();
        }
    }

    @NotNull
    @Override
    public FluidStack getFluid() {
        return stored;
    }

    @Override
    public void setStack(FluidStack stack) {
        setStack(stack, true);
    }

    /**
     * Helper method to allow easily setting a rate at which this {@link BasicFluidTank} can insert/extract fluids.
     *
     * @param automationType The automation type to limit the rate by or null if we don't have access to an automation type.
     *
     * @return The rate this tank can insert/extract at.
     *
     * @implNote By default, this returns {@link Integer#MAX_VALUE} to not actually limit the tank's rate. By default, this is also ignored for direct setting of the
     * stack/stack size
     */
    protected long getRate(@Nullable AutomationType automationType) {
        //TODO: Decide if we want to split this into a rate for inserting and a rate for extracting.
        return Long.MAX_VALUE;
    }

    @Override
    public void setStackUnchecked(FluidStack stack) {
        setStack(stack, false);
    }

    private void setStack(FluidStack stack, boolean validateStack) {
        if (stack.isEmpty()) {
            if (stored.isEmpty()) {
                //If we are already empty just exit, to not fire onContentsChanged
                return;
            }
            stored = FluidStack.EMPTY;
        } else if (!validateStack || isFluidValid(stack)) {
            stored = new FluidStack(stack, stack.amount());
        } else {
            //Throws a RuntimeException as specified is allowed when something unexpected happens
            // As setStack is more meant to be used as an internal method
            throw new RuntimeException("Invalid fluid for tank: " + RegistryUtils.getName(stack.variant().getFluid()) + " " + stack.amount());
        }
        onContentsChanged();
    }

//    @Deprecated(forRemoval = true)
//    public FluidStack insert(@NotNull FluidStack stack, Action action, AutomationType automationType) {
//        if (stack.isEmpty() || !isFluidValid(stack) || !canInsert.test(stack, automationType)) {
//            //"Fail quick" if the given stack is empty, or we can never insert the fluid or currently are unable to insert it
//            return stack;
//        }
//        long needed = Math.min(getRate(automationType), getNeeded());
//        if (needed <= 0) {
//            //Fail if we are a full tank or our rate is zero
//            return stack;
//        }
//        boolean sameType = false;
//        if (isEmpty() || (sameType = stored.equals(stack))) {
//            long toAdd = Math.min(stack.amount(), needed);
//            if (action.execute()) {
//                //If we want to actually insert the fluid, then update the current fluid
//                if (sameType) {
//                    //We can just grow our stack by the amount we want to increase it
//                    stored.grow(toAdd);
//                    onContentsChanged();
//                } else {
//                    //If we are not the same type then we have to copy the stack and set it
//                    // Just set it unchecked as we have already validated it
//                    // Note: this also will mark that the contents changed
//                    setStackUnchecked(new FluidStack(stack, toAdd));
//                }
//            }
//            return new FluidStack(stack, stack.amount() - toAdd);
//        }
//        //If we didn't accept this fluid, then just return the given stack
//        return stack;
//    }

//    @Deprecated(forRemoval = true)
//    public FluidStack extract(long amount, Action action, AutomationType automationType) {
//        if (isEmpty() || amount < 1 || !canExtract.test(stored, automationType)) {
//            //"Fail quick" if we don't can never extract from this tank, have a fluid stored, or the amount being requested is less than one
//            return FluidStack.EMPTY;
//        }
//        //Note: While we technically could just return the stack itself if we are removing all that we have, it would require a lot more checks
//        // We also are limiting it by the rate this tank has
//        long size = Math.min(Math.min(getRate(automationType), getAmount()), amount);
//        if (size == 0) {
//            return FluidStack.EMPTY;
//        }
//        FluidStack ret = new FluidStack(stored, size);
//        if (!ret.isEmpty() && action.execute()) {
//            //If shrink gets the size to zero it will update the empty state so that isEmpty() returns true.
//            stored.shrink(ret.amount());
//            onContentsChanged();
//        }
//        return ret;
//    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return validator.test(stack);
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Overwritten so that if we decide to change to returning a cached/copy of our stack in {@link #getFluid()}, we can optimize out the copying, and can also
     * directly modify our stack instead of having to make a copy.
     */
    @Override
    public long setStackSize(long amount) {
        if (isEmpty()) {
            return 0;
        } else if (amount <= 0) {
            setEmpty();
            return 0;
        }
        long maxStackSize = getCapacity();
        if (amount > maxStackSize) {
            amount = maxStackSize;
        }
        if (getAmount() == amount) {
            //If our size is not changing, or we are only simulating the change, don't do anything
            return amount;
        }
        stored = stored.withAmount(amount);
        onContentsChanged();
        return amount;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Overwritten so that we can make this obey the rate limit our tank may have
     */
    @Override
    public long growStack(long amount) {
        long current = getAmount();
        if (amount > 0) {
            //Cap adding amount at how much we need, so that we don't risk integer overflow
            amount = Math.min(Math.min(amount, getNeeded()), getRate(null));
        } else if (amount < 0) {
            amount = Math.max(amount, -getRate(null));
        }
        long newSize = setStackSize(current + amount);
        return newSize - current;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Overwritten so that if we decide to change to returning a cached/copy of our stack in {@link #getFluid()}, we can optimize out the copying.
     */
    @Override
    public boolean isEmpty() {
        return stored.isEmpty();
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Overwritten so that if we decide to change to returning a cached/copy of our stack in {@link #getFluid()}, we can optimize out the copying.
     */
    @Override
    public boolean isFluidEqual(FluidStack other) {
        return stored.equals(other);
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Overwritten so that if we decide to change to returning a cached/copy of our stack in {@link #getFluid()}, we can optimize out the copying.
     */
    @Override
    public long getAmount() {
        return stored.amount();
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Overwritten so that if we decide to change to returning a cached/copy of our stack in {@link #getFluid()}, we can optimize out the copying.
     */
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (!isEmpty()) {
            nbt.put(NBTConstants.STORED, stored.writeToNBT(new CompoundTag()));
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        NBTUtils.setFluidStackIfPresent(nbt, NBTConstants.STORED, this::setStackUnchecked);
    }

    @Override
    public FluidVariant getResource() {
        return stored.variant();
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        updateSnapshots(transaction);
        if (resource.isBlank() || maxAmount == 0 || !isFluidValid(new FluidStack(resource, maxAmount)) || !canInsert.test(new FluidStack(resource, maxAmount), null)) {
            //"Fail quick" if the given stack is empty, or we can never insert the fluid or currently are unable to insert it
            return 0;
        }
        long needed = Math.min(getRate(null), getNeeded());
        if (needed <= 0) {
            //Fail if we are a full tank or our rate is zero
            return 0;
        }
        boolean sameType = false;
        if (isEmpty() || (sameType = stored.equals(new FluidStack(resource, maxAmount)))) {
            long toAdd = Math.min(maxAmount, needed);
            //If we want to actually insert the fluid, then update the current fluid
            if (sameType) {
                //We can just grow our stack by the amount we want to increase it
                stored.grow(toAdd);
                onContentsChanged();
            } else {
                //If we are not the same type then we have to copy the stack and set it
                // Just set it unchecked as we have already validated it
                // Note: this also will mark that the contents changed
                setStackUnchecked(new FluidStack(resource, toAdd));
            }
            return toAdd;
        }
        //If we didn't accept this fluid, then just return the given stack
        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        updateSnapshots(transaction);
        if (isEmpty() || !resource.equals(stored.variant()) || maxAmount < 1 || !canExtract.test(stored, null)) {
            //"Fail quick" if we don't can never extract from this tank, have a fluid stored, or the amount being requested is less than one
            return 0;
        }
        //Note: While we technically could just return the stack itself if we are removing all that we have, it would require a lot more checks
        // We also are limiting it by the rate this tank has
        long size = Math.min(Math.min(getRate(null), getAmount()), maxAmount);
        if (size == 0) {
            return 0;
        }
        //If shrink gets the size to zero it will update the empty state so that isEmpty() returns true.
        stored.shrink(size);
        onContentsChanged();
        return size;
    }

    @Override
    public boolean isResourceBlank() {
        return false;
    }

    @Override
    protected FluidStack createSnapshot() {
        return stored.copy();
    }

    @Override
    protected void readSnapshot(FluidStack snapshot) {
        stored = snapshot;
    }
}