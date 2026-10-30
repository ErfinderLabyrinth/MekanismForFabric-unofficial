package mekanism.common.capabilities.holder.chemical;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.common.capabilities.holder.QuantumEntangloporterConfigHolder;
import mekanism.common.content.entangloporter.InventoryFrequency;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.TileEntityQuantumEntangloporter;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class QuantumEntangloporterChemicalTankHolder<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>,
      TANK extends IChemicalTank<CHEMICAL, STACK>> extends QuantumEntangloporterConfigHolder<TANK> implements IChemicalTankHolder<CHEMICAL, STACK, TANK> {

    private final BiFunction<InventoryFrequency, Direction, Storage<CHEMICAL>> tankResolver;
    private final TransmissionType transmissionType;

    public QuantumEntangloporterChemicalTankHolder(TileEntityQuantumEntangloporter entangloporter, TransmissionType transmissionType,
          BiFunction<InventoryFrequency, Direction, Storage<CHEMICAL>> tankResolver) {
        super(entangloporter);
        this.transmissionType = transmissionType;
        this.tankResolver = tankResolver;
    }

    @Override
    protected TransmissionType getTransmissionType() {
        return transmissionType;
    }

    @NotNull
    @Override
    public Storage<CHEMICAL> getTanks(@Nullable Direction side) {
        return entangloporter.hasFrequency() ? tankResolver.apply(entangloporter.getFreq(), side) : Storage.empty();
    }
}