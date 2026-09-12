package mekanism.client.model.obj;


import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BakedObjModel {

    private final Map<String, List<BakedQuad>> objectQuads;

    public BakedObjModel(Map<String, List<BakedQuad>> objectQuads) {
        this.objectQuads = objectQuads;
    }

    public List<BakedQuad> getQuads(Set<String> objects) {

        List<BakedQuad> out = new ArrayList<>();

        for (String o : objects) {
            List<BakedQuad> q = objectQuads.get(o);
            if (q != null) out.addAll(q);
        }

        return out;
    }

    public List<BakedQuad> getAllQuads() {
        return objectQuads.values()
                .stream()
                .flatMap(List::stream)
                .toList();
    }

    public boolean hasObject(String objectName) {
        return objectQuads.containsKey(objectName);
    }

    public BakedModel allWrapper(BakedModel bakedOwner) {
        return new BakedModel() {
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
                return BakedObjModel.this.getAllQuads();
            }

            @Override
            public boolean useAmbientOcclusion() {
                return bakedOwner.useAmbientOcclusion();
            }

            @Override
            public boolean isGui3d() {
                return bakedOwner.isGui3d();
            }

            @Override
            public boolean usesBlockLight() {
                return bakedOwner.usesBlockLight();
            }

            @Override
            public boolean isCustomRenderer() {
                return false;
            }

            @Override
            public TextureAtlasSprite getParticleIcon() {
                return bakedOwner.getParticleIcon();
            }

            @Override
            public ItemTransforms getTransforms() {
                return bakedOwner.getTransforms();
            }

            @Override
            public ItemOverrides getOverrides() {
                return bakedOwner.getOverrides();
            }
        };
    }

    public BakedModel wrapper(Set<String> objects, BakedModel bakedOwner) {
        return new BakedModel() {
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
                return BakedObjModel.this.getQuads(objects);
            }

            @Override
            public boolean useAmbientOcclusion() {
                return bakedOwner.useAmbientOcclusion();
            }

            @Override
            public boolean isGui3d() {
                return bakedOwner.isGui3d();
            }

            @Override
            public boolean usesBlockLight() {
                return bakedOwner.usesBlockLight();
            }

            @Override
            public boolean isCustomRenderer() {
                return false;
            }

            @Override
            public TextureAtlasSprite getParticleIcon() {
                return bakedOwner.getParticleIcon();
            }

            @Override
            public ItemTransforms getTransforms() {
                return bakedOwner.getTransforms();
            }

            @Override
            public ItemOverrides getOverrides() {
                return bakedOwner.getOverrides();
            }
        };
    }
}