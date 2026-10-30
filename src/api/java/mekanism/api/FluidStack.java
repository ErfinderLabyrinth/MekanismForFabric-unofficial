package mekanism.api;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;

public class FluidStack {
    public static FluidStack EMPTY = new FluidStack(FluidVariant.blank(), 0);

    FluidVariant variant;
    long amount;

    public FluidStack(FluidVariant variant, long amount) {
        this.variant = variant;
        this.amount = amount;
    }

    public FluidStack(FluidStack stack, long amount) {
        this(stack.variant(), amount);
    }

    public static FluidStack loadFluidStackFromNBT(CompoundTag compound) {
        FluidVariant variant = FluidVariant.fromNbt(compound);
        long amount = compound.getLong("amount");
        return new FluidStack(variant, amount);
    }

    public static FluidStack readFromBuffer(FriendlyByteBuf buffer) {
        FluidVariant variant = FluidVariant.fromPacket(buffer);
        long amount = buffer.readLong();
        return new FluidStack(variant, amount);
    }

    public void writeToBuffer(FriendlyByteBuf buffer) {
        variant.toPacket(buffer);
        buffer.writeLong(amount);
    }

    public CompoundTag writeToNBT(CompoundTag compound) {
        compound.merge(variant.toNbt());
        compound.putLong("amount", amount);
        return compound;
    }

    public FluidStack copy() {
        return new FluidStack(variant, amount);
    }

    public FluidStack withVariant(FluidVariant variant) {
        return new FluidStack(variant, amount);
    }

    public FluidStack withAmount(long amount) {
        return new FluidStack(variant, amount);
    }

    public boolean isEmpty() {
        return amount == 0 || variant.isBlank();
    }

    public boolean isFluidVariantEqual(FluidVariant resource) {
        return variant.equals(resource);
    }

    public FluidVariant variant() {
        return variant;
    }

    public long amount() {
        return amount;
    }

    public void grow(long toAdd) {
        amount += toAdd;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FluidStack that = (FluidStack) o;
        return isFluidVariantEqual(that.variant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variant, amount);
    }

    public void shrink(long amount) {
        grow(-amount);
    }

    @Deprecated(forRemoval = true)
    public void setAmount(long amount) {
        this.amount = amount;
    }

    public boolean hasTag() {
        return variant.hasNbt();
    }

    public CompoundTag getTag() {
        return variant.getNbt();
    }

    public Fluid getFluid() {
        return variant.getFluid();
    }

    public boolean isFluidStackIdentical(FluidStack fluidStack) {
        return variant.equals(fluidStack.variant) && amount == fluidStack.amount();
    }
}
