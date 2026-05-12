package mekanism.common.mixinhelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface PortalFrameBlock {
    boolean isPortalFrame(BlockState state, BlockGetter world, BlockPos pos);
}
