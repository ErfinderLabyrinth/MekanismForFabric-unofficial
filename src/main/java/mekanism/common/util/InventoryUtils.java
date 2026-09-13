package mekanism.common.util;

import mekanism.api.BigItemStack;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.security.ISecurityUtils;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.SelectedWindowData;
import mekanism.common.item.interfaces.IDroppableContents;
import mekanism.common.lib.inventory.TileTransitRequest;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class InventoryUtils {

    private InventoryUtils() {
    }

    /**
     * Helper to drop the contents of an inventory when it is destroyed if it is public or the cause of the destruction has access to the inventory.
     */
    public static void dropItemContents(ItemEntity entity, DamageSource source) {
        ItemStack stack = entity.getItem();
        if (!entity.level().isClientSide && !stack.isEmpty() && stack.getItem() instanceof IDroppableContents inventory && inventory.canContentsDrop(stack)) {
            boolean shouldDrop;
            if (source != null && source.getEntity() instanceof Player player) {
                //If the destroyer is a player use security utils to properly check for access
                shouldDrop = ISecurityUtils.INSTANCE.canAccess(player, stack);
            } else {
                // otherwise, just check against there being no known player
                shouldDrop = ISecurityUtils.INSTANCE.canAccess(null, stack, false);
            }
            if (shouldDrop) {
                for (IInventorySlot slot : inventory.getDroppedSlots(stack, entity.getServer())) {
                    if (!slot.isEmpty()) {
                        //Note: While some implementations return a dummy slot, so we would be able to pass them directly without copying
                        // we have implementations that pass the actual backing slot, so we have to copy the stack just in case
                        dropStack(slot.getStack().copy(), slotStack -> entity.level().addFreshEntity(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), slotStack)));
                    }
                }
            }
        }
    }

    /**
     * Helper to drop a stack that may potentially be oversized.
     *
     * @param stack   Item Stack to drop, may be passed directly to the dropper.
     * @param dropper Called to drop the item.
     */
    public static void dropStack(ItemStack stack, Consumer<ItemStack> dropper) {
        int count = stack.getCount();
        int max = stack.getMaxStackSize();
        if (count > max) {
            //If we have more than a stack of the item (such as we are a bin) or some other thing that allows for compressing
            // stack counts, drop as many stacks as we need at their max size
            while (count > max) {
                dropper.accept(stack.copyWithCount(max));
                count -= max;
            }
            if (count > 0) {
                //If we have anything left to drop afterward, do so
                dropper.accept(stack.copyWithCount(count));
            }
        } else {
            //If we have a valid stack, we can just directly drop that instead without requiring any copies
            dropper.accept(stack);
        }
    }

    /**
     * @param toInsert stack a
     * @param inSlot   stack b
     *
     * @return true if they are compatible
     */
    public static boolean areItemsStackable(ItemStack toInsert, ItemStack inSlot) {
        return ItemStack.isSameItemSameTags(toInsert, inSlot);
    }

    @Nullable
    public static Storage<ItemVariant> assertItemHandler(String desc, Level level, BlockPos pos, Direction side) {
        Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, side);
        if (storage != null) {
            return storage;
        }
        Mekanism.logger.warn("'{}' was wrapped around a non-IItemHandler inventory. This should not happen!", desc, new Exception());
        Mekanism.logger.warn(" - position: {}", pos);
        return null;
    }

    public static boolean isItemHandler(Level level, BlockPos pos, Direction side) {
        return ItemStorage.SIDED.find(level, pos, side) != null;
    }

    public static TileTransitRequest getEjectItemMap(Level tileLevel, BlockPos tilePos, Direction side, List<IInventorySlot> slots) {
        return getEjectItemMap(new TileTransitRequest(tileLevel, tilePos, side), slots);
    }

    //@Deprecated(forRemoval = true)
    @Contract("_, _ -> param1")
    public static <REQUEST extends TileTransitRequest> REQUEST getEjectItemMap(REQUEST request, List<IInventorySlot> slots) {
        // shuffle the order we look at our slots to avoid ejection patterns
        List<IInventorySlot> shuffled = new ArrayList<>(slots);
        Collections.shuffle(shuffled);
        for (IInventorySlot slot : shuffled) {
            //Note: We are using EXTERNAL as that is what we actually end up using when performing the extraction in the end
            long simulatedExtraction;
            try(Transaction t=Transaction.openOuter()) {
                simulatedExtraction = slot.extract(slot.getResource(), slot.getCount(), t);
            }
            if (simulatedExtraction != 0) {
                request.addItem(new BigItemStack(slot.getResource(), simulatedExtraction), slot);
            }
        }
        return request;
    }

    /**
     * Helper to first try inserting ignoring empty slots, and then insert not ignoring empty slots
     *
     * @param slots          Slots to insert into
     * @param stack          Stack to insert (do not modify).
     * @param t              The Transaction
     *
     * @return Remainder
     */
    public static ItemStack insertItem(List<? extends IInventorySlot> slots, @NotNull ItemStack stack, TransactionContext t) {
        stack = insertItem(slots, stack, true, false, t);
        return insertItem(slots, stack, false, false, t);
    }

    /**
     * Helper to try inserting a stack into a list of inventory slots only inserting into either empty slots or inserting into non-empty slots.
     *
     * @param slots          Slots to insert into
     * @param stack          Stack to insert (do not modify).
     * @param ignoreEmpty    {@code true} to ignore/skip empty slots, {@code false} to ignore/skip non-empty slots.
     * @param checkAll       {@code true} to check all slots regardless of empty state. When this is {@code true}, {@code ignoreEmpty} is ignored.
     * @param t              The Transaction
     *
     * @return Remainder
     *
     * @see mekanism.common.inventory.container.MekanismContainer#insertItem(List, ItemStack, boolean, boolean, SelectedWindowData, TransactionContext)
     */
    @NotNull
    public static ItemStack insertItem(List<? extends IInventorySlot> slots, @NotNull ItemStack stack, boolean ignoreEmpty, boolean checkAll, TransactionContext t) {
        if (stack.isEmpty()) {
            //Skip doing anything if the stack is already empty.
            // Makes it easier to chain calls, rather than having to check if the stack is empty after our previous call
            return stack;
        }
        long amountInserted = 0;
        for (IInventorySlot slot : slots) {
            if (!checkAll && ignoreEmpty == slot.isEmpty()) {
                //Skip checking empty stacks if we want to ignore them, and skip non-empty stacks if we don't want ot ignore them
                continue;
            }
            amountInserted = slot.insert(ItemVariant.of(stack), stack.getCount(), t);
            if (stack.isEmpty()) {
                break;
            }
        }
        return stack.copyWithCount(stack.getCount() - (int) amountInserted);
    }
}
