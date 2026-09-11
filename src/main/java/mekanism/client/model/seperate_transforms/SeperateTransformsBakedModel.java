package mekanism.client.model.seperate_transforms;


import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SeperateTransformsBakedModel implements BakedModel {

    BakedModel bake;
    Map<ItemDisplayContext, BakedModel> bakedPerspectives;
    public SeperateTransformsBakedModel(BakedModel bake, Map<ItemDisplayContext, BakedModel> bakedPerspectives) {
        this.bake = bake;
        this.bakedPerspectives = bakedPerspectives;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        return bake.getQuads(blockState, direction, randomSource);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return bake.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return bake.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return bake.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return bake.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return bake.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return bake.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return bake.getOverrides();
    }

    public BakedModel getPerspectiveModel(ItemDisplayContext context) {
        return bakedPerspectives.getOrDefault(context, bake);
    }
}