package mekanism.api;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public record BigItemStack(ItemVariant item, long amount) implements StorageView<ItemVariant> {

    public static final BigItemStack EMPTY = new BigItemStack(ItemVariant.blank(), 0);

    public static BigItemStack of(ItemStack stack) {
        return new BigItemStack(ItemVariant.of(stack), stack.getCount());
    }

    public static boolean matches(BigItemStack inserted, BigItemStack inserted1) {
        return inserted != null && inserted1 != null && inserted.equals(inserted1);
    }

    public static BigItemStack of(CompoundTag updateTag) {
        ItemVariant variant = ItemVariant.fromNbt(updateTag.getCompound("Item"));
        long amount = updateTag.getLong("Count");
        return new BigItemStack(variant, amount);
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return item.isBlank();
    }

    @Override
    public ItemVariant getResource() {
        return item;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public long getCapacity() {
        return amount;
    }

    public BigItemStack copyWithCount(long amount) {
        return new BigItemStack(item, amount);
    }
    public BigItemStack copy() {
        return copyWithCount(amount);
    }

    public boolean isEmpty() {
        return amount <= 0;
    }

    public ItemStack createStack(int totalCount) {
        return item.toStack(totalCount);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        BigItemStack that = (BigItemStack) object;
        return amount == that.amount && Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, amount);
    }

    public ItemStack createStack() {
        return createStack((int)amount);
    }

    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put("Item", item.toNbt());
        compoundTag.putLong("Count", amount);

        return compoundTag;
    }
}