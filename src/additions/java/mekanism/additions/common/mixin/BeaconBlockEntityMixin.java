package mekanism.additions.common.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mekanism.additions.common.block.plastic.CustomBeaconColor;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {
    @Definition(id = "BeaconBeamBlock", type = BeaconBeamBlock.class)
    @Expression("? instanceof BeaconBeamBlock")
    @WrapOperation(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean allowCustomBeaconColor(Object object, Operation<Boolean> original) {
        return original.call(object) || object instanceof CustomBeaconColor;
    }

    @Definition(id = "BeaconBeamBlock", type = BeaconBeamBlock.class)
    @Expression("(BeaconBeamBlock) ?")
    @WrapOperation(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static BeaconBeamBlock preventCast(Object object, Operation<BeaconBeamBlock> original) {
        if(object instanceof CustomBeaconColor) {
            //save an injection for getColor, will be overwritten anyway
            return (BeaconBeamBlock) Blocks.YELLOW_STAINED_GLASS;
        } else {
            return original.call(object);
        }
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/DyeColor;getTextureDiffuseColors()[F"))
    private static float[] modifyColor(float[] original, @Local Block block) {
        if(block instanceof CustomBeaconColor customBeaconColor) {
            return customBeaconColor.getCustomBeaconColor();
        } else {
            return original;
        }
    }
}
