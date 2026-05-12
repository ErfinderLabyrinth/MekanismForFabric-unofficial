package mekanism.common.tile.base;

import mekanism.api.DataHandlerUtils;
import mekanism.api.NBTConstants;
import mekanism.api.NBTSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.List;
import java.util.function.Function;

public enum SubstanceType {
    ENERGY(NBTConstants.ENERGY_CONTAINERS, tile -> tile.getEnergyManager().canHandle() ? tile.getEnergyManager().getHolder().getAll() : List.of()),
    FLUID(NBTConstants.FLUID_TANKS, tile -> tile.getFluidManager().canHandle() ? tile.getFluidManager().getHolder().getAll() : List.of()),
    GAS(NBTConstants.GAS_TANKS, tile -> tile.getGasManager().canHandle() ? tile.getGasManager().getHolder().getAll() : List.of()),
    INFUSION(NBTConstants.INFUSION_TANKS, tile -> tile.getInfusionManager().canHandle() ? tile.getInfusionManager().getHolder().getAll() : List.of()),
    PIGMENT(NBTConstants.PIGMENT_TANKS, tile -> tile.getPigmentManager().canHandle() ? tile.getPigmentManager().getHolder().getAll() : List.of()),
    SLURRY(NBTConstants.SLURRY_TANKS, tile -> tile.getSlurryManager().canHandle() ? tile.getSlurryManager().getHolder().getAll() : List.of()),
    HEAT(NBTConstants.HEAT_CAPACITORS, tile -> tile.getHeatCapacitors(null));

    private final String containerTag;
    private final Function<TileEntityMekanism, List<? extends NBTSerializable<CompoundTag>>> containerSupplier;

    SubstanceType(String containerTag, Function<TileEntityMekanism, List<? extends NBTSerializable<CompoundTag>>> containerSupplier) {
        this.containerTag = containerTag;
        this.containerSupplier = containerSupplier;
    }

    public void write(TileEntityMekanism tile, CompoundTag tag) {
        tag.put(containerTag, DataHandlerUtils.writeContainers(containerSupplier.apply(tile)));
    }

    public void read(TileEntityMekanism tile, CompoundTag tag) {
        DataHandlerUtils.readContainers(containerSupplier.apply(tile), tag.getList(containerTag, Tag.TAG_COMPOUND));
    }

    public String getContainerTag() {
        return containerTag;
    }

    public List<? extends NBTSerializable<CompoundTag>> getContainers(TileEntityMekanism tile) {
        return containerSupplier.apply(tile);
    }

    public boolean canHandle(TileEntityMekanism tile) {
        return switch (this) {
            case ENERGY -> tile.canHandleEnergy();
            case FLUID -> tile.canHandleFluid();
            case GAS -> tile.canHandleGas();
            case INFUSION -> tile.canHandleInfusion();
            case PIGMENT -> tile.canHandlePigment();
            case SLURRY -> tile.canHandleSlurry();
            case HEAT -> tile.canHandleHeat();
        };
    }
}
