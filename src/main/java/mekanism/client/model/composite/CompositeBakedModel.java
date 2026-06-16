package mekanism.client.model.composite;

import mekanism.client.mixinhelper.RenderTypeHolder;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CompositeBakedModel implements BakedModel {
    private static final RenderMaterial CUTOUT_MATERIAL =
            RendererAccess.INSTANCE.getRenderer()
                    .materialFinder()
                    .blendMode(BlendMode.CUTOUT)
                    .find();

    private static final RenderMaterial TRANSLUCENT_MATERIAL =
            RendererAccess.INSTANCE.getRenderer()
                    .materialFinder()
                    .blendMode(BlendMode.TRANSLUCENT)
                    .find();

    BakedModel origin;
    List<BakedModel> children, cutoutChildren, translucentChildren;
    CompositeBakedModel(BakedModel origin, List<BakedModel> children, List<BakedModel> cutoutChildren, List<BakedModel> translucentChildren) {
        this.origin = origin;
        this.children = children;
        this.cutoutChildren = cutoutChildren;
        this.translucentChildren = translucentChildren;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        List<BakedQuad> quads = new ArrayList<>();
        for (BakedModel child : children) {
            quads.addAll(child.getQuads(blockState, direction, randomSource));
        }
        for (BakedModel child : cutoutChildren) {
            quads.addAll(child.getQuads(blockState, direction, randomSource));
        }
        for (BakedModel child : translucentChildren) {
            quads.addAll(child.getQuads(blockState, direction, randomSource));
        }
        quads.addAll(origin.getQuads(blockState, direction, randomSource));
        return quads;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        origin.emitBlockQuads(blockView, state, pos, randomSupplier, context);

        for (BakedModel child : children) {
            child.emitBlockQuads(
                            blockView,
                            state,
                            pos,
                            randomSupplier,
                            context
                    );

            context.popTransform();
        }

        for (BakedModel cutoutChild : cutoutChildren) {
            context.pushTransform(quad -> {
                quad.material(CUTOUT_MATERIAL);
                return true;
            });

            cutoutChild.emitBlockQuads(
                    blockView,
                    state,
                    pos,
                    randomSupplier,
                    context
            );

            context.popTransform();
        }

        for (BakedModel translucentChild : translucentChildren) {
            context.pushTransform(quad -> {
                quad.material(TRANSLUCENT_MATERIAL);
                return true;
            });

            translucentChild.emitBlockQuads(
                    blockView,
                    state,
                    pos,
                    randomSupplier,
                    context
            );

            context.popTransform();
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        origin.emitItemQuads(stack, randomSupplier, context);

        for (BakedModel child : children) {
            child.emitItemQuads(
                    stack,
                    randomSupplier,
                    context
            );

            context.popTransform();
        }

        for (BakedModel cutoutChild : cutoutChildren) {
            context.pushTransform(quad -> {
                quad.material(CUTOUT_MATERIAL);
                return true;
            });

            cutoutChild.emitItemQuads(
                    stack,
                    randomSupplier,
                    context
            );

            context.popTransform();
        }

        for (BakedModel translucentChild : translucentChildren) {
            context.pushTransform(quad -> {
                quad.material(TRANSLUCENT_MATERIAL);
                return true;
            });

            translucentChild.emitItemQuads(
                    stack,
                    randomSupplier,
                    context
            );

            context.popTransform();
        }
    }

    @Override
    public boolean useAmbientOcclusion() {
        return origin.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return origin.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return origin.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return origin.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return origin.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return origin.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return origin.getOverrides();
    }
}
