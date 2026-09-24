package mekanism.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mekanism.common.Mekanism;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @ModifyReturnValue(method = "getDestroySpeed", at = @At("TAIL"))
    public float onLivingEntityActuallyHurt(float original, BlockState blockState) {
        return Mekanism.commonPlayerTickHandler.getBreakSpeed((Player) (Object) this, original, blockState, Optional.empty());
    }
}
