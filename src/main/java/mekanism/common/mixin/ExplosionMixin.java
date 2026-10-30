package mekanism.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mekanism.common.mixinhelper.OnExplodeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Shadow
    @Final
    private Level level;

    @Inject(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public void beforeBlockRemove(boolean bl, CallbackInfo ci, @Local BlockState state, @Local BlockPos pos) {
        if (state.getBlock() instanceof OnExplodeBlock onExplodeBlock) {
            onExplodeBlock.onBlockExploded(state, level, pos, (Explosion) (Object) this);
        }
    }
}
