package mekanism.client.lang;

import com.google.common.collect.Table.Cell;
import java.util.Map;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.providers.IItemProvider;
import mekanism.api.robit.RobitSkin;
import mekanism.api.text.APILang;
import mekanism.api.text.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.advancements.MekanismAdvancements;
import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.entity.RobitPrideSkinData;
import mekanism.common.integration.lookingat.jade.JadeConstants;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.registration.impl.PigmentRegistryObject;
import mekanism.common.registration.impl.SlurryRegistryObject;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismDamageTypes;
import mekanism.common.registries.MekanismDamageTypes.MekanismDamageType;
import mekanism.common.registries.MekanismEntityTypes;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismInfuseTypes;
import mekanism.common.registries.MekanismItems;
import mekanism.common.registries.MekanismModules;
import mekanism.common.registries.MekanismPigments;
import mekanism.common.registries.MekanismRobitSkins;
import mekanism.common.registries.MekanismSlurries;
import mekanism.common.registries.MekanismSounds;
import mekanism.common.resource.IResource;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import mekanism.common.resource.ore.OreBlockType;
import mekanism.common.resource.ore.OreType;
import mekanism.common.tier.FactoryTier;
import mekanism.common.util.EnumUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class MekanismLangProvider extends BaseLanguageProvider {

    public MekanismLangProvider(FabricDataOutput dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        addItems(builder);
        addBlocks(builder);
        addFluids(builder);
        addEntities(builder);
        addGases(builder);
        addInfusionTypes(builder);
        addPigments(builder);
        addSlurries(builder);
        addDamageSources(builder);
        addRobitSkins(builder);
        addSubtitles(builder);
        addMisc(builder);
        addAdvancements(builder);
    }

    private void addItems(TranslationBuilder builder) {
        add(builder, MekanismItems.ROBIT, "Robit");
        add(builder, MekanismItems.ENERGY_TABLET, "Energy Tablet");
        add(builder, MekanismItems.CONFIGURATION_CARD, "Configuration Card");
        add(builder, MekanismItems.CRAFTING_FORMULA, "Crafting Formula");
        add(builder, MekanismItems.TELEPORTATION_CORE, "Teleportation Core");
        add(builder, MekanismItems.ENRICHED_IRON, "Enriched Iron");
        add(builder, MekanismItems.ELECTROLYTIC_CORE, "Electrolytic Core");
        add(builder, MekanismItems.SAWDUST, "Sawdust");
        add(builder, MekanismItems.SALT, "Salt");
        add(builder, MekanismItems.SUBSTRATE, "Substrate");
        add(builder, MekanismItems.BIO_FUEL, "Bio Fuel");
        add(builder, MekanismItems.DYE_BASE, "Dye Base");
        add(builder, MekanismItems.FLUORITE_GEM, "Fluorite");
        add(builder, MekanismItems.YELLOW_CAKE_URANIUM, "Yellow Cake Uranium");
        add(builder, MekanismItems.ANTIMATTER_PELLET, "Antimatter Pellet");
        add(builder, MekanismItems.PLUTONIUM_PELLET, "Plutonium Pellet");
        add(builder, MekanismItems.POLONIUM_PELLET, "Polonium Pellet");
        add(builder, MekanismItems.REPROCESSED_FISSILE_FRAGMENT, "Reprocessed Fissile Fragment");
        add(builder, MekanismItems.MODULE_BASE, "Module Base");
        add(builder, MekanismItems.PORTABLE_QIO_DASHBOARD, "Portable QIO Dashboard");
        //Tools/Armor
        add(builder, MekanismItems.GAUGE_DROPPER, "Gauge Dropper");
        add(builder, MekanismItems.DICTIONARY, "Dictionary");
        add(builder, MekanismItems.CONFIGURATOR, "Configurator");
        add(builder, MekanismItems.NETWORK_READER, "Network Reader");
        add(builder, MekanismItems.SEISMIC_READER, "Seismic Reader");
        add(builder, MekanismItems.PORTABLE_TELEPORTER, "Portable Teleporter");
        add(builder, MekanismItems.ELECTRIC_BOW, "Electric Bow");
        add(builder, MekanismItems.ATOMIC_DISASSEMBLER, "Atomic Disassembler");
        add(builder, MekanismItems.SCUBA_MASK, "Scuba Mask");
        add(builder, MekanismItems.SCUBA_TANK, "Scuba Tank");
        add(builder, MekanismItems.FLAMETHROWER, "Flamethrower");
        add(builder, MekanismItems.FREE_RUNNERS, "Free Runners");
        add(builder, MekanismItems.ARMORED_FREE_RUNNERS, "Armored Free Runners");
        add(builder, MekanismItems.JETPACK, "Jetpack");
        add(builder, MekanismItems.ARMORED_JETPACK, "Armored Jetpack");
        add(builder, MekanismItems.HDPE_REINFORCED_ELYTRA, "HDPE Reinforced Elytra");
        add(builder, MekanismItems.GEIGER_COUNTER, "Geiger Counter");
        add(builder, MekanismItems.DOSIMETER, "Dosimeter");
        add(builder, MekanismItems.CANTEEN, "Canteen");
        add(builder, MekanismItems.MEKA_TOOL, "Meka-Tool");
        add(builder, MekanismItems.HAZMAT_MASK, "Hazmat Mask");
        add(builder, MekanismItems.HAZMAT_GOWN, "Hazmat Gown");
        add(builder, MekanismItems.HAZMAT_PANTS, "Hazmat Pants");
        add(builder, MekanismItems.HAZMAT_BOOTS, "Hazmat Boots");
        add(builder, MekanismItems.MEKASUIT_HELMET, "MekaSuit Helmet");
        add(builder, MekanismItems.MEKASUIT_BODYARMOR, "MekaSuit Bodyarmor");
        add(builder, MekanismItems.MEKASUIT_PANTS, "MekaSuit Pants");
        add(builder, MekanismItems.MEKASUIT_BOOTS, "MekaSuit Boots");
        //Drives
        add(builder, MekanismItems.BASE_QIO_DRIVE, "QIO Drive");
        add(builder, MekanismItems.HYPER_DENSE_QIO_DRIVE, "Hyper-Dense QIO Drive");
        add(builder, MekanismItems.TIME_DILATING_QIO_DRIVE, "Time-Dilating QIO Drive");
        add(builder, MekanismItems.SUPERMASSIVE_QIO_DRIVE, "Supermassive QIO Drive");
        //HDPE
        add(builder, MekanismItems.HDPE_PELLET, "HDPE Pellet");
        add(builder, MekanismItems.HDPE_ROD, "HDPE Rod");
        add(builder, MekanismItems.HDPE_SHEET, "HDPE Sheet");
        add(builder, MekanismItems.HDPE_STICK, "PlaStick");
        //Enriched Items
        add(builder, MekanismItems.ENRICHED_CARBON, "Enriched Carbon");
        add(builder, MekanismItems.ENRICHED_REDSTONE, "Enriched Redstone");
        add(builder, MekanismItems.ENRICHED_DIAMOND, "Enriched Diamond");
        add(builder, MekanismItems.ENRICHED_OBSIDIAN, "Enriched Obsidian");
        add(builder, MekanismItems.ENRICHED_GOLD, "Enriched Gold");
        add(builder, MekanismItems.ENRICHED_TIN, "Enriched Tin");
        //Upgrades
        add(builder, MekanismItems.SPEED_UPGRADE, "Speed Upgrade");
        add(builder, MekanismItems.ENERGY_UPGRADE, "Energy Upgrade");
        add(builder, MekanismItems.FILTER_UPGRADE, "Filter Upgrade");
        add(builder, MekanismItems.MUFFLING_UPGRADE, "Muffling Upgrade");
        add(builder, MekanismItems.GAS_UPGRADE, "Gas Upgrade");
        add(builder, MekanismItems.ANCHOR_UPGRADE, "Anchor Upgrade");
        add(builder, MekanismItems.STONE_GENERATOR_UPGRADE, "Stone Generator Upgrade");
        //Alloys
        add(builder, MekanismItems.INFUSED_ALLOY, "Infused Alloy");
        add(builder, MekanismItems.REINFORCED_ALLOY, "Reinforced Alloy");
        add(builder, MekanismItems.ATOMIC_ALLOY, "Atomic Alloy");
        //Ingots
        add(builder, MekanismItems.REFINED_OBSIDIAN_INGOT, "Refined Obsidian Ingot");
        add(builder, MekanismItems.BRONZE_INGOT, "Bronze Ingot");
        add(builder, MekanismItems.REFINED_GLOWSTONE_INGOT, "Refined Glowstone Ingot");
        add(builder, MekanismItems.STEEL_INGOT, "Steel Ingot");
        //Nuggets
        add(builder, MekanismItems.REFINED_OBSIDIAN_NUGGET, "Refined Obsidian Nugget");
        add(builder, MekanismItems.BRONZE_NUGGET, "Bronze Nugget");
        add(builder, MekanismItems.REFINED_GLOWSTONE_NUGGET, "Refined Glowstone Nugget");
        add(builder, MekanismItems.STEEL_NUGGET, "Steel Nugget");
        //Dusts
        add(builder, MekanismItems.BRONZE_DUST, "Bronze Dust");
        add(builder, MekanismItems.LAPIS_LAZULI_DUST, "Lapis Lazuli Dust");
        add(builder, MekanismItems.COAL_DUST, "Coal Dust");
        add(builder, MekanismItems.CHARCOAL_DUST, "Charcoal Dust");
        add(builder, MekanismItems.QUARTZ_DUST, "Quartz Dust");
        add(builder, MekanismItems.EMERALD_DUST, "Emerald Dust");
        add(builder, MekanismItems.DIAMOND_DUST, "Diamond Dust");
        add(builder, MekanismItems.NETHERITE_DUST, "Netherite Dust");
        add(builder, MekanismItems.STEEL_DUST, "Steel Dust");
        add(builder, MekanismItems.SULFUR_DUST, "Sulfur Dust");
        add(builder, MekanismItems.LITHIUM_DUST, "Lithium Dust");
        add(builder, MekanismItems.REFINED_OBSIDIAN_DUST, "Refined Obsidian Dust");
        add(builder, MekanismItems.OBSIDIAN_DUST, "Obsidian Dust");
        add(builder, MekanismItems.FLUORITE_DUST, "Fluorite Dust");
        //Scrap
        add(builder, MekanismItems.DIRTY_NETHERITE_SCRAP, "Dirty Netherite Scrap");
        //Tiered stuff
        addTiered(builder, MekanismItems.BASIC_CONTROL_CIRCUIT, MekanismItems.ADVANCED_CONTROL_CIRCUIT, MekanismItems.ELITE_CONTROL_CIRCUIT, MekanismItems.ULTIMATE_CONTROL_CIRCUIT, "Control Circuit");
        addTiered(builder, MekanismItems.BASIC_TIER_INSTALLER, MekanismItems.ADVANCED_TIER_INSTALLER, MekanismItems.ELITE_TIER_INSTALLER, MekanismItems.ULTIMATE_TIER_INSTALLER, "Tier Installer");

        for (Cell<ResourceType, PrimaryResource, ItemRegistryObject<Item>> item : MekanismItems.PROCESSED_RESOURCES.cellSet()) {
            String resourceName = formatAndCapitalize(item.getColumnKey().getRegistrySuffix());
            add(builder, item.getValue(), switch (item.getRowKey()) {
                case SHARD -> resourceName + " Shard";
                case CRYSTAL -> resourceName + " Crystal";
                case DUST -> resourceName + " Dust";
                case DIRTY_DUST -> "Dirty " + resourceName + " Dust";
                case CLUMP -> resourceName + " Clump";
                case INGOT -> resourceName + " Ingot";
                case RAW -> "Raw " + resourceName;
                case NUGGET -> resourceName + " Nugget";
                default -> throw new IllegalStateException("Unexpected resource type for primary resource.");
            });
        }
    }

    private static String formatAndCapitalize(String s) {
        boolean isFirst = true;
        StringBuilder ret = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '_') {
                isFirst = true;
                ret.append(' ');
            } else {
                ret.append(isFirst ? Character.toUpperCase(c) : c);
                isFirst = false;
            }
        }
        return ret.toString();
    }

    private void addBlocks(TranslationBuilder builder) {
        add(builder, MekanismBlocks.BOILER_CASING, "Boiler Casing");
        add(builder, MekanismBlocks.BOILER_VALVE, "Boiler Valve");
        add(builder, MekanismBlocks.CARDBOARD_BOX, "Cardboard Box");
        add(builder, MekanismBlocks.CHARGEPAD, "Chargepad");
        add(builder, MekanismBlocks.CHEMICAL_CRYSTALLIZER, "Chemical Crystallizer");
        add(builder, MekanismBlocks.CHEMICAL_DISSOLUTION_CHAMBER, "Chemical Dissolution Chamber", "C. Dissolution Chamber");
        add(builder, MekanismBlocks.CHEMICAL_INFUSER, "Chemical Infuser", "C. Infuser");
        add(builder, MekanismBlocks.CHEMICAL_INJECTION_CHAMBER, "Chemical Injection Chamber");
        add(builder, MekanismBlocks.CHEMICAL_OXIDIZER, "Chemical Oxidizer");
        add(builder, MekanismBlocks.CHEMICAL_WASHER, "Chemical Washer");
        add(builder, MekanismBlocks.COMBINER, "Combiner");
        add(builder, MekanismBlocks.CRUSHER, "Crusher");
        add(builder, MekanismBlocks.DIGITAL_MINER, "Digital Miner");
        add(builder, MekanismBlocks.DYNAMIC_TANK, "Dynamic Tank");
        add(builder, MekanismBlocks.DYNAMIC_VALVE, "Dynamic Valve");
        add(builder, MekanismBlocks.ELECTRIC_PUMP, "Electric Pump");
        add(builder, MekanismBlocks.ELECTROLYTIC_SEPARATOR, "Electrolytic Separator");
        add(builder, MekanismBlocks.ENERGIZED_SMELTER, "Energized Smelter");
        add(builder, MekanismBlocks.ENRICHMENT_CHAMBER, "Enrichment Chamber");
        add(builder, MekanismBlocks.FLUIDIC_PLENISHER, "Fluidic Plenisher");
        add(builder, MekanismBlocks.FORMULAIC_ASSEMBLICATOR, "Formulaic Assemblicator");
        add(builder, MekanismBlocks.FUELWOOD_HEATER, "Fuelwood Heater");
        add(builder, MekanismBlocks.INDUCTION_CASING, "Induction Casing");
        add(builder, MekanismBlocks.INDUCTION_PORT, "Induction Port");
        add(builder, MekanismBlocks.LASER, "Laser");
        add(builder, MekanismBlocks.LASER_AMPLIFIER, "Laser Amplifier");
        add(builder, MekanismBlocks.LASER_TRACTOR_BEAM, "Laser Tractor Beam");
        add(builder, MekanismBlocks.LOGISTICAL_SORTER, "Logistical Sorter");
        add(builder, MekanismBlocks.METALLURGIC_INFUSER, "Metallurgic Infuser");
        add(builder, MekanismBlocks.OREDICTIONIFICATOR, "Oredictionificator");
        add(builder, MekanismBlocks.OSMIUM_COMPRESSOR, "Osmium Compressor");
        add(builder, MekanismBlocks.PAINTING_MACHINE, "Painting Machine");
        add(builder, MekanismBlocks.PERSONAL_BARREL, "Personal Barrel");
        add(builder, MekanismBlocks.PERSONAL_CHEST, "Personal Chest");
        add(builder, MekanismBlocks.PIGMENT_EXTRACTOR, "Pigment Extractor");
        add(builder, MekanismBlocks.PIGMENT_MIXER, "Pigment Mixer");
        add(builder, MekanismBlocks.PRECISION_SAWMILL, "Precision Sawmill");
        add(builder, MekanismBlocks.PRESSURE_DISPERSER, "Pressure Disperser");
        add(builder, MekanismBlocks.PRESSURIZED_REACTION_CHAMBER, "Pressurized Reaction Chamber");
        add(builder, MekanismBlocks.PURIFICATION_CHAMBER, "Purification Chamber");
        add(builder, MekanismBlocks.QUANTUM_ENTANGLOPORTER, "Quantum Entangloporter");
        add(builder, MekanismBlocks.RESISTIVE_HEATER, "Resistive Heater");
        add(builder, MekanismBlocks.MODIFICATION_STATION, "Modification Station");
        add(builder, MekanismBlocks.ISOTOPIC_CENTRIFUGE, "Isotopic Centrifuge");
        add(builder, MekanismBlocks.NUTRITIONAL_LIQUIFIER, "Nutritional Liquifier");
        add(builder, MekanismBlocks.ROTARY_CONDENSENTRATOR, "Rotary Condensentrator");
        add(builder, MekanismBlocks.SALT_BLOCK, "Salt Block");
        add(builder, MekanismBlocks.SECURITY_DESK, "Security Desk");
        add(builder, MekanismBlocks.SEISMIC_VIBRATOR, "Seismic Vibrator");
        add(builder, MekanismBlocks.SOLAR_NEUTRON_ACTIVATOR, "Solar Neutron Activator");
        add(builder, MekanismBlocks.STEEL_CASING, "Steel Casing");
        add(builder, MekanismBlocks.STRUCTURAL_GLASS, "Structural Glass");
        add(builder, MekanismBlocks.SUPERHEATING_ELEMENT, "Superheating Element");
        add(builder, MekanismBlocks.TELEPORTER, "Teleporter");
        add(builder, MekanismBlocks.TELEPORTER_FRAME, "Teleporter Frame");
        add(builder, MekanismBlocks.THERMAL_EVAPORATION_BLOCK, "Thermal Evaporation Block");
        add(builder, MekanismBlocks.THERMAL_EVAPORATION_CONTROLLER, "Thermal Evaporation Controller");
        add(builder, MekanismBlocks.THERMAL_EVAPORATION_VALVE, "Thermal Evaporation Valve");
        add(builder, MekanismBlocks.RADIOACTIVE_WASTE_BARREL, "Radioactive Waste Barrel");
        add(builder, MekanismBlocks.INDUSTRIAL_ALARM, "Industrial Alarm");
        add(builder, MekanismBlocks.ANTIPROTONIC_NUCLEOSYNTHESIZER, "Antiprotonic Nucleosynthesizer");
        add(builder, MekanismBlocks.QIO_DRIVE_ARRAY, "QIO Drive Array");
        add(builder, MekanismBlocks.QIO_DASHBOARD, "QIO Dashboard");
        add(builder, MekanismBlocks.QIO_IMPORTER, "QIO Importer");
        add(builder, MekanismBlocks.QIO_EXPORTER, "QIO Exporter");
        add(builder, MekanismBlocks.QIO_REDSTONE_ADAPTER, "QIO Redstone Adapter");
        add(builder, MekanismBlocks.SPS_CASING, "SPS Casing");
        add(builder, MekanismBlocks.SPS_PORT, "SPS Port");
        add(builder, MekanismBlocks.SUPERCHARGED_COIL, "Supercharged Coil");
        add(builder, MekanismBlocks.DIMENSIONAL_STABILIZER, "Dimensional Stabilizer");
        //Bounding block (I don't think these lang keys actually will ever be used, but set them just in case)
        add(builder, MekanismBlocks.BOUNDING_BLOCK, "Bounding Block");
        //Ores
        addOre(builder, OreType.OSMIUM, "A strong mineral that can be found at nearly any height in the world. It is known to have many uses in the construction of machinery.");
        addOre(builder, OreType.TIN, "A lightweight, yet sturdy, conductive material.");
        addOre(builder, OreType.FLUORITE, "A mineral found relatively deep under the world's surface. The crystals can be processed into Hydrofluoric Acid, an essential chemical for Uranium processing.");
        addOre(builder, OreType.URANIUM, "A common, heavy metal, which can yield massive amounts of energy when properly processed. In its naturally-occurring form, it is not radioactive enough to cause harm.");
        addOre(builder, OreType.LEAD, "A somewhat rare metal that is excellent at resisting radioactive particles.");
        //Storage blocks
        add(builder, MekanismBlocks.BRONZE_BLOCK, "Bronze Block");
        add(builder, MekanismBlocks.REFINED_OBSIDIAN_BLOCK, "Refined Obsidian");
        add(builder, MekanismBlocks.CHARCOAL_BLOCK, "Charcoal Block");
        add(builder, MekanismBlocks.REFINED_GLOWSTONE_BLOCK, "Refined Glowstone");
        add(builder, MekanismBlocks.STEEL_BLOCK, "Steel Block");
        add(builder, MekanismBlocks.FLUORITE_BLOCK, "Fluorite Block");
        //Dynamic storage blocks
        for (Map.Entry<IResource, BlockRegistryObject<?, ?>> entry : MekanismBlocks.PROCESSED_RESOURCE_BLOCKS.entrySet()) {
            add(builder, entry.getValue(), formatAndCapitalize(entry.getKey().getRegistrySuffix()) + " Block");
        }

        //Tiered things
        addTiered(builder, MekanismBlocks.BASIC_INDUCTION_CELL, MekanismBlocks.ADVANCED_INDUCTION_CELL, MekanismBlocks.ELITE_INDUCTION_CELL, MekanismBlocks.ULTIMATE_INDUCTION_CELL, "Induction Cell");
        addTiered(builder, MekanismBlocks.BASIC_INDUCTION_PROVIDER, MekanismBlocks.ADVANCED_INDUCTION_PROVIDER, MekanismBlocks.ELITE_INDUCTION_PROVIDER, MekanismBlocks.ULTIMATE_INDUCTION_PROVIDER, "Induction Provider");
        addTiered(builder, MekanismBlocks.BASIC_BIN, MekanismBlocks.ADVANCED_BIN, MekanismBlocks.ELITE_BIN, MekanismBlocks.ULTIMATE_BIN, MekanismBlocks.CREATIVE_BIN, "Bin");
        addTiered(builder, MekanismBlocks.BASIC_ENERGY_CUBE, MekanismBlocks.ADVANCED_ENERGY_CUBE, MekanismBlocks.ELITE_ENERGY_CUBE, MekanismBlocks.ULTIMATE_ENERGY_CUBE, MekanismBlocks.CREATIVE_ENERGY_CUBE, "Energy Cube");
        addTiered(builder, MekanismBlocks.BASIC_FLUID_TANK, MekanismBlocks.ADVANCED_FLUID_TANK, MekanismBlocks.ELITE_FLUID_TANK, MekanismBlocks.ULTIMATE_FLUID_TANK, MekanismBlocks.CREATIVE_FLUID_TANK, "Fluid Tank");
        addTiered(builder, MekanismBlocks.BASIC_CHEMICAL_TANK, MekanismBlocks.ADVANCED_CHEMICAL_TANK, MekanismBlocks.ELITE_CHEMICAL_TANK, MekanismBlocks.ULTIMATE_CHEMICAL_TANK, MekanismBlocks.CREATIVE_CHEMICAL_TANK, "Chemical Tank");
        //Factories
        for (FactoryTier tier : EnumUtils.FACTORY_TIERS) {
            for (FactoryType type : EnumUtils.FACTORY_TYPES) {
                add(builder, MekanismBlocks.getFactory(tier, type), tier.getBaseTier().getSimpleName() + " " + type.getRegistryNameComponentCapitalized() + " Factory");
            }
        }
        //Transmitters
        add(builder, MekanismBlocks.RESTRICTIVE_TRANSPORTER, "Restrictive Transporter");
        add(builder, MekanismBlocks.DIVERSION_TRANSPORTER, "Diversion Transporter");
        addTiered(builder, MekanismBlocks.BASIC_UNIVERSAL_CABLE, MekanismBlocks.ADVANCED_UNIVERSAL_CABLE, MekanismBlocks.ELITE_UNIVERSAL_CABLE, MekanismBlocks.ULTIMATE_UNIVERSAL_CABLE, "Universal Cable");
        addTiered(builder, MekanismBlocks.BASIC_MECHANICAL_PIPE, MekanismBlocks.ADVANCED_MECHANICAL_PIPE, MekanismBlocks.ELITE_MECHANICAL_PIPE, MekanismBlocks.ULTIMATE_MECHANICAL_PIPE, "Mechanical Pipe");
        addTiered(builder, MekanismBlocks.BASIC_PRESSURIZED_TUBE, MekanismBlocks.ADVANCED_PRESSURIZED_TUBE, MekanismBlocks.ELITE_PRESSURIZED_TUBE, MekanismBlocks.ULTIMATE_PRESSURIZED_TUBE, "Pressurized Tube");
        addTiered(builder, MekanismBlocks.BASIC_LOGISTICAL_TRANSPORTER, MekanismBlocks.ADVANCED_LOGISTICAL_TRANSPORTER, MekanismBlocks.ELITE_LOGISTICAL_TRANSPORTER, MekanismBlocks.ULTIMATE_LOGISTICAL_TRANSPORTER, "Logistical Transporter");
        addTiered(builder, MekanismBlocks.BASIC_THERMODYNAMIC_CONDUCTOR, MekanismBlocks.ADVANCED_THERMODYNAMIC_CONDUCTOR, MekanismBlocks.ELITE_THERMODYNAMIC_CONDUCTOR, MekanismBlocks.ULTIMATE_THERMODYNAMIC_CONDUCTOR, "Thermodynamic Conductor");
    }

    private void addFluids(TranslationBuilder builder) {
        addFluid(builder, MekanismFluids.HYDROGEN, "Liquid Hydrogen");
        addFluid(builder, MekanismFluids.OXYGEN, "Liquid Oxygen");
        addFluid(builder, MekanismFluids.CHLORINE, "Liquid Chlorine");
        addFluid(builder, MekanismFluids.SULFUR_DIOXIDE, "Liquid Sulfur Dioxide");
        addFluid(builder, MekanismFluids.SULFUR_TRIOXIDE, "Liquid Sulfur Trioxide");
        addFluid(builder, MekanismFluids.SULFURIC_ACID, "Liquid Sulfuric Acid");
        addFluid(builder, MekanismFluids.HYDROGEN_CHLORIDE, "Liquid Hydrogen Chloride");
        addFluid(builder, MekanismFluids.HYDROFLUORIC_ACID, "Liquid Hydrofluoric Acid");
        addFluid(builder, MekanismFluids.URANIUM_OXIDE, "Liquid Uranium Oxide");
        addFluid(builder, MekanismFluids.URANIUM_HEXAFLUORIDE, "Liquid Uranium Hexafluoride");
        addFluid(builder, MekanismFluids.ETHENE, "Liquid Ethylene");
        addFluid(builder, MekanismFluids.SODIUM, "Liquid Sodium");
        addFluid(builder, MekanismFluids.SUPERHEATED_SODIUM, "Liquid Superheated Sodium");
        addFluid(builder, MekanismFluids.BRINE, "Brine");
        addFluid(builder, MekanismFluids.LITHIUM, "Liquid Lithium");
        addFluid(builder, MekanismFluids.STEAM, "Liquid Steam");
        addFluid(builder, MekanismFluids.HEAVY_WATER, "Heavy Water");
        addFluid(builder, MekanismFluids.NUTRITIONAL_PASTE, "Nutritional Paste");
    }

    private void addEntities(TranslationBuilder builder) {
        add(builder, MekanismEntityTypes.FLAME, "Flamethrower Flame");
        add(builder, MekanismEntityTypes.ROBIT, "Robit");
    }

    private void addGases(TranslationBuilder builder) {
        add(builder, MekanismAPI.EMPTY_GAS, "Empty");
        add(builder, MekanismGases.HYDROGEN, "Hydrogen");
        add(builder, MekanismGases.OXYGEN, "Oxygen");
        add(builder, MekanismGases.STEAM, "Steam");
        add(builder, MekanismGases.WATER_VAPOR, "Water Vapor");
        add(builder, MekanismGases.CHLORINE, "Chlorine");
        add(builder, MekanismGases.SULFUR_DIOXIDE, "Sulfur Dioxide");
        add(builder, MekanismGases.SULFUR_TRIOXIDE, "Sulfur Trioxide");
        add(builder, MekanismGases.SULFURIC_ACID, "Sulfuric Acid");
        add(builder, MekanismGases.HYDROGEN_CHLORIDE, "Hydrogen Chloride");
        add(builder, MekanismGases.HYDROFLUORIC_ACID, "Hydrofluoric Acid");
        add(builder, MekanismGases.URANIUM_OXIDE, "Uranium Oxide");
        add(builder, MekanismGases.URANIUM_HEXAFLUORIDE, "Uranium Hexafluoride");
        add(builder, MekanismGases.ETHENE, "Ethylene");
        add(builder, MekanismGases.SODIUM, "Sodium");
        add(builder, MekanismGases.SUPERHEATED_SODIUM, "Superheated Sodium");
        add(builder, MekanismGases.BRINE, "Gaseous Brine");
        add(builder, MekanismGases.LITHIUM, "Lithium");
        add(builder, MekanismGases.OSMIUM, "Osmium");
        add(builder, MekanismGases.FISSILE_FUEL, "Fissile Fuel");
        add(builder, MekanismGases.NUCLEAR_WASTE, "Nuclear Waste");
        add(builder, MekanismGases.SPENT_NUCLEAR_WASTE, "Spent Nuclear Waste");
        add(builder, MekanismGases.ANTIMATTER, "Antimatter");
        add(builder, MekanismGases.PLUTONIUM, "Plutonium");
        add(builder, MekanismGases.POLONIUM, "Polonium");
    }

    private void addInfusionTypes(TranslationBuilder builder) {
        add(builder, MekanismAPI.EMPTY_INFUSE_TYPE, "Empty");
        add(builder, MekanismInfuseTypes.CARBON, "Carbon");
        add(builder, MekanismInfuseTypes.REDSTONE, "Redstone");
        add(builder, MekanismInfuseTypes.DIAMOND, "Diamond");
        add(builder, MekanismInfuseTypes.REFINED_OBSIDIAN, "Refined Obsidian");
        add(builder, MekanismInfuseTypes.GOLD, "Gold");
        add(builder, MekanismInfuseTypes.TIN, "Tin");
        add(builder, MekanismInfuseTypes.FUNGI, "Fungi");
        add(builder, MekanismInfuseTypes.BIO, "Biomass");
    }

    private void addPigments(TranslationBuilder builder) {
        add(builder, MekanismAPI.EMPTY_PIGMENT, "Empty");
        for (Map.Entry<EnumColor, PigmentRegistryObject<Pigment>> entry : MekanismPigments.PIGMENT_COLOR_LOOKUP.entrySet()) {
            add(builder, entry.getValue(), entry.getKey().getEnglishName() + " Pigment");
        }
    }

    private void addSlurries(TranslationBuilder builder) {
        add(builder, MekanismAPI.EMPTY_SLURRY, "Empty");
        for (Map.Entry<PrimaryResource, SlurryRegistryObject<Slurry, Slurry>> entry : MekanismSlurries.PROCESSED_RESOURCES.entrySet()) {
            addSlurry(builder, entry.getValue(), formatAndCapitalize(entry.getKey().getRegistrySuffix()));
        }
    }

    private void addSlurry(TranslationBuilder builder, SlurryRegistryObject<Slurry, Slurry> slurryRO, String name) {
        add(builder, slurryRO.getDirtySlurry(), "Dirty " + name + " Slurry");
        add(builder, slurryRO.getCleanSlurry(), "Clean " + name + " Slurry");
    }

    private void addDamageSources(TranslationBuilder builder) {
        add(builder, MekanismDamageTypes.LASER, "%1$s was incinerated.", "%1$s was incinerated whilst trying to escape %2$s.");
        add(builder, MekanismDamageTypes.RADIATION, "%1$s was killed by radiation poisoning.", "%1$s was killed by radiation poisoning whilst trying to escape %2$s.");
    }

    private void add(TranslationBuilder builder, MekanismDamageType damageType, String value, String valueEscaping) {
        add(builder, damageType, value);
        add(builder, damageType.getTranslationKey() + ".player", valueEscaping);
    }

    private void addRobitSkins(TranslationBuilder builder) {
        addRobitSkin(builder, MekanismRobitSkins.BASE, "Default");
        addRobitSkin(builder, MekanismRobitSkins.ALLAY, "Allay Costume");
        for (Map.Entry<RobitPrideSkinData, ResourceKey<RobitSkin>> entry : MekanismRobitSkins.PRIDE_SKINS.entrySet()) {
            ResourceKey<RobitSkin> prideSkin = entry.getValue();
            String name = formatAndCapitalize(prideSkin.location().getPath());
            if (entry.getKey() != RobitPrideSkinData.PRIDE) {
                name += " Pride";
            }
            addRobitSkin(builder, prideSkin, name);
        }
    }

    private void addRobitSkin(TranslationBuilder builder, ResourceKey<RobitSkin> name, String value) {
        add(builder, RobitSkin.getTranslationKey(name), value);
    }

    private void addSubtitles(TranslationBuilder builder) {
        //Tiles
        add(builder, MekanismSounds.CHARGEPAD, "Chargepad hums");
        add(builder, MekanismSounds.CHEMICAL_CRYSTALLIZER, "Crystallizer hums");
        add(builder, MekanismSounds.CHEMICAL_DISSOLUTION_CHAMBER, "Dissolution Chamber hums");
        add(builder, MekanismSounds.CHEMICAL_INFUSER, "Chemical Infuser hums");
        add(builder, MekanismSounds.CHEMICAL_INJECTION_CHAMBER, "Injection Chamber processes");
        add(builder, MekanismSounds.CHEMICAL_OXIDIZER, "Oxidizer hums");
        add(builder, MekanismSounds.CHEMICAL_WASHER, "Washer hums");
        add(builder, MekanismSounds.COMBINER, "Combiner hums");
        add(builder, MekanismSounds.OSMIUM_COMPRESSOR, "Compressor hums");
        add(builder, MekanismSounds.CRUSHER, "Crusher clangs");
        add(builder, MekanismSounds.ELECTROLYTIC_SEPARATOR, "Separator separates");
        add(builder, MekanismSounds.ENRICHMENT_CHAMBER, "Enricher hums");
        add(builder, MekanismSounds.LASER, "Laser hums");
        add(builder, MekanismSounds.LOGISTICAL_SORTER, "Sorter clicks");
        add(builder, MekanismSounds.METALLURGIC_INFUSER, "Metallurgic infuser hums");
        add(builder, MekanismSounds.PRECISION_SAWMILL, "Sawmill cuts");
        add(builder, MekanismSounds.PRESSURIZED_REACTION_CHAMBER, "Reaction chamber hums");
        add(builder, MekanismSounds.PURIFICATION_CHAMBER, "Purifier hums");
        add(builder, MekanismSounds.RESISTIVE_HEATER, "Heater hums");
        add(builder, MekanismSounds.ROTARY_CONDENSENTRATOR, "Condensentrator rotates");
        add(builder, MekanismSounds.ENERGIZED_SMELTER, "Smelter whines");
        add(builder, MekanismSounds.ISOTOPIC_CENTRIFUGE, "Centrifuge spins");
        add(builder, MekanismSounds.NUTRITIONAL_LIQUIFIER, "Nutrients liquified");
        add(builder, MekanismSounds.INDUSTRIAL_ALARM, "Alarm sounds");
        add(builder, MekanismSounds.ANTIPROTONIC_NUCLEOSYNTHESIZER, "Nucleosynthesizer hums");
        add(builder, MekanismSounds.PIGMENT_EXTRACTOR, "Pigment extractor extracts");
        add(builder, MekanismSounds.PIGMENT_MIXER, "Pigment mixer sloshes");
        add(builder, MekanismSounds.PAINTING_MACHINE, "Painting machine sprays");
        add(builder, MekanismSounds.SPS, "SPS hums");
        //Gear
        add(builder, MekanismSounds.FLAMETHROWER_IDLE, "Flamethrower hisses");
        add(builder, MekanismSounds.FLAMETHROWER_ACTIVE, "Flamethrower burns");
        add(builder, MekanismSounds.SCUBA_MASK, "Air flows");
        add(builder, MekanismSounds.JETPACK, "Jetpack burns");
        add(builder, MekanismSounds.HYDRAULIC, "Hydraulic shifts");
        add(builder, MekanismSounds.GRAVITATIONAL_MODULATION_UNIT, "Gravity modulates");
        //Geiger
        add(builder, MekanismSounds.GEIGER_SLOW, "Geiger counter clicks slowly");
        add(builder, MekanismSounds.GEIGER_MEDIUM, "Geiger Counter clicks");
        add(builder, MekanismSounds.GEIGER_ELEVATED, "Elevated Geiger Counter clicks");
        add(builder, MekanismSounds.GEIGER_FAST, "Constant Geiger Counter clicks");
    }

    private void addAdvancements(TranslationBuilder builder) {
        add(builder, MekanismAdvancements.ROOT, basicModName, "Welcome to " + basicModName + "!");
        add(builder, MekanismAdvancements.MATERIALS, "First Steps", "Acquire some natural " + basicModName + " resources");

        add(builder, MekanismAdvancements.CLEANING_GAUGES, "Cleaning Gauges", "Use a Gauge Dropper on any Gauge in a " + basicModName + " GUI");

        add(builder, MekanismAdvancements.METALLURGIC_INFUSER, "A Metallur-what?", "Craft a Metallurgic Infuser");
        add(builder, MekanismAdvancements.STEEL_INGOT, "Industrial Revolution", "Infuse Iron with Carbon and repeat");
        add(builder, MekanismAdvancements.STEEL_CASING, "The Perfect Foundation", "Used in even the most advanced machines");

        add(builder, MekanismAdvancements.INFUSED_ALLOY, "The Alloy That Started it All", "Infuse Iron with Redstone");
        add(builder, MekanismAdvancements.REINFORCED_ALLOY, "Make it Stronger", "Diamonds make everything better!");
        add(builder, MekanismAdvancements.ATOMIC_ALLOY, "Top Tier Alloy", "Create one of the strongest alloys in existence");

        add(builder, MekanismAdvancements.BASIC_CONTROL_CIRCUIT, "Tricking a Rock Into Thinking", "Craft a Basic Control Circuit");
        add(builder, MekanismAdvancements.ADVANCED_CONTROL_CIRCUIT, "He's Really Advanced For His Age", "Craft an Advanced Control Circuit");
        add(builder, MekanismAdvancements.ELITE_CONTROL_CIRCUIT, "Make it Precise", "Create a circuit with even more pathways");
        add(builder, MekanismAdvancements.ULTIMATE_CONTROL_CIRCUIT, "Where's my Supercomputer?", "Can this thing run Minecraft yet?");

        add(builder, MekanismAdvancements.ALLOY_UPGRADING, "In Place Transmitter Upgrades", "Upgrade transmitters in world using the next tier of alloy");
        add(builder, MekanismAdvancements.LASER, "Pew Pew!", "Craft a Laser");
        add(builder, MekanismAdvancements.STOPPING_LASERS, "Get Outta My Spectrum!", "Block a Laser with a shield");
        add(builder, MekanismAdvancements.LASER_DEATH, "Mmmmmm, Crispy!", "Die by Laser");
        add(builder, MekanismAdvancements.AUTO_COLLECTION, "It's pulling us in!", "Tractor beams pull in the drops of blocks they break");

        add(builder, MekanismAdvancements.ALARM, "Woop Woop Woop!", "Alarms are loud, especially the industrial kind");
        add(builder, MekanismAdvancements.INSTALLER, "Your Distinctiveness Will Be Added to Our Own", "Craft any tier of Installer to upgrade your factories in place");
        add(builder, MekanismAdvancements.FACTORY, "The Factory Must Grow!", "Craft any kind of factory");
        add(builder, MekanismAdvancements.CONFIGURATION_COPYING, "Ctrl+C, Ctrl+V", "Use a configuration card to copy the configuration of one machine to another");
        add(builder, MekanismAdvancements.RUNNING_FREE, "Yeah I'm Freeeeee Fallin'", "Protect yourself from falling with a pair of Free Runners");
        add(builder, MekanismAdvancements.PLAYING_WITH_FIRE, "Playing With Fire", "Be responsible and don't burn down any forests; or do, we're not your manager");
        add(builder, MekanismAdvancements.MACHINE_SECURITY, "The Desk Job", "Create a Security Desk to more easily secure your machines");
        add(builder, MekanismAdvancements.SOLAR_NEUTRON_ACTIVATOR, "Does Not Use Neutrinos", "Craft a Solar Neutron Activator");
        add(builder, MekanismAdvancements.STABILIZING_CHUNKS, "Stabilizing The Universe", "Craft a Dimensional Stabilizer and an Anchor Upgrade");

        add(builder, MekanismAdvancements.PERSONAL_STORAGE, "Mine, Mine! All Mine", "Create a personal storage item to securely store items");
        add(builder, MekanismAdvancements.SIMPLE_MASS_STORAGE, "You Could Fit Your Whole House In There", "Create a bin to store large amounts of one item");

        add(builder, MekanismAdvancements.CONFIGURATOR, "Configure Everything", "Craft a configurator to change the settings of blocks");
        add(builder, MekanismAdvancements.NETWORK_READER, "Reading the Network", "View the contents of a transmitter network");
        add(builder, MekanismAdvancements.FLUID_TANK, "Bigger Buckets", "Make a Fluid Tank to store your fluids");
        add(builder, MekanismAdvancements.CHEMICAL_TANK, "Inhalation Not Recommended", "Craft a place to store (almost) all your chemicals");

        add(builder, MekanismAdvancements.BREATHING_ASSISTANCE, "Breathe Easy", "Craft some Scuba Gear to refill your oxygen supply under water and filter out contaminants");
        add(builder, MekanismAdvancements.HYDROGEN_POWERED_FLIGHT, "Hydrogen Powered Flight", "Use a Jetpack to take to the skies");

        add(builder, MekanismAdvancements.WASTE_REMOVAL, "Waste Removal", "Safe storage for your radioactive chemicals and disposal of Nuclear Waste");
        add(builder, MekanismAdvancements.ENVIRONMENTAL_RADIATION, "Oops, Did I Do That?", "Use a Geiger Counter to see how badly your experiments irradiated the environment");
        add(builder, MekanismAdvancements.PERSONAL_RADIATION, "That Wasn't Smart", "Use a Dosimeter to see how badly you irradiated yourself");
        add(builder, MekanismAdvancements.RADIATION_PREVENTION, "Unfashionable, But Smart", "Protect yourself from radiation with a Hazmat Suit");
        add(builder, MekanismAdvancements.RADIATION_POISONING, "Does This Taste Like Metal to You?", "Take damage from radiation poisoning");
        add(builder, MekanismAdvancements.RADIATION_POISONING_DEATH, "Not Great, Not Terrible", "Die by radiation poisoning");

        add(builder, MekanismAdvancements.PLUTONIUM, "Plutonium, Not Polonium", "Refine your Nuclear Waste into Plutonium");
        add(builder, MekanismAdvancements.SPS, "Supercritical Phase Shifting?", "This thing doesn't seem safe");
        add(builder, MekanismAdvancements.ANTIMATTER, "Impossible Material", "Create matter that shouldn't be able to exist here");
        add(builder, MekanismAdvancements.NUCLEOSYNTHESIZER, "Alchemy, But Make It Sciencey", "Craft an Antiprotonic Nucleosynthesizer and don't worry if you can't pronounce the name");

        add(builder, MekanismAdvancements.POLONIUM, "Polonium, Not Plutonium", "Refine your Nuclear Waste into Polonium");

        add(builder, MekanismAdvancements.QIO_DRIVE_ARRAY, "Wait, Where Are All The Cables?!", "Create a Quantum Item Orchestration Drive Array to store things on another plane of existence");
        add(builder, MekanismAdvancements.QIO_EXPORTER, "It Comes From Where?!", "Automate the removal of items from your QIO");
        add(builder, MekanismAdvancements.QIO_IMPORTER, "Where Did It Go?", "Automate the addition of items to your QIO");
        add(builder, MekanismAdvancements.QIO_REDSTONE_ADAPTER, "Now You're Thinking With Redstone", "Craft a QIO Redstone Adapter");
        add(builder, MekanismAdvancements.QIO_DASHBOARD, "So Much More Than a Monitor", "Wait this thing has HOW MANY crafting windows?");
        add(builder, MekanismAdvancements.PORTABLE_QIO_DASHBOARD, "Who Needs Backpacks?", "Craft a Portable QIO Dashboard");
        add(builder, MekanismAdvancements.BASIC_QIO_DRIVE, "Compressed Reality", "Create a QIO Drive to store your items");
        add(builder, MekanismAdvancements.ADVANCED_QIO_DRIVE, "High Density Storage", "Increase the storage bandwidth of your QIO Drive");
        add(builder, MekanismAdvancements.ELITE_QIO_DRIVE, "It's All Relative", "Use relativity to 'further' increase the bandwidth");
        add(builder, MekanismAdvancements.ULTIMATE_QIO_DRIVE, "Parallel Universe Detected", "Where do all the items go");

        add(builder, MekanismAdvancements.TELEPORTATION_CORE, "Thinking With Portals", "Construct the core of all teleportation technology");
        add(builder, MekanismAdvancements.QUANTUM_ENTANGLOPORTER, "Quantum Entanglement", "Instant resource transportation");
        add(builder, MekanismAdvancements.TELEPORTER, "Speedy Thing Goes In, Speedy Thing Comes Out", "Create and travel through a Teleporter");
        add(builder, MekanismAdvancements.PORTABLE_TELEPORTER, "Beam Me Up, Scotty", "Craft a Portable Teleporter");

        add(builder, MekanismAdvancements.ROBIT, "A New Best Friend!", "Craft and place a Robit on a Chargepad");
        add(builder, MekanismAdvancements.ROBIT_AESTHETICS, "A New Coat of Paint", "Equip a Robit with a new coat of paint");
        add(builder, MekanismAdvancements.DIGITAL_MINER, "Trapped Inside", "Turn your best friend into a Digital Miner");
        add(builder, MekanismAdvancements.DICTIONARY, "Time to Learn", "Craft a Dictionary to learn the generic 'tags' of the world around you");
        add(builder, MekanismAdvancements.STONE_GENERATOR, "Replace With Stone", "Preventing holes in the ground since 2021");

        add(builder, MekanismAdvancements.DISASSEMBLER, "Needs More Speeeeed!", "Craft an Atomic Disassembler");
        add(builder, MekanismAdvancements.MEKASUIT, "Mekanist", "Protect yourself with a complete MekaSuit and a Meka-Tool");
        add(builder, MekanismAdvancements.MODIFICATION_STATION, "Get Your Tweak On", "Craft a Modification Station to upgrade your MekaSuit and Meka-Tool");
        add(builder, MekanismAdvancements.UPGRADED_MEKASUIT, "True Dedication", "Install the max number of all modules in the MekaSuit and Meka-Tool");

        add(builder, MekanismAdvancements.FLUID_TRANSPORT, "Just Like A Straw", "Craft a Mechanical Pipe");
        add(builder, MekanismAdvancements.CHEMICAL_TRANSPORT, "Under Pressure", "Craft a Pressurized Tube");
        add(builder, MekanismAdvancements.ENERGY_TRANSPORT, "Electron Super Highway", "Craft a Universal Cable");
        add(builder, MekanismAdvancements.HEAT_TRANSPORT, "Spread The Warmth", "Craft a Thermodynamic Conductor");
        add(builder, MekanismAdvancements.ITEM_TRANSPORT, "Conveyors Are So Yesterday", "Craft a Logistical Transporter");
        add(builder, MekanismAdvancements.RESTRICTIVE_ITEM_TRANSPORT, "It's Getting Cosy In Here", "Craft a Restrictive Transporter to lower the priority of a path for transporting items");
        add(builder, MekanismAdvancements.DIVERSION_ITEM_TRANSPORT, "Green Light, Red Light", "Precise side control");
        add(builder, MekanismAdvancements.SORTER, "Pick and Place", "Filter which items you are sending to where");

        add(builder, MekanismAdvancements.ENERGY_CUBE, "Save it for Later", "Build an Energy Cube to store your excess power");

        add(builder, MekanismAdvancements.AUTOMATED_CRAFTING, "A Smart Crafting Table", "Craft a machine to do the crafting for you");
        add(builder, MekanismAdvancements.SEISMIC_VIBRATIONS, "Just Vibing", "Craft a Seismic Vibrator and Seismic Reader, and then view the world beneath you");
        add(builder, MekanismAdvancements.PAINTING_MACHINE, "Lets Paint!", "Craft a Painting Machine to change the color of items");

        add(builder, MekanismAdvancements.ENRICHER, "Getting More From Less", "Make an Enrichment Chamber to increase material efficiency");
        add(builder, MekanismAdvancements.INFUSING_EFFICIENCY, "Infusing Efficiency", "Enrich your infusion inputs to increase their efficiency");
        add(builder, MekanismAdvancements.YELLOW_CAKE, "Look, Don't Eat", "Create some cake that must not be eaten");

        add(builder, MekanismAdvancements.PURIFICATION_CHAMBER, "Continue Purifying", "Craft a Purification Chamber and make even more from less!");
        add(builder, MekanismAdvancements.INJECTION_CHAMBER, "Injecting... 1, 2, 3, 4", "Inject Chemicals, get more resources");
        add(builder, MekanismAdvancements.CHEMICAL_CRYSTALLIZER, "The Most Bang for Your Buck", "Craft a Chemical Crystallizer, Dissolution Chamber and Washer");

        add(builder, MekanismAdvancements.SAWMILL, "Cut Cut Cut", "Craft a Precision Sawmill");
        add(builder, MekanismAdvancements.MOVING_BLOCKS, "Moving Day!", "Use a Cardboard Box to move another block");

        add(builder, MekanismAdvancements.PUMP, "Slurp Slurp!", "Craft an Electric Pump in order to automatically 'suck up' fluids");
        add(builder, MekanismAdvancements.PLENISHER, "Now in Reverse", "Craft a Fluidic Plenisher to place the fluids back!");

        add(builder, MekanismAdvancements.LIQUIFIER, "Liquefy Then Drink", "Craft a Nutritional Liquifier to liquefy your food");
        add(builder, MekanismAdvancements.FULL_CANTEEN, "Tasty Paste", "Completely fill a Canteen with Nutritional Paste");
    }

    private void addJade(TranslationBuilder builder) {
        addJadeConfigTooltip(builder, JadeConstants.REMOVE_BUILTIN, "Remove overwritten builtin renderings");
        addJadeConfigTooltip(builder, JadeConstants.ENTITY_DATA, "Jade entity data provider");
        addJadeConfigTooltip(builder, JadeConstants.BLOCK_DATA, "Jade tile data provider");
        addJadeConfigTooltip(builder, JadeConstants.TOOLTIP_RENDERER, "Jade tooltip renderer");
        //TODO Add Support
//        addJadeConfigTooltip(builder, LookingAtUtils.ENERGY, "Energy");
//        addJadeConfigTooltip(builder, LookingAtUtils.FLUID, "Fluid");
//        addJadeConfigTooltip(builder, LookingAtUtils.GAS, "Gas");
//        addJadeConfigTooltip(builder, LookingAtUtils.INFUSE_TYPE, "Infuse Type");
//        addJadeConfigTooltip(builder, LookingAtUtils.PIGMENT, "Pigment");
//        addJadeConfigTooltip(builder, LookingAtUtils.SLURRY, "Slurry");
    }

    private void addJadeConfigTooltip(TranslationBuilder builder, ResourceLocation location, String value) {
        add(builder, "config.jade.plugin_" + location.getNamespace() + "." + location.getPath(), value);
    }

    private void addMisc(TranslationBuilder builder) {
        addJade(builder);
        //Upgrades
        add(builder, APILang.UPGRADE_SPEED, "Speed");
        add(builder, APILang.UPGRADE_SPEED_DESCRIPTION, "Increases speed of machinery.");
        add(builder, APILang.UPGRADE_ENERGY, "Energy");
        add(builder, APILang.UPGRADE_ENERGY_DESCRIPTION, "Increases energy efficiency and capacity of machinery.");
        add(builder, APILang.UPGRADE_FILTER, "Filter");
        add(builder, APILang.UPGRADE_FILTER_DESCRIPTION, "A filter that separates heavy water from regular water.");
        add(builder, APILang.UPGRADE_GAS, "Gas");
        add(builder, APILang.UPGRADE_GAS_DESCRIPTION, "Increases the efficiency of gas-using machinery.");
        add(builder, APILang.UPGRADE_MUFFLING, "Muffling");
        add(builder, APILang.UPGRADE_MUFFLING_DESCRIPTION, "Reduces noise generated by machinery.");
        add(builder, APILang.UPGRADE_ANCHOR, "Anchor");
        add(builder, APILang.UPGRADE_ANCHOR_DESCRIPTION, "Keeps a machine's chunk loaded.");
        add(builder, APILang.UPGRADE_STONE_GENERATOR, "Stone Generator");
        add(builder, APILang.UPGRADE_STONE_GENERATOR_DESCRIPTION, "Generates stone or cobblestone as needed.");
        add(builder, APILang.UPGRADE_MAX_INSTALLED, "Maximum Installed: %1$s");
        //Transmission types
        add(builder, MekanismLang.TRANSMISSION_TYPE_ENERGY, "Energy");
        add(builder, MekanismLang.TRANSMISSION_TYPE_FLUID, "Fluids");
        add(builder, MekanismLang.TRANSMISSION_TYPE_GAS, "Gases");
        add(builder, MekanismLang.TRANSMISSION_TYPE_INFUSION, "Infuse Types");
        add(builder, MekanismLang.TRANSMISSION_TYPE_PIGMENT, "Pigments");
        add(builder, MekanismLang.TRANSMISSION_TYPE_SLURRY, "Slurries");
        add(builder, MekanismLang.TRANSMISSION_TYPE_ITEM, "Items");
        add(builder, MekanismLang.TRANSMISSION_TYPE_HEAT, "Heat");
        //Chemical Attributes
        add(builder, APILang.CHEMICAL_ATTRIBUTE_RADIATION, " - Radioactivity: %1$s");
        add(builder, APILang.CHEMICAL_ATTRIBUTE_COOLANT_EFFICIENCY, " - Coolant Efficiency: %1$s");
        add(builder, APILang.CHEMICAL_ATTRIBUTE_COOLANT_ENTHALPY, " - Thermal Enthalpy: %1$s");
        add(builder, APILang.CHEMICAL_ATTRIBUTE_FUEL_BURN_TICKS, " - Burn Time: %1$s t");
        add(builder, APILang.CHEMICAL_ATTRIBUTE_FUEL_ENERGY_DENSITY, " - Energy Density: %1$s");
        //Colors
        for (EnumColor color : EnumUtils.COLORS) {
            add(builder, color.getLangEntry(), color.getEnglishName());
        }
        addPackData(builder, MekanismLang.MEKANISM, MekanismLang.PACK_DESCRIPTION);
        add(builder, MekanismLang.DEBUG_TITLE, modName + " Debug");
        add(builder, MekanismLang.LOG_FORMAT, "[%1$s] %2$s");
        add(builder, MekanismLang.FORGE, "MinecraftForge");
        add(builder, MekanismLang.IC2, "IndustrialCraft");
        add(builder, MekanismLang.ERROR, "Error");
        add(builder, MekanismLang.ALPHA_WARNING, "Warning: " + modName + " is currently in alpha, and is not recommended for widespread use in modpacks. There are likely to be game breaking bugs, and various other issues that you can read more about %1$s.");
        add(builder, MekanismLang.ALPHA_WARNING_HERE, "here");
        add(builder, MekanismLang.RECIPE_WARNING, "Broken tags in Mekanism recipes detected, please check server logs for details. You will be missing some recipes and machines may not accept expected inputs.");
        //Equipment
        add(builder, MekanismLang.HEAD, "Head");
        add(builder, MekanismLang.BODY, "Body");
        add(builder, MekanismLang.LEGS, "Legs");
        add(builder, MekanismLang.FEET, "Feet");
        add(builder, MekanismLang.MAINHAND, "Hand 1");
        add(builder, MekanismLang.OFFHAND, "Hand 2");
        //Multiblock
        add(builder, MekanismLang.MULTIBLOCK_INVALID_FRAME, "Couldn't create frame, invalid block at %1$s.");
        add(builder, MekanismLang.MULTIBLOCK_INVALID_INNER, "Couldn't validate center, found invalid block at %1$s.");
        add(builder, MekanismLang.MULTIBLOCK_INVALID_CONTROLLER_CONFLICT, "Controller conflict: found extra controller at %1$s.");
        add(builder, MekanismLang.MULTIBLOCK_INVALID_NO_CONTROLLER, "Couldn't form, no controller found.");
        //SPS
        add(builder, MekanismLang.SPS, "Supercritical Phase Shifter");
        add(builder, MekanismLang.SPS_INVALID_DISCONNECTED_COIL, "Couldn't form, found a coil without a connection to an SPS Port.");
        add(builder, MekanismLang.SPS_PORT_MODE, "Toggled SPS Port mode to: %1$s.");
        add(builder, MekanismLang.SPS_ENERGY_INPUT, "Energy Input: %1$s/t");
        //Boiler
        add(builder, MekanismLang.BOILER_INVALID_AIR_POCKETS, "Couldn't form, found disconnected interior air pockets.");
        add(builder, MekanismLang.BOILER_INVALID_EXTRA_DISPERSER, "Couldn't form, found invalid Pressure Dispersers.");
        add(builder, MekanismLang.BOILER_INVALID_MISSING_DISPERSER, "Couldn't form, expected but didn't find Pressure Disperser at %1$s.");
        add(builder, MekanismLang.BOILER_INVALID_NO_DISPERSER, "Couldn't form, no Pressure Disperser layer found.");
        add(builder, MekanismLang.BOILER_INVALID_SUPERHEATING, "Couldn't form, invalid Superheating Element arrangement.");
        //Conversion
        add(builder, MekanismLang.CONVERSION_ENERGY, "Item to Energy");
        add(builder, MekanismLang.CONVERSION_GAS, "Item to Gas");
        add(builder, MekanismLang.CONVERSION_INFUSION, "Item to Infuse Type");
        //QIO stuff
        add(builder, MekanismLang.SET_FREQUENCY, "Set Frequency");
        add(builder, MekanismLang.QIO_FREQUENCY_SELECT, "QIO Frequency Select");
        add(builder, MekanismLang.QIO_ITEMS_DETAIL, "Items: %1$s / %2$s");
        add(builder, MekanismLang.QIO_TYPES_DETAIL, "Types: %1$s / %2$s");
        add(builder, MekanismLang.QIO_ITEMS, "Items");
        add(builder, MekanismLang.QIO_TYPES, "Types");
        add(builder, MekanismLang.QIO_TRIGGER_COUNT, "Trigger count: %1$s");
        add(builder, MekanismLang.QIO_STORED_COUNT, "Stored count: %1$s");
        add(builder, MekanismLang.QIO_FUZZY_MODE, "Ignoring NBT: %1$s");
        add(builder, MekanismLang.QIO_ITEM_TYPE_UNDEFINED, "Item type undefined");
        add(builder, MekanismLang.QIO_IMPORT_WITHOUT_FILTER, "Import Without Filter:");
        add(builder, MekanismLang.QIO_EXPORT_WITHOUT_FILTER, "Export Without Filter:");
        add(builder, MekanismLang.QIO_COMPENSATE_TOOLTIP, "What are you trying to compensate for?");
        add(builder, MekanismLang.LIST_SORT_COUNT, "Count");
        add(builder, MekanismLang.LIST_SORT_NAME, "Name");
        add(builder, MekanismLang.LIST_SORT_MOD, "Mod");
        add(builder, MekanismLang.LIST_SORT_NAME_DESC, "Sort items by name.");
        add(builder, MekanismLang.LIST_SORT_COUNT_DESC, "Sort items by count.");
        add(builder, MekanismLang.LIST_SORT_MOD_DESC, "Sort items by mod.");
        add(builder, MekanismLang.LIST_SORT_ASCENDING_DESC, "Sort items in ascending order.");
        add(builder, MekanismLang.LIST_SORT_DESCENDING_DESC, "Sort items in descending order.");
        add(builder, MekanismLang.LIST_SEARCH, "Search:");
        add(builder, MekanismLang.LIST_SORT, "Sort:");
        //JEI
        add(builder, MekanismLang.JEI_AMOUNT_WITH_CAPACITY, "%1$s / %2$s mB");
        add(builder, MekanismLang.JEI_INFO_HEAVY_WATER, "%1$s mB of Heavy Water can be extracted from a water source block via an electric pump with a filter upgrade installed.");
        add(builder, MekanismLang.JEI_INFO_MODULE_INSTALLATION, "Using a Modification Station, modules can be installed on the various MekaSuit pieces and on the Meka-Tool.");
        //Key
        add(builder, MekanismLang.KEY_HAND_MODE, "Item Mode Switch");
        add(builder, MekanismLang.KEY_HEAD_MODE, "Head Mode Switch");
        add(builder, MekanismLang.KEY_CHEST_MODE, "Chest Mode Switch");
        add(builder, MekanismLang.KEY_LEGS_MODE, "Legs Mode Switch");
        add(builder, MekanismLang.KEY_FEET_MODE, "Feet Mode Switch");
        add(builder, MekanismLang.KEY_DETAILS_MODE, "Show Details");
        add(builder, MekanismLang.KEY_DESCRIPTION_MODE, "Show Description");
        add(builder, MekanismLang.KEY_BOOST, "Boost");
        add(builder, MekanismLang.KEY_MODULE_TWEAKER, "Module Tweaker");
        add(builder, MekanismLang.KEY_HUD, "Show HUD");
        //Holiday
        add(builder, MekanismLang.HOLIDAY_BORDER, "%1$s%2$s%1$s");
        add(builder, MekanismLang.HOLIDAY_SIGNATURE, "-aidancbrady");
        add(builder, MekanismLang.CHRISTMAS_LINE_ONE, "Merry Christmas, %1$s!");
        add(builder, MekanismLang.CHRISTMAS_LINE_TWO, "May you have plenty of Christmas cheer");
        add(builder, MekanismLang.CHRISTMAS_LINE_THREE, "and have a relaxing holiday with your");
        add(builder, MekanismLang.CHRISTMAS_LINE_FOUR, "family :)");
        add(builder, MekanismLang.NEW_YEAR_LINE_ONE, "Happy New Year, %1$s!");
        add(builder, MekanismLang.NEW_YEAR_LINE_TWO, "Best wishes to you as we enter this");
        add(builder, MekanismLang.NEW_YEAR_LINE_THREE, "new and exciting year of %1$s! :)");
        add(builder, MekanismLang.MAY_4_LINE_ONE, "May the 4th be with you, %1$s!");
        //Generic
        //Note: How translation text component is implemented requires a double percent sign to make it show up as a single percent sign
        add(builder, MekanismLang.GENERIC_PERCENT, "%1$s%%");
        add(builder, MekanismLang.GENERIC_WITH_COMMA, "%1$s, %2$s");
        add(builder, MekanismLang.GENERIC_STORED, "%1$s: %2$s");
        add(builder, MekanismLang.GENERIC_STORED_MB, "%1$s: %2$s mB");
        add(builder, MekanismLang.GENERIC_MB, "%1$s mB");
        add(builder, MekanismLang.GENERIC_PRE_COLON, "%1$s:");
        add(builder, MekanismLang.GENERIC_SQUARE_BRACKET, "[%1$s]");
        add(builder, MekanismLang.GENERIC_PARENTHESIS, "(%1$s)");
        add(builder, MekanismLang.GENERIC_WITH_PARENTHESIS, "%1$s (%2$s)");
        add(builder, MekanismLang.GENERIC_FRACTION, "%1$s/%2$s");
        add(builder, MekanismLang.GENERIC_TRANSFER, "- %1$s (%2$s)");
        add(builder, MekanismLang.GENERIC_PER_TICK, "%1$s/t");
        add(builder, MekanismLang.GENERIC_PER_MB, "%1$s/mB");
        add(builder, MekanismLang.GENERIC_PRE_STORED, "%1$s %2$s: %3$s");
        add(builder, MekanismLang.GENERIC_BLOCK_POS, "%1$s, %2$s, %3$s");
        add(builder, MekanismLang.GENERIC_HEX, "#%1$s");
        add(builder, MekanismLang.GENERIC_LIST, "- %1$s");
        add(builder, MekanismLang.GENERIC_MINUTES, "%1$sm");
        add(builder, MekanismLang.GENERIC_HOURS_MINUTES, "%1$sh %2$sm");
        //Directions
        add(builder, APILang.DOWN, "Down");
        add(builder, APILang.UP, "Up");
        add(builder, APILang.NORTH, "North");
        add(builder, APILang.SOUTH, "South");
        add(builder, APILang.WEST, "West");
        add(builder, APILang.EAST, "East");
        add(builder, MekanismLang.NORTH_SHORT, "N");
        add(builder, MekanismLang.SOUTH_SHORT, "S");
        add(builder, MekanismLang.WEST_SHORT, "W");
        add(builder, MekanismLang.EAST_SHORT, "E");
        //Relative sides
        add(builder, APILang.FRONT, "Front");
        add(builder, APILang.LEFT, "Left");
        add(builder, APILang.RIGHT, "Right");
        add(builder, APILang.BACK, "Back");
        add(builder, APILang.TOP, "Top");
        add(builder, APILang.BOTTOM, "Bottom");
        //Hold for
        add(builder, MekanismLang.HOLD_FOR_DETAILS, "Hold %1$s for details.");
        add(builder, MekanismLang.HOLD_FOR_DESCRIPTION, "Hold %1$s for a description.");
        add(builder, MekanismLang.HOLD_FOR_MODULES, "Hold %1$s for installed modules.");
        add(builder, MekanismLang.HOLD_FOR_SUPPORTED_ITEMS, "Hold %1$s for supporting items and conflicting modules.");
        //Commands
        add(builder, MekanismLang.COMMAND_CHUNK_WATCH, "Chunk (%1$s) added to watch list.");
        add(builder, MekanismLang.COMMAND_CHUNK_UNWATCH, "Chunk (%1$s) removed from watch list.");
        add(builder, MekanismLang.COMMAND_CHUNK_CLEAR, "%1$s chunks removed from watch list.");
        add(builder, MekanismLang.COMMAND_CHUNK_FLUSH, "%1$s chunks unloaded.");
        add(builder, MekanismLang.COMMAND_CHUNK_LOADED, "Loaded chunk (%1$s).");
        add(builder, MekanismLang.COMMAND_CHUNK_UNLOADED, "Unloaded chunk (%1$s).");
        add(builder, MekanismLang.COMMAND_DEBUG, "Toggled debug mode: %1$s.");
        add(builder, MekanismLang.COMMAND_TEST_RULES, "Enabled keepInventory, and disabled doMobSpawning, doDaylightCycle, doWeatherCycle and mobGriefing!");
        add(builder, MekanismLang.COMMAND_TP, "Teleported to (%1$s) - saved last position on stack.");
        add(builder, MekanismLang.COMMAND_TPOP, "Returned to (%1$s); %2$s positions on stack.");
        add(builder, MekanismLang.COMMAND_ERROR_TPOP_EMPTY, "No positions on stack.");
        add(builder, MekanismLang.COMMAND_BUILD_REMOVED, "Build successfully removed.");
        add(builder, MekanismLang.COMMAND_BUILD_BUILT, "Finished building: %1$s.");
        add(builder, MekanismLang.COMMAND_BUILD_BUILT_EMPTY, "Finished building empty: %1$s.");
        add(builder, MekanismLang.COMMAND_ERROR_BUILD_MISS, "No valid target found.");
        add(builder, MekanismLang.COMMAND_RADIATION_ADD, "Added %1$s radiation at (%2$s) in %3$s.");
        add(builder, MekanismLang.COMMAND_RADIATION_ADD_ENTITY, "Added %1$s radiation to player.");
        add(builder, MekanismLang.COMMAND_RADIATION_ADD_ENTITY_TARGET, "Added %1$s radiation to entity: %2$s.");
        add(builder, MekanismLang.COMMAND_RADIATION_GET, "Current radiation at (%1$s) in %2$s: %3$s");
        add(builder, MekanismLang.COMMAND_RADIATION_CLEAR, "Cleared player radiation.");
        add(builder, MekanismLang.COMMAND_RADIATION_CLEAR_ENTITY, "Cleared entity radiation for: %1$s.");
        add(builder, MekanismLang.COMMAND_RADIATION_REDUCE, "Reduced player radiation by %1$s.");
        add(builder, MekanismLang.COMMAND_RADIATION_REDUCE_TARGET, "Reduced entity radiation for %1$s by %2$s.");
        add(builder, MekanismLang.COMMAND_RADIATION_REMOVE_ALL, "Removed all radiation sources.");
        add(builder, MekanismLang.COMMAND_RETROGEN_CHUNK_QUEUED, "Queued chunk (%1$s) in %2$s for retrogen.");
        add(builder, MekanismLang.COMMAND_ERROR_RETROGEN_DISABLED, "Retrogen is disabled, please enable it in the config.");
        add(builder, MekanismLang.COMMAND_ERROR_RETROGEN_FAILURE, "Failed to queue any chunks for retrogen.");
        //Tooltip stuff
        add(builder, MekanismLang.MODE, "Mode: %1$s");
        add(builder, MekanismLang.FIRE_MODE, "Fire Mode: %1$s");
        add(builder, MekanismLang.BUCKET_MODE, "Bucket Mode: %1$s");
        add(builder, MekanismLang.STORED_ENERGY, "Stored energy: %1$s");
        add(builder, MekanismLang.STORED, "Stored %1$s: %2$s");
        add(builder, MekanismLang.STORED_MB_PERCENTAGE, "Stored %1$s: %2$s mB (%3$s)");
        add(builder, MekanismLang.ITEM_AMOUNT, "Item amount: %1$s");
        add(builder, MekanismLang.LOCKED, "Locked to item: %1$s");
        add(builder, MekanismLang.FLOWING, "Flowing: %1$s");
        add(builder, MekanismLang.INVALID, "(Invalid)");
        add(builder, MekanismLang.HAS_INVENTORY, "Inventory: %1$s");
        add(builder, MekanismLang.NO_GAS, "No gas stored.");
        add(builder, MekanismLang.NO_FLUID_TOOLTIP, "No fluid stored.");
        add(builder, MekanismLang.FREE_RUNNERS_MODE, "Runners Mode: %1$s");
        add(builder, MekanismLang.JETPACK_MODE, "Jetpack Mode: %1$s");
        add(builder, MekanismLang.SCUBA_TANK_MODE, "Scuba Tank: %1$s");
        add(builder, MekanismLang.FREE_RUNNERS_STORED, "Runners Energy: %1$s");
        add(builder, MekanismLang.FLAMETHROWER_STORED, "Flamethrower: %1$s");
        add(builder, MekanismLang.JETPACK_STORED, "Jetpack Fuel: %1$s");
        add(builder, MekanismLang.PROGRESS, "Progress: %1$s");
        add(builder, MekanismLang.PROCESS_RATE, "Process Rate: %1$s");
        add(builder, MekanismLang.PROCESS_RATE_MB, "Process Rate: %1$s mB/t");
        add(builder, MekanismLang.TICKS_REQUIRED, "Ticks Required: %1$s");
        add(builder, MekanismLang.DECAY_IMMUNE, "Will not decay inside a Radioactive Waste Barrel");
        //Gui stuff
        add(builder, MekanismLang.WIDTH, "Width");
        add(builder, MekanismLang.HEIGHT, "Height");
        add(builder, MekanismLang.BACK, "Back");
        add(builder, MekanismLang.CRAFTING_TAB, "Crafting (%1$s/%2$s)");
        add(builder, MekanismLang.CRAFTING_WINDOW, "Crafting Window %1$s");
        add(builder, MekanismLang.CRAFTING_WINDOW_CLEAR, "Empty Crafting Window contents into storage, hold shift to empty into player inventory instead.");
        add(builder, MekanismLang.MIN, "Min: %1$s");
        add(builder, MekanismLang.MAX, "Max: %1$s");
        add(builder, MekanismLang.INFINITE, "Infinite");
        add(builder, MekanismLang.NONE, "None");
        add(builder, MekanismLang.EMPTY, "Empty");
        add(builder, MekanismLang.MAX_OUTPUT, "Max Output: %1$s/t");
        add(builder, MekanismLang.STORING, "Storing: %1$s");
        add(builder, MekanismLang.DISSIPATED_RATE, "Dissipated: %1$s/t");
        add(builder, MekanismLang.TRANSFERRED_RATE, "Transferred: %1$s/t");
        add(builder, MekanismLang.FUEL, "Fuel: %1$s");
        add(builder, MekanismLang.VOLUME, "Volume: %1$s");
        add(builder, MekanismLang.NO_FLUID, "No fluid");
        add(builder, MekanismLang.CHEMICAL, "Chemical: %1$s");
        add(builder, MekanismLang.GAS, "Gas: %1$s");
        add(builder, MekanismLang.INFUSE_TYPE, "Infuse Type: %1$s");
        add(builder, MekanismLang.PIGMENT, "Pigment: %1$s");
        add(builder, MekanismLang.SLURRY, "Slurry: %1$s");
        add(builder, MekanismLang.LIQUID, "Liquid: %1$s");
        add(builder, MekanismLang.UNIT, "Unit: %1$s");
        add(builder, MekanismLang.USING, "Using: %1$s/t");
        add(builder, MekanismLang.NEEDED, "Needed: %1$s");
        add(builder, MekanismLang.NEEDED_PER_TICK, "Needed: %1$s/t");
        add(builder, MekanismLang.FINISHED, "Finished: %1$s");
        add(builder, MekanismLang.NO_RECIPE, "(No recipe)");
        add(builder, MekanismLang.EJECT, "Eject: %1$s");
        add(builder, MekanismLang.NO_DELAY, "No Delay");
        add(builder, MekanismLang.DELAY, "Delay: %1$st");
        add(builder, MekanismLang.ENERGY, "Energy: %1$s");
        add(builder, MekanismLang.RESISTIVE_HEATER_USAGE, "Usage: %1$s/t");
        add(builder, MekanismLang.DYNAMIC_TANK, "Dynamic Tank");
        add(builder, MekanismLang.MOVE_UP, "Move Up");
        add(builder, MekanismLang.MOVE_DOWN, "Move Down");
        add(builder, MekanismLang.MOVE_TO_TOP, "Hold shift to move to top");
        add(builder, MekanismLang.MOVE_TO_BOTTOM, "Hold shift to move to bottom");
        add(builder, MekanismLang.SET, "Set:");
        add(builder, MekanismLang.TRUE, "True");
        add(builder, MekanismLang.FALSE, "False");
        add(builder, APILang.TRUE_LOWER, "true");
        add(builder, APILang.FALSE_LOWER, "false");
        add(builder, MekanismLang.CLOSE, "Close");
        add(builder, MekanismLang.RADIATION_DOSE, "Radiation Dose: %1$s");
        add(builder, MekanismLang.RADIATION_EXPOSURE, "Radiation Exposure: %1$s");
        add(builder, MekanismLang.RADIATION_EXPOSURE_ENTITY, "Entity Radiation Exposure: %1$s");
        add(builder, MekanismLang.RADIATION_DECAY_TIME, "Time to Decay: %1$s");
        add(builder, MekanismLang.RGB, "RGB:");
        add(builder, MekanismLang.RGBA, "RGBA:");
        add(builder, MekanismLang.COLOR_PICKER, "Color Picker");
        add(builder, MekanismLang.HELMET_OPTIONS, "Helmet Options");
        add(builder, MekanismLang.HUD_OVERLAY, "HUD Overlay:");
        add(builder, MekanismLang.OPACITY, "Opacity");
        add(builder, MekanismLang.DEFAULT, "Default");
        add(builder, MekanismLang.WARNING, "Warning");
        add(builder, MekanismLang.DANGER, "Danger");
        add(builder, MekanismLang.COMPASS, "Compass");
        add(builder, MekanismLang.RADIAL_SCREEN, "Radial Selector Screen");
        add(builder, MekanismLang.VISUALS, "Visuals: %1$s");
        add(builder, MekanismLang.VISUALS_TOO_BIG, "Area too large to display visuals.");
        //GUI Issues
        add(builder, MekanismLang.ISSUES, "Issues:");
        add(builder, MekanismLang.ISSUE_NOT_ENOUGH_ENERGY, " - Not enough energy to operate");
        add(builder, MekanismLang.ISSUE_NOT_ENOUGH_ENERGY_REDUCED_RATE, " - Not enough energy to run at maximum speed");
        add(builder, MekanismLang.ISSUE_NO_SPACE_IN_OUTPUT, " - Not enough room in output");
        add(builder, MekanismLang.ISSUE_NO_SPACE_IN_OUTPUT_OVERFLOW, " - Not enough room in output, overflow stored in internal buffer");
        add(builder, MekanismLang.ISSUE_NO_MATCHING_RECIPE, " - No matching recipe or not enough input");
        add(builder, MekanismLang.ISSUE_INPUT_DOESNT_PRODUCE_OUTPUT, " - Input does not produce output");
        add(builder, MekanismLang.ISSUE_INVALID_OREDICTIONIFICATOR_FILTER, " - Filter is no longer valid or supported");
        add(builder, MekanismLang.ISSUE_FILTER_HAS_BLACKLISTED_ELEMENT, " - Filter contains at least one element that is blacklisted");
        //Laser Amplifier
        add(builder, MekanismLang.ENTITY_DETECTION, "Entity Detection");
        add(builder, MekanismLang.ENERGY_CONTENTS, "Energy Contents");
        add(builder, MekanismLang.REDSTONE_OUTPUT, "Redstone Output: %1$s");
        //Frequency
        add(builder, MekanismLang.FREQUENCY, "Frequency: %1$s");
        add(builder, MekanismLang.NO_FREQUENCY, "No frequency");
        add(builder, MekanismLang.FREQUENCY_DELETE_CONFIRM, "Are you sure you want to delete this frequency? This can't be undone.");
        //Owner
        add(builder, MekanismLang.NOW_OWN, "You now own this item.");
        add(builder, MekanismLang.OWNER, "Owner: %1$s");
        add(builder, MekanismLang.NO_OWNER, "No Owner");
        //Tab
        add(builder, MekanismLang.MAIN_TAB, "Main");
        //Evaporation
        add(builder, MekanismLang.EVAPORATION_HEIGHT, "Height: %1$s");
        add(builder, MekanismLang.FLUID_PRODUCTION, "Production: %1$s mB/t");
        add(builder, MekanismLang.EVAPORATION_PLANT, "Thermal Evaporation Plant");
        //Configuration
        add(builder, MekanismLang.TRANSPORTER_CONFIG, "Transporter Config");
        add(builder, MekanismLang.SIDE_CONFIG, "Side Config");
        add(builder, MekanismLang.SIDE_CONFIG_CLEAR, "Clear Side Config (sets all sides to none)");
        add(builder, MekanismLang.STRICT_INPUT, "Strict Input");
        add(builder, MekanismLang.STRICT_INPUT_ENABLED, "Strict Input (%1$s)");
        add(builder, MekanismLang.CONFIG_TYPE, "%1$s Config");
        add(builder, MekanismLang.NO_EJECT, "Can't Eject");
        add(builder, MekanismLang.CANT_EJECT_TOOLTIP, "Auto-eject is not supported, manual extraction may still be possible.");
        add(builder, MekanismLang.SLOTS, "Slots");
        //Auto
        add(builder, MekanismLang.AUTO_PULL, "Auto-pull");
        add(builder, MekanismLang.AUTO_EJECT, "Auto-eject");
        add(builder, MekanismLang.AUTO_SORT, "Auto-sort");
        //Gas mode
        add(builder, MekanismLang.IDLE, "Idle");
        add(builder, MekanismLang.DUMPING_EXCESS, "Dumping Excess");
        add(builder, MekanismLang.DUMPING, "Dumping");
        //Dictionary
        add(builder, MekanismLang.DICTIONARY_KEY, " - %1$s");
        add(builder, MekanismLang.DICTIONARY_NO_KEY, "No key.");
        add(builder, MekanismLang.DICTIONARY_BLOCK_TAGS_FOUND, "Block Tag(s) found:");
        add(builder, MekanismLang.DICTIONARY_FLUID_TAGS_FOUND, "Fluid Tag(s) found:");
        add(builder, MekanismLang.DICTIONARY_ENTITY_TYPE_TAGS_FOUND, "Entity Type Tag(s) found:");
        add(builder, MekanismLang.DICTIONARY_BLOCK_ENTITY_TYPE_TAGS_FOUND, "Block Entity Type Tag(s) found:");
        add(builder, MekanismLang.DICTIONARY_TAG_TYPE, "Tag Type:");
        add(builder, MekanismLang.DICTIONARY_ITEM, "Item");
        add(builder, MekanismLang.DICTIONARY_ITEM_DESC, "Display Item Tags");
        add(builder, MekanismLang.DICTIONARY_BLOCK, "Block");
        add(builder, MekanismLang.DICTIONARY_BLOCK_DESC, "Display Block Tags");
        add(builder, MekanismLang.DICTIONARY_FLUID, "Fluid");
        add(builder, MekanismLang.DICTIONARY_FLUID_DESC, "Display Fluid Tags");
        add(builder, MekanismLang.DICTIONARY_ENTITY_TYPE, "Entity Type");
        add(builder, MekanismLang.DICTIONARY_ENTITY_TYPE_DESC, "Display Entity Type Tags");
        add(builder, MekanismLang.DICTIONARY_ATTRIBUTE, "Attribute");
        add(builder, MekanismLang.DICTIONARY_ATTRIBUTE_DESC, "Display Attribute Tags");
        add(builder, MekanismLang.DICTIONARY_POTION, "Potion");
        add(builder, MekanismLang.DICTIONARY_POTION_DESC, "Display Potion Tags");
        add(builder, MekanismLang.DICTIONARY_MOB_EFFECT, "Mob Effect");
        add(builder, MekanismLang.DICTIONARY_MOB_EFFECT_DESC, "Display Mob Effect Tags");
        add(builder, MekanismLang.DICTIONARY_ENCHANTMENT, "Enchantment");
        add(builder, MekanismLang.DICTIONARY_ENCHANTMENT_DESC, "Display Enchantment Tags");
        add(builder, MekanismLang.DICTIONARY_BLOCK_ENTITY_TYPE, "Block Entity Type");
        add(builder, MekanismLang.DICTIONARY_BLOCK_ENTITY_TYPE_DESC, "Display Block Entity Type Tags");
        add(builder, MekanismLang.DICTIONARY_GAS, "Gas");
        add(builder, MekanismLang.DICTIONARY_GAS_DESC, "Display Gas Tags");
        add(builder, MekanismLang.DICTIONARY_INFUSE_TYPE, "Infuse Type");
        add(builder, MekanismLang.DICTIONARY_INFUSE_TYPE_DESC, "Display Infuse Type Tags");
        add(builder, MekanismLang.DICTIONARY_PIGMENT, "Pigment");
        add(builder, MekanismLang.DICTIONARY_PIGMENT_DESC, "Display Pigment Tags");
        add(builder, MekanismLang.DICTIONARY_SLURRY, "Slurry");
        add(builder, MekanismLang.DICTIONARY_SLURRY_DESC, "Display Slurry Tags");
        //Oredictionificator
        add(builder, MekanismLang.LAST_ITEM, "Last Item");
        add(builder, MekanismLang.NEXT_ITEM, "Next Item");
        //Stabilizer
        add(builder, MekanismLang.STABILIZER_CENTER, "Chunk at (%1$s, %2$s) is always loaded.");
        add(builder, MekanismLang.STABILIZER_ENABLE_RADIUS, "Left click to enable chunk loading for all chunks with a radius of %1$s around (%2$s, %3$s).");
        add(builder, MekanismLang.STABILIZER_DISABLE_RADIUS, "Right click to disable chunk loading for all chunks with a radius of %1$s around (%2$s, %3$s).");
        add(builder, MekanismLang.STABILIZER_TOGGLE_LOADING, "Toggle chunk loading %1$s at (%2$s, %3$s)");
        //Status
        add(builder, MekanismLang.STATUS, "Status: %1$s");
        add(builder, MekanismLang.STATUS_OK, "All OK");
        //Fluid container
        add(builder, MekanismLang.FLUID_CONTAINER_BOTH, "Both");
        add(builder, MekanismLang.FLUID_CONTAINER_FILL, "Fill");
        add(builder, MekanismLang.FLUID_CONTAINER_EMPTY, "Empty");
        //Boolean values
        add(builder, MekanismLang.YES, "yes");
        add(builder, MekanismLang.NO, "no");
        add(builder, MekanismLang.ON, "on");
        add(builder, MekanismLang.OFF, "off");
        add(builder, MekanismLang.INPUT, "Input");
        add(builder, MekanismLang.OUTPUT, "Output");
        add(builder, MekanismLang.ACTIVE, "Active");
        add(builder, MekanismLang.DISABLED, "Disabled");
        add(builder, MekanismLang.ON_CAPS, "ON");
        add(builder, MekanismLang.OFF_CAPS, "OFF");
        //Capacity
        add(builder, MekanismLang.CAPACITY, "Capacity: %1$s");
        add(builder, MekanismLang.CAPACITY_ITEMS, "Capacity: %1$s Items");
        add(builder, MekanismLang.CAPACITY_MB, "Capacity: %1$s mB");
        add(builder, MekanismLang.CAPACITY_PER_TICK, "Capacity: %1$s/t");
        add(builder, MekanismLang.CAPACITY_MB_PER_TICK, "Capacity: %1$s mB/t");
        //Cardboard box
        add(builder, MekanismLang.BLOCK_DATA, "Block data: %1$s");
        add(builder, MekanismLang.BLOCK, "Block: %1$s");
        add(builder, MekanismLang.BLOCK_ENTITY, "Block Entity: %1$s");
        //Crafting Formula
        add(builder, MekanismLang.INGREDIENTS, "Ingredients:");
        add(builder, MekanismLang.ENCODED, "(Encoded)");
        //Multiblock
        add(builder, MekanismLang.MULTIBLOCK_INCOMPLETE, "Incomplete");
        add(builder, MekanismLang.MULTIBLOCK_FORMED, "Formed");
        add(builder, MekanismLang.MULTIBLOCK_CONFLICT, "Conflict");
        add(builder, MekanismLang.MULTIBLOCK_FORMED_CHAT, "Multiblock Formed");
        //Transmitter tooltips
        add(builder, MekanismLang.UNIVERSAL, "universal");
        add(builder, MekanismLang.ITEMS, "- Items (%1$s)");
        add(builder, MekanismLang.BLOCKS, "- Blocks (%1$s)");
        add(builder, MekanismLang.FLUIDS, "- Fluids (%1$s)");
        add(builder, MekanismLang.GASES, "- Gases (%1$s)");
        add(builder, MekanismLang.INFUSE_TYPES, "- Infuse Types (%1$s)");
        add(builder, MekanismLang.PIGMENTS, "- Pigments (%1$s)");
        add(builder, MekanismLang.SLURRIES, "- Slurries (%1$s)");
        add(builder, MekanismLang.HEAT, "- Heat (%1$s)");
        add(builder, MekanismLang.CONDUCTION, "Conduction: %1$s");
        add(builder, MekanismLang.INSULATION, "Insulation: %1$s");
        add(builder, MekanismLang.HEAT_CAPACITY, "Heat Capacity: %1$s");
        add(builder, MekanismLang.CAPABLE_OF_TRANSFERRING, "Capable of transferring:");
        add(builder, MekanismLang.DIVERSION_CONTROL_DISABLED, "Always active");
        add(builder, MekanismLang.DIVERSION_CONTROL_HIGH, "Active with signal");
        add(builder, MekanismLang.DIVERSION_CONTROL_LOW, "Active without signal");
        add(builder, MekanismLang.TOGGLE_DIVERTER, "Diverter mode changed to: %1$s");
        add(builder, MekanismLang.PUMP_RATE, "Pump Rate: %1$s/s");
        add(builder, MekanismLang.PUMP_RATE_MB, "Pump Rate: %1$s mB/t");
        add(builder, MekanismLang.SPEED, "Speed: %1$s m/s");
        //Condensentrator
        add(builder, MekanismLang.CONDENSENTRATOR_TOGGLE, "Toggle operation");
        add(builder, MekanismLang.CONDENSENTRATING, "Condensentrating");
        add(builder, MekanismLang.DECONDENSENTRATING, "Decondensentrating");
        //Upgrades
        add(builder, MekanismLang.UPGRADE_DISPLAY_LEVEL, "- %1$s: x%2$s");
        add(builder, MekanismLang.UPGRADES_EFFECT, "Effect: %1$sx");
        add(builder, MekanismLang.UPGRADES, "Upgrades");
        add(builder, MekanismLang.UPGRADE_NO_SELECTION, "No selection.");
        add(builder, MekanismLang.UPGRADES_SUPPORTED, "Supported:");
        add(builder, MekanismLang.UPGRADE_COUNT, "Amount: %1$s/%2$s");
        add(builder, MekanismLang.UPGRADE_TYPE, "%1$s Upgrade");
        add(builder, MekanismLang.UPGRADE_NOT_SUPPORTED, "%1$s (Not Supported)");
        add(builder, MekanismLang.UPGRADE_UNINSTALL, "Uninstall");
        add(builder, MekanismLang.UPGRADE_UNINSTALL_TOOLTIP, "Uninstalls a single upgrade, hold shift to uninstall all.");
        //Filter
        add(builder, MekanismLang.CREATE_FILTER_TITLE, "Create New Filter");
        add(builder, MekanismLang.FILTERS, "Filters:");
        add(builder, MekanismLang.FILTER_COUNT, "T: %1$s");
        add(builder, MekanismLang.FILTER_ALLOW_DEFAULT, "Allow Default");
        add(builder, MekanismLang.FILTER, "Filter");
        add(builder, MekanismLang.FILTER_NEW, "New: %1$s");
        add(builder, MekanismLang.FILTER_EDIT, "Edit: %1$s");
        add(builder, MekanismLang.FILTER_STATE, "Filter: %1$s");
        add(builder, MekanismLang.SORTER_SIZE_MODE, "Size Mode");
        add(builder, MekanismLang.SORTER_SIZE_MODE_CONFLICT, "Size Mode - has no effect currently, because single item mode is turned on.");
        add(builder, MekanismLang.FUZZY_MODE, "Fuzzy Mode");
        add(builder, MekanismLang.TEXT_FILTER_NO_MATCHES, "No matching targets");
        add(builder, MekanismLang.TAG_FILTER, "Tag Filter");
        add(builder, MekanismLang.TAG_FILTER_NO_TAG, "No tag");
        add(builder, MekanismLang.TAG_FILTER_SAME_TAG, "Same tag");
        add(builder, MekanismLang.TAG_FILTER_TAG, "Tag: %1$s");
        add(builder, MekanismLang.MODID_FILTER, "Mod ID Filter");
        add(builder, MekanismLang.MODID_FILTER_NO_ID, "No ID");
        add(builder, MekanismLang.MODID_FILTER_SAME_ID, "Same ID");
        add(builder, MekanismLang.MODID_FILTER_ID, "ID: %1$s");
        add(builder, MekanismLang.ITEM_FILTER, "Item Filter");
        add(builder, MekanismLang.ITEM_FILTER_NO_ITEM, "No item");
        add(builder, MekanismLang.SORTER_FILTER_SIZE_MODE, "%1$s!");
        add(builder, MekanismLang.SORTER_FILTER_MAX_LESS_THAN_MIN, "Max < min");
        add(builder, MekanismLang.SORTER_FILTER_OVER_SIZED, "Max > 64");
        add(builder, MekanismLang.SORTER_FILTER_SIZE_MISSING, "Max/min");
        add(builder, MekanismLang.OREDICTIONIFICATOR_FILTER, "Oredictionificator Filter");
        add(builder, MekanismLang.OREDICTIONIFICATOR_FILTER_INVALID_NAMESPACE, "Invalid tag namespace");
        add(builder, MekanismLang.OREDICTIONIFICATOR_FILTER_INVALID_PATH, "Invalid tag path");
        add(builder, MekanismLang.OREDICTIONIFICATOR_FILTER_UNSUPPORTED_TAG, "Unsupported tag");
        //Radioactive Waste Barrel
        add(builder, MekanismLang.WASTE_BARREL_DECAY_RATE, "Decay Rate: %1$s mB/t");
        add(builder, MekanismLang.WASTE_BARREL_DECAY_RATE_ACTUAL, "Actual Decay Rate: %1$s mB / %2$s ticks");
        //Seismic Vibrator
        add(builder, MekanismLang.CHUNK, "Chunk: %1$s, %2$s");
        add(builder, MekanismLang.VIBRATING, "Vibrating");
        //Seismic Reader
        add(builder, MekanismLang.NEEDS_ENERGY, "Not enough energy to interpret vibration");
        add(builder, MekanismLang.NO_VIBRATIONS, "Unable to discover any vibrations");
        add(builder, MekanismLang.ABUNDANCY, "Abundancy: %1$s");
        //Redstone Control
        add(builder, MekanismLang.REDSTONE_CONTROL_DISABLED, "Redstone Detection: IGNORED");
        add(builder, MekanismLang.REDSTONE_CONTROL_HIGH, "Redstone Detection: NORMAL");
        add(builder, MekanismLang.REDSTONE_CONTROL_LOW, "Redstone Detection: INVERTED");
        add(builder, MekanismLang.REDSTONE_CONTROL_PULSE, "Redstone Detection: PULSE");
        //Security
        add(builder, MekanismLang.SECURITY, "Security: %1$s");
        add(builder, MekanismLang.SECURITY_OVERRIDDEN, "(Overridden)");
        add(builder, MekanismLang.SECURITY_OFFLINE, "Security Offline");
        add(builder, MekanismLang.SECURITY_ADD, "Add:");
        add(builder, MekanismLang.SECURITY_OVERRIDE, "Security Override: %1$s");
        add(builder, MekanismLang.NO_ACCESS, "You don't have access.");
        add(builder, MekanismLang.TRUSTED_PLAYERS, "Trusted Players");
        add(builder, APILang.PUBLIC, "Public");
        add(builder, APILang.TRUSTED, "Trusted");
        add(builder, APILang.PRIVATE, "Private");
        add(builder, MekanismLang.PUBLIC_MODE, "Public Mode");
        add(builder, MekanismLang.TRUSTED_MODE, "Trusted Mode");
        add(builder, MekanismLang.PRIVATE_MODE, "Private Mode");
        //Formulaic Assemblicator
        add(builder, MekanismLang.ENCODE_FORMULA, "Encode Formula");
        add(builder, MekanismLang.CRAFT_SINGLE, "Craft Single Item");
        add(builder, MekanismLang.CRAFT_AVAILABLE, "Craft Available Items");
        add(builder, MekanismLang.EMPTY_ASSEMBLICATOR, "Empty Grid");
        add(builder, MekanismLang.FILL_ASSEMBLICATOR, "Fill Grid");
        add(builder, MekanismLang.STOCK_CONTROL, "Stock Control: %1$s");
        add(builder, MekanismLang.AUTO_MODE, "Auto-Mode: %1$s");
        //Factory Type
        add(builder, MekanismLang.FACTORY_TYPE, "Recipe type: %1$s");
        add(builder, MekanismLang.SMELTING, "Smelting");
        add(builder, MekanismLang.ENRICHING, "Enriching");
        add(builder, MekanismLang.CRUSHING, "Crushing");
        add(builder, MekanismLang.COMPRESSING, "Compressing");
        add(builder, MekanismLang.COMBINING, "Combining");
        add(builder, MekanismLang.PURIFYING, "Purifying");
        add(builder, MekanismLang.INJECTING, "Injecting");
        add(builder, MekanismLang.INFUSING, "Infusing");
        add(builder, MekanismLang.SAWING, "Sawing");
        //Transmitter Networks
        add(builder, MekanismLang.NETWORK_DESCRIPTION, "[%1$s] %2$s transmitters, %3$s acceptors.");
        add(builder, MekanismLang.INVENTORY_NETWORK, "InventoryNetwork");
        add(builder, MekanismLang.FLUID_NETWORK, "FluidNetwork");
        add(builder, MekanismLang.CHEMICAL_NETWORK, "ChemicalNetwork");
        add(builder, MekanismLang.HEAT_NETWORK, "HeatNetwork");
        add(builder, MekanismLang.ENERGY_NETWORK, "EnergyNetwork");
        add(builder, MekanismLang.NO_NETWORK, "No Network");
        add(builder, MekanismLang.HEAT_NETWORK_STORED, "%1$s above ambient");
        add(builder, MekanismLang.HEAT_NETWORK_FLOW, "%1$s transferred to acceptors, %2$s lost to environment.");
        add(builder, MekanismLang.HEAT_NETWORK_FLOW_EFFICIENCY, "%1$s transferred to acceptors, %2$s lost to environment, %3$s efficiency.");
        add(builder, MekanismLang.FLUID_NETWORK_NEEDED, "%1$s buckets");
        add(builder, MekanismLang.NETWORK_MB_PER_TICK, "%1$s mB/t");
        add(builder, MekanismLang.NETWORK_MB_STORED, "%1$s (%2$s mB)");
        //Button
        add(builder, MekanismLang.BUTTON_CONFIRM, "Confirm");
        add(builder, MekanismLang.BUTTON_START, "Start");
        add(builder, MekanismLang.BUTTON_STOP, "Stop");
        add(builder, MekanismLang.BUTTON_CONFIG, "Config");
        add(builder, MekanismLang.BUTTON_REMOVE, "Remove");
        add(builder, MekanismLang.BUTTON_CANCEL, "Cancel");
        add(builder, MekanismLang.BUTTON_SAVE, "Save");
        add(builder, MekanismLang.BUTTON_SET, "Set");
        add(builder, MekanismLang.BUTTON_DELETE, "Delete");
        add(builder, MekanismLang.BUTTON_OPTIONS, "Options");
        add(builder, MekanismLang.BUTTON_TELEPORT, "Teleport");
        add(builder, MekanismLang.BUTTON_NEW_FILTER, "New Filter");
        add(builder, MekanismLang.BUTTON_ITEMSTACK_FILTER, "ItemStack");
        add(builder, MekanismLang.BUTTON_TAG_FILTER, "Tag");
        add(builder, MekanismLang.BUTTON_MODID_FILTER, "Mod ID");
        //Configuration Card
        add(builder, MekanismLang.CONFIG_CARD_GOT, "Retrieved configuration data from %1$s");
        add(builder, MekanismLang.CONFIG_CARD_SET, "Injected configuration data of type %1$s");
        add(builder, MekanismLang.CONFIG_CARD_UNEQUAL, "Unequal configuration data formats.");
        add(builder, MekanismLang.CONFIG_CARD_HAS_DATA, "Data: %1$s");
        //Connection Type
        add(builder, MekanismLang.CONNECTION_NORMAL, "Normal");
        add(builder, MekanismLang.CONNECTION_PUSH, "Push");
        add(builder, MekanismLang.CONNECTION_PULL, "Pull");
        add(builder, MekanismLang.CONNECTION_NONE, "None");
        //Teleporter
        add(builder, MekanismLang.TELEPORTER_READY, "Ready");
        add(builder, MekanismLang.TELEPORTER_NO_FRAME, "No frame");
        add(builder, MekanismLang.TELEPORTER_NO_LINK, "No link");
        add(builder, MekanismLang.TELEPORTER_NEEDS_ENERGY, "Needs energy");
        //Matrix
        add(builder, MekanismLang.MATRIX, "Induction Matrix");
        add(builder, MekanismLang.MATRIX_RECEIVING_RATE, "Receiving: %1$s/t");
        add(builder, MekanismLang.MATRIX_OUTPUT_AMOUNT, "Output: %1$s");
        add(builder, MekanismLang.MATRIX_OUTPUT_RATE, "Output: %1$s/t");
        add(builder, MekanismLang.MATRIX_OUTPUTTING_RATE, "Outputting: %1$s/t");
        add(builder, MekanismLang.MATRIX_INPUT_AMOUNT, "Input: %1$s");
        add(builder, MekanismLang.MATRIX_INPUT_RATE, "Input: %1$s/t");
        add(builder, MekanismLang.MATRIX_CONSTITUENTS, "Constituents:");
        add(builder, MekanismLang.MATRIX_DIMENSIONS, "Dimensions:");
        add(builder, MekanismLang.MATRIX_DIMENSION_REPRESENTATION, "%1$s x %2$s x %3$s");
        add(builder, MekanismLang.MATRIX_STATS, "Matrix Statistics");
        add(builder, MekanismLang.MATRIX_CELLS, "%1$s cells");
        add(builder, MekanismLang.MATRIX_PROVIDERS, "%1$s providers");
        add(builder, MekanismLang.INDUCTION_PORT_MODE, "Toggled Induction Port transfer mode to %1$s.");
        add(builder, MekanismLang.INDUCTION_PORT_OUTPUT_RATE, "Output Rate: %1$s");
        //Miner
        add(builder, MekanismLang.MINER_BUFFER_FREE, "Free Buffer: %1$s");
        add(builder, MekanismLang.MINER_TO_MINE, "To mine: %1$s");
        add(builder, MekanismLang.MINER_SILK_ENABLED, "Silk: %1$s");
        add(builder, MekanismLang.MINER_AUTO_PULL, "Pull: %1$s");
        add(builder, MekanismLang.MINER_RUNNING, "Running");
        add(builder, MekanismLang.MINER_LOW_POWER, "Low Power");
        add(builder, MekanismLang.MINER_ENERGY_CAPACITY, "Energy Capacity: %1$s");
        add(builder, MekanismLang.MINER_MISSING_BLOCK, "Missing block");
        add(builder, MekanismLang.MINER_WELL, "All is well!");
        add(builder, MekanismLang.MINER_CONFIG, "Digital Miner Config");
        add(builder, MekanismLang.MINER_SILK, "Silk touch");
        add(builder, MekanismLang.MINER_RESET, "Reset");
        add(builder, MekanismLang.MINER_INVERSE, "Inverse mode");
        add(builder, MekanismLang.MINER_REQUIRE_REPLACE, "Require replace: %1$s");
        add(builder, MekanismLang.MINER_REQUIRE_REPLACE_INVERSE, "Inverse mode requires replacement: %1$s");
        add(builder, MekanismLang.MINER_RADIUS, "Radi: %1$s");
        add(builder, MekanismLang.MINER_IDLE, "Not ready");
        add(builder, MekanismLang.MINER_SEARCHING, "Searching");
        add(builder, MekanismLang.MINER_PAUSED, "Paused");
        add(builder, MekanismLang.MINER_READY, "Ready");
        //Boiler
        add(builder, MekanismLang.BOILER, "Thermoelectric Boiler");
        add(builder, MekanismLang.BOILER_STATS, "Boiler Statistics");
        add(builder, MekanismLang.BOILER_MAX_WATER, "Max Water: %1$s mB");
        add(builder, MekanismLang.BOILER_MAX_STEAM, "Max Steam: %1$s mB");
        add(builder, MekanismLang.BOILER_HEAT_TRANSFER, "Heat Transfer");
        add(builder, MekanismLang.BOILER_HEATERS, "Superheaters: %1$s");
        add(builder, MekanismLang.BOILER_CAPACITY, "Boil Capacity: %1$s mB/t");
        add(builder, MekanismLang.BOIL_RATE, "Boil Rate: %1$s mB/t");
        add(builder, MekanismLang.MAX_BOIL_RATE, "Max Boil: %1$s mB/t");
        add(builder, MekanismLang.BOILER_VALVE_MODE_CHANGE, "Valve mode changed to: %1$s");
        add(builder, MekanismLang.BOILER_VALVE_MODE_INPUT, "input only");
        add(builder, MekanismLang.BOILER_VALVE_MODE_OUTPUT_COOLANT, "output coolant");
        add(builder, MekanismLang.BOILER_VALVE_MODE_OUTPUT_STEAM, "output steam");
        add(builder, MekanismLang.BOILER_WATER_TANK, "Water Tank");
        add(builder, MekanismLang.BOILER_STEAM_TANK, "Steam Tank");
        add(builder, MekanismLang.BOILER_HEATED_COOLANT_TANK, "Heated Coolant Tank");
        add(builder, MekanismLang.BOILER_COOLANT_TANK, "Coolant Tank");
        //Temperature
        add(builder, MekanismLang.TEMPERATURE, "Temp: %1$s");
        add(builder, MekanismLang.TEMPERATURE_LONG, "Temperature: %1$s");
        add(builder, MekanismLang.TEMPERATURE_KELVIN, "Kelvin");
        add(builder, MekanismLang.TEMPERATURE_KELVIN_SHORT, "K");
        add(builder, MekanismLang.TEMPERATURE_CELSIUS, "Celsius");
        add(builder, MekanismLang.TEMPERATURE_CELSIUS_SHORT, "C");
        add(builder, MekanismLang.TEMPERATURE_RANKINE, "Rankine");
        add(builder, MekanismLang.TEMPERATURE_RANKINE_SHORT, "R");
        add(builder, MekanismLang.TEMPERATURE_FAHRENHEIT, "Fahrenheit");
        add(builder, MekanismLang.TEMPERATURE_FAHRENHEIT_SHORT, "F");
        add(builder, MekanismLang.TEMPERATURE_AMBIENT, "Ambient");
        add(builder, MekanismLang.TEMPERATURE_AMBIENT_SHORT, "STP");
        //Energy
        add(builder, MekanismLang.ENERGY_JOULES, "Joule");
        add(builder, MekanismLang.ENERGY_JOULES_PLURAL, "Joules");
        add(builder, MekanismLang.ENERGY_JOULES_SHORT, "J");
        add(builder, MekanismLang.ENERGY_FORGE, "Forge Energy");
        add(builder, MekanismLang.ENERGY_FORGE_SHORT, "FE");
        add(builder, MekanismLang.ENERGY_EU, "Electrical Unit");
        add(builder, MekanismLang.ENERGY_EU_PLURAL, "Electrical Units");
        add(builder, MekanismLang.ENERGY_EU_SHORT, "EU");
        //Network Reader
        add(builder, MekanismLang.NETWORK_READER_BORDER, "%1$s %2$s %1$s");
        add(builder, MekanismLang.NETWORK_READER_TEMPERATURE, " *Temperature: %1$s");
        add(builder, MekanismLang.NETWORK_READER_TRANSMITTERS, " *Transmitters: %1$s");
        add(builder, MekanismLang.NETWORK_READER_ACCEPTORS, " *Acceptors: %1$s");
        add(builder, MekanismLang.NETWORK_READER_NEEDED, " *Needed: %1$s");
        add(builder, MekanismLang.NETWORK_READER_BUFFER, " *Buffer: %1$s");
        add(builder, MekanismLang.NETWORK_READER_THROUGHPUT, " *Throughput: %1$s");
        add(builder, MekanismLang.NETWORK_READER_CAPACITY, " *Capacity: %1$s");
        add(builder, MekanismLang.NETWORK_READER_CONNECTED_SIDES, " *Connected sides: %1$s");
        //Sorter
        add(builder, MekanismLang.SORTER_DEFAULT, "Default:");
        add(builder, MekanismLang.SORTER_SINGLE_ITEM, "Single:");
        add(builder, MekanismLang.SORTER_ROUND_ROBIN, "RR:");
        add(builder, MekanismLang.SORTER_AUTO_EJECT, "Auto:");
        add(builder, MekanismLang.SORTER_SINGLE_ITEM_DESCRIPTION, "Sends a single item instead of a whole stack each time (overrides min and max set in ItemStack filters).");
        add(builder, MekanismLang.SORTER_ROUND_ROBIN_DESCRIPTION, "Cycles between all connected inventories when sending items.");
        add(builder, MekanismLang.SORTER_AUTO_EJECT_DESCRIPTION, "Ejects unfiltered items automatically to connected inventories, using the default configuration.");
        //Side data/config
        add(builder, MekanismLang.SIDE_DATA_NONE, "None");
        add(builder, MekanismLang.SIDE_DATA_INPUT, "Input");
        add(builder, MekanismLang.SIDE_DATA_INPUT_1, "Input (1)");
        add(builder, MekanismLang.SIDE_DATA_INPUT_2, "Input (2)");
        add(builder, MekanismLang.SIDE_DATA_OUTPUT, "Output");
        add(builder, MekanismLang.SIDE_DATA_OUTPUT_1, "Output (1)");
        add(builder, MekanismLang.SIDE_DATA_OUTPUT_2, "Output (2)");
        add(builder, MekanismLang.SIDE_DATA_INPUT_OUTPUT, "Input/Output");
        add(builder, MekanismLang.SIDE_DATA_ENERGY, "Energy");
        add(builder, MekanismLang.SIDE_DATA_EXTRA, "Extra");
        //Free runner modes
        add(builder, MekanismLang.FREE_RUNNER_MODE_CHANGE, "Free runner mode changed to: %1$s");
        add(builder, MekanismLang.FREE_RUNNER_NORMAL, "Regular");
        add(builder, MekanismLang.FREE_RUNNER_SAFETY, "Safety");
        add(builder, MekanismLang.FREE_RUNNER_DISABLED, "Disabled");
        //Jetpack Modes
        add(builder, MekanismLang.JETPACK_MODE_CHANGE, "Jetpack mode changed to: %1$s");
        add(builder, MekanismLang.JETPACK_NORMAL, "Regular");
        add(builder, MekanismLang.JETPACK_HOVER, "Hover");
        add(builder, MekanismLang.JETPACK_DISABLED, "Disabled");
        //Disassembler Mode
        add(builder, MekanismLang.DISASSEMBLER_MODE_CHANGE, "Mode toggled to: %1$s (%2$s)");
        add(builder, MekanismLang.DISASSEMBLER_EFFICIENCY, "Efficiency: %1$s");
        //Flamethrower Modes
        add(builder, MekanismLang.FLAMETHROWER_MODE_CHANGE, "Flamethrower mode changed to: %1$s");
        add(builder, MekanismLang.FLAMETHROWER_COMBAT, "Combat");
        add(builder, MekanismLang.FLAMETHROWER_HEAT, "Heat");
        add(builder, MekanismLang.FLAMETHROWER_INFERNO, "Inferno");
        //Configurator
        add(builder, MekanismLang.CONFIGURE_STATE, "Configure State: %1$s");
        add(builder, MekanismLang.STATE, "State: %1$s");
        add(builder, MekanismLang.TOGGLE_COLOR, "Color bumped to: %1$s");
        add(builder, MekanismLang.CURRENT_COLOR, "Current color: %1$s");
        add(builder, MekanismLang.PUMP_RESET, "Reset Electric Pump calculation");
        add(builder, MekanismLang.PLENISHER_RESET, "Reset Fluidic Plenisher calculation");
        add(builder, MekanismLang.REDSTONE_SENSITIVITY, "Redstone sensitivity turned: %1$s");
        add(builder, MekanismLang.CONNECTION_TYPE, "Connection type changed to: %1$s");
        //Configurator Modes
        add(builder, MekanismLang.CONFIGURATOR_VIEW_MODE, "Current %1$s behavior: %2$s (%3$s)");
        add(builder, MekanismLang.CONFIGURATOR_TOGGLE_MODE, "%1$s behavior bumped to: %2$s (%3$s)");
        add(builder, MekanismLang.CONFIGURATOR_CONFIGURATE, "Configurate (%1$s)");
        add(builder, MekanismLang.CONFIGURATOR_EMPTY, "Empty");
        add(builder, MekanismLang.CONFIGURATOR_ROTATE, "Rotate");
        add(builder, MekanismLang.CONFIGURATOR_WRENCH, "Wrench");
        //Robit
        add(builder, MekanismLang.ROBIT, "Robit");
        add(builder, MekanismLang.ROBIT_NAME, "Name: %1$s");
        add(builder, MekanismLang.ROBIT_SMELTING, "Robit Smelting");
        add(builder, MekanismLang.ROBIT_CRAFTING, "Robit Crafting");
        add(builder, MekanismLang.ROBIT_INVENTORY, "Robit Inventory");
        add(builder, MekanismLang.ROBIT_REPAIR, "Robit Repair");
        add(builder, MekanismLang.ROBIT_TELEPORT, "Teleport back home");
        add(builder, MekanismLang.ROBIT_TOGGLE_PICKUP, "Toggle 'drop pickup' mode");
        add(builder, MekanismLang.ROBIT_RENAME, "Rename this Robit");
        add(builder, MekanismLang.ROBIT_SKIN, "Skin: %1$s");
        add(builder, MekanismLang.ROBIT_SKIN_SELECT, "Change this Robit's appearance");
        add(builder, MekanismLang.ROBIT_TOGGLE_FOLLOW, "Toggle 'follow' mode");
        add(builder, MekanismLang.ROBIT_GREETING, "Hi, I'm %1$s!");
        add(builder, MekanismLang.ROBIT_OWNER, "Owner: %1$s");
        add(builder, MekanismLang.ROBIT_FOLLOWING, "Following: %1$s");
        add(builder, MekanismLang.ROBIT_DROP_PICKUP, "Drop pickup: %1$s");
        //Descriptions
        add(builder, MekanismLang.DESCRIPTION_QIO_DRIVE_ARRAY, "The foundation of any Quantum Item Orchestration system. QIO Drives are stored here.");
        add(builder, MekanismLang.DESCRIPTION_QIO_DASHBOARD, "A placeable monitor used to access an Quantum Item Orchestration system's contents.");
        add(builder, MekanismLang.DESCRIPTION_QIO_IMPORTER, "A QIO-linked item import unit. Place on a block and import its contents to your QIO system.");
        add(builder, MekanismLang.DESCRIPTION_QIO_EXPORTER, "A QIO-linked item export unit. Place on a block and export contents from your QIO system to the block.");
        add(builder, MekanismLang.DESCRIPTION_QIO_REDSTONE_ADAPTER, "A QIO-linked redstone adapter. Use to monitor your QIO system's contents.");
        add(builder, MekanismLang.DESCRIPTION_DICTIONARY, "A tool used for viewing the tags of various components such as: items, blocks, and fluids.");
        add(builder, MekanismLang.DESCRIPTION_SEISMIC_READER, "A portable machine that uses seismic vibrations to provide information on differing layers of the world.");
        add(builder, MekanismLang.DESCRIPTION_BIN, "A block used to store large quantities of a single type of item.");
        add(builder, MekanismLang.DESCRIPTION_TELEPORTER_FRAME, "The frame used to construct the Teleporter multiblock, allowing a portal to be generated within the structure.");
        add(builder, MekanismLang.DESCRIPTION_STEEL_CASING, "A sturdy, steel-based casing used as a foundation for machinery.");
        add(builder, MekanismLang.DESCRIPTION_DYNAMIC_TANK, "The casing used in the Dynamic Tank multiblock, a structure capable of storing great amounts of fluid and chemicals.");
        add(builder, MekanismLang.DESCRIPTION_STRUCTURAL_GLASS, "An advanced, reinforced material of glass that drops when broken and can be used in the structure of any applicable multiblock.");
        add(builder, MekanismLang.DESCRIPTION_DYNAMIC_VALVE, "A valve that can be placed on a Dynamic Tank multiblock, allowing for fluids and chemicals to be inserted and extracted via external piping.");
        add(builder, MekanismLang.DESCRIPTION_THERMAL_EVAPORATION_CONTROLLER, "The controller for a Thermal Evaporation Plant, acting as the master block of the structure. Only one of these should be placed on a multiblock.");
        add(builder, MekanismLang.DESCRIPTION_THERMAL_EVAPORATION_VALVE, "A valve that can be placed on a Thermal Evaporation Plant multiblock, allowing for fluids to be inserted and extracted via external piping.");
        add(builder, MekanismLang.DESCRIPTION_THERMAL_EVAPORATION_BLOCK, "A copper-alloyed casing used in the structure of a Thermal Evaporation Plant, using its advanced material to conduct the great amounts of heat necessary for processing.");
        add(builder, MekanismLang.DESCRIPTION_INDUCTION_CASING, "A type of energy-resistant casing used in the creation of an Energized Induction Matrix multiblock.");
        add(builder, MekanismLang.DESCRIPTION_INDUCTION_PORT, "A port that can be placed on an Energized Induction Matrix multiblock, allowing for energy to be inserted from and output to external cabling.");
        add(builder, MekanismLang.DESCRIPTION_INDUCTION_CELL, "A highly conductive energy capacitor capable of storing massive amounts of energy in a single block. Housed in an Energized Induction Matrix to expand the multiblock's energy storage.");
        add(builder, MekanismLang.DESCRIPTION_INDUCTION_PROVIDER, "An advanced complex of coolant systems, conductors and transformers capable of expanding the Energized Induction Matrix's maximum rate of energy transfer.");
        add(builder, MekanismLang.DESCRIPTION_SUPERHEATING_ELEMENT, "A modular, somewhat dangerous radiator that is capable of emitting massive amounts of heat to its surroundings.");
        add(builder, MekanismLang.DESCRIPTION_PRESSURE_DISPERSER, "A block used to disperse steam throughout a multiblock structure. These should form a gapless, horizontal plane in order to properly control steam flow.");
        add(builder, MekanismLang.DESCRIPTION_BOILER_CASING, "A pressure-resistant, dense casing used in the creation of a Thermoelectric Boiler multiblock.");
        add(builder, MekanismLang.DESCRIPTION_BOILER_VALVE, "A valve that can be placed on a Thermoelectric Boiler multiblock, allowing for the insertion of energy and water along with the extraction of produced steam.");
        add(builder, MekanismLang.DESCRIPTION_SECURITY_DESK, "A central control hub for managing the security of all your owned machinery.");
        add(builder, MekanismLang.DESCRIPTION_ENRICHMENT_CHAMBER, "A simple machine used to enrich ores into two of their dust counterparts, as well as perform many other operations.");
        add(builder, MekanismLang.DESCRIPTION_OSMIUM_COMPRESSOR, "A fairly advanced machine used to compress osmium into various dusts in order to create their ingot counterparts.");
        add(builder, MekanismLang.DESCRIPTION_COMBINER, "A machine used to combine items together. For example, raw ores and cobblestone to form their ore counterparts.");
        add(builder, MekanismLang.DESCRIPTION_CRUSHER, "A machine used to crush ingots into their dust counterparts, as well as perform many other operations.");
        add(builder, MekanismLang.DESCRIPTION_DIGITAL_MINER, "A highly-advanced, filter-based, auto-miner that can mine whatever block you tell it to within a 32 block (max) radius.");
        add(builder, MekanismLang.DESCRIPTION_METALLURGIC_INFUSER, "A machine used to infuse various materials into (generally) metals to create metal alloys and other compounds.");
        add(builder, MekanismLang.DESCRIPTION_PURIFICATION_CHAMBER, "An advanced machine capable of processing ores into three clumps, serving as the initial stage of 300% ore processing.");
        add(builder, MekanismLang.DESCRIPTION_ENERGIZED_SMELTER, "A simple machine that serves as a " + modName + "-based furnace that runs off of energy.");
        add(builder, MekanismLang.DESCRIPTION_TELEPORTER, "A machine capable of teleporting players to various locations defined by another teleporter.");
        add(builder, MekanismLang.DESCRIPTION_ELECTRIC_PUMP, "An advanced, upgradeable pump, capable of extracting any type of fluid.");
        add(builder, MekanismLang.DESCRIPTION_PERSONAL_BARREL, "A 54-slot barrel that can be opened anywhere- even from your own inventory.");
        add(builder, MekanismLang.DESCRIPTION_PERSONAL_CHEST, "A 54-slot chest that can be opened from your own inventory.");
        add(builder, MekanismLang.DESCRIPTION_CHARGEPAD, "A universal chargepad that can charge any energized item from any mod.");
        add(builder, MekanismLang.DESCRIPTION_LOGISTICAL_SORTER, "A filter-based, advanced sorting machine that can auto-eject specified items out of and into adjacent inventories and Logistical Transporters.");
        add(builder, MekanismLang.DESCRIPTION_ROTARY_CONDENSENTRATOR, "A machine capable of converting gases into their fluid form and vice versa.");
        add(builder, MekanismLang.DESCRIPTION_CHEMICAL_INJECTION_CHAMBER, "An elite machine capable of processing ores into four shards, serving as the initial stage of 400% ore processing.");
        add(builder, MekanismLang.DESCRIPTION_ELECTROLYTIC_SEPARATOR, "A machine that uses the process of electrolysis to split apart a certain gas into two different gases.");
        add(builder, MekanismLang.DESCRIPTION_PRECISION_SAWMILL, "A machine used to process logs and other wood-based items more efficiently, as well as to obtain sawdust.");
        add(builder, MekanismLang.DESCRIPTION_CHEMICAL_DISSOLUTION_CHAMBER, "An ultimate machine used to chemically dissolve all impurities of an ore, leaving an unprocessed slurry behind.");
        add(builder, MekanismLang.DESCRIPTION_CHEMICAL_WASHER, "An ultimate machine that cleans unprocessed slurry and prepares it for crystallization.");
        add(builder, MekanismLang.DESCRIPTION_CHEMICAL_CRYSTALLIZER, "An ultimate machine used to crystallize purified ore slurry into ore crystals.");
        add(builder, MekanismLang.DESCRIPTION_CHEMICAL_OXIDIZER, "A machine capable of oxidizing solid materials into gas phase.");
        add(builder, MekanismLang.DESCRIPTION_CHEMICAL_INFUSER, "A machine that produces a new gas by infusing two others.");
        add(builder, MekanismLang.DESCRIPTION_SEISMIC_VIBRATOR, "A machine that uses seismic vibrations to provide information on differing layers of the world.");
        add(builder, MekanismLang.DESCRIPTION_PRESSURIZED_REACTION_CHAMBER, "An advanced machine that processes a solid, liquid and gaseous mixture and creates both a gaseous and solid product.");
        add(builder, MekanismLang.DESCRIPTION_FLUID_TANK, "A handy, sturdy, portable tank that lets you carry multiple buckets of fluid wherever you please. Also doubles as a bucket!");
        add(builder, MekanismLang.DESCRIPTION_FLUIDIC_PLENISHER, "A machine that is capable of creating entire lakes by filling ravines with fluids.");
        add(builder, MekanismLang.DESCRIPTION_LASER, "An advanced form of linear energy transfer that utilizes an extremely collimated beam of light.");
        add(builder, MekanismLang.DESCRIPTION_LASER_AMPLIFIER, "A block that can be used to merge, redirect and amplify laser beams, with fine controls over when to fire.");
        add(builder, MekanismLang.DESCRIPTION_LASER_TRACTOR_BEAM, "A block used to merge and redirect laser beams. Collects drops from blocks it has broken.");
        add(builder, MekanismLang.DESCRIPTION_SOLAR_NEUTRON_ACTIVATOR, "A machine that directs the neutron radiation of the sun into its internal reservoir, allowing for the slow creation of various isotopes.");
        add(builder, MekanismLang.DESCRIPTION_OREDICTIONIFICATOR, "A machine used to unify and translate between various items and blocks using item tags.");
        add(builder, MekanismLang.DESCRIPTION_FACTORY, "A machine that serves as an upgrade to regular machinery, allowing for multiple processing operations to occur at once.");
        add(builder, MekanismLang.DESCRIPTION_RESISTIVE_HEATER, "A condensed, coiled resistor capable of converting electrical energy directly into heat energy.");
        add(builder, MekanismLang.DESCRIPTION_FORMULAIC_ASSEMBLICATOR, "A machine that uses energy to rapidly craft items and blocks from Crafting Formulas. Doubles as an advanced crafting bench.");
        add(builder, MekanismLang.DESCRIPTION_FUELWOOD_HEATER, "A machine that is capable of producing large quantities of heat energy by burning combustible items.");
        add(builder, MekanismLang.DESCRIPTION_MODIFICATION_STATION, "An advanced workbench capable of installing and removing modules from modular equipment (i.e. MekaSuit!)");
        add(builder, MekanismLang.DESCRIPTION_ISOTOPIC_CENTRIFUGE, "A machine with one single purpose: to spin its contents really, REALLY fast.");
        add(builder, MekanismLang.DESCRIPTION_QUANTUM_ENTANGLOPORTER, "A highly-advanced block capable of transmitting any practical resource across long distances and dimensions.");
        add(builder, MekanismLang.DESCRIPTION_NUTRITIONAL_LIQUIFIER, "A machine that is capable of processing any foods into non-dangerous, easily-digestible Nutritional Paste.");
        add(builder, MekanismLang.DESCRIPTION_ANTIPROTONIC_NUCLEOSYNTHESIZER, "A machine which uses bits of antimatter and mass amounts of energy to atomically transmute various resources.");
        add(builder, MekanismLang.DESCRIPTION_PIGMENT_EXTRACTOR, "A machine used to extract pigments from blocks and items.");
        add(builder, MekanismLang.DESCRIPTION_PIGMENT_MIXER, "A sturdy machine capable of mixing two pigments together to produce a new pigment.");
        add(builder, MekanismLang.DESCRIPTION_PAINTING_MACHINE, "A machine used to color blocks and items via a careful application of a stored pigment.");
        add(builder, MekanismLang.DESCRIPTION_RADIOACTIVE_WASTE_BARREL, "A barrel that can be used to 'safely' store radioactive waste. WARNING: breaking this barrel will release its contents into the atmosphere.");
        add(builder, MekanismLang.DESCRIPTION_INDUSTRIAL_ALARM, "Not just your everyday alarm... this is an 'industrial' alarm!");
        add(builder, MekanismLang.DESCRIPTION_ENERGY_CUBE, "An advanced device for storing and distributing energy.");
        add(builder, MekanismLang.DESCRIPTION_CHEMICAL_TANK, "A portable tank that lets you carry chemicals wherever you please.");
        add(builder, MekanismLang.DESCRIPTION_DIVERSION, "- Controllable by redstone");
        add(builder, MekanismLang.DESCRIPTION_RESTRICTIVE, "- Only used if no other paths available");
        add(builder, MekanismLang.DESCRIPTION_SPS_CASING, "Reinforced casing capable of resisting intense chemical and thermal effects from phase-shifting reactions.");
        add(builder, MekanismLang.DESCRIPTION_SPS_PORT, "A port used for the transfer of energy and substances in the Supercritical Phase Shifter.");
        add(builder, MekanismLang.DESCRIPTION_SUPERCHARGED_COIL, "Used in Supercritical Phase Shifter multiblock to supply large quantities of energy. Must be attached to a SPS Port.");
        add(builder, MekanismLang.DESCRIPTION_DIMENSIONAL_STABILIZER, "A machine that prevents areas of the world from disappearing when not observed.");
        // Radial Menu
        add(builder, MekanismLang.RADIAL_VEIN, "Vein Mining");
        add(builder, MekanismLang.RADIAL_VEIN_NORMAL, "Vein");
        add(builder, MekanismLang.RADIAL_VEIN_EXTENDED, "Extended Vein");

        add(builder, MekanismLang.RADIAL_EXCAVATION_SPEED, "Excavation Speed");
        add(builder, MekanismLang.RADIAL_EXCAVATION_SPEED_OFF, "Off");
        add(builder, MekanismLang.RADIAL_EXCAVATION_SPEED_SLOW, "Slow");
        add(builder, MekanismLang.RADIAL_EXCAVATION_SPEED_NORMAL, "Normal");
        add(builder, MekanismLang.RADIAL_EXCAVATION_SPEED_FAST, "Fast");
        add(builder, MekanismLang.RADIAL_EXCAVATION_SPEED_SUPER, "Super Fast");
        add(builder, MekanismLang.RADIAL_EXCAVATION_SPEED_EXTREME, "Extreme");

        add(builder, MekanismLang.RADIAL_BLASTING_POWER, "Blasting Power");
        add(builder, MekanismLang.RADIAL_BLASTING_POWER_OFF, "Off");
        add(builder, MekanismLang.RADIAL_BLASTING_POWER_LOW, "Low");
        add(builder, MekanismLang.RADIAL_BLASTING_POWER_MED, "Medium");
        add(builder, MekanismLang.RADIAL_BLASTING_POWER_HIGH, "High");
        add(builder, MekanismLang.RADIAL_BLASTING_POWER_EXTREME, "Extreme");
        // Modules
        add(builder, MekanismLang.MODULE_ENABLED, "Enabled");
        add(builder, MekanismLang.MODULE_ENABLED_LOWER, "enabled");
        add(builder, MekanismLang.MODULE_DISABLED_LOWER, "disabled");
        add(builder, MekanismLang.MODULE_DAMAGE, "Damage Amplification: %1$s");
        add(builder, MekanismLang.MODULE_TWEAKER, "Module Tweaker");
        add(builder, MekanismLang.MODULE_INSTALLED, "Installed: %1$s");
        add(builder, MekanismLang.MODULE_SUPPORTED, "Supported by:");
        add(builder, MekanismLang.MODULE_CONFLICTING, "Conflicts with:");
        add(builder, MekanismLang.MODULE_STACKABLE, "Stackable: %1$s");
        add(builder, MekanismLang.MODULE_EXCLUSIVE, "(Exclusive Module)");
        add(builder, MekanismLang.MODULE_HANDLE_MODE_CHANGE, "Handle Mode Key");
        add(builder, MekanismLang.MODULE_RENDER_HUD, "Show in HUD");
        add(builder, MekanismLang.MODULE_MODE, "Mode");
        add(builder, MekanismLang.MODULE_COLOR, "ARGB Color");
        add(builder, MekanismLang.MODULE_BONUS_ATTACK_DAMAGE, "Bonus Attack Damage");
        add(builder, MekanismLang.MODULE_FARMING_RADIUS, "Farming Radius");
        add(builder, MekanismLang.MODULE_JUMP_BOOST, "Jump Boost");
        add(builder, MekanismLang.MODULE_STEP_ASSIST, "Step Assist");
        add(builder, MekanismLang.MODULE_RANGE, "Range");
        add(builder, MekanismLang.MODULE_SPRINT_BOOST, "Sprint Boost");
        add(builder, MekanismLang.MODULE_SWIM_BOOST, "Swim Boost");
        add(builder, MekanismLang.MODULE_EXTENDED_MODE, "Extended Mode");
        add(builder, MekanismLang.MODULE_EXTENDED_ENABLED, "Extended Vein Mining: %1$s");
        add(builder, MekanismLang.MODULE_EXCAVATION_RANGE, "Excavation Range");
        add(builder, MekanismLang.MODULE_BLAST_RADIUS, "Blast Radius");
        add(builder, MekanismLang.MODULE_BLASTING_ENABLED, "Blast Radius: %1$s");
        add(builder, MekanismLang.MODULE_BLAST_AREA, "%1$sx%1$s");
        add(builder, MekanismLang.MODULE_EFFICIENCY, "Efficiency");
        add(builder, MekanismLang.MODULE_MODE_CHANGE, "%1$s bumped to: %2$s");
        add(builder, MekanismLang.MODULE_JETPACK_MODE, "Jetpack Mode");
        add(builder, MekanismLang.MODULE_GRAVITATIONAL_MODULATION, "Gravitational Modulation");
        add(builder, MekanismLang.MODULE_MAGNETIC_ATTRACTION, "Magnetic Attraction");
        add(builder, MekanismLang.MODULE_CHARGE_SUIT, "Charge Suit");
        add(builder, MekanismLang.MODULE_CHARGE_INVENTORY, "Charge Inventory");
        add(builder, MekanismLang.MODULE_SPEED_BOOST, "Speed Boost");
        add(builder, MekanismLang.MODULE_VISION_ENHANCEMENT, "Vision Enhancement");
        add(builder, MekanismLang.MODULE_BREATHING_HELD, "Fill Held");
        add(builder, MekanismLang.MODULE_PURIFICATION_BENEFICIAL, "Remove Beneficial");
        add(builder, MekanismLang.MODULE_PURIFICATION_NEUTRAL, "Remove Neutral");
        add(builder, MekanismLang.MODULE_PURIFICATION_HARMFUL, "Remove Harmful");
        add(builder, MekanismLang.MODULE_TELEPORT_REQUIRES_BLOCK, "Requires Block Target");

        add(builder, MekanismModules.ENERGY_UNIT, "Energy Unit", "Increases maximum energy capacity.");
        add(builder, MekanismModules.COLOR_MODULATION_UNIT, "Color Modulation Unit", "Uses advanced holographic projectors to modulate the perceived color of the MekaSuit.");
        add(builder, MekanismModules.LASER_DISSIPATION_UNIT, "Laser Dissipation Unit", "Refracts and safely dissipates lasers that hit any MekaSuit armor piece.");
        add(builder, MekanismModules.RADIATION_SHIELDING_UNIT, "Radiation Shielding Unit", "Provides thick, radiation-proof metal plating to any MekaSuit armor piece.");

        add(builder, MekanismModules.EXCAVATION_ESCALATION_UNIT, "Excavation Escalation Unit", "Increases digging speed on any block.");
        add(builder, MekanismModules.ATTACK_AMPLIFICATION_UNIT, "Attack Amplification Unit", "Amplifies melee attacks on players or mobs.");
        add(builder, MekanismModules.SILK_TOUCH_UNIT, "Silk Touch Unit", "Allows all mined blocks to drop as themselves.");
        add(builder, MekanismModules.FORTUNE_UNIT, "Ore Refinement Unit", "Increases ore yields.");
        add(builder, MekanismModules.BLASTING_UNIT, "Blasting Unit", "Uses controlled explosions to destroy nearby blocks in the target plane.");
        add(builder, MekanismModules.VEIN_MINING_UNIT, "Vein Mining Unit", "Allows for quick mining of ore deposits and rapid felling of trees.");
        add(builder, MekanismModules.FARMING_UNIT, "Farming Unit", "Allows for soil tilling, log stripping, and soil flattening.");
        add(builder, MekanismModules.SHEARING_UNIT, "Shearing Unit", "Allows the creation of energy blades for precise cutting jobs. Does not add laser swords.");
        add(builder, MekanismModules.TELEPORTATION_UNIT, "Teleportation Unit", "Provides for quick travel to nearby blocks.");

        add(builder, MekanismModules.ELECTROLYTIC_BREATHING_UNIT, "Electrolytic Breathing Unit", "Uses electrolysis to create breathable oxygen from water. Will also fill a jetpack module with hydrogen when necessary.");
        add(builder, MekanismModules.INHALATION_PURIFICATION_UNIT, "Inhalation Purification Unit", "Applies a miniature electromagnetic field around the breathing apparatus, preventing selected potion effect types.");
        add(builder, MekanismModules.VISION_ENHANCEMENT_UNIT, "Vision Enhancement Unit", "Brightens the surrounding environment, allowing the user to see through darkness. Install multiple for more effective night vision.");
        add(builder, MekanismModules.NUTRITIONAL_INJECTION_UNIT, "Nutritional Injection Unit", "Automatically feeds the player Nutritional Paste when hungry.");
        add(builder, MekanismModules.JETPACK_UNIT, "Jetpack Unit", "Applies a hydrogen-fueled jetpack to the MekaSuit.");
        add(builder, MekanismModules.GRAVITATIONAL_MODULATING_UNIT, "Gravitational Modulating Unit", "Using experimental technologies and the tremendous energy of antimatter, allows the user to defy gravity.");
        add(builder, MekanismModules.ELYTRA_UNIT, "Elytra Unit", "Applies an HDPE Reinforced Elytra to the MekaSuit.");
        add(builder, MekanismModules.GYROSCOPIC_STABILIZATION_UNIT, "Gyroscopic Stabilization Unit", "Allows the user to act as though they are on solid ground.");
        add(builder, MekanismModules.MOTORIZED_SERVO_UNIT, "Motorized Servo Unit", "Uses motorized servos to reduce the strain of sneaking.");
        add(builder, MekanismModules.HYDROSTATIC_REPULSOR_UNIT, "Hydrostatic Repulsor Unit", "Uses advanced technology to repel water, lowering the resistance felt while moving through it.");
        add(builder, MekanismModules.CHARGE_DISTRIBUTION_UNIT, "Charge Distribution Unit", "Evenly distributes charge throughout all worn MekaSuit armor.");
        add(builder, MekanismModules.DOSIMETER_UNIT, "Dosimeter Unit", "Displays the user's current radiation dose in the HUD.");
        add(builder, MekanismModules.GEIGER_UNIT, "Geiger Unit", "Displays the ambient radiation level in the HUD.");
        add(builder, MekanismModules.LOCOMOTIVE_BOOSTING_UNIT, "Locomotive Boosting Unit", "Increases the user's sprinting speed (and jumping distance).");
        add(builder, MekanismModules.HYDRAULIC_PROPULSION_UNIT, "Hydraulic Propulsion Unit", "Allows the user to both step and jump higher.");
        add(builder, MekanismModules.MAGNETIC_ATTRACTION_UNIT, "Magnetic Attraction Unit", "Uses powerful magnets to draw distant items towards the player. Install multiple for a greater range.");
        add(builder, MekanismModules.FROST_WALKER_UNIT, "Frost Walker Unit", "Uses liquid hydrogen to freeze any water the player walks on. Install multiple for a greater range.");
    }

    private void addOre(TranslationBuilder builder, OreType type, String description) {
        String name = formatAndCapitalize(type.getResource().getRegistrySuffix());
        OreBlockType oreBlockType = MekanismBlocks.ORES.get(type);
        add(builder, oreBlockType.stone(), name + " Ore");
        add(builder, oreBlockType.stoneBlock().getDescriptionTranslationKey(), description);
        add(builder, oreBlockType.deepslate(), "Deepslate " + name + " Ore");
    }

    private void addTiered(TranslationBuilder builder, IItemProvider basic, IItemProvider advanced, IItemProvider elite, IItemProvider ultimate, String name) {
        add(builder, basic, "Basic " + name);
        add(builder, advanced, "Advanced " + name);
        add(builder, elite, "Elite " + name);
        add(builder, ultimate, "Ultimate " + name);
    }

    private void addTiered(TranslationBuilder builder, IItemProvider basic, IItemProvider advanced, IItemProvider elite, IItemProvider ultimate, IItemProvider creative, String name) {
        addTiered(builder, basic, advanced, elite, ultimate, name);
        add(builder, creative, "Creative " + name);
    }
}