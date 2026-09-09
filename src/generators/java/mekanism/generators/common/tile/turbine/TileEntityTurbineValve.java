package mekanism.generators.common.tile.turbine;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.lib.multiblock.IMultiblockEjector;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.util.CableUtils;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class TileEntityTurbineValve extends TileEntityTurbineCasing implements IMultiblockEjector {

    private Set<Direction> outputDirections = Collections.emptySet();

    public TileEntityTurbineValve(BlockPos pos, BlockState state) {
        super(GeneratorsBlocks.TURBINE_VALVE, pos, state);
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener) {
        return new IChemicalTankHolder<Gas, GasStack, IGasTank>() {
            @Override
            public @NotNull Storage<Gas> getTanks(@Nullable Direction side) {
                return getMultiblock().getGasStorage(side);
            }

            @Override
            public List<IGasTank> getAll() {
                return getMultiblock().getGasTanks();
            }
        };
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        return new IEnergyContainerHolder() {
            @Override
            public List<IEnergyContainer> getAll() {
                return getMultiblock().getEnergyContainers();
            }

            @Override
            public @NotNull EnergyStorage getEnergyContainers(@Nullable Direction side) {
                return getMultiblock().getEnergyStorage(side);
            }
        };
    }

    @Override
    protected boolean onUpdateServer(TurbineMultiblockData multiblock) {
        boolean needsPacket = super.onUpdateServer(multiblock);
        if (multiblock.isFormed()) {
            CableUtils.emit(outputDirections, multiblock.energyContainer, this);
        }
        return needsPacket;
    }

    @Override
    public boolean persists(SubstanceType type) {
        //Do not handle gas when it comes to syncing it/saving this tile to disk
        if (type == SubstanceType.GAS || type == SubstanceType.ENERGY) {
            return false;
        }
        return super.persists(type);
    }

    @Override
    public void setEjectSides(Set<Direction> sides) {
        outputDirections = sides;
    }

    @Override
    public int getRedstoneLevel() {
        return getMultiblock().getCurrentRedstoneLevel();
    }
}