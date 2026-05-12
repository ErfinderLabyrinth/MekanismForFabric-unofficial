package mekanism.common.capabilities.holder.fluid;

import mekanism.api.RelativeSide;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.holder.BasicHolder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class FluidTankHolder extends BasicHolder<IExtendedFluidTank, FluidVariant> implements IFluidTankHolder {

    FluidTankHolder(Supplier<Direction> facingSupplier) {
        super(facingSupplier);
    }

    void addTank(@NotNull IExtendedFluidTank tank, RelativeSide... sides) {
        addSlotInternal(tank, sides);
    }

    @Override
    public @NotNull Storage<FluidVariant> getTanks(@Nullable Direction direction) {
        return new CombinedStorage<>(getSlots(direction));
    }
}