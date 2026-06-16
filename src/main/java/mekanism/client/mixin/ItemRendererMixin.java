package mekanism.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mekanism.client.RobitSpriteUploader;
import mekanism.client.model.robit.RobitBakedModel;
import mekanism.client.model.robit.RobitModelDataBakedModel;
import mekanism.common.item.ItemRobit;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
//    @WrapOperation(method = "renderModelLists", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;)Ljava/util/List;"))
//    private List<BakedQuad> fixRobitRendering(BakedModel instance, BlockState state, Direction direction, RandomSource randomSource, Operation<List<BakedQuad>> original, @Local(argsOnly = true) ItemStack itemStack) {
//        if (itemStack.getItem() instanceof ItemRobit robit && instance instanceof RobitBakedModel robitBakedModel) {
//            return robitBakedModel.getQuads(state, direction, randomSource, robit.getRobitSkin(itemStack));
//        }
//        return original.call(instance, state, direction, randomSource);
//    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemBlockRenderTypes;getRenderType(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/client/renderer/RenderType;"))
    public RenderType replaceRenderTypeForRobit(ItemStack itemStack, boolean bl, Operation<RenderType> original, @Local(argsOnly = true) BakedModel bakedModel) {
        if (bakedModel instanceof RobitModelDataBakedModel) {
            return RobitSpriteUploader.RENDER_TYPE;
        }
        return original.call(itemStack, bl);
    }
}
