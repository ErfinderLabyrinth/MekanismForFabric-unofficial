package mekanism.common.capabilities.holder.energy;

import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.holder.ProxiedHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ProxiedEnergyContainerHolder extends ProxiedHolder<IEnergyContainer> implements IEnergyContainerHolder {

    private final Function<Direction, EnergyStorage> containerFunction;
    private final Supplier<List<IEnergyContainer>> allFunction;

    public static ProxiedEnergyContainerHolder create(Predicate<Direction> insertPredicate, Predicate<Direction> extractPredicate,
          Function<Direction, EnergyStorage> containerFunction, Supplier<List<IEnergyContainer>> allFunction) {
        return new ProxiedEnergyContainerHolder(insertPredicate, extractPredicate, containerFunction, allFunction);
    }

    private ProxiedEnergyContainerHolder(Predicate<Direction> insertPredicate, Predicate<Direction> extractPredicate,
          Function<Direction, EnergyStorage> containerFunction, Supplier<List<IEnergyContainer>> allFunction) {
        super(insertPredicate, extractPredicate);
        this.containerFunction = containerFunction;
        this.allFunction = allFunction;
    }

    @Override
    public @NotNull EnergyStorage getEnergyContainers(@Nullable Direction side) {
        return containerFunction.apply(side);
    }

    @Override
    public List<IEnergyContainer> getAll() {
        return allFunction.get();
    }
}