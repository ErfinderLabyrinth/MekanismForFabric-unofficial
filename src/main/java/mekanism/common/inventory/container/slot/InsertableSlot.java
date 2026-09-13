package mekanism.common.inventory.container.slot;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class InsertableSlot extends Slot implements IInsertableSlot {
    SnapshotParticipant<ItemStack> snapshotParticipant = new SnapshotParticipant<>() {
        @Override
        protected ItemStack createSnapshot() {
            return getItem();
        }

        @Override
        protected void readSnapshot(ItemStack snapshot) {
            set(snapshot);
        }
    };

    public InsertableSlot(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(getMaxStackSize(), stack.getMaxStackSize());
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        snapshotParticipant.updateSnapshots(transaction);
        ItemStack stack = resource.toStack((int)Math.min(maxAmount, Integer.MAX_VALUE));
        if (resource.isBlank() || maxAmount == 0 || !mayPlace(stack)) {
            //TODO: Should we even be checking isItemValid
            //"Fail quick" if the given stack is empty or we are not valid for the slot
            return 0;
        }
        ItemStack current = getItem();
        int needed = getMaxStackSize(stack) - current.getCount();
        if (needed <= 0) {
            //Fail if we are a full slot
            return 0;
        }
        if (current.isEmpty() || ItemEntity.areMergable(current, stack)) {
            int toAdd = Math.min(stack.getCount(), needed);
            set(stack.copyWithCount(current.getCount() + toAdd));
            return toAdd;
        }
        //If we didn't accept this item, then just return the given stack
        return 0;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        snapshotParticipant.updateSnapshots(transaction);
        if (ItemVariant.of(getItem()).equals(resource)) {
            return remove((int)Math.min(maxAmount, Integer.MAX_VALUE)).getCount();
        }
        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return ItemVariant.of(getItem()).isBlank();
    }

    @Override
    public ItemVariant getResource() {
        return ItemVariant.of(getItem());
    }

    @Override
    public long getAmount() {
        return getItem().getCount();
    }

    @Override
    public long getCapacity() {
        return getMaxStackSize();
    }

//    @NotNull
//    @Override
//    public ItemStack insertItem(@NotNull ItemStack stack, Action action) {
//        if (stack.isEmpty() || !mayPlace(stack)) {
//            //TODO: Should we even be checking isItemValid
//            //"Fail quick" if the given stack is empty or we are not valid for the slot
//            return stack;
//        }
//        ItemStack current = getItem();
//        int needed = getMaxStackSize(stack) - current.getCount();
//        if (needed <= 0) {
//            //Fail if we are a full slot
//            return stack;
//        }
//        if (current.isEmpty() || ItemEntity.areMergable(current, stack)) {
//            int toAdd = Math.min(stack.getCount(), needed);
//            if (action.execute()) {
//                //If we want to actually insert the item, then update the current item
//                //Set the stack to our new stack (we have no simple way to increment the stack size) so we have to set it instead of being able to just grow it
//                set(stack.copyWithCount(current.getCount() + toAdd));
//            }
//            return stack.copyWithCount(stack.getCount() - toAdd);
//        }
//        //If we didn't accept this item, then just return the given stack
//        return stack;
//    }
}