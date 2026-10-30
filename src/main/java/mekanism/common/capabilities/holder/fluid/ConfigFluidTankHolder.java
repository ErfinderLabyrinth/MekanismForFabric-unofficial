package mekanism.common.capabilities.holder.fluid;

import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.holder.ConfigHolder;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.config.slot.FluidSlotInfo;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.function.Supplier;

public class ConfigFluidTankHolder extends ConfigHolder<IExtendedFluidTank> implements IFluidTankHolder {

    public ConfigFluidTankHolder(Supplier<Direction> facingSupplier, Supplier<TileComponentConfig> configSupplier) {
        super(facingSupplier, configSupplier);
    }

    void addTank(@NotNull IExtendedFluidTank tank) {
        slots.add(tank);
    }

    @Override
    protected TransmissionType getTransmissionType() {
        return TransmissionType.FLUID;
    }

    @Override
    public @NotNull Storage<FluidVariant> getTanks(@Nullable Direction direction) {
        return new CombinedStorage<>(getSlots(direction, slotInfo -> slotInfo instanceof FluidSlotInfo info ? info.getTanks() : Collections.emptyList()));
    }
}