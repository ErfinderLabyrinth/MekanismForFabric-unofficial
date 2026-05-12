package mekanism.api.fluid;

import mekanism.api.*;
import mekanism.api.annotations.NothingNullByDefault;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.nbt.CompoundTag;

@NothingNullByDefault
public interface IExtendedFluidTank extends SingleSlotStorage<FluidVariant>, IContentsListener, NBTSerializable<CompoundTag> {

    /**
     * Overrides the stack in this {@link IExtendedFluidTank}.
     *
     * @param stack {@link FluidStack} to set this tanks' contents to (may be empty).
     *
     * @throws RuntimeException if this tank is called in a way that it was not expecting.
     * @implNote If the internal stack does get updated make sure to call {@link #onContentsChanged()}
     */
    void setStack(FluidStack stack);

    /**
     * Overrides the stack in this {@link IExtendedFluidTank}.
     *
     * @param stack {@link FluidStack} to set this tank's contents to (may be empty).
     *
     * @apiNote Unsafe version of {@link #setStack(FluidStack)}. This method is exposed for implementation and code deduplication reasons only and should
     * <strong>NOT</strong> be directly called outside your own {@link IExtendedFluidTank} where you already know the given {@link FluidStack} is valid, or on the
     * client side for purposes of receiving sync data and rendering.
     * @implNote If the internal stack does get updated make sure to call {@link #onContentsChanged()}
     */
    void setStackUnchecked(FluidStack stack);

    /**
     * <p>
     * Inserts a {@link FluidStack} into this {@link IExtendedFluidTank} and return the remainder. The {@link FluidStack} <em>should not</em> be modified in this
     * function!
     * </p>
     * Note: This behaviour is subtly <strong>different</strong> from {@link net.minecraftforge.fluids.capability.IFluidHandler#fill(FluidStack, FluidAction)}
     *
     * @param stack          {@link FluidStack} to insert. This must not be modified by the tank.
     * @param action         The action to perform, either {@link Action#EXECUTE} or {@link Action#SIMULATE}
     * @param automationType The method that this tank is being interacted from.
     *
     * @return The remaining {@link FluidStack} that was not inserted (if the entire stack is accepted, then return an empty {@link FluidStack}). May be the same as the
     * input {@link FluidStack} if unchanged, otherwise a new {@link FluidStack}. The returned {@link FluidStack} can be safely modified after
     *
     * @implNote The {@link FluidStack} <em>should not</em> be modified in this function! If the internal stack does get updated make sure to call
     * {@link #onContentsChanged()}. It is also recommended to override this if your internal {@link FluidStack} is mutable so that a copy does not have to be made every
     * run
     */
    @Deprecated(forRemoval = true)
    default FluidStack insert(FluidStack stack, Action action, AutomationType automationType) {
        if (stack.isEmpty() || !isFluidValid(stack)) {
            //"Fail quick" if the given stack is empty, or we can never insert the item or currently are unable to insert it
            return stack;
        }
        long needed = getNeeded();
        if (needed <= 0) {
            //Fail if we are a full tank
            return stack;
        }
        boolean sameType = false;
        if (isEmpty() || (sameType = stack.isFluidVariantEqual(getResource()))) {
            long toAdd = Math.min(stack.amount(), needed);
            if (action.execute()) {
                //If we want to actually insert the fluid, then update the current fluid
                if (sameType) {
                    //We can just grow our stack by the amount we want to increase it
                    // Note: this also will mark that the contents changed
                    growStack(toAdd);
                } else {
                    //If we are not the same type then we have to copy the stack and set it
                    // Note: this also will mark that the contents changed
                    setStack(new FluidStack(stack, toAdd));
                }
            }
            return new FluidStack(stack, stack.amount() - toAdd);
        }
        //If we didn't accept this fluid, then just return the given stack
        return stack;
    }

    /**
     * Extracts a {@link FluidStack} from this {@link IExtendedFluidTank}.
     * <p>
     * The returned value must be empty if nothing is extracted, otherwise its stack size must be less than or equal to {@code amount}.
     * </p>
     *
     * @param amount         Amount to extract (may be greater than the current stack's max limit)
     * @param action         The action to perform, either {@link Action#EXECUTE} or {@link Action#SIMULATE}
     * @param automationType The method that this tank is being interacted from.
     *
     * @return {@link FluidStack} extracted from the tank, must be empty if nothing can be extracted. The returned {@link FluidStack} can be safely modified after, so the
     * tank should return a new or copied stack.
     *
     * @implNote The returned {@link FluidStack} can be safely modified after, so a new or copied stack should be returned. If the internal stack does get updated make
     * sure to call {@link #onContentsChanged()}. It is also recommended to override this if your internal {@link FluidStack} is mutable so that a copy does not have to
     * be made every run
     */
    @Deprecated(forRemoval = true)
    default FluidStack extract(long amount, AutomationType automationType) {
        if (isEmpty() || amount < 1) {
            return FluidStack.EMPTY;
        }
        FluidStack ret = new FluidStack(getResource(), Math.min(getAmount(), amount));
        if (!ret.isEmpty()) {
            // Note: this also will mark that the contents changed
            shrinkStack(ret.amount());
        }
        return ret;
    }

    /**
     * Convenience method for modifying the size of the stored stack.
     * <p>
     * If there is a stack stored in this tank, set the size of it to the given amount. Capping at this fluid tank's limit. If the amount is less than or equal to zero,
     * then this instead sets the stack to the empty stack.
     *
     * @param amount The desired size to set the stack to.
     * @param action The action to perform, either {@link Action#EXECUTE} or {@link Action#SIMULATE}
     *
     * @return Actual size the stack was set to.
     *
     * @implNote It is recommended to override this if your internal {@link FluidStack} is mutable so that a copy does not have to be made every run. If the internal
     * stack does get updated make sure to call {@link #onContentsChanged()}
     */
    default long setStackSize(long amount) {
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
        setStack(new FluidStack(getResource(), amount));
        return amount;
    }

    /**
     * Convenience method for growing the size of the stored stack.
     * <p>
     * If there is a stack stored in this tank, increase its size by the given amount. Capping at this fluid tank's limit. If the stack shrinks to an amount of less than
     * or equal to zero, then this instead sets the stack to the empty stack.
     *
     * @param amount The desired amount to grow the stack by.
     * @param action The action to perform, either {@link Action#EXECUTE} or {@link Action#SIMULATE}
     *
     * @return Actual amount the stack grew.
     *
     * @apiNote Negative values for amount are valid, and will instead cause the stack to shrink.
     * @implNote If the internal stack does get updated make sure to call {@link #onContentsChanged()}
     */
    default long growStack(long amount) {
        long current = getAmount();
        if (amount > 0) {
            //Cap adding amount at how much we need, so that we don't risk integer overflow
            amount = Math.min(amount, getNeeded());
        }
        long newSize = setStackSize(current + amount);
        return newSize - current;
    }

    /**
     * Convenience method for shrinking the size of the stored stack.
     * <p>
     * If there is a stack stored in this tank, shrink its size by the given amount. If this causes its size to become less than or equal to zero, then the stack is set
     * to the empty stack. If this method is used to grow the stack the size gets capped at this fluid tank's limit.
     *
     * @param amount The desired amount to shrink the stack by.
     * @param action The action to perform, either {@link Action#EXECUTE} or {@link Action#SIMULATE}
     *
     * @return Actual amount the stack shrunk.
     *
     * @apiNote Negative values for amount are valid, and will instead cause the stack to grow.
     * @implNote If the internal stack does get updated make sure to call {@link #onContentsChanged()}
     */
    default long shrinkStack(long amount) {
        return -growStack(-amount);
    }

    /**
     * Convenience method for checking if this tank is empty.
     *
     * @return True if the tank is empty, false otherwise.
     *
     * @implNote If your implementation of {@link #getFluid()} returns a copy, this should be overridden to directly check against the internal stack.
     */
    default boolean isEmpty() {
        return getAmount() == 0;
    }

    /**
     * Convenience method for emptying this {@link IExtendedFluidTank}.
     */
    default void setEmpty() {
        setStack(FluidStack.EMPTY);
    }

    /**
     * Convenience method for checking if this tank's contents are of an equal type to a given fluid stack's.
     *
     * @param other The stack to compare to.
     *
     * @return True if the tank's contents are equal, false otherwise.
     *
     * @implNote If your implementation of {@link #getFluid()} returns a copy, this should be overridden to directly check against the internal stack.
     */
    default boolean isFluidEqual(FluidStack other) {
        return getResource().equals(other.variant());
    }

    /**
     * Gets the amount of fluid needed by this {@link IExtendedFluidTank} to reach a filled state.
     *
     * @return Amount of fluid needed
     */
    default long getNeeded() {
        return Math.max(0, getCapacity() - getAmount());
    }

    @Override
    default CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (!isEmpty()) {
            nbt.put(NBTConstants.STORED, getFluid().writeToNBT(new CompoundTag()));
        }
        return nbt;
    }

    /**
     * {@inheritDoc}
     *
     * Wrapped to properly use our method declarations
     */
//    @Override
//    @Deprecated
//    default int fill(FluidStack stack, FluidAction action) {
//        return stack.getAmount() - insert(stack, Action.fromFluidAction(action), AutomationType.EXTERNAL).getAmount();
//    }

    /**
     * {@inheritDoc}
     *
     * Wrapped to properly use our method declarations
     */
//    @Override
//    @Deprecated
//    default FluidStack drain(FluidStack stack, FluidAction action) {
//        if (!isEmpty() && getFluid().isFluidEqual(stack)) {
//            return extract(stack.getAmount(), Action.fromFluidAction(action), AutomationType.EXTERNAL);
//        }
//        return FluidStack.EMPTY;
//    }

    /**
     * {@inheritDoc}
     *
     * Wrapped to properly use our method declarations
     */
//    @Override
//    @Deprecated
//    default FluidStack drain(int amount, FluidAction action) {
//        return extract(amount, Action.fromFluidAction(action), AutomationType.EXTERNAL);
//    }

    boolean isFluidValid(FluidStack stack);

    FluidStack getFluid();
}