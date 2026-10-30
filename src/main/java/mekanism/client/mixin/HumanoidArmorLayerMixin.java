package mekanism.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.mixinhelper.HumanoidArmorLayerPartialTicks;
import mekanism.common.mixinhelper.CustomArmorTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
    @Shadow
    @Final
    private static Map<String, ResourceLocation> ARMOR_LOCATION_CACHE;

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/Entity;FFFFFF)V", at = @At("HEAD"))
    private void capturePartialTicks(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, Entity entity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        HumanoidArmorLayerPartialTicks.THREAD_LOCAL.set(h);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/Entity;FFFFFF)V", at = @At("RETURN"))
    private void resetPartialTicks(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, Entity entity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        HumanoidArmorLayerPartialTicks.THREAD_LOCAL.remove();
    }

    @WrapMethod(method = "getArmorLocation")
    public ResourceLocation replaceArmorLocation(ArmorItem armorItem, boolean bl, String string, Operation<ResourceLocation> original) {
        if (armorItem instanceof CustomArmorTexture customArmorTexture) {
            String customTexture = customArmorTexture.getArmorTexture();
            return ARMOR_LOCATION_CACHE.computeIfAbsent(customTexture, ResourceLocation::new);
        }
        return original.call(armorItem, bl, string);
    }
}
