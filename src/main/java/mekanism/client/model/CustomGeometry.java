package mekanism.client.model;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Function;

public abstract class CustomGeometry {
    List<BlockElement> elements;
    protected boolean gui3d = true;
    public CustomGeometry(List<BlockElement> elements) {
        this.elements = elements;
    }

    public CustomGeometry() {
        this(List.of());
    }

    public BakedModel bake(BlockModel origin, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
                           ItemOverrides overrides, ResourceLocation modelLocation, BakedModel alreadyBaked) {
        return alreadyBaked;
    }

    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter) {}

    public void setGui3d(boolean gui3d) {
        this.gui3d = gui3d;
    }
}
