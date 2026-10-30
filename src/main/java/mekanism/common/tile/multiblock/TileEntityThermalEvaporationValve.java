package mekanism.common.tile.multiblock;

import mekanism.api.IContentsListener;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.base.SubstanceType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
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

public class TileEntityThermalEvaporationValve extends TileEntityThermalEvaporationBlock {

    public TileEntityThermalEvaporationValve(BlockPos pos, BlockState state) {
        super(MekanismBlocks.THERMAL_EVAPORATION_VALVE, pos, state);
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
            public @NotNull List<IHeatCapacitor> getHeatCapacitors(@Nullable Direction side) {
                return getMultiblock().getHeatCapacitors(side);
            }

            @Override
            public List<IHeatCapacitor> getAll() {
                return getHeatCapacitors(null);
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
        //But that we do not handle fluid when it comes to syncing it/saving this tile to disk
        if (type == SubstanceType.FLUID || type == SubstanceType.HEAT) {
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
                    getMultiblock().triggerValveTransfer(TileEntityThermalEvaporationValve.this);
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