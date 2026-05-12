package mekanism.client.model.robit;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mekanism.client.model.CustomGeometry;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RobitModel extends CustomGeometry {

    private RobitModel(List<BlockElement> elements) {
        super(elements);
    }

    @Override
    public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
          ItemOverrides overrides, ResourceLocation modelLocation) {
        return new RobitBakedModel(super.bake(baker, spriteGetter, modelTransform, overrides, modelLocation));
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter) {

    }

    /**
     * Mekanism model loader that gets automatically wrapped into a robit baked model
     */
    public static class Loader {

        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @NotNull
        public RobitModel read(@NotNull JsonObject modelContents, @NotNull JsonDeserializationContext ctx) {
            if (!modelContents.has("elements")) {
                throw new JsonParseException("An element model must have an \"elements\" member.");
            }
            List<BlockElement> elements = new ArrayList<>();
            for (JsonElement element : GsonHelper.getAsJsonArray(modelContents, "elements")) {
                elements.add(ctx.deserialize(element, BlockElement.class));
            }
            return new RobitModel(elements);
        }
    }
}
