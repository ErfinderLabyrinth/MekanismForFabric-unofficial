package mekanism.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.ClientRegistration;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private int ticks;

    @Inject(method = "renderChunkLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;clearRenderState()V", shift = At.Shift.BEFORE))
    public void afterChunkLayerRendering(RenderType renderType, PoseStack poseStack, double d, double e, double f, Matrix4f matrix4f, CallbackInfo ci) {
        if (renderType == RenderType.translucent()) {
            ClientRegistration.RENDER_TICK_HANDLER.renderAfterTranslucentBlocks(minecraft.gameRenderer.getMainCamera(), poseStack, ticks);

        }
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;F)V", shift = At.Shift.AFTER))
    public void afterChunkLayerRendering(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        ClientRegistration.RENDER_TICK_HANDLER.renderAfterParticles(minecraft.gameRenderer.getMainCamera(), poseStack, ticks);
    }
}
