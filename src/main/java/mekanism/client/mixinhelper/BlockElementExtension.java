package mekanism.client.mixinhelper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface BlockElementExtension {
    int mekanism$getBlockLight();
    int mekanism$getSkyLight();

    void mekanism$setLight(int blockLight, int skyLight);

    static void loadExtensionData(JsonElement jsonElement, BlockElementExtension original) {
        JsonObject object = jsonElement.getAsJsonObject();
        if(object.has("mekanism:data")) {
            JsonObject data = object.getAsJsonObject("mekanism:data");
            int blockLight = data.get("block_light").getAsInt();
            int skyLight = data.get("sky_light").getAsInt();
            original.mekanism$setLight(blockLight, skyLight);
        }
    }
}
