package mekanism.client.mixin;

import mekanism.client.model.TextureAtlasStitchEvent;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureAtlas.class)
public class TextureAtlasMixin {
    @Inject(method = "upload", at = @At("TAIL"))
    private void mekanism$onStitch(SpriteLoader.Preparations preparations, CallbackInfo info) {
        TextureAtlasStitchEvent.EVENT.invoker().onTextureAtlasStitch((TextureAtlas) (Object)this);
    }
}
