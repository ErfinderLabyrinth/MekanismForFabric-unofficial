package mekanism.common.capabilities.holder.energy;

import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.holder.ConfigHolder;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.config.slot.EnergySlotInfo;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ConfigEnergyContainerHolder extends ConfigHolder<IEnergyContainer> implements IEnergyContainerHolder {

    public ConfigEnergyContainerHolder(Supplier<Direction> facingSupplier, Supplier<TileComponentConfig> configSupplier) {
        super(facingSupplier, configSupplier);
    }

    void addContainer(@NotNull IEnergyContainer container) {
        slots.add(container);
    }

    @Override
    protected TransmissionType getTransmissionType() {
        return TransmissionType.ENERGY;
    }

    @NotNull
    @Override
    public EnergyStorage getEnergyContainers(@Nullable Direction direction) {
        List<IEnergyContainer> storages = getSlots(direction, slotInfo -> slotInfo instanceof EnergySlotInfo info ? info.getContainers() : Collections.emptyList());
        if (storages.isEmpty()) return EnergyStorage.EMPTY;
        return storages.get(0);
    }
}