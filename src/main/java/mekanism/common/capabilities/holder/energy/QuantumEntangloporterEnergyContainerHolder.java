package mekanism.common.capabilities.holder.energy;

import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.holder.QuantumEntangloporterConfigHolder;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.TileEntityQuantumEntangloporter;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class QuantumEntangloporterEnergyContainerHolder extends QuantumEntangloporterConfigHolder<IEnergyContainer> implements IEnergyContainerHolder {

    public QuantumEntangloporterEnergyContainerHolder(TileEntityQuantumEntangloporter entangloporter) {
        super(entangloporter);
    }

    @Override
    protected TransmissionType getTransmissionType() {
        return TransmissionType.ENERGY;
    }

    @Override
    public @NotNull EnergyStorage getEnergyContainers(@Nullable Direction side) {
        return entangloporter.hasFrequency() ? entangloporter.getFreq().getEnergyContainers(side).get(0) : EnergyStorage.EMPTY;
    }
}