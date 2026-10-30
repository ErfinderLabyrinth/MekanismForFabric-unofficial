package mekanism.client.model.obj.model;

import com.google.gson.JsonObject;
import mekanism.client.model.CustomGeometry;
import mekanism.client.model.obj.ObjModel;
import mekanism.client.model.obj.ObjParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class ObjModelLoader {
    public static final ObjModelLoader INSTANCE = new ObjModelLoader();

    public CustomGeometry read(@NotNull JsonObject jsonObject) {
        boolean flipV = false;
        if (jsonObject.has("flip_v") && jsonObject.get("flip_v").isJsonPrimitive() && jsonObject.getAsJsonPrimitive("flip_v").isBoolean()) {
            flipV = jsonObject.get("flip_v").getAsBoolean();
        }
        ObjModel model;
        try {
            ResourceLocation modelRl = new ResourceLocation(jsonObject.get("model").getAsString());
            model = ObjParser.load(Minecraft.getInstance().getResourceManager().open(modelRl), modelRl.withPath(modelRl.getPath().substring(0, modelRl.getPath().lastIndexOf('/'))), flipV);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return createCustomGeometry(model);
    }

    private CustomGeometry createCustomGeometry(ObjModel model) {
        return new CustomGeometry() {
            @Override
            public BakedModel bake(BlockModel origin, @Nullable Set<String> parts, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation, BakedModel alreadyBaked) {
                return model.bake(origin, spriteGetter).allWrapper(alreadyBaked);
            }

            @Override
            public CustomGeometry clone() {
                return createCustomGeometry(model);
            }
        };
    }
}
