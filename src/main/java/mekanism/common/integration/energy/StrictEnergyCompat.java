package mekanism.common.integration.energy;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.capabilities.Capabilities;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class StrictEnergyCompat implements IEnergyCompat<IStrictEnergyHandler> {

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public IStrictEnergyHandler getHandlerAs(IStrictEnergyHandler handler) {
        return handler;
    }

    @Override
    public @Nullable IStrictEnergyHandler getStrictEnergyHandler(Level level, BlockPos pos, @Nullable Direction side) {
        return Capabilities.STRICT_ENERGY_BLOCK.find(level, pos, side);
    }

    @Override
    public @Nullable IStrictEnergyHandler getStrictEnergyHandler(ItemStack stack, ContainerItemContext context) {
        return Capabilities.STRICT_ENERGY_ITEM.find(stack, context);
    }
}