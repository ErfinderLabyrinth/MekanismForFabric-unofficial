package mekanism.common.integration.energy.teamreborn;

import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.integration.energy.IEnergyCompat;
import mekanism.common.util.UnitDisplayUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class TeamRebornEnergyCompat implements IEnergyCompat<EnergyStorage> {

    @Override
    public boolean isUsable() {
        return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.isEnabled();
    }

    @Override
    public EnergyStorage getHandlerAs(IStrictEnergyHandler handler) {
        return TeamRebornEnergyIntegration.createWrapper(handler);
    }

    @Override
    public @Nullable IStrictEnergyHandler getStrictEnergyHandler(Level level, BlockPos pos, @Nullable Direction side) {
        EnergyStorage storage = EnergyStorage.SIDED.find(level, pos, side);
        if(storage == null) {
            return null;
        }
        return TeamRebornStrictEnergyHandler.createWrapper(storage);
    }

    @Override
    public @Nullable IStrictEnergyHandler getStrictEnergyHandler(ItemStack stack, ContainerItemContext context) {
        EnergyStorage storage = EnergyStorage.ITEM.find(stack, context);
        if(storage == null) {
            return null;
        }
        return TeamRebornStrictEnergyHandler.createWrapper(storage);
    }
}
