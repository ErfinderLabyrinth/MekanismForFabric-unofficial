package mekanism.common.capabilities.proxy;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.inventory.ISidedItemHandler;
import mekanism.common.capabilities.holder.IHolder;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

@NothingNullByDefault
public class ProxyItemHandler extends ProxyHandler implements Storage<ItemVariant> {

    private final ISidedItemHandler inventory;

    public ProxyItemHandler(ISidedItemHandler inventory, @Nullable Direction side, @Nullable IHolder holder) {
        super(side, holder);
        this.inventory = inventory;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return readOnly || readOnlyInsert.getAsBoolean() ? 0 : inventory.getContainers(side).insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return readOnly || readOnlyExtract.getAsBoolean() ? 0 : inventory.getContainers(side).extract(resource, maxAmount, transaction);    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        return null;
    }

//    @Override
//    public int getSlots() {
//        return inventory.getSlots(side);
//    }
//
//    @Override
//    public ItemStack getStackInSlot(int slot) {
//        return inventory.getStackInSlot(slot, side);
//    }
//
//    @Override
//    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
//        return readOnly || readOnlyInsert.getAsBoolean() ? stack : inventory.insertItem(slot, stack, side, Action.get(!simulate));
//    }
//
//    @Override
//    public ItemStack extractItem(int slot, int amount, boolean simulate) {
//        return readOnly || readOnlyExtract.getAsBoolean() ? ItemStack.EMPTY : inventory.extractItem(slot, amount, side, Action.get(!simulate));
//    }
//
//    @Override
//    public int getSlotLimit(int slot) {
//        return inventory.getSlotLimit(slot, side);
//    }
//
//    @Override
//    public boolean isItemValid(int slot, ItemStack stack) {
//        return !readOnly || inventory.isItemValid(slot, stack, side);
//    }
//
//    @Override
//    public void setStackInSlot(int slot, ItemStack stack) {
//        if (!readOnly) {
//            inventory.setStackInSlot(slot, stack, side);
//        }
//    }
}