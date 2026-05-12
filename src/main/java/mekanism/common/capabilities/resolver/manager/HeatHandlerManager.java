package mekanism.common.capabilities.resolver.manager;

import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.heat.ISidedHeatHandler;
import mekanism.common.capabilities.holder.IHolder;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.capabilities.proxy.ProxyHeatHandler;
import mekanism.common.capabilities.resolver.BasicSidedCapabilityResolver;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Helper class to make reading instead of having as messy generics
 */
public class HeatHandlerManager extends BasicSidedCapabilityResolver<ProxyHeatHandler, ISidedHeatHandler> {
    IHeatCapacitorHolder holder;
    public HeatHandlerManager(@Nullable IHeatCapacitorHolder holder, @NotNull ISidedHeatHandler baseHandler) {
        super(/*baseHandler,*/ ProxyHeatHandler::new, holder != null);
        this.holder = holder;
    }

    public List<IHeatCapacitor> getContainers(@Nullable Direction side) {
        return canHandle() ? holder.getHeatCapacitors(side) : List.of();
    }

    @Nullable
    public IHolder<IHeatCapacitor> getHolder() {
        return holder;
    }

    public boolean canHandle() {
        return holder != null;
    }
}