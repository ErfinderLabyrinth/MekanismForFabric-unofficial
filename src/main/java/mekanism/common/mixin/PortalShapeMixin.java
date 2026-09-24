package mekanism.common.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mekanism.common.mixinhelper.PortalFrameBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PortalShape.class)
public class PortalShapeMixin {
    @Definition(id = "FRAME", field = "Lnet/minecraft/world/level/portal/PortalShape;FRAME:Lnet/minecraft/world/level/block/state/BlockBehaviour$StatePredicate;")
    @Expression("FRAME = @(?)")
    @ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static BlockBehaviour.StatePredicate checkForCustomPortalFrames(BlockBehaviour.StatePredicate original) {
        return (blockState, blockGetter, blockPos) -> {
            if (blockState.getBlock() instanceof PortalFrameBlock portalFrameBlock && portalFrameBlock.isPortalFrame(blockState, blockGetter, blockPos)) {
                return true;
            } else {
                return original.test(blockState, blockGetter, blockPos);
            }
        };
    }
}
