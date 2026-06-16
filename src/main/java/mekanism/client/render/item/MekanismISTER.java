package mekanism.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class MekanismISTER extends BlockEntityWithoutLevelRenderer implements IdentifiableResourceReloadListener {

    protected MekanismISTER() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    protected EntityModelSet getEntityModels() {
        //Just have this method as a helper for what we pass as entity models rather than bothering to
        // use an AT to access it directly
        return Minecraft.getInstance().getEntityModels();
    }

    protected BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
        //Just have this method as a helper for what we pass as the block entity render dispatcher
        // rather than bothering to use an AT to access it directly
        return Minecraft.getInstance().getBlockEntityRenderDispatcher();
    }

    protected Camera getCamera() {
        return getBlockEntityRenderDispatcher().camera;
    }

    @Override
    public abstract void onResourceManagerReload(@NotNull ResourceManager resourceManager);

    @Override
    public abstract void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer,
          int light, int overlayLight);

    /**
     * @implNote Heavily based on/from vanilla's ItemRenderer#render code that calls the renderByItem method on the ISBER
     */
    protected void renderBlockItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer,
                                   int light, int overlayLight/*, ModelData modelData*/, QuadsGetter quadsGetter) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        Block block = blockItem.getBlock();

        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();

        BlockState state = block.defaultBlockState();
        BakedModel model = mc.getModelManager().getBlockModelShaper().getBlockModel(state);

        boolean hasGlint = stack.isEnchanted();
        long seed = 42L;
        RandomSource random = RandomSource.create(seed);

        /*
         * Fabric / Vanilla:
         * - KEINE RenderPasses
         * - KEINE RenderTypes
         * - EIN VertexConsumer
         */
        VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(
                renderer,
                ItemBlockRenderTypes.getRenderType(stack, true),
                true,
                hasGlint
        );

        for (Direction direction : Direction.values()) {
            random.setSeed(seed);
            itemRenderer.renderQuadList(
                    matrix,
                    buffer,
                    quadsGetter.getQuads(model, state, direction, random),
                    stack,
                    light,
                    overlayLight
            );
        }

        random.setSeed(seed);
        itemRenderer.renderQuadList(
                matrix,
                buffer,
                quadsGetter.getQuads(model, state, null, random),
                stack,
                light,
                overlayLight
        );
    }

    public interface QuadsGetter {
        List<BakedQuad> getQuads(BakedModel model, BlockState state, Direction direction, RandomSource random);
    }
}