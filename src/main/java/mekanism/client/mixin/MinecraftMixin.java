package mekanism.client.mixin;

import mekanism.client.RobitSpriteUploader;
import mekanism.client.render.RenderTickHandler;
import mekanism.common.integration.MekanismHooks;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public Screen screen;

    @Shadow
    public abstract TextureManager getTextureManager();

    @Inject(method = "setScreen", at = @At("HEAD"))
    public void checkIfJEIGuiOpened(Screen screen, CallbackInfo ci) {
        if (FabricLoader.getInstance().isModLoaded(MekanismHooks.JEI_MOD_ID)) {
            RenderTickHandler.guiOpening(this.screen, screen);
        }
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/renderer/entity/ItemRenderer;)V", shift = At.Shift.BEFORE))
    public void registerRobitSpriteUploader(GameConfig gameConfig, CallbackInfo ci) {
        ResourceManagerHelper clientResource = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
        //Robit Texture Atlas
        clientResource.registerReloadListener(new RobitSpriteUploader(getTextureManager()));
    }
}
