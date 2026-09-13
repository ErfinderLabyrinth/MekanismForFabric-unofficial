package mekanism.common.capabilities.holder.heat;

import mekanism.api.heat.IHeatCapacitor;
import mekanism.common.capabilities.holder.IHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IHeatCapacitorHolder extends IHolder<IHeatCapacitor> {

    @NotNull
    List<IHeatCapacitor> getHeatCapacitors(@Nullable Direction side);
}
