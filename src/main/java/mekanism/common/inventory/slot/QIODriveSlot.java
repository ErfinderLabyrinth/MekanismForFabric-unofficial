package mekanism.common.inventory.slot;

import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.content.qio.IQIODriveHolder;
import mekanism.common.content.qio.IQIODriveItem;
import mekanism.common.content.qio.QIODriveData.QIODriveKey;
import mekanism.common.content.qio.QIOFrequency;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class QIODriveSlot extends BasicInventorySlot {

    private final IQIODriveHolder driveHolder;
    private final QIODriveKey key;

    public <TILE extends IQIODriveHolder> QIODriveSlot(TILE inventory, int slot, @Nullable IContentsListener listener, int x, int y) {
        super(notExternal, notExternal, stack -> stack.getItem() instanceof IQIODriveItem, listener, x, y);
        key = new QIODriveKey(inventory, slot);
        driveHolder = inventory;
    }

    @Override
    public void setStack(ItemStack stack) {
        // if we're about to empty this slot and a drive already exists here, remove the current drive from the frequency
        // Note: We don't check to see if the new stack is empty so that we properly are able to handle direct changes
        if (!isRemote() && !isEmpty()) {
            removeDrive();
        }
        super.setStack(stack);
        // if we just added a new drive, add it to the frequency
        // (note that both of these operations can happen in this order if a user replaces the drive in the slot)
        if (!isRemote() && !isEmpty()) {
            addDrive(getStack());
        }
    }

    @Override
    protected void setStackUnchecked(ItemStack stack) {
        // if we're about to empty this slot and a drive already exists here, remove the current drive from the frequency
        // Note: We don't check to see if the new stack is empty so that we properly are able to handle direct changes
        if (!isRemote() && !isEmpty()) {
            removeDrive();
        }
        super.setStackUnchecked(stack);
        // if we just added a new drive, add it to the frequency
        // (note that both of these operations can happen in this order if a user replaces the drive in the slot)
        if (!isRemote() && !isEmpty()) {
            addDrive(getStack());
        }
    }

    @Override
    public long insert(ItemVariant resource, long amount, TransactionContext transaction) {
        long amountInserted = super.insert(resource, amount, transaction);
        if (!isRemote() && amountInserted != 0) {
            addDrive(resource.toStack((int)amount));
        }
        return amountInserted;
    }

    @Override
    public long extract(ItemVariant resource, long amount, TransactionContext transaction) {
        long amountExtracted = super.extract(resource, amount, transaction);
        if (!isRemote() && amountExtracted != 0) {
            removeDrive();
        }
        return amountExtracted;
    }

    public QIODriveKey getKey() {
        return key;
    }

    private boolean isRemote() {
        Level world = ((BlockEntity) driveHolder).getLevel();
        //Treat world as remote if it is null (hasn't been assigned yet)
        // which may happen when loading the drives from memory
        return world == null || world.isClientSide();
    }

    private void addDrive(ItemStack stack) {
        QIOFrequency frequency = driveHolder.getQIOFrequency();
        if (frequency != null) {
            frequency.addDrive(key);
        }
    }

    private void removeDrive() {
        QIOFrequency frequency = driveHolder.getQIOFrequency();
        if (frequency != null) {
            frequency.removeDrive(key, true);
        }
    }
}
