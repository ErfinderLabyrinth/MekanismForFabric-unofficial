package mekanism.common.inventory.container.slot;

import mekanism.api.AutomationType;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.inventory.warning.ISupportsWarning;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

//Like net.minecraftforge.items.SlotItemHandler, except directly interacts with the IInventorySlot instead
public class InventoryContainerSlot extends Slot implements IInsertableSlot {

    private static final Container emptyInventory = new SimpleContainer(0);
    private final Consumer<ItemStack> uncheckedSetter;
    private final ContainerSlotType slotType;
    private final BasicInventorySlot slot;
    @Nullable
    private final SlotOverlay slotOverlay;
    @Nullable
    private final Consumer<ISupportsWarning<?>> warningAdder;

    public InventoryContainerSlot(BasicInventorySlot slot, int x, int y, ContainerSlotType slotType, @Nullable SlotOverlay slotOverlay,
          @Nullable Consumer<ISupportsWarning<?>> warningAdder, Consumer<ItemStack> uncheckedSetter) {
        super(emptyInventory, 0, x, y);
        this.slot = slot;
        this.slotType = slotType;
        this.slotOverlay = slotOverlay;
        this.warningAdder = warningAdder;
        this.uncheckedSetter = uncheckedSetter;
    }

    public IInventorySlot getInventorySlot() {
        return slot;
    }

    public void addWarnings(ISupportsWarning<?> slot) {
        if (warningAdder != null) {
            warningAdder.accept(slot);
        }
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        long inserted = slot.insert(resource, maxAmount, transaction);
        if (inserted != 0) {
            transaction.addOuterCloseCallback(e -> {
                setChanged(); //TODO does we need that and is this correct
            });
        }
        return inserted;
    }

    //    @NotNull
//    @Override
//    public ItemStack insertItem(@NotNull ItemStack stack, Action action) {
//        ItemStack remainder = slot.insertItem(stack, action, AutomationType.MANUAL);
//        if (action.execute() && stack.getCount() != remainder.getCount()) {
//            setChanged();
//        }
//        return remainder;
//    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (slot.isEmpty()) {
            //If the slot is currently empty, just try simulating the insertion
            try(Transaction t=Transaction.openOuter()) {
                return slot.insert(ItemVariant.of(stack), stack.getCount(), t) > 0;
            }
        }
        //Otherwise, we need to check if we can extract the current item
        try(Transaction t=Transaction.openOuter()) {
            if (slot.extract(slot.getResource(), 1, t) == 0) {
                //If we can't, fail
                return false;
            }
        }
        //If we can check if we can insert the item ignoring the current contents
        return slot.isItemValidForInsertion(stack, AutomationType.MANUAL);
    }

    @NotNull
    @Override
    public ItemStack getItem() {
        return slot.getStack().copy();
    }

    @Override
    public boolean hasItem() {
        return !slot.isEmpty();
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        //Note: We have to set the stack in an unchecked manner here, so that if we sync a stack from the server to the client that
        // the client does not think is valid for the stack, it doesn't cause major issues. Additionally, we do this directly in
        // our putStack method rather than having a separate unchecked method, as if some modder is modifying inventories directly
        // for some reason, and the machine has invalid items in it, it could cause various issues/crashes which are not entirely
        // worth dealing with, as it is relatively reasonable to assume if an item is stored in a slot, more items of that type
        // are valid in the same slot without having to check isItemValid.
        uncheckedSetter.accept(stack);
        setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        slot.onContentsChanged();
    }

    @Override
    public int getMaxStackSize() {
        return slot.getLimit(ItemStack.EMPTY);
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return slot.getLimit(stack);
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        try(Transaction t=Transaction.openOuter()) {
            return slot.extract(slot.getResource(), 1, t, AutomationType.MANUAL) != 0;
        }
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return slot.extract(resource, maxAmount, transaction);
    }

    @NotNull
    @Override
    public ItemStack remove(int amount) {
        try(Transaction t=Transaction.openOuter()) {
            ItemVariant resource = slot.getResource();
            long amountExtracted = slot.extract(resource, amount, t, AutomationType.MANUAL);
            t.commit();
            return resource.toStack((int)amountExtracted);
        }
    }

    //TODO: Forge has a TODO for implementing isSameInventory.
    // We can compare inventories at the very least for BasicInventorySlots as they have an instance of IMekanismInventory stored
    /*@Override
    public boolean isSameInventory(Slot other) {
        return other instanceof SlotItemHandler handler && handler.getItemHandler() == this.itemHandler;
    }*/

    public ContainerSlotType getSlotType() {
        return slotType;
    }

    @Nullable
    public SlotOverlay getSlotOverlay() {
        return slotOverlay;
    }

    @Override
    public boolean isResourceBlank() {
        return slot.isResourceBlank();
    }

    @Override
    public ItemVariant getResource() {
        return slot.getResource();
    }

    @Override
    public long getAmount() {
        return slot.getAmount();
    }

    @Override
    public long getCapacity() {
        return slot.getCapacity();
    }
}