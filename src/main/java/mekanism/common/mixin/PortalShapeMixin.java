package mekanism.common.mixin;

import mekanism.common.mixinhelper.PortalFrameBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PortalShape.class)
public class PortalShapeMixin {
    @Inject(method = "method_30487", at = @At("HEAD"), cancellable = true)
    private static void checkForCustomPortalFrames(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        if (blockState.getBlock() instanceof PortalFrameBlock portalFrameBlock && portalFrameBlock.isPortalFrame(blockState, blockGetter, blockPos)) {
            cir.setReturnValue(true);
        }
    }
}
