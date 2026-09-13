package mekanism.common.inventory.slot;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.inventory.container.slot.InventoryContainerSlot;
import mekanism.common.item.block.ItemBlockBin;
import mekanism.common.tier.BinTier;
import mekanism.common.util.NBTUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

@NothingNullByDefault
public class BinInventorySlot extends BasicInventorySlot {

    private static final Predicate<@NotNull ItemStack> validator = stack -> !(stack.getItem() instanceof ItemBlockBin);

    public static BinInventorySlot create(@Nullable IContentsListener listener, BinTier tier) {
        Objects.requireNonNull(tier, "Bin tier cannot be null");
        return new BinInventorySlot(listener, tier);
    }

    private final boolean isCreative;
    private ItemStack lockStack = ItemStack.EMPTY;

    private BinInventorySlot(@Nullable IContentsListener listener, BinTier tier) {
        super(tier.getStorage(), alwaysTrueBi, alwaysTrueBi, validator, listener, 0, 0);
        isCreative = tier == BinTier.CREATIVE;
        obeyStackLimit = false;
    }

    @Override
    public long insert(ItemVariant resource, long amount, TransactionContext transaction, AutomationType automationType) {
        updateSnapshots(transaction);
        if (isEmpty()) {
            if (isLocked() && !ItemEntity.areMergable(lockStack, resource.toStack((int) amount))) {
                // When locked, we need to make sure the correct item type is being inserted
                return 0;
            } else if (isCreative) {
                //If a player manually inserts into a creative bin, that is empty we need to allow setting the type,
                // Note: We check that it is not external insertion because an empty creative bin acts as a "void" for automation
                long amountInserted = super.insert(resource, amount, transaction, automationType);
                if (amountInserted == amount) {
                    //If we are able to insert it then set perform the action of setting it to full
                    setStackUnchecked(resource.toStack(getLimit(resource.toStack(1))));
                }
                return amountInserted;
            }
        }
        long amountInserted;
        try(Transaction t=Transaction.openOuter()) {
            amountInserted = super.insert(resource, amount, t);
            if (!isCreative) {
                t.commit();
            }
        }
        return amountInserted;
    }

    @Override
    public long extract(ItemVariant resource, long amount, TransactionContext t, AutomationType type) {
        long amountExtracted;
        try(Transaction t2=Transaction.openOuter()) {
            amountExtracted = super.extract(resource, amount, t2, type);
            if (!isCreative) {
                t2.commit();
            }
        }
        return amountExtracted;
    }

    /**
     * {@inheritDoc}
     *
     * Note: We are only patching {@link #setStackSize(int, Action)}, as both {@link #growStack(int, Action)} and {@link #shrinkStack(int, Action)} are wrapped through
     * this method.
     */
    @Override
    public int setStackSize(int amount) {
        if (isCreative) {
            if (isEmpty() || amount <= 0) {
                return 0;
            }

            return Math.min(amount, getLimit(current.getStack()));
        }
        return super.setStackSize(amount);
    }

    @Nullable
    @Override
    public InventoryContainerSlot createContainerSlot() {
        return null;
    }

    /**
     * Gets the "bottom" stack for the bin, this is the stack that can be extracted/interacted with directly.
     *
     * @return The "bottom" stack for the bin
     *
     * @apiNote The returned stack can be safely modified.
     */
    public ItemStack getBottomStack() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        return current.getStack().copyWithCount(Math.min(getCount(), current.getStack().getMaxStackSize()));
    }

    /**
     * Modifies the lock state of the slot.
     *
     * @param lock if the slot should be locked
     *
     * @return if the lock state was modified
     */
    public boolean setLocked(boolean lock) {
        // Don't lock if:
        // - We are a creative bin
        // - We already have the same state as the one we're supposed to switch to
        // - We were asked to lock, but we're empty
        if (isCreative || isLocked() == lock || (lock && isEmpty())) {
            return false;
        }
        lockStack = lock ? current.getStack().copyWithCount(1) : ItemStack.EMPTY;
        return true;
    }

    /**
     * For use by upgrade recipes, do not use this in place of {@link #setLocked(boolean)}
     */
    public void setLockStack(@NotNull ItemStack stack) {
        lockStack = stack.copyWithCount(1);
    }

    public boolean isLocked() {
        return !lockStack.isEmpty();
    }

    public ItemStack getRenderStack() {
        return isLocked() ? getLockStack() : getStack();
    }

    public ItemStack getLockStack() {
        return lockStack;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        if (isLocked()) {
            nbt.put(NBTConstants.LOCK_STACK, lockStack.save(new CompoundTag()));
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        NBTUtils.setItemStackOrEmpty(nbt, NBTConstants.LOCK_STACK, s -> this.lockStack = s);
        super.deserializeNBT(nbt);
    }
}