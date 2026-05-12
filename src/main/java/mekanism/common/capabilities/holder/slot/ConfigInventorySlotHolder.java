package mekanism.common.capabilities.holder.slot;

import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.holder.ConfigHolder;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.function.Supplier;

public class ConfigInventorySlotHolder extends ConfigHolder<IInventorySlot> implements IInventorySlotHolder {

    ConfigInventorySlotHolder(Supplier<Direction> facingSupplier, Supplier<TileComponentConfig> configSupplier) {
        super(facingSupplier, configSupplier);
    }

    void addSlot(@NotNull IInventorySlot slot) {
        slots.add(slot);
    }

    @Override
    protected TransmissionType getTransmissionType() {
        return TransmissionType.ITEM;
    }

    @Override
    public @NotNull Storage<ItemVariant> getInventorySlots(@Nullable Direction direction) {
        return new CombinedStorage<>(getSlots(direction, slotInfo -> slotInfo instanceof InventorySlotInfo info ? info.getSlots() : Collections.emptyList()));
    }
}