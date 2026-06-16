package mekanism.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mekanism.client.model.energycube.EnergyCubeBakedModel;
import mekanism.common.tile.TileEntityEnergyCube;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {
    @WrapOperation(method = {"tesselateWithAO", "tesselateWithoutAO"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;)Ljava/util/List;"))
    public List<BakedQuad> mekansim$renderEnergyCubeCorrectly(BakedModel instance, @Nullable BlockState state, @Nullable Direction direction, RandomSource randomSource, Operation<List<BakedQuad>> original, @Local(argsOnly = true) BlockAndTintGetter blockAndTintGetter, @Local(argsOnly = true) BlockPos pos) {
        if (instance instanceof EnergyCubeBakedModel energyCubeBakedModel) {
            BlockEntity blockEntity = blockAndTintGetter.getBlockEntity(pos);
            if (blockEntity instanceof TileEntityEnergyCube energyCube) {
                return energyCubeBakedModel.getQuads(state, direction, randomSource, energyCube.getSideStates());
            }
        }
        return original.call(instance, state, direction, randomSource);
    }
}
