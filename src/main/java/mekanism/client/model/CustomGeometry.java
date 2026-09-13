package mekanism.client.model;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public abstract class CustomGeometry {
    protected boolean gui3d = true;

    public CustomGeometry() {
    }

    public BakedModel bake(BlockModel origin, @Nullable Set<String> parts, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
                           ItemOverrides overrides, ResourceLocation modelLocation, BakedModel alreadyBaked) {
        return alreadyBaked;
    }

    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter) {}

    public void setGui3d(boolean gui3d) {
        this.gui3d = gui3d;
    }

    public abstract CustomGeometry clone();
}
