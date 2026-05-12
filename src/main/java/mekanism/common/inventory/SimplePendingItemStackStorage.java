// TODO Currently unused, delete
//
//package mekanism.common.inventory;
//
//import mekanism.api.PendingItemStack;
//import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
//import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
//import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
//import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
//import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
//import net.minecraft.world.item.ItemStack;
//
//public class SimplePendingItemStackStorage extends SnapshotParticipant<PendingItemStack> implements SingleSlotStorage<ItemVariant> {
//    PendingItemStack pendingItemStack;
//    int capacity;
//
//    @Override
//    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
//        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
//
//        if (pendingItemStack.item().equals(resource) || pendingItemStack.isResourceBlank() || pendingItemStack.amount() <= 0) {
//            long amountInserted = Math.min(maxAmount, capacity - pendingItemStack.amount());
//            if (amountInserted > 0) {
//                updateSnapshots(transaction);
//                pendingItemStack = new PendingItemStack(resource, pendingItemStack.amount() + amountInserted);
//                return amountInserted;
//            }
//        }
//
//        return 0;
//    }
//
//    @Override
//    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
//        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
//
//        if (pendingItemStack.item().equals(resource)) {
//            long amountExtracted = Math.min(maxAmount, pendingItemStack.amount());
//            if (amountExtracted > 0) {
//                updateSnapshots(transaction);
//                pendingItemStack = new PendingItemStack(resource, pendingItemStack.amount() - amountExtracted);
//                return amountExtracted;
//            }
//        }
//
//        return 0;
//    }
//
//    @Override
//    public boolean isResourceBlank() {
//        return pendingItemStack.isResourceBlank();
//    }
//
//    @Override
//    public ItemVariant getResource() {
//        return pendingItemStack.getResource();
//    }
//
//    @Override
//    public long getAmount() {
//        return pendingItemStack.amount();
//    }
//
//    @Override
//    public long getCapacity() {
//        return capacity;
//    }
//
//    @Override
//    protected PendingItemStack createSnapshot() {
//        return pendingItemStack;
//    }
//
//    @Override
//    protected void readSnapshot(PendingItemStack snapshot) {
//        pendingItemStack = snapshot;
//    }
//}
