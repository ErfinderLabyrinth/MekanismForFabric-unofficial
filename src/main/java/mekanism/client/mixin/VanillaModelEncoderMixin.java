package mekanism.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mekanism.client.model.energycube.EnergyCubeBakedModel;
import mekanism.common.mixin.AbstractBlockRenderContextMixin;
import mekanism.common.tile.TileEntityEnergyCube;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.fabricmc.fabric.impl.renderer.VanillaModelEncoder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(VanillaModelEncoder.class)
public class VanillaModelEncoderMixin {
    @WrapOperation(method = "emitBlockQuads", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;)Ljava/util/List;"))
    private static List<BakedQuad> mekansim$renderEnergyCubeCorrectly(BakedModel instance, BlockState state, Direction direction, RandomSource randomSource, Operation<List<BakedQuad>> original, @Local(argsOnly = true) RenderContext context) {
        if (instance instanceof EnergyCubeBakedModel energyCubeBakedModel && context instanceof AbstractBlockRenderContext blockRenderContext) {
            BlockEntity blockEntity = ((AbstractBlockRenderContextMixin)blockRenderContext).getBlockInfo().blockView.getBlockEntity(((AbstractBlockRenderContextMixin)blockRenderContext).getBlockInfo().blockPos);
            if (blockEntity instanceof TileEntityEnergyCube energyCube) {
                return energyCubeBakedModel.getQuads(state, direction, randomSource, energyCube.getSideStates());
            }
        }
        return original.call(instance, state, direction, randomSource);
    }
}
