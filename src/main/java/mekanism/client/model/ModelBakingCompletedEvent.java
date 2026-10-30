package mekanism.client.model;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface ModelBakingCompletedEvent {
    Event<ModelBakingCompletedEvent> EVENT = EventFactory.createArrayBacked(ModelBakingCompletedEvent.class, events -> (manager, bakery, bakedRegistry) -> {
        for(ModelBakingCompletedEvent event : events) {
            event.onModelBakingCompleted(manager, bakery, bakedRegistry);
        }
    });

    void onModelBakingCompleted(ModelManager manager, ModelBakery bakery, Map<ResourceLocation, BakedModel> bakedRegistry);
}
