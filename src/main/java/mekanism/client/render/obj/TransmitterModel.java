package mekanism.client.render.obj;

import mekanism.client.model.CustomGeometry;
import mekanism.client.model.obj.ObjModel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

public class TransmitterModel extends CustomGeometry {

    private final ObjModel internal;
    @Nullable
    private final ObjModel glass;

    public TransmitterModel(ObjModel internalModel, @Nullable ObjModel glass) {
        this.internal = internalModel;
        this.glass = glass;
    }

    @Override
    public BakedModel bake(BlockModel blockModel, @Nullable Set<String> parts, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
                           ItemOverrides overrides, ResourceLocation modelLocation, BakedModel alreadyBaked) {
        return new TransmitterBakedModel(internal.bake(blockModel, spriteGetter), glass == null ? null : glass.bake(blockModel, spriteGetter), baker, spriteGetter, modelTransform, overrides, modelLocation, blockModel, alreadyBaked);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter) {
//        internal.resolveParents(modelGetter);
//        if (glass != null) {
//            glass.resolveParents(modelGetter);
//        }
    }

    @Override
    public CustomGeometry clone() {
        return new TransmitterModel(this.internal, this.glass);
    }
}