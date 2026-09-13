package mekanism.common.item.interfaces;

import mekanism.api.inventory.IInventorySlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IDroppableContents {

    default boolean canContentsDrop(ItemStack stack) {
        return true;
    }

    /**
     * Helper to get the inventory slots that should have their contents dropped into the world
     *
     * @apiNote Server side only.
     */
    List<IInventorySlot> getDroppedSlots(ItemStack stack, MinecraftServer server);
}