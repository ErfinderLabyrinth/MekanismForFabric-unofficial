package mekanism.common.tags;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import mekanism.api.chemical.ChemicalTags;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.common.Mekanism;
import mekanism.common.resource.BlockResourceInfo;
import mekanism.common.resource.IResource;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import mekanism.common.resource.ore.OreType;
import mekanism.common.util.EnumUtils;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class MekanismTags {

    /**
     * Call to force make sure this is all initialized
     */
    public static void init() {
        Items.init();
        Blocks.init();
        Biomes.init();
        DamageTypes.init();
        Fluids.init();
        Gases.init();
        InfuseTypes.init();
        MobEffects.init();
        Slurries.init();
        TileEntityTypes.init();
    }

    private MekanismTags() {
    }

    public static class Items {

        private static void init() {
        }

        private Items() {
        }

        public static final Table<ResourceType, PrimaryResource, TagKey<Item>> PROCESSED_RESOURCES = HashBasedTable.create();
        public static final Map<IResource, TagKey<Item>> PROCESSED_RESOURCE_BLOCKS = new HashMap<>();
        public static final Map<OreType, TagKey<Item>> ORES = new EnumMap<>(OreType.class);

        static {
            for (PrimaryResource resource : EnumUtils.PRIMARY_RESOURCES) {
                for (ResourceType type : EnumUtils.RESOURCE_TYPES) {
                    if (type.usedByPrimary(resource)) {
                        if (type.isVanilla() || type == ResourceType.DUST) {
                            if(type.isPrefix()) {
                                PROCESSED_RESOURCES.put(type, resource, cTag(type.getBaseTagPath() + "_" + resource.getRegistrySuffix()));
                            } else {
                                PROCESSED_RESOURCES.put(type, resource, cTag(resource.getRegistrySuffix() + "_" + type.getBaseTagPath()));
                            }
                        } else {
                            PROCESSED_RESOURCES.put(type, resource, tag(type.getBaseTagPath() + "/" + resource.getRegistrySuffix()));
                        }
                    }
                }
                if (!resource.isVanilla()) {
                    PROCESSED_RESOURCE_BLOCKS.put(resource, cTag(resource.getRegistrySuffix() + "_blocks"));
                    BlockResourceInfo rawResource = resource.getRawResourceBlockInfo();
                    if (rawResource != null) {
                        PROCESSED_RESOURCE_BLOCKS.put(rawResource, cTag(rawResource.getRegistrySuffix() + "_blocks"));
                    }
                }
            }
            for (OreType ore : EnumUtils.ORE_TYPES) {
                ORES.put(ore, cTag(ore.getResource().getRegistrySuffix() + "_ores"));
            }
        }

        public static final TagKey<Item> CONFIGURATORS = tag("configurators");
        public static final TagKey<Item> WRENCHES = cTag("wrenches");
        public static final TagKey<Item> PERSONAL_STORAGE = tag("personal_storage");

        public static final TagKey<Item> BATTERIES = cTag("batteries");

        public static final TagKey<Item> RODS = cTag("rods");
        public static final TagKey<Item> RODS_PLASTIC = cTag("plastic_rods");

        public static final TagKey<Item> FUELS = cTag("fuels");
        public static final TagKey<Item> FUELS_BIO = cTag("biofuels");

        public static final TagKey<Item> SALT = cTag("salt");
        public static final TagKey<Item> SAWDUST = cTag("saw_dusts");
        public static final TagKey<Item> YELLOW_CAKE_URANIUM = cTag("yellow_cake_uranium");

        public static final TagKey<Item> PELLETS_ANTIMATTER = cTag("antimatter_pellets");
        public static final TagKey<Item> PELLETS_PLUTONIUM = cTag("plutonium_pellets");
        public static final TagKey<Item> PELLETS_POLONIUM = cTag("polonium_pellets");

        public static final TagKey<Item> DUSTS_BRONZE = cTag("bronze_dusts");
        public static final TagKey<Item> DUSTS_CHARCOAL = cTag("charcoal_dusts");
        public static final TagKey<Item> DUSTS_COAL = cTag("coal_dusts");
        public static final TagKey<Item> DUSTS_DIAMOND = cTag("diamond_dusts");
        public static final TagKey<Item> DUSTS_EMERALD = cTag("emerald_dusts");
        public static final TagKey<Item> DUSTS_NETHERITE = cTag("netherite_dusts");
        public static final TagKey<Item> DUSTS_LAPIS = cTag("lapis_dusts");
        public static final TagKey<Item> DUSTS_LITHIUM = cTag("lithium_dusts");
        public static final TagKey<Item> DUSTS_OBSIDIAN = cTag("obsidian_dusts");
        public static final TagKey<Item> DUSTS_QUARTZ = cTag("quartz_dusts");
        public static final TagKey<Item> DUSTS_REFINED_OBSIDIAN = cTag("refined_obsidian_dusts");
        public static final TagKey<Item> DUSTS_SALT = cTag("salt_dusts");
        public static final TagKey<Item> DUSTS_STEEL = cTag("steel_dusts");
        public static final TagKey<Item> DUSTS_SULFUR = cTag("sulfur_dusts");
        public static final TagKey<Item> DUSTS_WOOD = cTag("wood_dusts");
        public static final TagKey<Item> DUSTS_FLUORITE = cTag("fluorite_dusts");

        public static final TagKey<Item> NUGGETS_BRONZE = cTag("bronze_nuggets");
        public static final TagKey<Item> NUGGETS_REFINED_GLOWSTONE = cTag("refined_glowstone_nuggets");
        public static final TagKey<Item> NUGGETS_REFINED_OBSIDIAN = cTag("refined_obsidian_nuggets");
        public static final TagKey<Item> NUGGETS_STEEL = cTag("steel_nuggets");

        public static final TagKey<Item> INGOTS_BRONZE = cTag("bronze_ingots");
        public static final TagKey<Item> INGOTS_REFINED_GLOWSTONE = cTag("refined_glowstone_ingots");
        public static final TagKey<Item> INGOTS_REFINED_OBSIDIAN = cTag("refined_obsidian_ingots");
        public static final TagKey<Item> INGOTS_STEEL = cTag("steel_ingots");

        public static final TagKey<Item> STORAGE_BLOCKS_BRONZE = cTag("bronze_blocks");
        public static final TagKey<Item> STORAGE_BLOCKS_CHARCOAL = cTag("charcoal_blocks");
        public static final TagKey<Item> STORAGE_BLOCKS_REFINED_GLOWSTONE = cTag("refined_glowstone_blocks");
        public static final TagKey<Item> STORAGE_BLOCKS_REFINED_OBSIDIAN = cTag("refined_obsidian_blocks");
        public static final TagKey<Item> STORAGE_BLOCKS_STEEL = cTag("steel_blocks");
        public static final TagKey<Item> STORAGE_BLOCKS_FLUORITE = cTag("fluorite_blocks");

        public static final TagKey<Item> CIRCUITS = cTag("circuits");
        public static final TagKey<Item> CIRCUITS_BASIC = cTag("basic_circuits");
        public static final TagKey<Item> CIRCUITS_ADVANCED = cTag("advanced_circuits");
        public static final TagKey<Item> CIRCUITS_ELITE = cTag("elite_circuits");
        public static final TagKey<Item> CIRCUITS_ULTIMATE = cTag("ultimate_circuits");

        public static final TagKey<Item> ALLOYS = tag("alloys");
        public static final TagKey<Item> ALLOYS_BASIC = tag("alloys/basic");
        public static final TagKey<Item> ALLOYS_INFUSED = tag("alloys/infused");
        public static final TagKey<Item> ALLOYS_REINFORCED = tag("alloys/reinforced");
        public static final TagKey<Item> ALLOYS_ATOMIC = tag("alloys/atomic");
        //Forge alloy tags
        public static final TagKey<Item> FORGE_ALLOYS = cTag("alloys");
        public static final TagKey<Item> ALLOYS_ADVANCED = cTag("advanced_alloys");
        public static final TagKey<Item> ALLOYS_ELITE = cTag("elite_alloys");
        public static final TagKey<Item> ALLOYS_ULTIMATE = cTag("ultimate_alloys");

        public static final TagKey<Item> ENRICHED = tag("enriched");
        public static final TagKey<Item> ENRICHED_CARBON = tag("enriched/carbon");
        public static final TagKey<Item> ENRICHED_DIAMOND = tag("enriched/diamond");
        public static final TagKey<Item> ENRICHED_OBSIDIAN = tag("enriched/obsidian");
        public static final TagKey<Item> ENRICHED_REDSTONE = tag("enriched/redstone");
        public static final TagKey<Item> ENRICHED_GOLD = tag("enriched/gold");
        public static final TagKey<Item> ENRICHED_TIN = tag("enriched/tin");

        public static final TagKey<Item> DIRTY_DUSTS = tag("dirty_dusts");
        public static final TagKey<Item> CLUMPS = tag("clumps");
        public static final TagKey<Item> SHARDS = tag("shards");
        public static final TagKey<Item> CRYSTALS = tag("crystals");

        public static final TagKey<Item> GEMS_FLUORITE = cTag("fluorite");
        public static final TagKey<Item> MEKASUIT_HUD_RENDERER = tag("mekasuit_hud_renderer");

        public static final TagKey<Item> COLORABLE_WOOL = tag("colorable/wool");
        public static final TagKey<Item> COLORABLE_CARPETS = tag("colorable/carpets");
        public static final TagKey<Item> COLORABLE_BEDS = tag("colorable/beds");
        public static final TagKey<Item> COLORABLE_GLASS = tag("colorable/glass");
        public static final TagKey<Item> COLORABLE_GLASS_PANES = tag("colorable/glass_panes");
        public static final TagKey<Item> COLORABLE_TERRACOTTA = tag("colorable/terracotta");
        public static final TagKey<Item> COLORABLE_CANDLE = tag("colorable/candle");
        public static final TagKey<Item> COLORABLE_CONCRETE = tag("colorable/concrete");
        public static final TagKey<Item> COLORABLE_CONCRETE_POWDER = tag("colorable/concrete_powder");
        public static final TagKey<Item> COLORABLE_BANNERS = tag("colorable/banners");

        public static final TagKey<Item> ARMORS_HELMETS_HAZMAT = cTag("hazmat_armors");
        public static final TagKey<Item> ARMORS_CHESTPLATES_HAZMAT = cTag("hazmat_chestplates");
        public static final TagKey<Item> ARMORS_LEGGINGS_HAZMAT = cTag("hazmat_leggings");
        public static final TagKey<Item> ARMORS_BOOTS_HAZMAT = cTag("hazmat_boots");

        //forge tags that are missing on fabric
        public static final TagKey<Item> BRICKS = cTag("bricks");
        public static final TagKey<Item> GUNPOWDER = cTag("gunpowder");
        public static final TagKey<Item> GLOWSTONE_DUSTS = cTag("glowstone_dusts");
        public static final TagKey<Item> WHEAT = cTag("wheat");
        public static final TagKey<Item> NORMAL_COBBLESTONES = cTag("normal_cobblestones");
        public static final TagKey<Item> COBBLED_DEEPSLATES = cTag("cobbled_deepslates");
        public static final TagKey<Item> GRAVELS = cTag("gravels");
        public static final TagKey<Item> SANDS = cTag("sands");
        public static final TagKey<Item> OBSIDIAN = cTag("obsidian");
        public static final TagKey<Item> MUSHROOMS = cTag("mushrooms");
        public static final TagKey<Item> NETHER_STARS = cTag("nether_stars");
        public static final TagKey<Item> LEATHERS = cTag("leathers");
        public static final TagKey<Item> STRINGS = cTag("strings");
        public static final TagKey<Item> ANCIENT_DEBRIS = cTag("ancient_debris");
        public static final TagKey<Item> WOODEN_RODS = cTag("wooden_rods");
        public static final TagKey<Item> SLIME_BALLS = cTag("slime_balls");

        public static final TagKey<Item> QUARTZ_BLOCKS = cTag("quartz_blocks");
        public static final TagKey<Item> REDSTONE_BLOCKS = cTag("redstone_blocks");
        public static final TagKey<Item> COAL_BLOCKS = cTag("coal_blocks");

        private static TagKey<Item> cTag(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation("c", name));
        }

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, Mekanism.rl(name));
        }
    }

    public static class Blocks {

        private static void init() {
        }

        private Blocks() {
        }

        public static final Map<IResource, TagKey<Block>> RESOURCE_STORAGE_BLOCKS = new HashMap<>();
        public static final Map<OreType, TagKey<Block>> ORES = new EnumMap<>(OreType.class);

        static {
            for (PrimaryResource resource : EnumUtils.PRIMARY_RESOURCES) {
                if (!resource.isVanilla()) {
                    RESOURCE_STORAGE_BLOCKS.put(resource, cTag(resource.getRegistrySuffix() + "_blocks"));
                    BlockResourceInfo rawResource = resource.getRawResourceBlockInfo();
                    if (rawResource != null) {
                        RESOURCE_STORAGE_BLOCKS.put(rawResource, cTag(rawResource.getRegistrySuffix() + "_blocks"));
                    }
                }
            }
            for (OreType ore : EnumUtils.ORE_TYPES) {
                ORES.put(ore, cTag(ore.getResource().getRegistrySuffix() + "_ores"));
            }
        }

        public static final TagKey<Block> RELOCATION_NOT_SUPPORTED = ConventionalBlockTags.MOVEMENT_RESTRICTED;
        public static final TagKey<Block> CARDBOARD_BLACKLIST = tag("cardboard_blacklist");
        public static final TagKey<Block> MINER_BLACKLIST = tag("miner_blacklist");
//        public static final LazyTagLookup<Block> MINER_BLACKLIST_LOOKUP = LazyTagLookup.create(BuiltInRegistries.BLOCK, MINER_BLACKLIST);
        public static final TagKey<Block> ATOMIC_DISASSEMBLER_ORE = tag("atomic_disassembler_ore");
        /**
         * For use in the farming module to target blocks that should be effectively ignored when checking if the block below should be targeted.
         */
        public static final TagKey<Block> FARMING_OVERRIDE = tag("farming_override");

        public static final TagKey<Block> CHESTS_ELECTRIC = cTag("electric_chests");
        public static final TagKey<Block> CHESTS_PERSONAL = cTag("personal_chests");
        public static final TagKey<Block> BARRELS_PERSONAL = cTag("personal_barrels");
        public static final TagKey<Block> PERSONAL_STORAGE = tag("personal_storage");

        public static final TagKey<Block> STORAGE_BLOCKS_BRONZE = cTag("bronze_blocks");
        public static final TagKey<Block> STORAGE_BLOCKS_CHARCOAL = cTag("charcoal_blocks");
        public static final TagKey<Block> STORAGE_BLOCKS_REFINED_GLOWSTONE = cTag("refined_glowstone_blocks");
        public static final TagKey<Block> STORAGE_BLOCKS_REFINED_OBSIDIAN = cTag("refined_obsidian_blocks");
        public static final TagKey<Block> STORAGE_BLOCKS_STEEL = cTag("steel_blocks");
        public static final TagKey<Block> STORAGE_BLOCKS_FLUORITE = cTag("fluorite_blocks");

        private static TagKey<Block> cTag(String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation("c", name));
        }

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, Mekanism.rl(name));
        }
    }

    public static class Biomes {

        private static void init() {
        }

        private Biomes() {
        }

        public static final TagKey<Biome> SPAWN_ORES = tag("spawn_ores");

        private static TagKey<Biome> tag(String name) {
            return TagUtils.createKey(Registries.BIOME, Mekanism.rl(name));
        }
    }

    public static class DamageTypes {

        private static void init() {
        }

        private DamageTypes() {
        }

        public static final TagKey<DamageType> MEKASUIT_ALWAYS_SUPPORTED = tag("mekasuit_always_supported");
        public static final TagKey<DamageType> IS_PREVENTABLE_MAGIC = tag("is_preventable_magic");

        private static TagKey<DamageType> tag(String name) {
            return TagUtils.createKey(Registries.DAMAGE_TYPE, Mekanism.rl(name));
        }
    }

    public static class Fluids {

        private static void init() {
        }

        private Fluids() {
        }

        public static final TagKey<Fluid> BRINE = cTag("brine");
        public static final TagKey<Fluid> CHLORINE = cTag("chlorine");
        public static final TagKey<Fluid> ETHENE = cTag("ethene");
        public static final TagKey<Fluid> HEAVY_WATER = cTag("heavy_water");
        public static final TagKey<Fluid> HYDROGEN = cTag("hydrogen");
        public static final TagKey<Fluid> HYDROGEN_CHLORIDE = cTag("hydrogen_chloride");
        public static final TagKey<Fluid> URANIUM_OXIDE = cTag("uranium_oxide");
        public static final TagKey<Fluid> URANIUM_HEXAFLUORIDE = cTag("uranium_hexafluoride");
        public static final TagKey<Fluid> LITHIUM = cTag("lithium");
        public static final TagKey<Fluid> OXYGEN = cTag("oxygen");
        public static final TagKey<Fluid> SODIUM = cTag("sodium");
        public static final TagKey<Fluid> SUPERHEATED_SODIUM = cTag("superheated_sodium");
        public static final TagKey<Fluid> STEAM = cTag("steam");
        public static final TagKey<Fluid> SULFUR_DIOXIDE = cTag("sulfur_dioxide");
        public static final TagKey<Fluid> SULFUR_TRIOXIDE = cTag("sulfur_trioxide");
        public static final TagKey<Fluid> SULFURIC_ACID = cTag("sulfuric_acid");
        public static final TagKey<Fluid> HYDROFLUORIC_ACID = cTag("hydrofluoric_acid");

        public static final LazyTagLookup<Fluid> WATER_LOOKUP = LazyTagLookup.create(BuiltInRegistries.FLUID, FluidTags.WATER);
        public static final LazyTagLookup<Fluid> LAVA_LOOKUP = LazyTagLookup.create(BuiltInRegistries.FLUID, FluidTags.LAVA);

        private static TagKey<Fluid> cTag(String name) {
            return TagKey.create(Registries.FLUID, new ResourceLocation("c", name));
        }
    }

    public static class Gases {

        private static void init() {
        }

        private Gases() {
        }

        public static final TagKey<Gas> WATER_VAPOR = tag("water_vapor");
        public static final TagKey<Gas> WASTE_BARREL_DECAY_BLACKLIST = tag("waste_barrel_decay_blacklist");
        public static final LazyTagLookup<Gas> WASTE_BARREL_DECAY_LOOKUP = LazyTagLookup.create(ChemicalTags.GAS, WASTE_BARREL_DECAY_BLACKLIST);

        private static TagKey<Gas> tag(String name) {
            return ChemicalTags.GAS.tag(Mekanism.rl(name));
        }
    }

    public static class InfuseTypes {

        private static void init() {
        }

        private InfuseTypes() {
        }

        public static final TagKey<InfuseType> CARBON = tag("carbon");
        public static final TagKey<InfuseType> REDSTONE = tag("redstone");
        public static final TagKey<InfuseType> DIAMOND = tag("diamond");
        public static final TagKey<InfuseType> REFINED_OBSIDIAN = tag("refined_obsidian");
        public static final TagKey<InfuseType> BIO = tag("bio");
        public static final TagKey<InfuseType> FUNGI = tag("fungi");
        public static final TagKey<InfuseType> GOLD = tag("gold");
        public static final TagKey<InfuseType> TIN = tag("tin");

        private static TagKey<InfuseType> tag(String name) {
            return ChemicalTags.INFUSE_TYPE.tag(Mekanism.rl(name));
        }
    }

    public static class Slurries {

        private static void init() {
        }

        private Slurries() {
        }

        public static final TagKey<Slurry> DIRTY = tag("dirty");
        public static final LazyTagLookup<Slurry> DIRTY_LOOKUP = LazyTagLookup.create(ChemicalTags.SLURRY, DIRTY);
        public static final TagKey<Slurry> CLEAN = tag("clean");

        private static TagKey<Slurry> tag(String name) {
            return ChemicalTags.SLURRY.tag(Mekanism.rl(name));
        }
    }

    public static class MobEffects {

        private static void init() {
        }

        private MobEffects() {
        }

        public static final TagKey<MobEffect> SPEED_UP_BLACKLIST = tag("speed_up_blacklist");
        public static final LazyTagLookup<MobEffect> SPEED_UP_BLACKLIST_LOOKUP = LazyTagLookup.create(BuiltInRegistries.MOB_EFFECT, SPEED_UP_BLACKLIST);

        private static TagKey<MobEffect> tag(String name) {
            return TagUtils.createKey(Registries.MOB_EFFECT, Mekanism.rl(name));
        }
    }

    public static class TileEntityTypes {

        private static void init() {
        }

        private TileEntityTypes() {
        }

        public static final TagKey<BlockEntityType<?>> CARDBOARD_BLACKLIST = tag("cardboard_blacklist");
        public static final LazyTagLookup<BlockEntityType<?>> CARDBOARD_BLACKLIST_LOOKUP = LazyTagLookup.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CARDBOARD_BLACKLIST);
        public static final TagKey<BlockEntityType<?>> RELOCATION_NOT_SUPPORTED = forgeTag("relocation_not_supported");
        public static final TagKey<BlockEntityType<?>> IMMOVABLE = forgeTag("immovable");

        private static TagKey<BlockEntityType<?>> tag(String name) {
            return TagUtils.createKey(Registries.BLOCK_ENTITY_TYPE, Mekanism.rl(name));
        }

        private static TagKey<BlockEntityType<?>> forgeTag(String name) {
            return TagUtils.createKey(Registries.BLOCK_ENTITY_TYPE, new ResourceLocation("forge", name));
        }
    }
}