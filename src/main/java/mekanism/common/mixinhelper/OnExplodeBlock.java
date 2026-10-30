package mekanism.common.mixinhelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface OnExplodeBlock {
    void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion);
}
