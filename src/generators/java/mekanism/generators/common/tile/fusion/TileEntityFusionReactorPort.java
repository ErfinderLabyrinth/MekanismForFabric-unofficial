package mekanism.generators.common.tile.fusion;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.heat.IHeatHandler;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.text.EnumColor;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.lib.multiblock.IMultiblockEjector;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.util.CableUtils;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.WorldUtils;
import mekanism.common.util.text.BooleanStateDisplay.InputOutput;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class TileEntityFusionReactorPort extends TileEntityFusionReactorBlock implements IMultiblockEjector {

    private Set<Direction> outputDirections = Collections.emptySet();

    public TileEntityFusionReactorPort(BlockPos pos, BlockState state) {
        super(GeneratorsBlocks.FUSION_REACTOR_PORT, pos, state);
        delaySupplier = NO_DELAY;
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener) {
        //Note: We can just use a proxied holder as the input/output restrictions are done in the tanks themselves
        return new IChemicalTankHolder<>() {
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
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return new IFluidTankHolder() {
            @Override
            public @NotNull Storage<FluidVariant> getTanks(@Nullable Direction side) {
                return getMultiblock().getFluidStorage(side);
            }

            @Override
            public List<IExtendedFluidTank> getAll() {
                return getMultiblock().getFluidTanks();
            }
        };
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        return new IEnergyContainerHolder() {
            @Override
            public @NotNull EnergyStorage getEnergyContainers(@Nullable Direction side) {
                return getMultiblock().getEnergyStorage(side);
            }

            @Override
            public List<IEnergyContainer> getAll() {
                return getMultiblock().getEnergyContainers();
            }
        };
    }

    @NotNull
    @Override
    protected IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener, CachedAmbientTemperature ambientTemperature) {
        return new IHeatCapacitorHolder() {

            @Override
            public List<IHeatCapacitor> getAll() {
                return getMultiblock().getHeatCapacitors(null);
            }

            @Override
            public @NotNull List<IHeatCapacitor> getHeatCapacitors(@Nullable Direction side) {
                return getMultiblock().getHeatCapacitors(side);
            }
        };
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        return new IInventorySlotHolder() {
            @Override
            public @NotNull Storage<ItemVariant> getInventorySlots(@Nullable Direction side) {
                return getMultiblock().getInventoryStorage(side);
            }

            @Override
            public List<IInventorySlot> getAll() {
                return getMultiblock().getInventorySlots();
            }
        };
    }

    @Override
    public boolean persists(SubstanceType type) {
        if (type == SubstanceType.GAS || type == SubstanceType.FLUID || type == SubstanceType.ENERGY || type == SubstanceType.HEAT) {
            return false;
        }
        return super.persists(type);
    }

    @Override
    public void setEjectSides(Set<Direction> sides) {
        outputDirections = sides;
    }

    @Override
    protected boolean onUpdateServer(FusionReactorMultiblockData multiblock) {
        boolean needsPacket = super.onUpdateServer(multiblock);
        if (getActive() && multiblock.isFormed()) {
            ChemicalUtil.emit(outputDirections, multiblock.steamTank, this.level, this.worldPosition);
            CableUtils.emit(outputDirections, multiblock.energyContainer, this);
        }
        return needsPacket;
    }

    @Nullable
    @Override
    public IHeatHandler getAdjacent(@NotNull Direction side) {
        if (canHandleHeat() && getHeatCapacitorCount(side) > 0) {
            BlockEntity adj = WorldUtils.getTileEntity(getLevel(), getBlockPos().relative(side));
            if (!(adj instanceof TileEntityFusionReactorBlock)) {
                return Capabilities.HEAT_HANDLER_BLOCK.find(getLevel(), getBlockPos().relative(side), side.getOpposite());
            }
        }
        return null;
    }

    @Override
    public InteractionResult onSneakRightClick(Player player) {
        if (!isRemote()) {
            boolean oldMode = getActive();
            setActive(!oldMode);
            player.displayClientMessage(GeneratorsLang.REACTOR_PORT_EJECT.translateColored(EnumColor.GRAY, InputOutput.of(oldMode, true)), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public int getRedstoneLevel() {
        return getMultiblock().getCurrentRedstoneLevel();
    }

    //Methods relating to IComputerTile
    @Override
    public boolean exposesMultiblockToComputer() {
        return false;
    }

    @ComputerMethod(methodDescription = "true -> output, false -> input")
    boolean getMode() {
        return getActive();
    }

    @ComputerMethod(methodDescription = "true -> output, false -> input")
    void setMode(boolean output) {
        setActive(output);
    }
    //End methods IComputerTile
}
