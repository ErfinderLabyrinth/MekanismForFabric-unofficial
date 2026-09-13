package mekanism.common.capabilities.holder.energy;

import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.holder.IHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public interface IEnergyContainerHolder extends IHolder<IEnergyContainer> {

    @NotNull
    EnergyStorage getEnergyContainers(@Nullable Direction side);
}