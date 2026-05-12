package mekanism.common.mixinhelper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ElytraFlyable {
    boolean canElytraFly(ItemStack stack, LivingEntity entity);
    boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks);
}
