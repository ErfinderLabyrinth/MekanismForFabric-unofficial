package mekanism.additions.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mekanism.additions.common.block.BlockObsidianTNT;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    @ModifyExpressionValue(method = "checkBurnOut", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"))
    private Block checkObsidianTnt(Block block) {
        if(block instanceof BlockObsidianTNT) {
            BlockObsidianTNT.OBSIDIAN_TNT_EXPLODING.set(true);
        }
        return block;
    }
}
