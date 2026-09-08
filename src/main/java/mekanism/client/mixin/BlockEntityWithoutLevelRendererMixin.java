package mekanism.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.render.RenderPropertiesProvider;
import mekanism.client.render.armor.ISpecialGearGetter;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {
    @Inject(method = "renderByItem", at = @At("RETURN"))
    private void renderCustomItems(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo ci) {
        if (itemStack.getItem() instanceof RenderPropertiesProvider.MekRenderPropertiesGetter getter) {
            getter.getRenderProperties().getCustomRenderer().renderByItem(itemStack, itemDisplayContext, poseStack, multiBufferSource, i, j);
        }else if (itemStack.getItem() instanceof ISpecialGearGetter getter && getter.getSpecialGear() instanceof RenderPropertiesProvider.MekRenderProperties properties) {
            properties.getCustomRenderer().renderByItem(itemStack, itemDisplayContext, poseStack, multiBufferSource, i, j);
        }
    }
}
