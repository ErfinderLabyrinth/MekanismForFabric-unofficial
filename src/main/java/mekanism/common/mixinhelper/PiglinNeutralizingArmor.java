package mekanism.common.mixinhelper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface PiglinNeutralizingArmor {
    boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer);
}
