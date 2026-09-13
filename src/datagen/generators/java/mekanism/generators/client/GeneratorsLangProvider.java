package mekanism.generators.client;

import mekanism.client.lang.BaseLanguageProvider;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.MekanismGenerators;
import mekanism.generators.common.advancements.GeneratorsAdvancements;
import mekanism.generators.common.registries.GeneratorsBlocks;
import mekanism.generators.common.registries.GeneratorsFluids;
import mekanism.generators.common.registries.GeneratorsGases;
import mekanism.generators.common.registries.GeneratorsItems;
import mekanism.generators.common.registries.GeneratorsModules;
import mekanism.generators.common.registries.GeneratorsSounds;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.PackOutput;

public class GeneratorsLangProvider extends BaseLanguageProvider {

    public GeneratorsLangProvider(FabricDataOutput output) {
        super(output, MekanismGenerators.instance);
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        addItems(builder);
        addBlocks(builder);
        addFluids(builder);
        addGases(builder);
        addSubtitles(builder);
        addAdvancements(builder);
        addMisc(builder);
    }

    private void addItems(TranslationBuilder builder) {
        add(builder, GeneratorsItems.SOLAR_PANEL, "Solar Panel");
        add(builder, GeneratorsItems.HOHLRAUM, "Hohlraum");
        add(builder, GeneratorsItems.TURBINE_BLADE, "Turbine Blade");
    }

    private void addBlocks(TranslationBuilder builder) {
        add(builder, GeneratorsBlocks.ADVANCED_SOLAR_GENERATOR, "Advanced Solar Generator");
        add(builder, GeneratorsBlocks.BIO_GENERATOR, "Bio-Generator");
        add(builder, GeneratorsBlocks.ELECTROMAGNETIC_COIL, "Electromagnetic Coil");
        add(builder, GeneratorsBlocks.GAS_BURNING_GENERATOR, "Gas-Burning Generator");
        add(builder, GeneratorsBlocks.HEAT_GENERATOR, "Heat Generator");
        add(builder, GeneratorsBlocks.SOLAR_GENERATOR, "Solar Generator");
        add(builder, GeneratorsBlocks.WIND_GENERATOR, "Wind Generator");
        add(builder, GeneratorsBlocks.REACTOR_GLASS, "Reactor Glass");
        add(builder, GeneratorsBlocks.FISSION_REACTOR_CASING, "Fission Reactor Casing");
        add(builder, GeneratorsBlocks.FISSION_REACTOR_PORT, "Fission Reactor Port");
        add(builder, GeneratorsBlocks.FISSION_REACTOR_LOGIC_ADAPTER, "Fission Reactor Logic Adapter");
        add(builder, GeneratorsBlocks.FISSION_FUEL_ASSEMBLY, "Fission Fuel Assembly");
        add(builder, GeneratorsBlocks.CONTROL_ROD_ASSEMBLY, "Control Rod Assembly");
        add(builder, GeneratorsBlocks.LASER_FOCUS_MATRIX, "Laser Focus Matrix");
        add(builder, GeneratorsBlocks.FUSION_REACTOR_CONTROLLER, "Fusion Reactor Controller");
        add(builder, GeneratorsBlocks.FUSION_REACTOR_FRAME, "Fusion Reactor Frame");
        add(builder, GeneratorsBlocks.FUSION_REACTOR_LOGIC_ADAPTER, "Fusion Reactor Logic Adapter");
        add(builder, GeneratorsBlocks.FUSION_REACTOR_PORT, "Fusion Reactor Port");
        add(builder, GeneratorsBlocks.ROTATIONAL_COMPLEX, "Rotational Complex");
        add(builder, GeneratorsBlocks.SATURATING_CONDENSER, "Saturating Condenser");
        add(builder, GeneratorsBlocks.TURBINE_CASING, "Turbine Casing");
        add(builder, GeneratorsBlocks.TURBINE_ROTOR, "Turbine Rotor");
        add(builder, GeneratorsBlocks.TURBINE_VALVE, "Turbine Valve");
        add(builder, GeneratorsBlocks.TURBINE_VENT, "Turbine Vent");
    }

    private void addFluids(TranslationBuilder builder) {
        addFluid(builder, GeneratorsFluids.BIOETHANOL, "Bioethanol");
        addFluid(builder, GeneratorsFluids.DEUTERIUM, "Liquid Deuterium");
        addFluid(builder, GeneratorsFluids.FUSION_FUEL, "Liquid D-T Fuel");
        addFluid(builder, GeneratorsFluids.TRITIUM, "Liquid Tritium");
    }

    private void addGases(TranslationBuilder builder) {
        add(builder, GeneratorsGases.DEUTERIUM, "Deuterium");
        add(builder, GeneratorsGases.FUSION_FUEL, "D-T Fuel");
        add(builder, GeneratorsGases.TRITIUM, "Tritium");
    }

    private void addSubtitles(TranslationBuilder builder) {
        add(builder, GeneratorsSounds.BIO_GENERATOR, "Bio generator hums");
        add(builder, GeneratorsSounds.GAS_BURNING_GENERATOR, "Gas generator hums");
        add(builder, GeneratorsSounds.FISSION_REACTOR, "Fission reactor hums");
        add(builder, GeneratorsSounds.FUSION_REACTOR, "Fusion reactor hums");
        add(builder, GeneratorsSounds.HEAT_GENERATOR, "Heat generator hums");
        add(builder, GeneratorsSounds.SOLAR_GENERATOR, "Solar generator hums");
        add(builder, GeneratorsSounds.WIND_GENERATOR, "Wind generator wooshes");
    }

    private void addAdvancements(TranslationBuilder builder) {
        add(builder, GeneratorsAdvancements.HEAT_GENERATOR, "Your First Generator", "Craft a Heat Generator to start making power");
        add(builder, GeneratorsAdvancements.SOLAR_GENERATOR, "Power of the Sun", "Renewable daytime energy");
        add(builder, GeneratorsAdvancements.WIND_GENERATOR, "Spin Baby Spin", "Hopefully there is some wind nearby");
        add(builder, GeneratorsAdvancements.BURN_THE_GAS, "Burn the Gas", "Craft a Gas-Burning Generator to burn Ethylene");
    }

    private void addMisc(TranslationBuilder builder) {
        addPackData(builder, GeneratorsLang.MEKANISM_GENERATORS, GeneratorsLang.PACK_DESCRIPTION);
        add(builder, GeneratorsLang.REACTOR_LOGIC_ACTIVE_COOLING, "Active cooling: %1$s");
        add(builder, GeneratorsLang.GAS_BURN_RATE, "Burn Rate: %1$s mB/t");
        add(builder, GeneratorsLang.STATS_TAB, "Stats");
        add(builder, GeneratorsLang.FUEL_TAB, "Fuel");
        add(builder, GeneratorsLang.HEAT_TAB, "Heat");
        add(builder, GeneratorsLang.INSUFFICIENT_FUEL, "Insufficient Fuel");
        add(builder, GeneratorsLang.IS_LIMITING, "(Limiting)");
        add(builder, GeneratorsLang.TURBINE_MAX_WATER_OUTPUT, "Max Water Output: %1$s mB/t");
        add(builder, GeneratorsLang.NO_WIND, "No wind");
        add(builder, GeneratorsLang.REACTOR_LOGIC_OUTPUTTING, "Outputting");
        add(builder, GeneratorsLang.REACTOR_LOGIC_ACTIVATION, "Activation");
        add(builder, GeneratorsLang.REACTOR_LOGIC_TEMPERATURE, "High Temperature");
        add(builder, GeneratorsLang.REACTOR_LOGIC_EXCESS_WASTE, "Excess Waste");
        add(builder, GeneratorsLang.REACTOR_LOGIC_DAMAGED, "Damage Critical");
        add(builder, GeneratorsLang.REACTOR_LOGIC_POWERED, "Powered");
        add(builder, GeneratorsLang.OUTPUT_RATE_SHORT, "Out: %1$s/t");
        add(builder, GeneratorsLang.POWER, "Power: %1$s");
        add(builder, GeneratorsLang.PRODUCING_AMOUNT, "Producing: %1$s/t");
        add(builder, GeneratorsLang.TURBINE_PRODUCTION, "Production");
        add(builder, GeneratorsLang.TURBINE_PRODUCTION_AMOUNT, "Production: %1$s");
        add(builder, GeneratorsLang.REACTOR_ACTIVE, "Water-Cooled");
        add(builder, GeneratorsLang.REACTOR_LOGIC_CAPACITY, "Heat Capacity Met");
        add(builder, GeneratorsLang.REACTOR_CASE, "Case: %1$s");
        add(builder, GeneratorsLang.REACTOR_LOGIC_DEPLETED, "Insufficient Fuel");
        add(builder, GeneratorsLang.REACTOR_LOGIC_DISABLED, "Disabled");
        add(builder, GeneratorsLang.REACTOR_EDIT_RATE, "Edit Rate:");
        add(builder, GeneratorsLang.REACTOR_IGNITION, "Ignition Temp: %1$s");
        add(builder, GeneratorsLang.REACTOR_INJECTION_RATE, "Injection Rate: %1$s");
        add(builder, GeneratorsLang.REACTOR_MAX_CASING, "Max. Casing Temp: %1$s");
        add(builder, GeneratorsLang.REACTOR_MAX_PLASMA, "Max. Plasma Temp: %1$s");
        add(builder, GeneratorsLang.REACTOR_MIN_INJECTION, "Min. Inject Rate: %1$s");
        add(builder, GeneratorsLang.FUSION_REACTOR, "Fusion Reactor");
        add(builder, GeneratorsLang.REACTOR_PASSIVE, "Air-Cooled");
        add(builder, GeneratorsLang.REACTOR_PASSIVE_RATE, "Passive Generation: %1$s/t");
        add(builder, GeneratorsLang.REACTOR_PLASMA, "Plasma: %1$s");
        add(builder, GeneratorsLang.REACTOR_PORT_EJECT, "Toggled Reactor Port eject mode to: %1$s.");
        add(builder, GeneratorsLang.REACTOR_LOGIC_READY, "Ready for Ignition");
        add(builder, GeneratorsLang.REACTOR_STEAM_PRODUCTION, "Steam Production: %1$s mB/t");
        add(builder, GeneratorsLang.READY_FOR_REACTION, "Ready for Reaction!");
        add(builder, GeneratorsLang.REACTOR_LOGIC_REDSTONE_MODE, "Redstone mode: %1$s");
        add(builder, GeneratorsLang.SKY_BLOCKED, "Sky blocked");
        add(builder, GeneratorsLang.TURBINE_STEAM_FLOW, "Steam Flow");
        add(builder, GeneratorsLang.TURBINE_STEAM_INPUT_RATE, "Steam Input: %1$s mB/t");
        add(builder, GeneratorsLang.STORED_BIO_FUEL, "BioFuel: %1$s");
        add(builder, GeneratorsLang.TURBINE_TANK_VOLUME, "Tank Volume: %1$s");
        add(builder, GeneratorsLang.REACTOR_LOGIC_TOGGLE_COOLING, "Toggle Cooling Measurements");

        //Industrial Turbine
        add(builder, GeneratorsLang.TURBINE_INVALID_BAD_COMPLEX, "Couldn't form, found improperly placed Rotational Complex at %1$s. Complex must be centered above Turbine Rotors.");
        add(builder, GeneratorsLang.TURBINE_INVALID_BAD_ROTOR, "Couldn't form, found invalid Turbine Rotor at %1$s. Turbine Rotors must be centered below Rotational Complex.");
        add(builder, GeneratorsLang.TURBINE_INVALID_BAD_ROTORS, "Couldn't form, invalid Turbine Rotor arrangement.");
        add(builder, GeneratorsLang.TURBINE_INVALID_CONDENSER_BELOW_COMPLEX, "Couldn't form, found improperly placed Saturating Condenser at %1$s. Saturating Condensers must be above Pressure Disperser layer.");
        add(builder, GeneratorsLang.TURBINE_INVALID_EVEN_LENGTH, "Couldn't form, width and length of structure must be odd.");
        add(builder, GeneratorsLang.TURBINE_INVALID_MALFORMED_COILS, "Couldn't form, Electromagnetic Coil arrangement is malformed. Coils must be connected to Rotational Complex and adjacently connected.");
        add(builder, GeneratorsLang.TURBINE_INVALID_MALFORMED_DISPERSERS, "Couldn't form, Pressure Disperser arrangement is malformed. Dispersers must create complete horizontal layer surrounding Rotational Complex.");
        add(builder, GeneratorsLang.TURBINE_INVALID_MISSING_COMPLEX, "Couldn't form, no Rotational Complex present.");
        add(builder, GeneratorsLang.TURBINE_INVALID_MISSING_DISPERSER, "Couldn't form, expected but didn't find Pressure Disperser at %1$s.");
        add(builder, GeneratorsLang.TURBINE_INVALID_ROTORS_NOT_CONTIGUOUS, "Couldn't form, rotors are invalid (non-contiguous).");
        add(builder, GeneratorsLang.TURBINE_INVALID_TOO_NARROW, "Couldn't form, structure is too narrow to support turbine size.");
        add(builder, GeneratorsLang.TURBINE_INVALID_VENT_BELOW_COMPLEX, "Couldn't form, found a Turbine Vent below Pressure Disperser layer. Vents must be at or above vertical position of disperser layer.");
        add(builder, GeneratorsLang.TURBINE_INVALID_MISSING_COILS, "Couldn't form, no Electromagnetic Coils present.");

        add(builder, GeneratorsLang.TURBINE, "Industrial Turbine");
        add(builder, GeneratorsLang.TURBINE_BLADES, "Blades: %1$s %2$s");
        add(builder, GeneratorsLang.TURBINE_CAPACITY, "Capacity: %1$s mB");
        add(builder, GeneratorsLang.TURBINE_COILS, "Coils: %1$s %2$s");
        add(builder, GeneratorsLang.TURBINE_DISPERSERS, "Dispersers: %1$s %2$s");
        add(builder, GeneratorsLang.TURBINE_FLOW_RATE, "Flow rate: %1$s mB/t");
        add(builder, GeneratorsLang.TURBINE_MAX_FLOW, "Max flow: %1$s mB/t");
        add(builder, GeneratorsLang.TURBINE_MAX_PRODUCTION, "Max Production: %1$s");
        add(builder, GeneratorsLang.TURBINE_STATS, "Turbine Statistics");
        add(builder, GeneratorsLang.TURBINE_VENTS, "Vents: %1$s %2$s");
        add(builder, GeneratorsLang.TURBINE_DUMPING_STEAM, "Dumping Steam");
        add(builder, GeneratorsLang.TURBINE_DUMPING_EXCESS_STEAM, "Dumping excess Steam");
        add(builder, GeneratorsLang.TURBINE_DUMPING_STEAM_WARNING, "Water will NOT be recycled");
        //Fission Reactor
        add(builder, GeneratorsLang.FISSION_INVALID_BAD_CONTROL_ROD, "Couldn't form, improper placement for Control Rod Assembly at %1$s.");
        add(builder, GeneratorsLang.FISSION_INVALID_MISSING_CONTROL_ROD, "Couldn't form, missing control rod for fuel assembly at %1$s.");
        add(builder, GeneratorsLang.FISSION_INVALID_BAD_FUEL_ASSEMBLY, "Couldn't form, missing fuel assembly for control rod at %1$s.");
        add(builder, GeneratorsLang.FISSION_INVALID_EXTRA_CONTROL_ROD, "Couldn't form, found extra Control Rod Assembly at %1$s.");
        add(builder, GeneratorsLang.FISSION_INVALID_MALFORMED_FUEL_ASSEMBLY, "Couldn't form, invalid Fission Fuel Assembly placement at %1$s.");
        add(builder, GeneratorsLang.FISSION_INVALID_MISSING_FUEL_ASSEMBLY, "Couldn't form, no fuel assembly structures present.");

        add(builder, GeneratorsLang.FISSION_REACTOR, "Fission Reactor");
        add(builder, GeneratorsLang.FISSION_REACTOR_STATS, "Fission Reactor Statistics");
        add(builder, GeneratorsLang.FISSION_ACTIVATE, "Activate");
        add(builder, GeneratorsLang.FISSION_SCRAM, "SCRAM");
        add(builder, GeneratorsLang.FISSION_DAMAGE, "Damage: %1$s");
        add(builder, GeneratorsLang.FISSION_HEAT_STATISTICS, "Heat Statistics");
        add(builder, GeneratorsLang.FISSION_FORCE_DISABLED, "Reactor must reach safe damage and temperature levels before it can be reactivated.");
        add(builder, GeneratorsLang.FISSION_FUEL_STATISTICS, "Fuel Statistics");
        add(builder, GeneratorsLang.FISSION_HEAT_CAPACITY, "Heat Capacity: %1$s J/K");
        add(builder, GeneratorsLang.FISSION_SURFACE_AREA, "Fuel Surface Area: %1$s m2");
        add(builder, GeneratorsLang.FISSION_BOIL_EFFICIENCY, "Boil Efficiency: %1$s");
        add(builder, GeneratorsLang.FISSION_MAX_BURN_RATE, "Max Burn Rate: %1$s mB/t");
        add(builder, GeneratorsLang.FISSION_RATE_LIMIT, "Rate Limit: %1$s mB/t");
        add(builder, GeneratorsLang.FISSION_CURRENT_BURN_RATE, "Current Burn Rate:");
        add(builder, GeneratorsLang.FISSION_HEATING_RATE, "Heating Rate: %1$s mB/t");
        add(builder, GeneratorsLang.FISSION_SET_RATE_LIMIT, "Set Rate Limit:");
        add(builder, GeneratorsLang.FISSION_COOLANT_TANK, "Coolant Tank");
        add(builder, GeneratorsLang.FISSION_FUEL_TANK, "Fuel Tank");
        add(builder, GeneratorsLang.FISSION_HEATED_COOLANT_TANK, "Heated Coolant Tank");
        add(builder, GeneratorsLang.FISSION_WASTE_TANK, "Waste Tank");
        add(builder, GeneratorsLang.FISSION_HEAT_GRAPH, "Heat Graph:");
        add(builder, GeneratorsLang.FISSION_PORT_MODE_CHANGE, "Port mode changed to: %1$s");
        add(builder, GeneratorsLang.FISSION_PORT_MODE_INPUT, "input only");
        add(builder, GeneratorsLang.FISSION_PORT_MODE_OUTPUT_WASTE, "output waste");
        add(builder, GeneratorsLang.FISSION_PORT_MODE_OUTPUT_COOLANT, "output coolant");
        //Descriptions
        add(builder, GeneratorsLang.DESCRIPTION_REACTOR_CAPACITY, "Output when the reactor's core heat capacity has been met");
        add(builder, GeneratorsLang.DESCRIPTION_REACTOR_ACTIVATION, "Activate the reactor when powered, and deactivate when unpowered");
        add(builder, GeneratorsLang.DESCRIPTION_REACTOR_TEMPERATURE, "Output when the reactor reaches dangerous temperatures");
        add(builder, GeneratorsLang.DESCRIPTION_REACTOR_DAMAGED, "Output when the reactor reaches critical damage levels (100%+).");
        add(builder, GeneratorsLang.DESCRIPTION_REACTOR_EXCESS_WASTE, "Output when the reactor has excess waste");
        add(builder, GeneratorsLang.DESCRIPTION_REACTOR_DEPLETED, "Output when the reactor has insufficient fuel to sustain a reaction");
        add(builder, GeneratorsLang.DESCRIPTION_REACTOR_DISABLED, "Will not handle redstone");
        add(builder, GeneratorsLang.DESCRIPTION_REACTOR_READY, "Output when the reactor has reached the required heat level to ignite");
        //Generators
        add(builder, GeneratorsLang.DESCRIPTION_ADVANCED_SOLAR_GENERATOR, "An advanced generator that directly absorbs the sun's rays with little loss to produce energy.");
        add(builder, GeneratorsLang.DESCRIPTION_BIO_GENERATOR, "A generator that burns organic materials of the world to produce energy.");
        add(builder, GeneratorsLang.DESCRIPTION_ELECTROMAGNETIC_COIL, "A block that converts kinetic energy from a Rotational Complex into usable electricity. These can be placed in any arrangement above your Rotational Complex, as long as they are all touching each other and the complex itself.");
        add(builder, GeneratorsLang.DESCRIPTION_GAS_BURNING_GENERATOR, "A generator that harnesses the varying molecular gases to produce energy.");
        add(builder, GeneratorsLang.DESCRIPTION_HEAT_GENERATOR, "A generator that uses the heat of lava or other burnable resources to produce energy.");
        add(builder, GeneratorsLang.DESCRIPTION_SOLAR_GENERATOR, "A generator that uses the power of the sun to produce energy.");
        add(builder, GeneratorsLang.DESCRIPTION_WIND_GENERATOR, "A generator that uses the strength of the wind to produce energy, with greater efficiency at higher levels.");
        //Fission Reactor
        add(builder, GeneratorsLang.DESCRIPTION_FISSION_REACTOR_CASING, "Lead-infused steel casing used to create a Fission Reactor. Mostly heat-resistant, mostly radiation-resistant, and mostly safe!");
        add(builder, GeneratorsLang.DESCRIPTION_FISSION_REACTOR_PORT, "A port which can be placed on a Fission Reactor multiblock to transfer coolant, fuel, and waste.");
        add(builder, GeneratorsLang.DESCRIPTION_FISSION_REACTOR_LOGIC_ADAPTER, "A block that can be used to monitor or control the Fission Reactor with redstone.");
        add(builder, GeneratorsLang.DESCRIPTION_FISSION_FUEL_ASSEMBLY, "A cluster of fuel rods used to house fission fuel within a Fission Reactor. These can be stacked on top of each other.");
        add(builder, GeneratorsLang.DESCRIPTION_CONTROL_ROD_ASSEMBLY, "A collection of control rods used to halt a fission chain reaction. Placed on top of a tower of Fission Fuel Assemblies.");
        //Fusion Reactor
        add(builder, GeneratorsLang.DESCRIPTION_LASER_FOCUS_MATRIX, "A panel of Fusion Reactor Glass that is capable of absorbing optical energy and thereby heating up the Fusion Reactor.");
        add(builder, GeneratorsLang.DESCRIPTION_FUSION_REACTOR_CONTROLLER, "The controlling block for the entire Fusion Reactor structure.");
        add(builder, GeneratorsLang.DESCRIPTION_FUSION_REACTOR_FRAME, "Reinforced framing that can be used in the Fusion Reactor multiblock.");
        add(builder, GeneratorsLang.DESCRIPTION_REACTOR_GLASS, "Reinforced glass that can be used in the Fission Reactor and Fusion Reactor multiblocks (as well as any others!).");
        add(builder, GeneratorsLang.DESCRIPTION_FUSION_REACTOR_LOGIC_ADAPTER, "A block that can be used to allow basic monitoring of a reactor using redstone.");
        add(builder, GeneratorsLang.DESCRIPTION_FUSION_REACTOR_PORT, "A block of reinforced framing that is capable of managing both the gas and energy transfer of the Fusion Reactor.");
        //Turbine
        add(builder, GeneratorsLang.DESCRIPTION_ROTATIONAL_COMPLEX, "A connector that is placed on the highest Turbine Rotor of an Industrial Turbine to carry kinetic energy into its Electromagnetic Coils.");
        add(builder, GeneratorsLang.DESCRIPTION_SATURATING_CONDENSER, "A block that condenses steam processed by an Industrial Turbine into reusable water. These can be placed in any arrangement above your rotational complex.");
        add(builder, GeneratorsLang.DESCRIPTION_TURBINE_CASING, "Pressure-resistant casing used in the creation of an Industrial Turbine.");
        add(builder, GeneratorsLang.DESCRIPTION_TURBINE_ROTOR, "The steel rod that is used to house Turbine Blades within an Industrial Turbine.");
        add(builder, GeneratorsLang.DESCRIPTION_TURBINE_VALVE, "A type of Turbine Casing that includes a port for the transfer of energy and steam.");
        add(builder, GeneratorsLang.DESCRIPTION_TURBINE_VENT, "A type of Turbine Casing with an integrated vent for the release of steam. These should be placed on the level of or above the turbine's Rotational Complex.");

        //Modules
        add(builder, GeneratorsModules.GEOTHERMAL_GENERATOR_UNIT, "Geothermal Generator Unit", "Harnesses geothermal energy from the surrounding environment, and improves protection against damage from heat sources. Install multiple for faster charging and greater protection.");
        add(builder, GeneratorsModules.SOLAR_RECHARGING_UNIT, "Solar Recharging Unit", "Harnesses the power of the sun to charge your MekaSuit. Install multiple for faster charging.");
    }
}