package mekanism.common.capabilities.holder.slot;

import mekanism.api.inventory.IInventorySlot;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReadOnlyInventorySlotHolder implements IInventorySlotHolder {

    private final List<IInventorySlot> inventorySlots = new ArrayList<>();

    ReadOnlyInventorySlotHolder() {
    }

    void addSlot(@NotNull IInventorySlot slot) {
        inventorySlots.add(slot);
    }

    @Override
    public @NotNull Storage<ItemVariant> getInventorySlots(@Nullable Direction direction) {
        //Only expose the slots if it is internal
        return direction == null ? new CombinedStorage<>(getAll()) : Storage.empty();
    }

    @Override
    public boolean canInsert(@Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canExtract(@Nullable Direction direction) {
        return false;
    }

    @Override
    public List<IInventorySlot> getAll() {
        return inventorySlots;
    }
}