package mekanism.generators.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mekanism.generators.common.GeneratorTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = {"net.minecraft.world.entity.monster.EnderMan$EndermanLeaveBlockGoal"})
public class EnderMan$EndermanLeaveBlockGoalMixin {
    @ModifyReturnValue(method = "canPlaceBlock", at = @At("RETURN"))
    private boolean preventBlockPlace(boolean original, Level level, BlockPos blockPos, BlockState place, BlockState placeIn, BlockState placeOn, BlockPos blockPos2) {
        return original && !placeOn.is(GeneratorTags.Blocks.ENDERMAN_CANNOT_PLACE_ON);
    }
}

