package mekanism.additions.client;

import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.registries.AdditionsBlocks;
import mekanism.additions.common.registries.AdditionsItems;
import mekanism.api.providers.IItemProvider;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

public class AdditionsItemModelProvider {
    public static void generateItemModels(ItemModelGenerators gen) {
        withParent(gen, AdditionsItems.BALLOONS, "item/balloon");
        withParent(gen, AdditionsBlocks.GLOW_PANELS, "item/glow_panel");
        withParent(gen, AdditionsBlocks.PLASTIC_BLOCKS, "block/plastic/block");
        withParent(gen, AdditionsBlocks.SLICK_PLASTIC_BLOCKS, "block/plastic/slick");
        withParent(gen, AdditionsBlocks.PLASTIC_GLOW_BLOCKS, "block/plastic/glow");
        withParent(gen, AdditionsBlocks.REINFORCED_PLASTIC_BLOCKS, "block/plastic/reinforced");
        withParent(gen, AdditionsBlocks.PLASTIC_ROADS, "block/plastic/glow");
        withParent(gen, AdditionsBlocks.TRANSPARENT_PLASTIC_BLOCKS, "block/plastic/transparent");
        withParent(gen, AdditionsBlocks.PLASTIC_STAIRS, "block/plastic/stairs");
        withParent(gen, AdditionsBlocks.PLASTIC_SLABS, "block/plastic/slab");
        withParent(gen, AdditionsBlocks.PLASTIC_FENCES, "block/plastic/fence_inventory");
        withParent(gen, AdditionsBlocks.PLASTIC_FENCE_GATES, "block/plastic/fence_gate");
        withParent(gen, AdditionsBlocks.PLASTIC_GLOW_STAIRS, "block/plastic/glow_stairs");
        withParent(gen, AdditionsBlocks.PLASTIC_GLOW_SLABS, "block/plastic/glow_slab");
        withParent(gen, AdditionsBlocks.TRANSPARENT_PLASTIC_STAIRS, "block/plastic/transparent_stairs");
        withParent(gen, AdditionsBlocks.TRANSPARENT_PLASTIC_SLABS, "block/plastic/transparent_slab");
    }

    private static void withParent(ItemModelGenerators gen, Map<?, ? extends IItemProvider> items, String modelName) {
        ModelTemplate parent = new ModelTemplate(Optional.of(MekanismAdditions.rl(modelName)), Optional.empty());
        for (IItemProvider item : items.values()) {
            ResourceLocation model = ModelLocationUtils.getModelLocation(item.asItem());
            parent.create(model, new TextureMapping(), gen.output);
        }
    }
}