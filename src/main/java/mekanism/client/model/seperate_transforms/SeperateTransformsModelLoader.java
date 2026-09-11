package mekanism.client.model.seperate_transforms;

import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SeperateTransformsModelLoader {
    public static final SeperateTransformsModelLoader INSTANCE = new SeperateTransformsModelLoader();

    public SeperateTransformsGeometry read(@NotNull JsonObject jsonObject) {
        BlockModel base = BlockModel.GSON.fromJson(jsonObject.get("base"), BlockModel.class);

        JsonObject perspectiveData = jsonObject.getAsJsonObject("perspective");

        Map<ItemDisplayContext, BlockModel> perspectives = new HashMap<>();
        for (ItemDisplayContext transform : ItemDisplayContext.values())
        {
            if (perspectiveData.has(transform.getSerializedName()))
            {
                BlockModel perspectiveModel = BlockModel.GSON.fromJson(jsonObject.get(transform.getSerializedName()), BlockModel.class);
                perspectives.put(transform, perspectiveModel);
            }
        }


        return new SeperateTransformsGeometry(base, perspectives);
    }
}
