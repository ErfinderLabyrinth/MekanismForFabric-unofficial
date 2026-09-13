package mekanism.common.mixinhelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;

public interface BypassSneakItem {
    boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player);
}
