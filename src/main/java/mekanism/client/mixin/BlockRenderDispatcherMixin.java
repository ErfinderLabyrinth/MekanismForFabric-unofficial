package mekanism.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.render.RenderPropertiesProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockRenderDispatcher.class)
public class BlockRenderDispatcherMixin {
    @WrapOperation(method = "renderSingleBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockEntityWithoutLevelRenderer;renderByItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"))
    public void replaceBlockEntityRenderer(BlockEntityWithoutLevelRenderer instance, ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, Operation<Void> original) {
        if (itemStack.getItem() instanceof RenderPropertiesProvider.MekRenderPropertiesGetter getter) {
            getter.getRenderProperties().getCustomRenderer().renderByItem(itemStack, itemDisplayContext, poseStack, multiBufferSource, i, j);
        }else {
            original.call(instance, itemStack, itemDisplayContext, poseStack, multiBufferSource, i, j);
        }
    }
}
