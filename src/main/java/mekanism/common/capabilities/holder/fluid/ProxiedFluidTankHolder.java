package mekanism.common.capabilities.holder.fluid;

import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.holder.ProxiedHolder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ProxiedFluidTankHolder extends ProxiedHolder<IExtendedFluidTank> implements IFluidTankHolder {

    private final Function<Direction, List<IExtendedFluidTank>> tankFunction;

    public static ProxiedFluidTankHolder create(Predicate<Direction> insertPredicate, Predicate<Direction> extractPredicate,
          Function<Direction, List<IExtendedFluidTank>> tankFunction) {
        return new ProxiedFluidTankHolder(insertPredicate, extractPredicate, tankFunction);
    }

    private ProxiedFluidTankHolder(Predicate<Direction> insertPredicate, Predicate<Direction> extractPredicate,
          Function<Direction, List<IExtendedFluidTank>> tankFunction) {
        super(insertPredicate, extractPredicate);
        this.tankFunction = tankFunction;
    }

    @Override
    public @NotNull Storage<FluidVariant> getTanks(@Nullable Direction side) {
        return new CombinedStorage<>(tankFunction.apply(side));
    }

    @Override
    public List<IExtendedFluidTank> getAll() {
        return tankFunction.apply(null);
    }
}