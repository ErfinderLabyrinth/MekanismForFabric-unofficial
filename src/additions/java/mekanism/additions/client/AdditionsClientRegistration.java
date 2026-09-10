package mekanism.additions.client;

import mekanism.additions.client.model.AdditionsModelCache;
import mekanism.additions.client.model.ModelBabyCreeper;
import mekanism.additions.client.render.entity.RenderBabyCreeper;
import mekanism.additions.client.render.entity.RenderBabyEnderman;
import mekanism.additions.client.render.entity.RenderBalloon;
import mekanism.additions.client.render.entity.RenderObsidianTNTPrimed;
import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.item.ItemBalloon;
import mekanism.additions.common.item.ItemWalkieTalkie;
import mekanism.additions.common.registries.AdditionsBlocks;
import mekanism.additions.common.registries.AdditionsEntityTypes;
import mekanism.additions.common.registries.AdditionsItems;
import mekanism.api.text.EnumColor;
import mekanism.client.ClientRegistrationUtil;
import mekanism.client.model.MekanismModelLoadingPlugin;
import mekanism.client.model.ModelBakingCompletedEvent;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.ItemRegistryObject;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.StrayRenderer;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;

import java.util.Map;

public class AdditionsClientRegistration {
    public static void init() {
        ModelBakingCompletedEvent.EVENT.register(AdditionsModelCache.INSTANCE::onBake);
        ClientRegistrationUtil.setPropertyOverride(AdditionsItems.WALKIE_TALKIE, MekanismAdditions.rl("channel"), (stack, world, entity, seed) -> {
            ItemWalkieTalkie item = (ItemWalkieTalkie) stack.getItem();
            return item.getOn(stack) ? item.getChannel(stack) : 0;
        });
        AdditionsKeyHandler.registerKeybindings();
        registerRenderers();
        ModelLoadingPlugin.register(new MekanismModelLoadingPlugin());
        registerLayer();
        registerColorHandlers();
    }

    public static void registerRenderers() {
        //Register entity rendering handlers
        EntityRendererRegistry.register(AdditionsEntityTypes.OBSIDIAN_TNT.get(), RenderObsidianTNTPrimed::new);
        EntityRendererRegistry.register(AdditionsEntityTypes.BALLOON.get(), RenderBalloon::new);
        EntityRendererRegistry.register(AdditionsEntityTypes.BABY_CREEPER.get(), RenderBabyCreeper::new);
        EntityRendererRegistry.register(AdditionsEntityTypes.BABY_ENDERMAN.get(), RenderBabyEnderman::new);
        EntityRendererRegistry.register(AdditionsEntityTypes.BABY_SKELETON.get(), SkeletonRenderer::new);
        EntityRendererRegistry.register(AdditionsEntityTypes.BABY_STRAY.get(), StrayRenderer::new);
        EntityRendererRegistry.register(AdditionsEntityTypes.BABY_WITHER_SKELETON.get(), WitherSkeletonRenderer::new);
    }

    public static void registerLayer() {
        EntityModelLayerRegistry.registerModelLayer(ModelBabyCreeper.CREEPER_LAYER, () -> ModelBabyCreeper.createBodyLayer(CubeDeformation.NONE));
        //Note: Use 1 instead of 2 for size
        EntityModelLayerRegistry.registerModelLayer(ModelBabyCreeper.ARMOR_LAYER, () -> ModelBabyCreeper.createBodyLayer(new CubeDeformation(1)));
    }

    public static void registerColorHandlers() {
        registerIColoredBlocks();
        ItemColor balloonColorHandler = (stack, tintIndex) -> stack.getItem() instanceof ItemBalloon balloon ? MekanismRenderer.getColorARGB(balloon.getColor(), 1) : -1;
        for (ItemRegistryObject<ItemBalloon> balloon : AdditionsItems.BALLOONS.values()) {
            ClientRegistrationUtil.registerItemColorHandler(balloonColorHandler, balloon);
        }
    }

    private static void registerIColoredBlocks() {
        registerBlockColorHandles(AdditionsBlocks.GLOW_PANELS, AdditionsBlocks.PLASTIC_BLOCKS,
              AdditionsBlocks.SLICK_PLASTIC_BLOCKS, AdditionsBlocks.PLASTIC_GLOW_BLOCKS, AdditionsBlocks.REINFORCED_PLASTIC_BLOCKS, AdditionsBlocks.PLASTIC_ROADS,
              AdditionsBlocks.TRANSPARENT_PLASTIC_BLOCKS, AdditionsBlocks.PLASTIC_STAIRS, AdditionsBlocks.PLASTIC_SLABS, AdditionsBlocks.PLASTIC_FENCES,
              AdditionsBlocks.PLASTIC_FENCE_GATES, AdditionsBlocks.PLASTIC_GLOW_STAIRS, AdditionsBlocks.PLASTIC_GLOW_SLABS, AdditionsBlocks.TRANSPARENT_PLASTIC_STAIRS,
              AdditionsBlocks.TRANSPARENT_PLASTIC_SLABS);
    }

    @SafeVarargs
    private static void registerBlockColorHandles(Map<EnumColor, ? extends BlockRegistryObject<?, ?>>... blocks) {
        for (Map<EnumColor, ? extends BlockRegistryObject<?, ?>> blockMap : blocks) {
            for (BlockRegistryObject<?, ?> block : blockMap.values()) {
                ClientRegistrationUtil.registerIColoredBlockHandler(true, block);
                ClientRegistrationUtil.registerIColoredBlockHandler(false, block);
            }
        }
    }
}