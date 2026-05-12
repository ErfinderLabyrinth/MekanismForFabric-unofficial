package mekanism.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import mekanism.client.ClientRegistration;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Shadow
    private static float fogRed;

    @Shadow
    private static float fogBlue;

    @Shadow
    private static float fogGreen;

    @Inject(method = "setupColor", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;getNightVisionScale(Lnet/minecraft/world/entity/LivingEntity;F)F")), at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V", shift = At.Shift.BEFORE))
    private static void fogColor(Camera camera, float f, ClientLevel clientLevel, int i, float g, CallbackInfo ci) {
        Vector3f changeTo = ClientRegistration.TICK_HANDLER.onFogLighting(new Vector3f(fogRed, fogGreen, fogBlue));
        fogRed = changeTo.x;
        fogGreen = changeTo.y;
        fogBlue = changeTo.z;
    }

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void renderFog(Camera camera, FogRenderer.FogMode fogMode, float f, boolean bl, float g, CallbackInfo ci, @Local FogRenderer.FogData fogData, @Local FogType fogType) {
        Map.Entry<Float, Float> result = ClientRegistration.TICK_HANDLER.onFog(camera, fogData.start, fogData.end, fogType);
        RenderSystem.setShaderFogStart(result.getKey());
        RenderSystem.setShaderFogEnd(result.getValue());
    }
}
