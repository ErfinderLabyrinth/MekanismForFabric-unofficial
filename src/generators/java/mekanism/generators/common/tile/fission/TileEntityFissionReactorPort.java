package mekanism.generators.common.tile.fission;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import mekanism.api.Action;
import mekanism.api.FluidStack;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.heat.IHeatHandler;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.lib.multiblock.IMultiblockEjector;
import mekanism.common.tile.TileEntityFluidTank;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.block.attribute.AttributeStateFissionPortMode;
import mekanism.generators.common.block.attribute.AttributeStateFissionPortMode.FissionPortMode;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityFissionReactorPort extends TileEntityFissionReactorCasing implements IMultiblockEjector {

    private Set<Direction> outputDirections = Collections.emptySet();

    public TileEntityFissionReactorPort(BlockPos pos, BlockState state) {
        super(GeneratorsBlocks.FISSION_REACTOR_PORT, pos, state);
    }

    @Override
    protected boolean onUpdateServer(FissionReactorMultiblockData multiblock) {
        boolean needsPacket = super.onUpdateServer(multiblock);
        if (multiblock.isFormed()) {
            FissionPortMode mode = getMode();
            if (mode == FissionPortMode.OUTPUT_COOLANT) {
                ChemicalUtil.emit(outputDirections, multiblock.heatedCoolantTank, this.level, this.worldPosition);
            } else if (mode == FissionPortMode.OUTPUT_WASTE) {
                ChemicalUtil.emit(outputDirections, multiblock.wasteTank, this.level, this.worldPosition);
            }
        }
        return needsPacket;
    }

    @Nullable
    @Override
    public IHeatHandler getAdjacent(@NotNull Direction side) {
        if (canHandleHeat() && getHeatCapacitorCount(side) > 0) {
            BlockEntity adj = WorldUtils.getTileEntity(getLevel(), getBlockPos().relative(side));
            if (!(adj instanceof TileEntityFissionReactorPort)) {
                return Capabilities.HEAT_HANDLER_BLOCK.find(getLevel(), getBlockPos().relative(side), side.getOpposite());
            }
        }
        return null;
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener) {
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

    @Override
    public boolean persists(SubstanceType type) {
        if (type == SubstanceType.HEAT || type == SubstanceType.GAS || type == SubstanceType.FLUID) {
            return false;
        }
        return super.persists(type);
    }

    @Override
    public void setEjectSides(Set<Direction> sides) {
        outputDirections = sides;
    }

    @ComputerMethod
    FissionPortMode getMode() {
        return getBlockState().getValue(AttributeStateFissionPortMode.modeProperty);
    }

    @ComputerMethod
    void setMode(FissionPortMode mode) {
        if (mode != getMode()) {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(AttributeStateFissionPortMode.modeProperty, mode));
        }
    }

    @Override
    public InteractionResult onSneakRightClick(Player player) {
        if (!isRemote()) {
            FissionPortMode mode = getMode().getNext();
            setMode(mode);
            player.displayClientMessage(MekanismLang.BOILER_VALVE_MODE_CHANGE.translateColored(EnumColor.GRAY, mode), true);
        }
        return InteractionResult.SUCCESS;
    }

    //TODO
//    @NotNull
//    @Override
//    public FluidStack insertFluid(@NotNull FluidStack stack, Direction side, @NotNull Action action) {
//        FluidStack ret = super.insertFluid(stack, side, action);
//        if (ret.getAmount() < stack.getAmount() && action.execute()) {
//            getMultiblock().triggerValveTransfer(this);
//        }
//        return ret;
//    }

    @Override
    public boolean insertGasCheck(int tank, @Nullable Direction side) {
        if (getMode() != FissionPortMode.INPUT) {
            //Don't allow inserting into the fuel tanks, if we are on output mode
            return false;
        }
        return super.insertGasCheck(tank, side);
    }

    @Override
    public boolean extractGasCheck(int tank, @Nullable Direction side) {
        //TODO: Do this better so there is no magic numbers
        FissionPortMode mode = getMode();
        if (mode == FissionPortMode.INPUT || (tank == 2 && mode == FissionPortMode.OUTPUT_COOLANT) || (tank == 1 && mode == FissionPortMode.OUTPUT_WASTE)) {
            // don't allow extraction from tanks based on mode
            return false;
        }
        return super.extractGasCheck(tank, side);
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

    @ComputerMethod
    void incrementMode() {
        setMode(getMode().getNext());
    }

    @ComputerMethod
    void decrementMode() {
        setMode(getMode().getPrevious());
    }
    //End methods IComputerTile

    @Override
    public @Nullable Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        Storage<FluidVariant> storage = super.getFluidStorage(side);
        if(side == Direction.UP) {
            return new FluidTankWrapper(storage);
        } else {
            return storage;
        }
    }

    public class FluidTankWrapper extends FilteringStorage<FluidVariant> {
        public FluidTankWrapper(Storage<FluidVariant> backingStorage) {
            super(backingStorage);
        }

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            long inserted = super.insert(resource, maxAmount, transaction);

            if(inserted > 0) {
                transaction.addOuterCloseCallback(result -> {
                    if(result.wasCommitted()) {
                        getMultiblock().triggerValveTransfer(TileEntityFissionReactorPort.this);
                    }
                });
            }

            return inserted;
        }
    }
}