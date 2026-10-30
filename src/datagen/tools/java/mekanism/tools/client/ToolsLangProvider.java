package mekanism.tools.client;

import mekanism.api.providers.IItemProvider;
import mekanism.api.text.EnumColor;
import mekanism.client.lang.BaseLanguageProvider;
import mekanism.common.util.EnumUtils;
import mekanism.tools.common.MekanismTools;
import mekanism.tools.common.ToolsLang;
import mekanism.tools.common.advancements.ToolsAdvancements;
import mekanism.tools.common.registries.ToolsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public class ToolsLangProvider extends BaseLanguageProvider {

    public ToolsLangProvider(FabricDataOutput output) {
        super(output, MekanismTools.instance);
    }

    @Override
    public void generateTranslations(TranslationBuilder t) {
        addItems(t);
        addAdvancements(t);
        addMisc(t);
    }

    private void addItems(TranslationBuilder t) {
        //Vanilla Paxels
        t.add(ToolsItems.WOOD_PAXEL.get(), "Wood Paxel");
        t.add(ToolsItems.STONE_PAXEL.get(), "Stone Paxel");
        t.add(ToolsItems.IRON_PAXEL.get(), "Iron Paxel");
        t.add(ToolsItems.GOLD_PAXEL.get(), "Gold Paxel");
        t.add(ToolsItems.DIAMOND_PAXEL.get(), "Diamond Paxel");
        t.add(ToolsItems.NETHERITE_PAXEL.get(), "Netherite Paxel");
        //Tool sets
        addSet(t, "Bronze", ToolsItems.BRONZE_HELMET, ToolsItems.BRONZE_CHESTPLATE, ToolsItems.BRONZE_LEGGINGS, ToolsItems.BRONZE_BOOTS, ToolsItems.BRONZE_SWORD,
              ToolsItems.BRONZE_PICKAXE, ToolsItems.BRONZE_AXE, ToolsItems.BRONZE_SHOVEL, ToolsItems.BRONZE_HOE, ToolsItems.BRONZE_PAXEL, ToolsItems.BRONZE_SHIELD);
        addSet(t, "Lapis Lazuli", ToolsItems.LAPIS_LAZULI_HELMET, ToolsItems.LAPIS_LAZULI_CHESTPLATE, ToolsItems.LAPIS_LAZULI_LEGGINGS, ToolsItems.LAPIS_LAZULI_BOOTS,
              ToolsItems.LAPIS_LAZULI_SWORD, ToolsItems.LAPIS_LAZULI_PICKAXE, ToolsItems.LAPIS_LAZULI_AXE, ToolsItems.LAPIS_LAZULI_SHOVEL, ToolsItems.LAPIS_LAZULI_HOE,
              ToolsItems.LAPIS_LAZULI_PAXEL, ToolsItems.LAPIS_LAZULI_SHIELD);
        addSet(t, "Osmium", ToolsItems.OSMIUM_HELMET, ToolsItems.OSMIUM_CHESTPLATE, ToolsItems.OSMIUM_LEGGINGS, ToolsItems.OSMIUM_BOOTS, ToolsItems.OSMIUM_SWORD,
              ToolsItems.OSMIUM_PICKAXE, ToolsItems.OSMIUM_AXE, ToolsItems.OSMIUM_SHOVEL, ToolsItems.OSMIUM_HOE, ToolsItems.OSMIUM_PAXEL, ToolsItems.OSMIUM_SHIELD);
        addSet(t, "Refined Glowstone", ToolsItems.REFINED_GLOWSTONE_HELMET, ToolsItems.REFINED_GLOWSTONE_CHESTPLATE, ToolsItems.REFINED_GLOWSTONE_LEGGINGS,
              ToolsItems.REFINED_GLOWSTONE_BOOTS, ToolsItems.REFINED_GLOWSTONE_SWORD, ToolsItems.REFINED_GLOWSTONE_PICKAXE, ToolsItems.REFINED_GLOWSTONE_AXE,
              ToolsItems.REFINED_GLOWSTONE_SHOVEL, ToolsItems.REFINED_GLOWSTONE_HOE, ToolsItems.REFINED_GLOWSTONE_PAXEL, ToolsItems.REFINED_GLOWSTONE_SHIELD);
        addSet(t, "Refined Obsidian", ToolsItems.REFINED_OBSIDIAN_HELMET, ToolsItems.REFINED_OBSIDIAN_CHESTPLATE, ToolsItems.REFINED_OBSIDIAN_LEGGINGS,
              ToolsItems.REFINED_OBSIDIAN_BOOTS, ToolsItems.REFINED_OBSIDIAN_SWORD, ToolsItems.REFINED_OBSIDIAN_PICKAXE, ToolsItems.REFINED_OBSIDIAN_AXE,
              ToolsItems.REFINED_OBSIDIAN_SHOVEL, ToolsItems.REFINED_OBSIDIAN_HOE, ToolsItems.REFINED_OBSIDIAN_PAXEL, ToolsItems.REFINED_OBSIDIAN_SHIELD);
        addSet(t, "Steel", ToolsItems.STEEL_HELMET, ToolsItems.STEEL_CHESTPLATE, ToolsItems.STEEL_LEGGINGS, ToolsItems.STEEL_BOOTS, ToolsItems.STEEL_SWORD,
              ToolsItems.STEEL_PICKAXE, ToolsItems.STEEL_AXE, ToolsItems.STEEL_SHOVEL, ToolsItems.STEEL_HOE, ToolsItems.STEEL_PAXEL, ToolsItems.STEEL_SHIELD);
    }

    private void addAdvancements(TranslationBuilder t) {
        add(t, ToolsAdvancements.PAXEL, "Multi-Tool", "Craft any Paxel (Pickaxe, Axe, Shovel)");
        add(t, ToolsAdvancements.ALTERNATE_ARMOR, "More Armor Types!", "Craft any piece of Armor from " + basicModName);
        add(t, ToolsAdvancements.ALTERNATE_TOOLS, "More Tool Types!", "Craft any tool or weapon (except Paxels) from " + basicModName);
        add(t, ToolsAdvancements.NOT_ENOUGH_SHIELDING, "Not Enough Shielding", "Craft any Shield added by " + basicModName);
        add(t, ToolsAdvancements.BETTER_THAN_NETHERITE, "Better Than Netherite", "Protect yourself with a piece of Refined Obsidian Armor");
        add(t, ToolsAdvancements.LOVED_BY_PIGLINS, "Loved By Piglins", "Refined Glowstone Armor glows even brighter than gold!");
    }

    private void addMisc(TranslationBuilder t) {
        addPackData(t, ToolsLang.MEKANISM_TOOLS, ToolsLang.PACK_DESCRIPTION);
        add(t, ToolsLang.HP, "HP: %1$s");
    }

    private void addSet(TranslationBuilder t, String type, IItemProvider helmet, IItemProvider chestplate, IItemProvider leggings, IItemProvider boots, IItemProvider sword,
          IItemProvider pickaxe, IItemProvider axe, IItemProvider shovel, IItemProvider hoe, IItemProvider paxel, IItemProvider shield) {
        add(t, helmet, type + " Helmet");
        add(t, chestplate, type + " Chestplate");
        add(t, leggings, type + " Leggings");
        add(t, boots, type + " Boots");
        add(t, sword, type + " Sword");
        add(t, pickaxe, type + " Pickaxe");
        add(t, axe, type + " Axe");
        add(t, shovel, type + " Shovel");
        add(t, hoe, type + " Hoe");
        add(t, paxel, type + " Paxel");
        addShield(t, shield, type + " Shield");
    }

    private void addShield(TranslationBuilder t, IItemProvider shield, String name) {
        add(t, shield, name);
        //Add names for all the bannered overlay types
        for (EnumColor color : EnumUtils.COLORS) {
            if (color.getDyeColor() != null) {
                add(t, shield.getTranslationKey() + "." + color.getRegistryPrefix(), color.getEnglishName() + " " + name);
            }
        }
    }
}