package mekanism.common.capabilities.fluid;

import mekanism.api.FluidStack;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.merged.MergedTank;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

/**
 * Like {@link mekanism.api.chemical.merged.ChemicalTankWrapper}
 */
@NothingNullByDefault
public class FluidTankWrapper implements IExtendedFluidTank {

    private final IExtendedFluidTank internal;
    private final BooleanSupplier insertCheck;
    private final MergedTank mergedTank;

    public FluidTankWrapper(MergedTank mergedTank, IExtendedFluidTank internal, BooleanSupplier insertCheck) {
        //TODO: Do we want to short circuit it so that if we are not empty it allows for inserting before checking the insertCheck
        this.mergedTank = mergedTank;
        this.internal = internal;
        this.insertCheck = insertCheck;
    }

    public MergedTank getMergedTank() {
        return mergedTank;
    }

    @Override
    public void setStack(FluidStack stack) {
        internal.setStack(stack);
    }

    @Override
    public void setStackUnchecked(FluidStack stack) {
        internal.setStackUnchecked(stack);
    }

//    @Override
//    public FluidStack insert(FluidStack stack, Action action, AutomationType automationType) {
//        //Only allow inserting if we pass the check
//        return insertCheck.getAsBoolean() ? internal.insert(stack, action, automationType) : stack;
//    }

//    @Override
//    public FluidStack extract(int amount, Action action, AutomationType automationType) {
//        return internal.extract(amount, action, automationType);
//    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return insertCheck.getAsBoolean() ? internal.insert(resource, maxAmount, transaction) : 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return internal.extract(resource, maxAmount, transaction);
    }

    @Override
    public boolean isResourceBlank() {
        return internal.isResourceBlank();
    }

    @Override
    public FluidVariant getResource() {
        return internal.getResource();
    }

    @Override
    public long getAmount() {
        return internal.getAmount();
    }

    @Override
    public void onContentsChanged() {
        internal.onContentsChanged();
    }

    @Override
    public long setStackSize(long amount) {
        return internal.setStackSize(amount);
    }

    @Override
    public long growStack(long amount) {
        return internal.growStack(amount);
    }

    @Override
    public long shrinkStack(long amount) {
        return internal.shrinkStack(amount);
    }

    @Override
    public boolean isEmpty() {
        return internal.isEmpty();
    }

    @Override
    public void setEmpty() {
        internal.setEmpty();
    }

    @Override
    public boolean isFluidEqual(FluidStack other) {
        return internal.isFluidEqual(other);
    }

    @Override
    public long getNeeded() {
        return internal.getNeeded();
    }

    @Override
    public CompoundTag serializeNBT() {
        return internal.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        internal.deserializeNBT(nbt);
    }

    @NotNull
    @Override
    public FluidStack getFluid() {
        return internal.getFluid();
    }

//    @Override
//    public long getFluidAmount() {
//        return internal.getFluid().amount();
//    }

    @Override
    public long getCapacity() {
        return internal.getCapacity();
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return internal.isFluidValid(stack);
    }
}