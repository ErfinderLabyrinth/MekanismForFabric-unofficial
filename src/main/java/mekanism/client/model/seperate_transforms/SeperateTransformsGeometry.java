package mekanism.client.model.seperate_transforms;

import mekanism.client.model.CustomGeometry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class SeperateTransformsGeometry extends CustomGeometry {
    BlockModel base;
    Map<ItemDisplayContext, BlockModel> perspectives;
    public SeperateTransformsGeometry(BlockModel base, Map<ItemDisplayContext, BlockModel> perspectives) {
        this.base = base;
        this.perspectives = perspectives;
    }

    @Override
    public BakedModel bake(BlockModel origin, @Nullable Set<String> parts, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation, BakedModel alreadyBaked) {
        Map<ItemDisplayContext, BakedModel> bakedPerspectives = new HashMap<>();

        for (Map.Entry<ItemDisplayContext, BlockModel> itemDisplayContextBlockModelEntry : perspectives.entrySet()) {
            bakedPerspectives.put(itemDisplayContextBlockModelEntry.getKey(), itemDisplayContextBlockModelEntry.getValue().bake(baker, spriteGetter, modelTransform, modelLocation));
        }

        return new SeperateTransformsBakedModel(base.bake(baker, spriteGetter, modelTransform, modelLocation), bakedPerspectives);
    }
}
