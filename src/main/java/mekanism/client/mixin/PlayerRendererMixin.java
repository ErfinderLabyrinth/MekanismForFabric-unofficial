package mekanism.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.ClientRegistration;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Inject(method = {"renderLeftHand"}, at = @At("HEAD"), cancellable = true)
    public void onRenderLeftHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AbstractClientPlayer abstractClientPlayer, CallbackInfo ci) {
        if (ClientRegistration.RENDER_TICK_HANDLER.renderArm(abstractClientPlayer, HumanoidArm.LEFT, poseStack, multiBufferSource, i))
            ci.cancel();
    }

    @Inject(method = {"renderRightHand"}, at = @At("HEAD"), cancellable = true)
    public void onRenderRightHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AbstractClientPlayer abstractClientPlayer, CallbackInfo ci) {
        if (ClientRegistration.RENDER_TICK_HANDLER.renderArm(abstractClientPlayer, HumanoidArm.RIGHT, poseStack, multiBufferSource, i))
            ci.cancel();
    }
}
