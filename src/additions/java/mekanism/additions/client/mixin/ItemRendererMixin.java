package mekanism.additions.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.additions.client.model.AdditionsModelCache;
import mekanism.additions.common.item.ItemBalloon;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    private BakedModel modifyModel(BakedModel original, ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel) {
        if(itemStack.getItem() instanceof ItemBalloon) {
            if(itemDisplayContext == ItemDisplayContext.GUI) {
                return AdditionsModelCache.INSTANCE.BALLOON_GUI.getBakedModel();
            }
            if(itemDisplayContext == ItemDisplayContext.GROUND || itemDisplayContext == ItemDisplayContext.FIXED) {
                return AdditionsModelCache.INSTANCE.BALLOON_FIXED.getBakedModel();
            }
        }
        return original;
    }
}
