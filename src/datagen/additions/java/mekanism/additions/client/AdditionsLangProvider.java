package mekanism.additions.client;

import java.util.Map;
import mekanism.additions.common.AdditionsLang;
import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.advancements.AdditionsAdvancements;
import mekanism.additions.common.item.ItemBalloon;
import mekanism.additions.common.registries.AdditionsBlocks;
import mekanism.additions.common.registries.AdditionsEntityTypes;
import mekanism.additions.common.registries.AdditionsItems;
import mekanism.additions.common.registries.AdditionsSounds;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.text.EnumColor;
import mekanism.client.lang.BaseLanguageProvider;
import mekanism.common.registration.impl.ItemRegistryObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.PackOutput;

public class AdditionsLangProvider extends BaseLanguageProvider {

    public AdditionsLangProvider(FabricDataOutput dataGenerator) {
        super(dataGenerator, MekanismAdditions.instance);
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        addItems(builder);
        addBlocks(builder);
        addEntities(builder);
        addSubtitles(builder);
        addAdvancements(builder);
        addMisc(builder);
    }

    private void addItems(TranslationBuilder builder) {
        add(builder, AdditionsItems.BABY_CREEPER_SPAWN_EGG, "Baby Creeper Spawn Egg");
        add(builder, AdditionsItems.BABY_ENDERMAN_SPAWN_EGG, "Baby Enderman Spawn Egg");
        add(builder, AdditionsItems.BABY_SKELETON_SPAWN_EGG, "Baby Skeleton Spawn Egg");
        add(builder, AdditionsItems.BABY_STRAY_SPAWN_EGG, "Baby Stray Spawn Egg");
        add(builder, AdditionsItems.BABY_WITHER_SKELETON_SPAWN_EGG, "Baby Wither Skeleton Spawn Egg");
        add(builder, AdditionsItems.WALKIE_TALKIE, "Walkie-Talkie");
        for (Map.Entry<EnumColor, ItemRegistryObject<ItemBalloon>> entry : AdditionsItems.BALLOONS.entrySet()) {
            add(builder, entry.getValue(), entry.getKey().getEnglishName() + " Balloon");
        }
    }

    private void addBlocks(TranslationBuilder builder) {
        add(builder, AdditionsBlocks.OBSIDIAN_TNT, "Obsidian TNT");
        addColoredBlocks(builder, AdditionsBlocks.GLOW_PANELS, "Glow Panel");
        addColoredBlocks(builder, AdditionsBlocks.PLASTIC_BLOCKS, "Plastic Block");
        addColoredBlocks(builder, AdditionsBlocks.SLICK_PLASTIC_BLOCKS, "Slick Plastic Block");
        addColoredBlocks(builder, AdditionsBlocks.PLASTIC_GLOW_BLOCKS, "Glow Plastic Block");
        addColoredBlocks(builder, AdditionsBlocks.REINFORCED_PLASTIC_BLOCKS, "Reinforced Plastic Block");
        addColoredBlocks(builder, AdditionsBlocks.PLASTIC_ROADS, "Plastic Road");
        addColoredBlocks(builder, AdditionsBlocks.TRANSPARENT_PLASTIC_BLOCKS, "Transparent Plastic Block");
        addColoredBlocks(builder, AdditionsBlocks.PLASTIC_STAIRS, "Plastic Stairs");
        addColoredBlocks(builder, AdditionsBlocks.PLASTIC_SLABS, "Plastic Slab");
        addColoredBlocks(builder, AdditionsBlocks.PLASTIC_FENCES, "Plastic Barrier");
        addColoredBlocks(builder, AdditionsBlocks.PLASTIC_FENCE_GATES, "Plastic Gate");
        addColoredBlocks(builder, AdditionsBlocks.PLASTIC_GLOW_STAIRS, "Glow Plastic Stairs");
        addColoredBlocks(builder, AdditionsBlocks.PLASTIC_GLOW_SLABS, "Glow Plastic Slab");
        addColoredBlocks(builder, AdditionsBlocks.TRANSPARENT_PLASTIC_STAIRS, "Transparent Plastic Stairs");
        addColoredBlocks(builder, AdditionsBlocks.TRANSPARENT_PLASTIC_SLABS, "Transparent Plastic Slab");
    }

    private void addEntities(TranslationBuilder builder) {
        add(builder, AdditionsEntityTypes.BABY_CREEPER, "Baby Creeper");
        add(builder, AdditionsEntityTypes.BABY_ENDERMAN, "Baby Enderman");
        add(builder, AdditionsEntityTypes.BABY_SKELETON, "Baby Skeleton");
        add(builder, AdditionsEntityTypes.BABY_STRAY, "Baby Stray");
        add(builder, AdditionsEntityTypes.BABY_WITHER_SKELETON, "Baby Wither Skeleton");
        add(builder, AdditionsEntityTypes.BALLOON, "Balloon");
        add(builder, AdditionsEntityTypes.OBSIDIAN_TNT, "Obsidian TNT");
    }

    private void addSubtitles(TranslationBuilder builder) {
        add(builder, AdditionsSounds.POP, "Balloon pops");
    }

    private void addAdvancements(TranslationBuilder builder) {
        add(builder, AdditionsAdvancements.BALLOON, "Reach for the Skies", "Craft any color Balloon");
        add(builder, AdditionsAdvancements.POP_POP, "Pop Pop", "Pop a balloon");
        add(builder, AdditionsAdvancements.GLOW_IN_THE_DARK, "Glow in the Dark", "Craft any color Glow Panel");
        add(builder, AdditionsAdvancements.HURT_BY_BABIES, "Don't Try Taking Candy From Those Babies", "Get injured by all baby mobs from " + basicModName);
        add(builder, AdditionsAdvancements.NOT_THE_BABIES, "Not the Babies", "Kill any baby " + basicModName + " mob");
    }

    private void addMisc(TranslationBuilder builder) {
        addPackData(builder, AdditionsLang.MEKANISM_ADDITIONS, AdditionsLang.PACK_DESCRIPTION);
        add(builder, AdditionsLang.CHANNEL, "Channel: %1$s");
        add(builder, AdditionsLang.CHANNEL_CHANGE, "Channel changed to: %1$s");
        add(builder, AdditionsLang.WALKIE_DISABLED, "Voice server disabled.");
        add(builder, AdditionsLang.KEY_VOICE, "Voice");

        add(builder, AdditionsLang.DESCRIPTION_GLOW_PANEL, "A modern, ever-lasting light source. Now in many colors!");
        add(builder, AdditionsLang.DESCRIPTION_OBSIDIAN_TNT, "An extremely powerful, obsidian-infused block of TNT. Use at your own peril.");
    }

    private void addColoredBlocks(TranslationBuilder builder, Map<EnumColor, ? extends IBlockProvider> blocks, String suffix) {
        for (Map.Entry<EnumColor, ? extends IBlockProvider> entry : blocks.entrySet()) {
            add(builder, entry.getValue(), entry.getKey().getEnglishName() + " " + suffix);
        }
    }
}