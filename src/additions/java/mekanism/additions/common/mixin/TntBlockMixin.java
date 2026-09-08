package mekanism.additions.common.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import mekanism.additions.common.block.BlockObsidianTNT;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TntBlock.class)
public class TntBlockMixin {
    @WrapMethod(method = "explode(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)V")
    private static void explode(Level level, BlockPos blockPos, LivingEntity livingEntity, Operation<Void> original) {
        if(level.getBlockState(blockPos).getBlock() instanceof BlockObsidianTNT) {
            BlockObsidianTNT.explode(level, blockPos, livingEntity);
        } else {
            original.call(level, blockPos, livingEntity);
        }
    }
}
