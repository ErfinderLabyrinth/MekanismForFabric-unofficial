package mekanism.common.tile.multiblock;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.base.SubstanceType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class TileEntityDynamicValve extends TileEntityDynamicTank {

    public TileEntityDynamicValve(BlockPos pos, BlockState state) {
        super(MekanismBlocks.DYNAMIC_VALVE, pos, state);
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
                return getMultiblock().fluidTanks;
            }
        };
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
    public IChemicalTankHolder<InfuseType, InfusionStack, IInfusionTank> getInitialInfusionTanks(IContentsListener listener) {
        return new IChemicalTankHolder<>() {
            @Override
            public @NotNull Storage<InfuseType> getTanks(@Nullable Direction side) {
                return getMultiblock().getInfusionStorage(side);
            }

            @Override
            public List<IInfusionTank> getAll() {
                return getMultiblock().getInfusionTanks();
            }
        };
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Pigment, PigmentStack, IPigmentTank> getInitialPigmentTanks(IContentsListener listener) {
        return new IChemicalTankHolder<>() {
            @Override
            public @NotNull Storage<Pigment> getTanks(@Nullable Direction side) {
                return getMultiblock().getPigmentStorage(side);
            }

            @Override
            public List<IPigmentTank> getAll() {
                return getMultiblock().getPigmentTanks();
            }
        };
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Slurry, SlurryStack, ISlurryTank> getInitialSlurryTanks(IContentsListener listener) {
        return new IChemicalTankHolder<>() {
            @Override
            public @NotNull Storage<Slurry> getTanks(@Nullable Direction side) {
                return getMultiblock().getSlurryStorage(side);
            }

            @Override
            public List<ISlurryTank> getAll() {
                return getMultiblock().getSlurryTanks();
            }
        };
    }

    @Override
    public boolean persists(SubstanceType type) {
        //Do not handle fluid when it comes to syncing it/saving this tile to disk
        if (type == SubstanceType.FLUID || type == SubstanceType.GAS || type == SubstanceType.INFUSION || type == SubstanceType.PIGMENT || type == SubstanceType.SLURRY) {
            return false;
        }
        return super.persists(type);
    }

    @Override
    public @Nullable Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        Storage<FluidVariant> original = super.getFluidStorage(side);
        return new Storage<FluidVariant>() {
            @Override
            public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                long amountInserted = original.insert(resource, maxAmount, transaction);
                if (amountInserted != 0) {
                    getMultiblock().triggerValveTransfer(TileEntityDynamicValve.this);
                }
                return amountInserted;
            }

            @Override
            public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                return original.extract(resource, maxAmount, transaction);
            }

            @Override
            public Iterator<StorageView<FluidVariant>> iterator() {
                return original.iterator();
            }
        };
    }

    @Override
    public int getRedstoneLevel() {
        return getMultiblock().getCurrentRedstoneLevel();
    }
}