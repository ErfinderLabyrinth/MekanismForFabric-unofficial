package mekanism.client.model.composite;

import com.mojang.datafixers.util.Either;
import mekanism.client.mixinhelper.RenderTypeHolder;
import mekanism.client.model.CustomGeometry;
import mekanism.common.registration.impl.CreativeTabDeferredRegister;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class CompositeGeometry extends CustomGeometry {
    Map<String, BlockModel> children;
    public CompositeGeometry(Map<String, BlockModel> children) {
        this.children = children;
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter) {
        for (BlockModel child : children.values()) {
            child.resolveParents(modelGetter);
        }
    }

    @Override
    public BakedModel bake(BlockModel origin, @Nullable Set<String> parts, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation, BakedModel alreadyBaked) {
//        BlockModel fullModel = new BlockModel(origin.parentLocation, combine(origin.getElements(), getElements()), combine(origin.textureMap, getTextureMap()), origin.hasAmbientOcclusion(), origin.getGuiLight(), origin.getTransforms(), origin.getOverrides());
        List<BakedModel> children = new ArrayList<>();
        List<BakedModel> childrenCutout = new ArrayList<>();
        List<BakedModel> childrenTranslucent = new ArrayList<>();

        for (BlockModel value : this.children.values()) {
            BakedModel baked = value.bake(baker, spriteGetter, modelTransform, modelLocation);
            String renderType = ((RenderTypeHolder)value).mekanism$getRenderType();
            if ("minecraft:cutout".equals(renderType)) {
                childrenCutout.add(baked);
            }else if("minecraft:translucent".equals(renderType)) {
                childrenTranslucent.add(baked);
            }else {
                children.add(baked);
            }
        }

        return new CompositeBakedModel(alreadyBaked, children, childrenCutout, childrenTranslucent);
    }

    private List<BlockElement> combine(List<BlockElement> elements, List<BlockElement> elements1) {
        ArrayList<BlockElement> combined = new ArrayList<>(elements);
        combined.addAll(elements1);
        return combined;
    }

    private Map<String, Either<Material, String>> combine(Map<String, Either<Material, String>> textureMap, Map<String, Either<Material, String>> textureMap1) {
        HashMap<String, Either<Material, String>> combined = new HashMap<>(textureMap);
        combined.putAll(textureMap1);
        return combined;
    }

    private List<BlockElement> getElements() {
        ArrayList<BlockElement> elements = new ArrayList<>();
        for (BlockModel child : children.values()) {
            elements.addAll(child.getElements());
        }
        return elements;
    }

    private Map<String, Either<Material, String>> getTextureMap() {
        HashMap<String, Either<Material, String>> textureMap = new HashMap<>();
        for (BlockModel child : children.values()) {
            while(child != null) {
                textureMap.putAll(child.textureMap);
                child = child.parent;
            }
        }
        return textureMap;
    }

    @Override
    public CustomGeometry clone() {
        return new CompositeGeometry(this.children);
    }
}
