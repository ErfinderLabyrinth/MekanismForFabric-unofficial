package mekanism.client.model;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface TextureAtlasStitchEvent {
    Event<TextureAtlasStitchEvent> EVENT = EventFactory.createArrayBacked(TextureAtlasStitchEvent.class, events -> (atlas) -> {
        for(TextureAtlasStitchEvent event : events) {
            event.onTextureAtlasStitch(atlas);
        }
    });

    void onTextureAtlasStitch(TextureAtlas atlas);
}
