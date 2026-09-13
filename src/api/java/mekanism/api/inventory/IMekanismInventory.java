package mekanism.api.inventory;

import com.google.common.collect.Iterators;
import mekanism.api.Action;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@NothingNullByDefault
public interface IMekanismInventory extends IContentsListener {

    /**
     * Used to check if an instance of {@link IMekanismInventory} actually has an inventory.
     *
     * @return True if we are actually an inventory.
     *
     * @apiNote If for some reason you are comparing to {@link IMekanismInventory} without having gotten the object via the item handler capability, then you must call
     * this method to make sure that it really is an inventory. As most mekanism tiles have this class in their hierarchy.
     * @implNote If this returns false the capability should not be exposed AND methods should turn reasonable defaults for not doing anything.
     */
    default boolean hasInventory() {
        return true;
    }

    /**
     * Returns the list of IInventorySlots that this inventory exposes on the given side.
     *
     * @param side The side we are interacting with the handler from (null for internal).
     *
     * @return The list of all IInventorySlots that this {@link IMekanismInventory} contains for the given side. If there are no slots for the side or
     * {@link #hasInventory()} is false then it returns an empty list.
     *
     * @implNote When side is null (an internal request), this method <em>MUST</em> return all slots in the inventory. This will be used by the container generating code
     * to add all the proper slots that are needed. Additionally, if {@link #hasInventory()} is false, this <em>MUST</em> return an empty list.
     */
    Storage<ItemVariant> getItemStorage(@Nullable Direction side);

    /**
     * Returns the {@link IInventorySlot} that has the given index from the list of slots on the given side.
     *
     * @param slot The index of the slot to retrieve.
     * @param side The side we are interacting with the handler from (null for internal).
     *
     * @return The {@link IInventorySlot} that has the given index from the list of slots on the given side.
     */
    /*
    @Nullable
    @Deprecated(forRemoval = true)
    default StorageView<ItemVariant> getInventorySlot(int slot, @Nullable Direction side) {
        Storage<ItemVariant> slots = getItemStorage(side);
        return slot >= 0 && slot < Iterators.size(slots.iterator()) ? Iterators.get(slots.iterator(), slot) : null;
    }

    @Override
    @Deprecated(forRemoval = true)
    default void setStackInSlot(int slot, ItemStack stack, @Nullable Direction side) {
        StorageView<ItemVariant> inventorySlot = getInventorySlot(slot, side);
        if (inventorySlot != null) {
            //inventorySlot.setStack(stack);
        }
    }

    @Override
    @Deprecated(forRemoval = true)
    default int getSlots(@Nullable Direction side) {
        return Iterators.size(getItemStorage(side).iterator());
    }

    @Override
    default ItemStack getStackInSlot(int slot, @Nullable Direction side) {
        StorageView<ItemVariant> inventorySlot = getInventorySlot(slot, side);
        return inventorySlot == null ? ItemStack.EMPTY : inventorySlot.getResource().toStack((int) inventorySlot.getAmount());
    }

//    @Override
//    default ItemStack insertItem(int slot, ItemStack stack, @Nullable Direction side, Action action) {
//        StorageView<ItemVariant> inventorySlot = getInventorySlot(slot, side);
//        if (inventorySlot == null) {
//            return stack;
//        }
//        return inventorySlot.ina(stack, action, side == null ? AutomationType.INTERNAL : AutomationType.EXTERNAL);
//    }

    @Deprecated(forRemoval = true)
    default ItemStack extractItem(int slot, int amount, @Nullable Direction side, Action action) {
        StorageView<ItemVariant> inventorySlot = getInventorySlot(slot, side);
        if (inventorySlot == null) {
            return ItemStack.EMPTY;
        }
        try(Transaction t=Transaction.openOuter()) {
            ItemVariant resource = inventorySlot.getResource();
            long extractAmount = inventorySlot.extract(resource, amount, t);
            if (action == Action.EXECUTE) {
                t.commit();
            }
            return resource.toStack((int) extractAmount);
        }
    }

    @Override
    @Deprecated(forRemoval = true)
    default long getSlotLimit(int slot, @Nullable Direction side) {
        StorageView<ItemVariant> inventorySlot = getInventorySlot(slot, side);
        return inventorySlot == null ? 0 : inventorySlot.getCapacity();
    }

    @Override
    @Deprecated(forRemoval = true)
    default boolean isItemValid(int slot, ItemStack stack, @Nullable Direction side) {
        StorageView<ItemVariant> inventorySlot = getInventorySlot(slot, side);
        return true;
    }
*/
    /**
     * Are all the Slots empty?
     * @implNote named isInventoryEmpty to avoid clashing with any other isEmpty() method
     * @since 10.4.0
     *
     * @param side the side to query
     * @return true if completely empty on this side
     */
    default boolean isInventoryEmpty(@Nullable Direction side) {
        for (StorageView<ItemVariant> view : getItemStorage(side)) {
            if (!view.isResourceBlank() && view.getAmount() != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sided inventory helper for isEmpty
     * @since 10.4.0
     *
     * @return true if completely empty on the default side
     */
    default boolean isInventoryEmpty() {
        return isInventoryEmpty(null);
    }
}