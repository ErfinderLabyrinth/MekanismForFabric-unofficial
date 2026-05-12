package mekanism.client.model.baked;

import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@NothingNullByDefault
public class ModelDataBakedModel implements BakedModel {

    private final BakedModel original;
    private final List<BakedModel> renderPasses;

    public ModelDataBakedModel(BakedModel original) {
        this.original = original;
        this.renderPasses = Collections.singletonList(this);
    }

    @Override
    @Deprecated
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return original.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return original.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return original.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return original.isCustomRenderer();
    }

    @Override
    @Deprecated
    public TextureAtlasSprite getParticleIcon() {
        return getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return original.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

//    @Override
//    public BakedModel applyTransform(ItemDisplayContext displayContext, PoseStack mat, boolean applyLeftHandTransform) {
//        // have the original model apply any perspective transforms onto the MatrixStack
//        super.applyTransform(displayContext, mat, applyLeftHandTransform);
//        // return this model, as we want to draw the item variant quads ourselves
//        return this;
//    }
//
//    @Override
//    public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous) {
//        //Make sure our model is the one that gets rendered rather than the internal one
//        return renderPasses;
//    }


}