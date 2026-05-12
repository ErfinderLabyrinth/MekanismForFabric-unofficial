package mekanism.client.mixin;

import mekanism.client.render.RenderTickHandler;
import mekanism.common.integration.MekanismHooks;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    @Nullable
    public Screen screen;

    @Inject(method = "setScreen", at = @At("HEAD"))
    public void checkIfJEIGuiOpened(Screen screen, CallbackInfo ci) {
        if (FabricLoader.getInstance().isModLoaded(MekanismHooks.JEI_MOD_ID)) {
            RenderTickHandler.guiOpening(this.screen, screen);
        }
    }
}
