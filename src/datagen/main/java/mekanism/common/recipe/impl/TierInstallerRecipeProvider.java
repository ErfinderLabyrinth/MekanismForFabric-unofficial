package mekanism.common.recipe.impl;

import java.util.function.Consumer;
import mekanism.common.Mekanism;
import mekanism.common.item.ItemTierInstaller;
import mekanism.common.recipe.ISubRecipeProvider;
import mekanism.common.recipe.builder.ExtendedShapedRecipeBuilder;
import mekanism.common.recipe.pattern.Pattern;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.registries.MekanismItems;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import mekanism.common.tags.MekanismTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

class TierInstallerRecipeProvider implements ISubRecipeProvider {

    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        String basePath = "tier_installer/";
        addTierInstallerRecipe(consumer, basePath, MekanismItems.BASIC_TIER_INSTALLER, ConventionalItemTags.IRON_INGOTS, MekanismTags.Items.ALLOYS_BASIC, MekanismTags.Items.CIRCUITS_BASIC);
        addTierInstallerRecipe(consumer, basePath, MekanismItems.ADVANCED_TIER_INSTALLER, MekanismTags.Items.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.OSMIUM), MekanismTags.Items.ALLOYS_INFUSED, MekanismTags.Items.CIRCUITS_ADVANCED);
        addTierInstallerRecipe(consumer, basePath, MekanismItems.ELITE_TIER_INSTALLER, ConventionalItemTags.GOLD_INGOTS, MekanismTags.Items.ALLOYS_REINFORCED, MekanismTags.Items.CIRCUITS_ELITE);
        addTierInstallerRecipe(consumer, basePath, MekanismItems.ULTIMATE_TIER_INSTALLER, ConventionalItemTags.DIAMONDS, MekanismTags.Items.ALLOYS_ATOMIC, MekanismTags.Items.CIRCUITS_ULTIMATE);
    }

    private void addTierInstallerRecipe(Consumer<FinishedRecipe> consumer, String basePath, ItemRegistryObject<ItemTierInstaller> tierInstaller, TagKey<Item> ingotTag,
          TagKey<Item> alloyTag, TagKey<Item> circuitTag) {
        ExtendedShapedRecipeBuilder.shapedRecipe(tierInstaller)
              .pattern(MekanismRecipeProvider.TIER_PATTERN)
              .key(Pattern.PREVIOUS, ItemTags.PLANKS)
              .key(Pattern.CIRCUIT, circuitTag)
              .key(Pattern.INGOT, ingotTag)
              .key(Pattern.ALLOY, alloyTag)
              .build(consumer, Mekanism.rl(basePath + tierInstaller.asItem().getToTier().getLowerName()));
    }
}