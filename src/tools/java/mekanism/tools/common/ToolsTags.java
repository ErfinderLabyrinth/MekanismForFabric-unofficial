package mekanism.tools.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ToolsTags {

    /**
     * Call to force make sure this is all initialized
     */
    public static void init() {
        Blocks.init();
        Items.init();
    }

    private ToolsTags() {
    }

    public static class Blocks {

        private static void init() {
        }

        private Blocks() {
        }

        public static final TagKey<Block> MINEABLE_WITH_PAXEL = tag("mineable/paxel");
        public static final TagKey<Block> NEEDS_BRONZE_TOOL = tag("needs_bronze_tool");
        public static final TagKey<Block> NEEDS_LAPIS_LAZULI_TOOL = tag("needs_lapis_lazuli_tool");
        public static final TagKey<Block> NEEDS_OSMIUM_TOOL = tag("needs_osmium_tool");
        public static final TagKey<Block> NEEDS_REFINED_GLOWSTONE_TOOL = tag("needs_refined_glowstone_tool");
        public static final TagKey<Block> NEEDS_REFINED_OBSIDIAN_TOOL = tag("needs_refined_obsidian_tool");
        public static final TagKey<Block> NEEDS_STEEL_TOOL = tag("needs_steel_tool");

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, MekanismTools.rl(name));
        }
    }

    public static class Items {

        private static void init() {
        }

        private Items() {
        }

        public static final TagKey<Item> TOOLS_PAXELS = cTag("paxels");

        public static final TagKey<Item> TOOLS_PAXELS_WOOD = cTag("wood_paxels");
        public static final TagKey<Item> TOOLS_PAXELS_STONE = cTag("stone_paxels");
        public static final TagKey<Item> TOOLS_PAXELS_GOLD = cTag("gold_paxels");
        public static final TagKey<Item> TOOLS_PAXELS_IRON = cTag("iron_paxels");
        public static final TagKey<Item> TOOLS_PAXELS_DIAMOND = cTag("diamond_paxels");
        public static final TagKey<Item> TOOLS_PAXELS_NETHERITE = cTag("netherite_paxels");

        public static final TagKey<Item> TOOLS_PAXELS_BRONZE = cTag("bronze_paxels");
        public static final TagKey<Item> TOOLS_PAXELS_LAPIS_LAZULI = cTag("lapis_lazuli_paxels");
        public static final TagKey<Item> TOOLS_PAXELS_OSMIUM = cTag("osmium_paxels");
        public static final TagKey<Item> TOOLS_PAXELS_REFINED_GLOWSTONE = cTag("refined_glowstone_paxels");
        public static final TagKey<Item> TOOLS_PAXELS_REFINED_OBSIDIAN = cTag("refined_obsidian_paxels");
        public static final TagKey<Item> TOOLS_PAXELS_STEEL = cTag("steel_paxels");

        public static final TagKey<Item> TOOLS_SWORDS_BRONZE = cTag("bronze_swords");
        public static final TagKey<Item> TOOLS_SWORDS_LAPIS_LAZULI = cTag("lapis_lazuli_swords");
        public static final TagKey<Item> TOOLS_SWORDS_OSMIUM = cTag("osmium_swords");
        public static final TagKey<Item> TOOLS_SWORDS_REFINED_GLOWSTONE = cTag("refined_glowstone_swords");
        public static final TagKey<Item> TOOLS_SWORDS_REFINED_OBSIDIAN = cTag("refined_obsidian_swords");
        public static final TagKey<Item> TOOLS_SWORDS_STEEL = cTag("steel_swords");

        public static final TagKey<Item> TOOLS_AXES_BRONZE = cTag("bronze_axes");
        public static final TagKey<Item> TOOLS_AXES_LAPIS_LAZULI = cTag("lapis_lazuli_axes");
        public static final TagKey<Item> TOOLS_AXES_OSMIUM = cTag("osmium_axes");
        public static final TagKey<Item> TOOLS_AXES_REFINED_GLOWSTONE = cTag("refined_glowstone_axes");
        public static final TagKey<Item> TOOLS_AXES_REFINED_OBSIDIAN = cTag("refined_obsidian_axes");
        public static final TagKey<Item> TOOLS_AXES_STEEL = cTag("steel_axes");

        public static final TagKey<Item> TOOLS_PICKAXES_BRONZE = cTag("bronze_pickaxes");
        public static final TagKey<Item> TOOLS_PICKAXES_LAPIS_LAZULI = cTag("lapis_lazuli_pickaxes");
        public static final TagKey<Item> TOOLS_PICKAXES_OSMIUM = cTag("osmium_pickaxes");
        public static final TagKey<Item> TOOLS_PICKAXES_REFINED_GLOWSTONE = cTag("refined_glowstone_pickaxes");
        public static final TagKey<Item> TOOLS_PICKAXES_REFINED_OBSIDIAN = cTag("refined_obsidian_pickaxes");
        public static final TagKey<Item> TOOLS_PICKAXES_STEEL = cTag("steel_pickaxes");

        public static final TagKey<Item> TOOLS_SHOVELS_BRONZE = cTag("bronze_shovels");
        public static final TagKey<Item> TOOLS_SHOVELS_LAPIS_LAZULI = cTag("lapis_lazuli_shovels");
        public static final TagKey<Item> TOOLS_SHOVELS_OSMIUM = cTag("osmium_shovels");
        public static final TagKey<Item> TOOLS_SHOVELS_REFINED_GLOWSTONE = cTag("refined_glowstone_shovels");
        public static final TagKey<Item> TOOLS_SHOVELS_REFINED_OBSIDIAN = cTag("refined_obsidian_shovels");
        public static final TagKey<Item> TOOLS_SHOVELS_STEEL = cTag("steel_shovels");

        public static final TagKey<Item> TOOLS_HOES_BRONZE = cTag("bronze_hoes");
        public static final TagKey<Item> TOOLS_HOES_LAPIS_LAZULI = cTag("lapis_lazuli_hoes");
        public static final TagKey<Item> TOOLS_HOES_OSMIUM = cTag("osmium_hoes");
        public static final TagKey<Item> TOOLS_HOES_REFINED_GLOWSTONE = cTag("refined_glowstone_hoes");
        public static final TagKey<Item> TOOLS_HOES_REFINED_OBSIDIAN = cTag("refined_obsidian_hoes");
        public static final TagKey<Item> TOOLS_HOES_STEEL = cTag("steel_hoes");

        public static final TagKey<Item> TOOLS_SHIELDS_BRONZE = cTag("bronze_shields");
        public static final TagKey<Item> TOOLS_SHIELDS_LAPIS_LAZULI = cTag("lapis_lazuli_shields");
        public static final TagKey<Item> TOOLS_SHIELDS_OSMIUM = cTag("osmium_shields");
        public static final TagKey<Item> TOOLS_SHIELDS_REFINED_GLOWSTONE = cTag("refined_glowstone_shields");
        public static final TagKey<Item> TOOLS_SHIELDS_REFINED_OBSIDIAN = cTag("refined_obsidian_shields");
        public static final TagKey<Item> TOOLS_SHIELDS_STEEL = cTag("steel_shields");

        public static final TagKey<Item> ARMORS_HELMETS_BRONZE = cTag("bronze_helmets");
        public static final TagKey<Item> ARMORS_HELMETS_LAPIS_LAZULI = cTag("lapis_lazuli_helmets");
        public static final TagKey<Item> ARMORS_HELMETS_OSMIUM = cTag("osmium_helmets");
        public static final TagKey<Item> ARMORS_HELMETS_REFINED_GLOWSTONE = cTag("refined_glowstone_helmets");
        public static final TagKey<Item> ARMORS_HELMETS_REFINED_OBSIDIAN = cTag("refined_obsidian_helmets");
        public static final TagKey<Item> ARMORS_HELMETS_STEEL = cTag("steel_helmets");

        public static final TagKey<Item> ARMORS_CHESTPLATES_BRONZE = cTag("bronze_chestplates");
        public static final TagKey<Item> ARMORS_CHESTPLATES_LAPIS_LAZULI = cTag("lapis_lazuli_chestplates");
        public static final TagKey<Item> ARMORS_CHESTPLATES_OSMIUM = cTag("osmium_chestplates");
        public static final TagKey<Item> ARMORS_CHESTPLATES_REFINED_GLOWSTONE = cTag("refined_glowstone_chestplates");
        public static final TagKey<Item> ARMORS_CHESTPLATES_REFINED_OBSIDIAN = cTag("refined_obsidian_chestplates");
        public static final TagKey<Item> ARMORS_CHESTPLATES_STEEL = cTag("steel_chestplates");

        public static final TagKey<Item> ARMORS_LEGGINGS_BRONZE = cTag("bronze_leggings");
        public static final TagKey<Item> ARMORS_LEGGINGS_LAPIS_LAZULI = cTag("lapis_lazuli_leggings");
        public static final TagKey<Item> ARMORS_LEGGINGS_OSMIUM = cTag("osmium_leggings");
        public static final TagKey<Item> ARMORS_LEGGINGS_REFINED_GLOWSTONE = cTag("refined_glowstone_leggings");
        public static final TagKey<Item> ARMORS_LEGGINGS_REFINED_OBSIDIAN = cTag("refined_obsidian_leggings");
        public static final TagKey<Item> ARMORS_LEGGINGS_STEEL = cTag("steel_leggings");

        public static final TagKey<Item> ARMORS_BOOTS_BRONZE = cTag("bronze_boots");
        public static final TagKey<Item> ARMORS_BOOTS_LAPIS_LAZULI = cTag("lapis_lazuli_boots");
        public static final TagKey<Item> ARMORS_BOOTS_OSMIUM = cTag("osmium_boots");
        public static final TagKey<Item> ARMORS_BOOTS_REFINED_GLOWSTONE = cTag("refined_glowstone_boots");
        public static final TagKey<Item> ARMORS_BOOTS_REFINED_OBSIDIAN = cTag("refined_obsidian_boots");
        public static final TagKey<Item> ARMORS_BOOTS_STEEL = cTag("steel_boots");

        private static TagKey<Item> cTag(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation("c", name));
        }
    }
}