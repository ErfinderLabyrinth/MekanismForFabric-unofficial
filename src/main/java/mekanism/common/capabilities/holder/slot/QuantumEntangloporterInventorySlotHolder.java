package mekanism.common.capabilities.holder.slot;

import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.holder.QuantumEntangloporterConfigHolder;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.TileEntityQuantumEntangloporter;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuantumEntangloporterInventorySlotHolder extends QuantumEntangloporterConfigHolder<IInventorySlot> implements IInventorySlotHolder {

    public QuantumEntangloporterInventorySlotHolder(TileEntityQuantumEntangloporter entangloporter) {
        super(entangloporter);
    }

    @Override
    protected TransmissionType getTransmissionType() {
        return TransmissionType.ITEM;
    }

    @Override
    public @NotNull Storage<ItemVariant> getInventorySlots(@Nullable Direction side) {
        return entangloporter.hasFrequency() && entangloporter.hasInventory() ? new CombinedStorage<>(entangloporter.getFreq().getInventorySlots(side)) : Storage.empty();
    }
}