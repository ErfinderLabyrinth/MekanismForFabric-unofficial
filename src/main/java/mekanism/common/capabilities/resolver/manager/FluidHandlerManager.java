package mekanism.common.capabilities.resolver.manager;

import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.fluid.ISidedFluidHandler;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.proxy.ProxyFluidHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class to make reading instead of having as messy generics
 */
public class FluidHandlerManager extends CapabilityHandlerManager<IFluidTankHolder, FluidVariant, Storage<FluidVariant>, ISidedFluidHandler, IExtendedFluidTank> {

    public FluidHandlerManager(@Nullable IFluidTankHolder holder /*, @NotNull ISidedFluidHandler baseHandler*/) {
        super(holder, /*baseHandler,*/ ProxyFluidHandler::new, IFluidTankHolder::getTanks);
    }
}