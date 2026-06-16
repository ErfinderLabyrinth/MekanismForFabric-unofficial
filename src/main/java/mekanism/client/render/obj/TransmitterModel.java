package mekanism.client.render.obj;

import mekanism.client.model.CustomGeometry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TransmitterModel extends CustomGeometry {

    private final UnbakedModel internal;
    @Nullable
    private final UnbakedModel glass;

    public TransmitterModel(UnbakedModel internalModel, @Nullable UnbakedModel glass) {
        this.internal = internalModel;
        this.glass = glass;
    }

    @Override
    public BakedModel bake(BlockModel blockModel, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
                           ItemOverrides overrides, ResourceLocation modelLocation, BakedModel alreadyBaked) {
        return new TransmitterBakedModel(internal, glass, baker, spriteGetter, modelTransform, overrides, modelLocation);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter) {
        internal.resolveParents(modelGetter);
        if (glass != null) {
            glass.resolveParents(modelGetter);
        }
    }
}