package mekanism.client.model.item_layers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemLayersModelLoader {
    public static final ItemLayersModelLoader INSTANCE = new ItemLayersModelLoader();

    public ItemLayersGeometry read(@NotNull JsonObject jsonObject) {
        JsonObject dataJson = jsonObject.getAsJsonObject("mekanism_data");
        if (dataJson.has("full_light_layers") && dataJson.get("full_light_layers").isJsonArray()) {
            List<JsonElement> fullLightLayers = dataJson.getAsJsonArray("full_light_layers").asList();
            List<Integer> layers = new ArrayList<>();
            for (JsonElement fullLightLayer : fullLightLayers) {
                if (fullLightLayer.isJsonPrimitive() && fullLightLayer.getAsJsonPrimitive().isNumber()) {
                    layers.add(fullLightLayer.getAsInt());
                }
            }
            return new ItemLayersGeometry(layers);
        }else {
            return null;
        }
    }
}
