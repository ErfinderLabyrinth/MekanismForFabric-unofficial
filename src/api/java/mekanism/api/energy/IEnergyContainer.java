package mekanism.api.energy;

import mekanism.api.*;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.math.FloatingLong;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import team.reborn.energy.api.EnergyStorage;

@NothingNullByDefault
public interface IEnergyContainer extends NBTSerializable<CompoundTag>, IContentsListener, EnergyStorage {

    /**
     * Returns the energy in this container.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This {@link FloatingLong} <em>MUST NOT</em> be modified. This method is not for altering internal contents. Any implementers who are
     * able to detect modification via this method should throw an exception. It is ENTIRELY reasonable and likely that the value returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLOATING LONG</em></strong>
     * </p>
     *
     * @return Energy in this container. {@link FloatingLong#ZERO} if no energy is stored.
     */
    long getEnergy();

    @Override
    default long getAmount() {
        return getEnergy();
    }

    /**
     * Overrides the amount of energy in this {@link IEnergyContainer}.
     *
     * @param energy Energy to set this container's contents to (may be {@link FloatingLong#ZERO}).
     *
     * @throws RuntimeException if the handler is called in a way that the handler was not expecting. Such as if it was not expecting this to be called at all.
     * @implNote If the internal amount does get updated make sure to call {@link #onContentsChanged()}
     */
    void setEnergy(long energy, TransactionContext t);

    default void setEnergy(long energy) {
        try(Transaction t = Transaction.isOpen() ? Transaction.openNested(Transaction.getCurrentUnsafe()) : Transaction.openOuter()) {
            setEnergy(energy, t);
            t.commit();
        }
    }

    /**
     * <p>
     * Inserts energy into this {@link IEnergyContainer} and return the remainder. The {@link FloatingLong} <em>should not</em> be modified in this function!
     * </p>
     * Note: This behaviour is subtly different from
     * {@link net.minecraftforge.fluids.capability.IFluidHandler#fill(net.minecraftforge.fluids.FluidStack,
     * net.minecraftforge.fluids.capability.IFluidHandler.FluidAction)}
     *
     * @param amount         Energy to insert. This must not be modified by the container.
     * @param action         The action to perform, either {@link Action#EXECUTE} or {@link Action#SIMULATE}
     * @param automationType The method that this container is being interacted from.
     *
     * @return The remaining energy that was not inserted (if the entire amount is accepted, then return {@link FloatingLong#ZERO}). The returned {@link FloatingLong} can
     * be safely modified afterwards.
     *
     * @implNote The {@link FloatingLong} <em>should not</em> be modified in this function! If the internal amount does get updated make sure to call
     * {@link #onContentsChanged()}. It is also recommended to override this if your internal {@link FloatingLong} is mutable so that a copy does not have to be made
     * every run.
     */
//    @Deprecated(forRemoval = true)
//    default FloatingLong insert(FloatingLong amount, TransactionContext t, AutomationType automationType) {
//        if (amount.isZero()) {
//            //"Fail quick" if the given amount is empty
//            return amount;
//        }
//        long needed = getNeeded();
//        if (needed == 0) {
//            //Fail if we are a full container
//            return amount;
//        }
//        FloatingLong toAdd = amount.min(FloatingLong.create(needed));
//        if (!toAdd.isZero()) {
//            //If we want to actually insert the energy, then update the current energy
//            // Note: this also will mark that the contents changed
//            setEnergy(getEnergy() + toAdd.getValue(), t);
//        }
//        return amount.subtract(toAdd);
//    }
//
//    @Deprecated(forRemoval = true)
//    default FloatingLong insert(FloatingLong amount, Action action, AutomationType automationType) {
//        try(Transaction t = Transaction.openOuter()) {
//            long remainder = insert(amount.getValue(), t);
//            if(action.execute()) {
//                t.commit();
//            }
//            return FloatingLong.create(remainder);
//        }
//    }

    void updateSnapshots(TransactionContext t);

    default long insert(long amount, TransactionContext t) {
        updateSnapshots(t);
        if (amount == 0) {
            //"Fail quick" if the given amount is empty
            return 0;
        }
        long needed = getNeeded();
        if (needed == 0) {
            //Fail if we are a full container
            return 0;
        }
        long toAdd = Math.min(amount, needed);

        //If we want to actually insert the energy, then update the current energy
        // Note: this also will mark that the contents changed
        setEnergy(getEnergy() + toAdd, t);

        return toAdd;
    }

    /**
     * Extracts energy from this {@link IEnergyContainer}.
     * <p>
     * The returned value must be {@link FloatingLong#ZERO} if nothing is extracted, otherwise its must be less than or equal to {@code amount}.
     * </p>
     *
     * @param amount         Amount of energy to extract (may be greater than the current stored amount or the container's capacity) This must not be modified by the
     *                       handler.
     * @param action         The action to perform, either {@link Action#EXECUTE} or {@link Action#SIMULATE}
     * @param automationType The method that this container is being interacted from.
     *
     * @return Energy extracted from the container, must be {@link FloatingLong#ZERO} if no energy can be extracted. The returned {@link FloatingLong} can be safely
     * modified after, so the container should return a new or copied {@link FloatingLong}.
     *
     * @implNote The returned {@link FloatingLong} can be safely modified after, so a new or copied {@link FloatingLong} should be returned. If the internal amount does
     * get updated make sure to call {@link #onContentsChanged()}. It is also recommended to override this if your internal {@link FloatingLong} is mutable so that a copy
     * does not have to be made every run.
     */
    @Deprecated(forRemoval = true)
    default FloatingLong extract(FloatingLong amount, TransactionContext t, AutomationType automationType) {
        if (isEmpty() || amount.isZero()) {
            return FloatingLong.ZERO;
        }
        long ret = Math.min(getEnergy(), amount.longValue());
        if (ret != 0) {
            // Note: this also will mark that the contents changed
            setEnergy(getEnergy() - ret, t);
        }
        return FloatingLong.create(ret);
    }

    @Deprecated(forRemoval = true)
    default FloatingLong extract(FloatingLong amount, Action action, AutomationType automationType) {
        try(Transaction t = Transaction.openOuter()) {
            long extracted = extract(amount.longValue(), t);
            if(action.execute()) {
                t.commit();
            }
            return FloatingLong.create(extracted);
        }
    }

    @Override
    default long extract(long amount, TransactionContext transaction) {
        updateSnapshots(transaction);
        if (isEmpty() || amount == 0) {
            return 0;
        }
        long ret = Math.min(getEnergy(), amount);
        if (ret != 0) {
            // Note: this also will mark that the contents changed
            setEnergy(getEnergy() - ret, transaction);
        }
        return ret;
    }

    /**
     * Retrieves the maximum amount of energy allowed to exist in this {@link IEnergyContainer}.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This {@link FloatingLong} <em>MUST NOT</em> be modified. This method is not for altering internal max energy. Any implementers who are
     * able to detect modification via this method should throw an exception. It is ENTIRELY reasonable and likely that the value returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLOATING LONG</em></strong>
     * </p>
     *
     * @return The maximum amount of energy allowed in this {@link IEnergyContainer}.
     */
    long getMaxEnergy();

    @Override
    default long getCapacity() {
        return getMaxEnergy();
    }

    /**
     * Convenience method for checking if this container is empty.
     *
     * @return True if the container is empty, false otherwise.
     */
    default boolean isEmpty() {
        return getEnergy() == 0;
    }

    /**
     * Convenience method for emptying this {@link IEnergyContainer}.
     */
    default void setEmpty(TransactionContext t) {
        setEnergy(0, t);
    }

    default void setEmpty() {
        try(Transaction t = Transaction.isOpen() ? Transaction.openNested(Transaction.getCurrentUnsafe()) : Transaction.openOuter()) {
            setEmpty(t);
            t.commit();
        }
    }

    /**
     * Gets the amount of energy needed by this {@link IEnergyContainer} to reach a filled state.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This {@link FloatingLong} <em>MUST NOT</em> be modified. This method is not for altering remaining needed amount. Any implementers who
     * are able to detect modification via this method should throw an exception. It is ENTIRELY reasonable and likely that the value returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLOATING LONG</em></strong>
     * </p>
     *
     * @return Amount of energy needed
     */
    default long getNeeded() {
        return Math.max(getMaxEnergy() - getEnergy(), 0);
    }

    @Override
    default CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (!isEmpty()) {
            nbt.putLong(NBTConstants.STORED, getEnergy());
        }
        return nbt;
    }

    @Override
    default void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains(NBTConstants.STORED)) {
            setEnergy(nbt.getLong(NBTConstants.STORED));
        }
    }
}