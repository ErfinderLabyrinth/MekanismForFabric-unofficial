package mekanism.common.advancements;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import mekanism.api.datagen.recipe.RecipeCriterion;
import mekanism.common.Mekanism;
import mekanism.common.advancements.triggers.AlloyUpgradeTrigger;
import mekanism.common.advancements.triggers.BlockLaserTrigger;
import mekanism.common.advancements.triggers.ChangeRobitSkinTrigger;
import mekanism.common.advancements.triggers.ConfigurationCardTrigger;
import mekanism.common.advancements.triggers.MekanismDamageTrigger;
import mekanism.common.advancements.triggers.UnboxCardboardBoxTrigger;
import mekanism.common.advancements.triggers.UseGaugeDropperTrigger;
import mekanism.common.advancements.triggers.ViewVibrationsTrigger;
import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.entity.RobitPrideSkinData;
import mekanism.common.item.block.machine.ItemBlockFactory;
import mekanism.common.mixinhelper.ItemPredicateMekanismAddonAccessor;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismDamageTypes;
import mekanism.common.registries.MekanismEntityTypes;
import mekanism.common.registries.MekanismItems;
import mekanism.common.registries.MekanismRobitSkins;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import mekanism.common.tags.MekanismTags;
import mekanism.common.tier.FactoryTier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class MekanismAdvancementProvider extends BaseAdvancementProvider {

    public MekanismAdvancementProvider(FabricDataOutput output) {
        super(output, Mekanism.MODID);
    }

    //TODO - 1.19: xp rewards for any of these?
    @Override
    protected void registerAdvancements(@NotNull Consumer<Advancement> consumer) {
        Advancement root = advancement(MekanismAdvancements.ROOT)
                .display(MekanismItems.ATOMIC_DISASSEMBLER, Mekanism.rl("textures/block/block_osmium.png"), FrameType.GOAL, false, false, false)
                .addCriterion("automatic", new PlayerTrigger.TriggerInstance(MekanismCriteriaTriggers.LOGGED_IN.getId(), ContextAwarePredicate.ANY))
                .save(consumer);
        Advancement materials = advancement(MekanismAdvancements.MATERIALS)
                .parent(root)
                .display(MekanismItems.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.OSMIUM), FrameType.TASK, false)
                .orCriteria("material", MekanismItems.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.OSMIUM),
                        MekanismItems.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.TIN),
                        MekanismItems.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.LEAD),
                        MekanismItems.PROCESSED_RESOURCES.get(ResourceType.INGOT, PrimaryResource.URANIUM),
                        MekanismItems.FLUORITE_GEM
                ).save(consumer);

        Advancement cleaning_gauges = advancement(MekanismAdvancements.CLEANING_GAUGES)
                .parent(materials)
                .display(MekanismItems.GAUGE_DROPPER, FrameType.GOAL, true)
                .addCriterion("use_dropper", UseGaugeDropperTrigger.TriggerInstance.any())
                .save(consumer);

        Advancement metallurgic_infuser = advancement(MekanismAdvancements.METALLURGIC_INFUSER)
                .parent(materials)
                .displayAndCriterion(MekanismBlocks.METALLURGIC_INFUSER, FrameType.TASK, true)
                .save(consumer);
        Advancement steel_ingot = advancement(MekanismAdvancements.STEEL_INGOT)
                .parent(metallurgic_infuser)
                .displayAndCriterion(MekanismItems.STEEL_INGOT, FrameType.TASK, true)
                .save(consumer);
        Advancement steel_casing = advancement(MekanismAdvancements.STEEL_CASING)
                .parent(steel_ingot)
                .displayAndCriterion(MekanismBlocks.STEEL_CASING, FrameType.TASK, true)
                .save(consumer);

        Advancement infused_alloy = advancement(MekanismAdvancements.INFUSED_ALLOY)
                .parent(metallurgic_infuser)
                .displayAndCriterion(MekanismItems.INFUSED_ALLOY, FrameType.TASK, true)
                .save(consumer);
        Advancement reinforced_alloy = advancement(MekanismAdvancements.REINFORCED_ALLOY)
                .parent(infused_alloy)
                .displayAndCriterion(MekanismItems.REINFORCED_ALLOY, FrameType.TASK, false)
                .save(consumer);
        Advancement atomic_alloy = advancement(MekanismAdvancements.ATOMIC_ALLOY)
                .parent(reinforced_alloy)
                .displayAndCriterion(MekanismItems.ATOMIC_ALLOY, FrameType.GOAL, false)
                .save(consumer);

        Advancement basic_control_circuit = advancement(MekanismAdvancements.BASIC_CONTROL_CIRCUIT)
                .parent(metallurgic_infuser)
                .displayAndCriterion(MekanismItems.BASIC_CONTROL_CIRCUIT, FrameType.TASK, true)
                .save(consumer);
        Advancement advanced_control_circuit = advancement(MekanismAdvancements.ADVANCED_CONTROL_CIRCUIT)
                .parent(infused_alloy)
                .displayAndCriterion(MekanismItems.ADVANCED_CONTROL_CIRCUIT, FrameType.TASK, false)
                .save(consumer);
        Advancement elite_control_circuit = advancement(MekanismAdvancements.ELITE_CONTROL_CIRCUIT)
                .parent(reinforced_alloy)
                .displayAndCriterion(MekanismItems.ELITE_CONTROL_CIRCUIT, FrameType.TASK, false)
                .save(consumer);
        Advancement ultimate_control_circuit = advancement(MekanismAdvancements.ULTIMATE_CONTROL_CIRCUIT)
                .parent(atomic_alloy)
                .displayAndCriterion(MekanismItems.ULTIMATE_CONTROL_CIRCUIT, FrameType.GOAL, false)
                .save(consumer);

        Advancement alloy_upgrading = advancement(MekanismAdvancements.ALLOY_UPGRADING)
                .parent(infused_alloy)
                .display(MekanismItems.INFUSED_ALLOY, FrameType.GOAL, false)
                .addCriterion("upgrade", AlloyUpgradeTrigger.TriggerInstance.upgraded())
                .save(consumer);
        Advancement laser = advancement(MekanismAdvancements.LASER)
                .parent(reinforced_alloy)
                .displayAndCriterion(MekanismBlocks.LASER, FrameType.TASK, false)
                .save(consumer);
        Advancement laser_death = advancement(MekanismAdvancements.LASER_DEATH)
                .parent(laser)
                .display(Items.SKELETON_SKULL, null, FrameType.TASK, true, true, true)
                .addCriterion("death", MekanismDamageTrigger.TriggerInstance.killed(MekanismDamageTypes.LASER))
                .save(consumer);
        Advancement stopping_lasers = advancement(MekanismAdvancements.STOPPING_LASERS)
                .parent(laser)
                .display(Items.SHIELD, FrameType.TASK, true)
                .addCriterion("block", BlockLaserTrigger.TriggerInstance.block())
                .save(consumer);
        Advancement auto_collection = advancement(MekanismAdvancements.AUTO_COLLECTION)
                .parent(laser)
                .displayAndCriterion(MekanismBlocks.LASER_TRACTOR_BEAM, FrameType.TASK, false)
                .save(consumer);

        Advancement alarm = advancement(MekanismAdvancements.ALARM)
                .parent(basic_control_circuit)
                .displayAndCriterion(MekanismBlocks.INDUSTRIAL_ALARM, FrameType.TASK, false)
                .save(consumer);
        Advancement installer = advancement(MekanismAdvancements.INSTALLER)
                .parent(basic_control_circuit)
                .display(MekanismItems.BASIC_TIER_INSTALLER, FrameType.GOAL, false)
                .orCriteria("installer", MekanismItems.BASIC_TIER_INSTALLER,
                        MekanismItems.ADVANCED_TIER_INSTALLER,
                        MekanismItems.ELITE_TIER_INSTALLER,
                        MekanismItems.ULTIMATE_TIER_INSTALLER
                ).save(consumer);
        Advancement factory = advancement(MekanismAdvancements.FACTORY)
                .parent(basic_control_circuit)
                .display(MekanismBlocks.getFactory(FactoryTier.BASIC, FactoryType.SMELTING), FrameType.GOAL, true)
                .orCriteria("factory", getItems(MekanismBlocks.BLOCKS.getAllBlocks(), item -> item instanceof ItemBlockFactory))
                .save(consumer);
        Advancement configuration_copying = advancement(MekanismAdvancements.CONFIGURATION_COPYING)
                .parent(basic_control_circuit)
                .display(MekanismItems.CONFIGURATION_CARD, FrameType.TASK, false)
                .andCriteria(
                        new RecipeCriterion("copy", ConfigurationCardTrigger.TriggerInstance.copy()),
                        new RecipeCriterion("paste", ConfigurationCardTrigger.TriggerInstance.paste())
                ).save(consumer);

        Advancement running_free = advancement(MekanismAdvancements.RUNNING_FREE)
                .parent(basic_control_circuit)
                .displayAndCriterion(MekanismItems.FREE_RUNNERS, FrameType.TASK, true)
                .save(consumer);
        Advancement playing_with_fire = advancement(MekanismAdvancements.PLAYING_WITH_FIRE)
                .parent(advanced_control_circuit)
                .displayAndCriterion(MekanismItems.FLAMETHROWER, FrameType.TASK, false)
                .save(consumer);
        Advancement machine_security = advancement(MekanismAdvancements.MACHINE_SECURITY)
                .parent(elite_control_circuit)
                .displayAndCriterion(MekanismBlocks.SECURITY_DESK, FrameType.TASK, false)
                .save(consumer);
        Advancement solar_neuron_activator = advancement(MekanismAdvancements.SOLAR_NEUTRON_ACTIVATOR)
                .parent(elite_control_circuit)
                .displayAndCriterion(MekanismBlocks.SOLAR_NEUTRON_ACTIVATOR, FrameType.TASK, false)
                .save(consumer);
        Advancement stabilizing_chunks = advancement(MekanismAdvancements.STABILIZING_CHUNKS)
                .parent(ultimate_control_circuit)
                .displayAndCriterion(MekanismBlocks.DIMENSIONAL_STABILIZER, FrameType.CHALLENGE, true)
                .addCriterion(MekanismItems.ANCHOR_UPGRADE)
                .save(consumer);

        Advancement personal_storage = advancement(MekanismAdvancements.PERSONAL_STORAGE)
                .parent(basic_control_circuit)
                .display(MekanismBlocks.PERSONAL_CHEST, FrameType.TASK, false)
                .addCriterion("storage", hasItems(MekanismTags.Items.PERSONAL_STORAGE))
                .save(consumer);
        Advancement simple_mass_storage = advancement(MekanismAdvancements.SIMPLE_MASS_STORAGE)
                .parent(basic_control_circuit)
                .displayAndCriterion(MekanismBlocks.BASIC_BIN, FrameType.TASK, false)
                .save(consumer);

        Advancement configurator = advancement(MekanismAdvancements.CONFIGURATOR)
                .parent(infused_alloy)
                .displayAndCriterion(MekanismItems.CONFIGURATOR, FrameType.TASK, true)
                .save(consumer);
        Advancement network_reader = advancement(MekanismAdvancements.NETWORK_READER)
                .parent(infused_alloy)
                .displayAndCriterion(MekanismItems.NETWORK_READER, FrameType.TASK, false)
                .save(consumer);
        Advancement fluid_tank = advancement(MekanismAdvancements.FLUID_TANK)
                .parent(infused_alloy)
                .displayAndCriterion(MekanismBlocks.BASIC_FLUID_TANK, FrameType.TASK, false)
                .save(consumer);
        Advancement chemical_tank = advancement(MekanismAdvancements.CHEMICAL_TANK)
                .parent(infused_alloy)
                .displayAndCriterion(MekanismBlocks.BASIC_CHEMICAL_TANK, FrameType.TASK, false)
                .save(consumer);

        Advancement breathing_assistance = advancement(MekanismAdvancements.BREATHING_ASSISTANCE)
                .parent(chemical_tank)
                .display(MekanismItems.SCUBA_MASK, FrameType.GOAL, true)
                .addCriterion("scuba_gear", hasAllItems(
                        MekanismItems.SCUBA_MASK,
                        MekanismItems.SCUBA_TANK
                )).save(consumer);
        Advancement hydrogen_powered_flight = advancement(MekanismAdvancements.HYDROGEN_POWERED_FLIGHT)
                .parent(chemical_tank)
                .displayAndCriterion(MekanismItems.JETPACK, FrameType.TASK, true)
                .save(consumer);

        Advancement waste_removal = advancement(MekanismAdvancements.WASTE_REMOVAL)
                .parent(chemical_tank)
                .displayAndCriterion(MekanismBlocks.RADIOACTIVE_WASTE_BARREL, FrameType.TASK, false)
                .save(consumer);
        Advancement environmental_radiation = advancement(MekanismAdvancements.ENVIRONMENTAL_RADIATION)
                .parent(waste_removal)
                .display(MekanismItems.GEIGER_COUNTER, FrameType.TASK, false)
                .addCriterion("use_geiger_counter", new UsingItemTrigger.TriggerInstance(ContextAwarePredicate.ANY, predicate(MekanismItems.GEIGER_COUNTER)))
                .save(consumer);
        Advancement personal_radiation = advancement(MekanismAdvancements.PERSONAL_RADIATION)
                .parent(environmental_radiation)
                .display(MekanismItems.DOSIMETER, FrameType.TASK, false)
                .addCriterion("use_dosimeter", new UsingItemTrigger.TriggerInstance(ContextAwarePredicate.ANY, predicate(MekanismItems.DOSIMETER)))
                .save(consumer);
        Advancement radiation_revention = advancement(MekanismAdvancements.RADIATION_PREVENTION)
                .parent(waste_removal)
                .display(MekanismItems.HAZMAT_GOWN, FrameType.TASK, true)
                .addCriterion("full_set", hasAllItems(
                        MekanismItems.HAZMAT_MASK,
                        MekanismItems.HAZMAT_GOWN,
                        MekanismItems.HAZMAT_PANTS,
                        MekanismItems.HAZMAT_BOOTS
                )).save(consumer);
        Advancement radiation_poisoning = advancement(MekanismAdvancements.RADIATION_POISONING)
                .parent(personal_radiation)
                .display(MekanismBlocks.RADIOACTIVE_WASTE_BARREL, FrameType.TASK, true)
                .addCriterion("poisoned", MekanismDamageTrigger.TriggerInstance.damaged(MekanismDamageTypes.RADIATION))
                .save(consumer);
        Advancement radiation_poisoning_death = advancement(MekanismAdvancements.RADIATION_POISONING_DEATH)
                .parent(radiation_poisoning)
                .display(Items.PLAYER_HEAD, null, FrameType.TASK, true, true, true)
                .addCriterion("death", MekanismDamageTrigger.TriggerInstance.killed(MekanismDamageTypes.RADIATION))
                .save(consumer);

        Advancement plutonium = advancement(MekanismAdvancements.PLUTONIUM)
                .parent(waste_removal)
                .displayAndCriterion(MekanismItems.PLUTONIUM_PELLET, FrameType.TASK, true)
                .save(consumer);
        //TODO: If we end up adding a criteria for creating a multiblock switch the criteria for this to using that
        Advancement sps = advancement(MekanismAdvancements.SPS)
                .parent(plutonium)
                .display(MekanismBlocks.SPS_CASING, FrameType.TASK, false)
                .andCriteria(MekanismBlocks.SPS_CASING,
                        MekanismBlocks.SPS_PORT
                ).save(consumer);
        Advancement antimatter = advancement(MekanismAdvancements.ANTIMATTER)
                .parent(sps)
                .displayAndCriterion(MekanismItems.ANTIMATTER_PELLET, FrameType.TASK, true)
                .save(consumer);
        Advancement nucleosynthesizer = advancement(MekanismAdvancements.NUCLEOSYNTHESIZER)
                .parent(antimatter)
                .displayAndCriterion(MekanismBlocks.ANTIPROTONIC_NUCLEOSYNTHESIZER, FrameType.CHALLENGE, true)
                .save(consumer);

        Advancement polonium = advancement(MekanismAdvancements.POLONIUM)
                .parent(waste_removal)
                .displayAndCriterion(MekanismItems.POLONIUM_PELLET, FrameType.TASK, true)
                .save(consumer);

        Advancement qio_drive_array = advancement(MekanismAdvancements.QIO_DRIVE_ARRAY)
                .parent(polonium)
                .displayAndCriterion(MekanismBlocks.QIO_DRIVE_ARRAY, FrameType.TASK, true)
                .save(consumer);
        Advancement qio_exporter = advancement(MekanismAdvancements.QIO_EXPORTER)
                .parent(qio_drive_array)
                .displayAndCriterion(MekanismBlocks.QIO_EXPORTER, FrameType.TASK, false)
                .save(consumer);
        Advancement qio_importer = advancement(MekanismAdvancements.QIO_IMPORTER)
                .parent(qio_drive_array)
                .displayAndCriterion(MekanismBlocks.QIO_IMPORTER, FrameType.TASK, false)
                .save(consumer);
        Advancement qio_redstone_adapter = advancement(MekanismAdvancements.QIO_REDSTONE_ADAPTER)
                .parent(qio_drive_array)
                .displayAndCriterion(MekanismBlocks.QIO_REDSTONE_ADAPTER, FrameType.TASK, false)
                .save(consumer);
        Advancement qui_dashboard = advancement(MekanismAdvancements.QIO_DASHBOARD)
                .parent(qio_drive_array)
                .displayAndCriterion(MekanismBlocks.QIO_DASHBOARD, FrameType.TASK, true)
                .save(consumer);
        Advancement portable_qui_dashboard = advancement(MekanismAdvancements.PORTABLE_QIO_DASHBOARD)
                .parent(qui_dashboard)
                .displayAndCriterion(MekanismItems.PORTABLE_QIO_DASHBOARD, FrameType.GOAL, true)
                .save(consumer);
        Advancement basic_qio_drive = advancement(MekanismAdvancements.BASIC_QIO_DRIVE)
                .parent(qio_drive_array)
                .displayAndCriterion(MekanismItems.BASE_QIO_DRIVE, FrameType.TASK, true)
                .save(consumer);
        Advancement advanced_qio_drive = advancement(MekanismAdvancements.ADVANCED_QIO_DRIVE)
                .parent(basic_qio_drive)
                .displayAndCriterion(MekanismItems.HYPER_DENSE_QIO_DRIVE, FrameType.TASK, false)
                .save(consumer);
        Advancement elite_qio_drive = advancement(MekanismAdvancements.ELITE_QIO_DRIVE)
                .parent(advanced_qio_drive)
                .displayAndCriterion(MekanismItems.TIME_DILATING_QIO_DRIVE, FrameType.TASK, false)
                .save(consumer);
        Advancement ultimate_qio_drive = advancement(MekanismAdvancements.ULTIMATE_QIO_DRIVE)
                .parent(elite_qio_drive)
                .displayAndCriterion(MekanismItems.SUPERMASSIVE_QIO_DRIVE, FrameType.CHALLENGE, true)
                .save(consumer);

        Advancement teleportation_core = advancement(MekanismAdvancements.TELEPORTATION_CORE)
                .parent(atomic_alloy)
                .displayAndCriterion(MekanismItems.TELEPORTATION_CORE, FrameType.TASK, false)
                .save(consumer);
        Advancement quantum_entangloporter = advancement(MekanismAdvancements.QUANTUM_ENTANGLOPORTER)
                .parent(teleportation_core)
                .displayAndCriterion(MekanismBlocks.QUANTUM_ENTANGLOPORTER, FrameType.TASK, true)
                .save(consumer);
        Advancement teleporter = advancement(MekanismAdvancements.TELEPORTER)
                .parent(teleportation_core)
                .displayAndCriterion(MekanismBlocks.TELEPORTER, FrameType.TASK, true)
                .addCriterion("teleport", new PlayerTrigger.TriggerInstance(MekanismCriteriaTriggers.TELEPORT.getId(), ContextAwarePredicate.ANY))
                .save(consumer);
        Advancement portable_teleporter = advancement(MekanismAdvancements.PORTABLE_TELEPORTER)
                .parent(teleporter)
                .displayAndCriterion(MekanismItems.PORTABLE_TELEPORTER, FrameType.TASK, true)
                .save(consumer);

        Advancement robit = advancement(MekanismAdvancements.ROBIT)
                .parent(atomic_alloy)
                .display(MekanismItems.ROBIT, FrameType.GOAL, true)
                .addCriterion("summon", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(MekanismEntityTypes.ROBIT.getEntityType())))
                .save(consumer);
        ItemStack skinnedRobit = MekanismItems.ROBIT.getItemStack();
        MekanismItems.ROBIT.get().setSkin(skinnedRobit, MekanismRobitSkins.PRIDE_SKINS.get(RobitPrideSkinData.TRANS));
        Advancement robit_aesthetics = advancement(MekanismAdvancements.ROBIT_AESTHETICS)
                .parent(robit)
                .display(skinnedRobit, null, FrameType.TASK, true, false, true)
                .addCriterion("change_skin", ChangeRobitSkinTrigger.TriggerInstance.toAny())
                .save(consumer);
        Advancement digital_miner = advancement(MekanismAdvancements.DIGITAL_MINER)
                .parent(robit)
                .displayAndCriterion(MekanismBlocks.DIGITAL_MINER, FrameType.GOAL, true)
                .save(consumer);
        Advancement dictionary = advancement(MekanismAdvancements.DICTIONARY)
                .parent(digital_miner)
                .displayAndCriterion(MekanismItems.DICTIONARY, FrameType.TASK, false)
                .save(consumer);
        Advancement stone_generator = advancement(MekanismAdvancements.STONE_GENERATOR)
                .parent(digital_miner)
                .displayAndCriterion(MekanismItems.STONE_GENERATOR_UPGRADE, FrameType.TASK, true)
                .save(consumer);

        Advancement disassembler = advancement(MekanismAdvancements.DISASSEMBLER)
                .parent(atomic_alloy)
                .displayAndCriterion(MekanismItems.ATOMIC_DISASSEMBLER, FrameType.TASK, true)
                .save(consumer);
        Advancement mekasuit = advancement(MekanismAdvancements.MEKASUIT)
                .parent(disassembler)
                .display(MekanismItems.MEKASUIT_BODYARMOR, FrameType.GOAL, true)
                .addCriterion("full_set", hasAllItems(
                        MekanismItems.MEKASUIT_HELMET,
                        MekanismItems.MEKASUIT_BODYARMOR,
                        MekanismItems.MEKASUIT_PANTS,
                        MekanismItems.MEKASUIT_BOOTS,
                        MekanismItems.MEKA_TOOL
                )).save(consumer);
        Advancement modification_station = advancement(MekanismAdvancements.MODIFICATION_STATION)
                .parent(disassembler)
                .displayAndCriterion(MekanismBlocks.MODIFICATION_STATION, FrameType.TASK, true)
                .save(consumer);
        //Require having all of them maxed at once
        Advancement upgraded_mekasuit = advancement(MekanismAdvancements.UPGRADED_MEKASUIT)
                .parent(modification_station)
                .display(MekanismItems.MEKASUIT_BODYARMOR, null, FrameType.CHALLENGE, true, true, true)
                .addCriterion("maxed_gear", hasItems(Stream.of(
                                        MekanismItems.MEKASUIT_HELMET,
                                        MekanismItems.MEKASUIT_BODYARMOR,
                                        MekanismItems.MEKASUIT_PANTS,
                                        MekanismItems.MEKASUIT_BOOTS,
                                        MekanismItems.MEKA_TOOL
                                ).map(item -> {
                                    ItemPredicate predicate = new ItemPredicate(null, Set.of(item.asItem()), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, null, NbtPredicate.ANY);
                                    ((ItemPredicateMekanismAddonAccessor) predicate).setMaxedModuleContainer(true);
                                    return predicate;
                                })
                                .toArray(ItemPredicate[]::new)
                )).save(consumer);

        Advancement fluid_transport = advancement(MekanismAdvancements.FLUID_TRANSPORT)
                .parent(steel_ingot)
                .displayAndCriterion(MekanismBlocks.BASIC_MECHANICAL_PIPE, FrameType.TASK, false)
                .save(consumer);
        Advancement chemical_transport = advancement(MekanismAdvancements.CHEMICAL_TRANSPORT)
                .parent(steel_ingot)
                .displayAndCriterion(MekanismBlocks.BASIC_PRESSURIZED_TUBE, FrameType.TASK, false)
                .save(consumer);
        Advancement energy_transport = advancement(MekanismAdvancements.ENERGY_TRANSPORT)
                .parent(steel_ingot)
                .displayAndCriterion(MekanismBlocks.BASIC_UNIVERSAL_CABLE, FrameType.TASK, false)
                .save(consumer);
        Advancement heat_transport = advancement(MekanismAdvancements.HEAT_TRANSPORT)
                .parent(steel_ingot)
                .displayAndCriterion(MekanismBlocks.BASIC_THERMODYNAMIC_CONDUCTOR, FrameType.TASK, false)
                .save(consumer);
        Advancement item_transport = advancement(MekanismAdvancements.ITEM_TRANSPORT)
                .parent(steel_ingot)
                .displayAndCriterion(MekanismBlocks.BASIC_LOGISTICAL_TRANSPORTER, FrameType.TASK, false)
                .save(consumer);
        Advancement restrictive_transport = advancement(MekanismAdvancements.RESTRICTIVE_ITEM_TRANSPORT)
                .parent(item_transport)
                .displayAndCriterion(MekanismBlocks.RESTRICTIVE_TRANSPORTER, FrameType.TASK, false)
                .save(consumer);
        Advancement diverstion_item_transport = advancement(MekanismAdvancements.DIVERSION_ITEM_TRANSPORT)
                .parent(item_transport)
                .displayAndCriterion(MekanismBlocks.DIVERSION_TRANSPORTER, FrameType.TASK, false)
                .save(consumer);
        Advancement sorter = advancement(MekanismAdvancements.SORTER)
                .parent(item_transport)
                .displayAndCriterion(MekanismBlocks.LOGISTICAL_SORTER, FrameType.GOAL, true)
                .save(consumer);

        Advancement energy_cube = advancement(MekanismAdvancements.ENERGY_CUBE)
                .parent(steel_casing)
                .displayAndCriterion(MekanismBlocks.BASIC_ENERGY_CUBE, FrameType.TASK, false)
                .save(consumer);

        Advancement automated_crafting = advancement(MekanismAdvancements.AUTOMATED_CRAFTING)
                .parent(steel_casing)
                .displayAndCriterion(MekanismBlocks.FORMULAIC_ASSEMBLICATOR, FrameType.TASK, false)
                .save(consumer);
        Advancement seismic_vibrator = advancement(MekanismAdvancements.SEISMIC_VIBRATIONS)
                .parent(steel_casing)
                .displayAndCriterion(MekanismBlocks.SEISMIC_VIBRATOR, FrameType.TASK, false)
                .addCriterion(MekanismItems.SEISMIC_READER)
                .addCriterion("view_vibrations", ViewVibrationsTrigger.TriggerInstance.view())
                .save(consumer);
        Advancement painting_machine = advancement(MekanismAdvancements.PAINTING_MACHINE)
                .parent(advanced_control_circuit)
                .displayAndCriterion(MekanismBlocks.PAINTING_MACHINE, FrameType.TASK, false)
                .save(consumer);

        Advancement enricher = advancement(MekanismAdvancements.ENRICHER)
                .parent(steel_casing)
                .displayAndCriterion(MekanismBlocks.ENRICHMENT_CHAMBER, FrameType.TASK, true)
                .save(consumer);
        Advancement infusing_efficiency = advancement(MekanismAdvancements.INFUSING_EFFICIENCY)
                .parent(enricher)
                .display(MekanismItems.ENRICHED_REDSTONE, FrameType.TASK, true)
                .addCriterion("enriched_material", hasItems(MekanismTags.Items.ENRICHED))
                .save(consumer);
        Advancement yellow_cake = advancement(MekanismAdvancements.YELLOW_CAKE)
                .parent(enricher)
                .displayAndCriterion(MekanismItems.YELLOW_CAKE_URANIUM, FrameType.GOAL, false)
                .save(consumer);

        Advancement purification_camber = advancement(MekanismAdvancements.PURIFICATION_CHAMBER)
                .parent(enricher)
                .displayAndCriterion(MekanismBlocks.PURIFICATION_CHAMBER, FrameType.GOAL, false)
                .save(consumer);
        Advancement injection_chamber = advancement(MekanismAdvancements.INJECTION_CHAMBER)
                .parent(purification_camber)
                .displayAndCriterion(MekanismBlocks.CHEMICAL_INJECTION_CHAMBER, FrameType.GOAL, false)
                .save(consumer);
        Advancement chemical_crystallizer = advancement(MekanismAdvancements.CHEMICAL_CRYSTALLIZER)
                .parent(injection_chamber)
                .displayAndCriterion(MekanismBlocks.CHEMICAL_CRYSTALLIZER, FrameType.CHALLENGE, true)
                .andCriteria(MekanismBlocks.CHEMICAL_WASHER, MekanismBlocks.CHEMICAL_DISSOLUTION_CHAMBER)
                .save(consumer);

        Advancement sawmill = advancement(MekanismAdvancements.SAWMILL)
                .parent(steel_casing)
                .displayAndCriterion(MekanismBlocks.PRECISION_SAWMILL, FrameType.TASK, false)
                .save(consumer);
        Advancement moving_blocks = advancement(MekanismAdvancements.MOVING_BLOCKS)
                .parent(sawmill)
                .displayAndCriterion(MekanismBlocks.CARDBOARD_BOX, FrameType.TASK, true)
                .addCriterion("unbox", UnboxCardboardBoxTrigger.TriggerInstance.unbox())
                .save(consumer);

        Advancement pump = advancement(MekanismAdvancements.PUMP)
                .parent(steel_casing)
                .displayAndCriterion(MekanismBlocks.ELECTRIC_PUMP, FrameType.TASK, false)
                .save(consumer);
        Advancement plenisher = advancement(MekanismAdvancements.PLENISHER)
                .parent(pump)
                .displayAndCriterion(MekanismBlocks.FLUIDIC_PLENISHER, FrameType.TASK, false)
                .save(consumer);

        Advancement liquifier = advancement(MekanismAdvancements.LIQUIFIER)
                .parent(steel_casing)
                .displayAndCriterion(MekanismBlocks.NUTRITIONAL_LIQUIFIER, FrameType.TASK, false)
                .save(consumer);

        ItemPredicate FULL_CANTEEN = new ItemPredicate();
        ((ItemPredicateMekanismAddonAccessor) FULL_CANTEEN).setFullCanteen(true);

        Advancement full_canteen = advancement(MekanismAdvancements.FULL_CANTEEN)
                .parent(liquifier)
                .display(MekanismItems.CANTEEN, null, FrameType.GOAL, true, true, true)
                .addCriterion("full_canteen", hasItems(FULL_CANTEEN))
                .save(consumer);
    }
}