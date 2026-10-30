package mekanism.common.capabilities.resolver.manager;

import mekanism.api.inventory.IInventorySlot;
import mekanism.api.inventory.ISidedItemHandler;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.proxy.ProxyItemHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class to make reading instead of having as messy generics
 */
public class ItemHandlerManager extends CapabilityHandlerManager<IInventorySlotHolder, ItemVariant, Storage<ItemVariant>, ISidedItemHandler, IInventorySlot> {

    public ItemHandlerManager(@Nullable IInventorySlotHolder holder/*, @NotNull ISidedItemHandler baseHandler*/) {
        super(holder, /*baseHandler,*/ ProxyItemHandler::new, IInventorySlotHolder::getInventorySlots);
    }
}