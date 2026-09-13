package mekanism.common.capabilities.holder.fluid;

import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.holder.QuantumEntangloporterConfigHolder;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.TileEntityQuantumEntangloporter;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuantumEntangloporterFluidTankHolder extends QuantumEntangloporterConfigHolder<IExtendedFluidTank> implements IFluidTankHolder {

    public QuantumEntangloporterFluidTankHolder(TileEntityQuantumEntangloporter entangloporter) {
        super(entangloporter);
    }

    @Override
    protected TransmissionType getTransmissionType() {
        return TransmissionType.FLUID;
    }

    @Override
    public @NotNull Storage<FluidVariant> getTanks(@Nullable Direction side) {
        return entangloporter.hasFrequency() ? new CombinedStorage<>(entangloporter.getFreq().getFluidTanks(side)) : Storage.empty();
    }
}