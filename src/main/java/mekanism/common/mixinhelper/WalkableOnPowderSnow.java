package mekanism.common.mixinhelper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface WalkableOnPowderSnow {
    boolean canWalkOnPowderedSnow(@NotNull ItemStack stack, @NotNull LivingEntity wearer);
}
