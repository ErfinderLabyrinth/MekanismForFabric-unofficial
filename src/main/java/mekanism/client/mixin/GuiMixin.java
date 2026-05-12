package mekanism.client.mixin;

import mekanism.client.ClientRegistration;
import mekanism.client.render.hud.MekaSuitEnergyLevel;
import mekanism.client.render.hud.MekanismHUD;
import mekanism.client.render.hud.MekanismStatusOverlay;
import mekanism.client.render.hud.RadiationOverlay;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow
    private int screenWidth;

    @Shadow
    private int screenHeight;

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    public void beforeRenderCrosshair(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (ClientRegistration.RENDER_TICK_HANDLER.renderCrosshair()) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V"))
    public void afterRenderHotbar(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        MekanismHUD.INSTANCE.render((Gui) (Object)this, guiGraphics, f, screenWidth, screenHeight);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V"))
    public void afterRenderSpectatorHotbar(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        MekanismHUD.INSTANCE.render((Gui) (Object)this, guiGraphics, f, screenWidth, screenHeight);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    public void afterItemName(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        MekanismStatusOverlay.INSTANCE.render((Gui) (Object)this, guiGraphics, f, screenWidth, screenHeight);
    }

    @Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHearts(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V", shift = At.Shift.BEFORE))
    public void afterArmor(GuiGraphics guiGraphics, CallbackInfo ci) {
        MekaSuitEnergyLevel.INSTANCE.render((Gui) (Object)this, guiGraphics, /*Doesn't need*/0, screenWidth, screenHeight);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getDeltaFrameTime()F", shift = At.Shift.AFTER))
    public void beforeAll(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        RadiationOverlay.INSTANCE.render((Gui) (Object)this, guiGraphics, f, screenWidth, screenHeight);
    }
}
