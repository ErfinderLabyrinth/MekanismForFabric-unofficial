package mekanism.common.capabilities.holder.energy;

import mekanism.api.RelativeSide;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.holder.BasicHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;
import java.util.function.Supplier;

public class EnergyContainerHolder extends BasicHolder<IEnergyContainer, Object> implements IEnergyContainerHolder {

    EnergyContainerHolder(Supplier<Direction> facingSupplier) {
        super(facingSupplier);
    }

    void addContainer(@NotNull IEnergyContainer storage, RelativeSide... sides) {
        addSlotInternal(storage, sides);
    }

    @NotNull
    @Override
    public EnergyStorage getEnergyContainers(@Nullable Direction direction) {
        List<IEnergyContainer> storages = getSlots(direction);
        if (storages.size() == 0) return IEnergyContainer.EMPTY;
        return storages.get(0);
    }
}