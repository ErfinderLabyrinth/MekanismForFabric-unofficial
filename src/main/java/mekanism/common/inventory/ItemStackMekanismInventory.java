package mekanism.common.inventory;

import mekanism.api.DataHandlerUtils;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.inventory.IMekanismInventory;
import mekanism.common.item.interfaces.IItemSustainedInventory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Helper class for implementing handling of inventories for items
 */
public abstract class ItemStackMekanismInventory implements IMekanismInventory {

    private final List<IInventorySlot> slots;
    @NotNull
    protected final ItemStack stack;

    protected ItemStackMekanismInventory(@NotNull ItemStack stack) {
        this.stack = stack;
        this.slots = getInitialInventory();
        if (!stack.isEmpty() && stack.getItem() instanceof IItemSustainedInventory sustainedInventory) {
            DataHandlerUtils.readContainers(slots, sustainedInventory.getSustainedInventory(stack));
        }
    }

    protected abstract List<IInventorySlot> getInitialInventory();

    @Override
    public Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        return new CombinedStorage<>(slots);
    }

    @Override
    public void onContentsChanged() {
        if (!stack.isEmpty() && stack.getItem() instanceof IItemSustainedInventory sustainedInventory) {
            sustainedInventory.setSustainedInventory(DataHandlerUtils.writeContainers(slots), stack);
        }
    }
}
