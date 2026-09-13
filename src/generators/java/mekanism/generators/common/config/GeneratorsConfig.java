package mekanism.generators.common.config;

import me.shedaniel.autoconfig.annotation.Config;
import mekanism.api.math.FloatingLong;
import mekanism.common.config.BaseMekanismConfig;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.ArrayList;
import java.util.List;

@Config(name = "mekanism/generators-generator")

public class GeneratorsConfig extends BaseMekanismConfig {

    private static final String TURBINE_CATEGORY = "turbine";
    private static final String WIND_CATEGORY = "wind_generator";
    private static final String BIO_CATEGORY = "bio_generator";
    private static final String HEAT_CATEGORY = "heat_generator";
    private static final String GAS_CATEGORY = "gas_generator";
    private static final String HOHLRAUM_CATEGORY = "hohlraum";
    private static final String FUSION_CATEGORY = "fusion_reactor";
    private static final String FISSION_CATEGORY = "fission_reactor";

    private static final long BASE_MAX_WATER = 1_000 * FluidConstants.BUCKET;

    public long advancedSolarGeneration = 300;

    public long bioGeneration = 350;
    public long bioTankCapacity = 24 * FluidConstants.BUCKET;

    public double heatGeneration = 200;
    public double heatGenerationLava = 30;
    public double heatGenerationNether = 100;
    public long heatTankCapacity = 24 * FluidConstants.BUCKET;
    public int heatGenerationFluidRate = 10;

    public long gbgTankCapacity = 18L * FluidConstants.BUCKET;
    public int ethyleneBurnTicks = 40;
    public long ethyleneDensityMultiplier = 40;

    public long solarGeneration = 50;
    public int turbineBladesPerCoil = 4;
    public double turbineVentGasFlow = 32_000D;
    public double turbineDisperserGasFlow = 1_280D;
    public long turbineEnergyCapacityPerVolume = 16_000_000L;
    public long turbineGasPerTank = 64L * FluidConstants.BUCKET;
    public int condenserRate = 64_000;

    public double energyPerFusionFuel = 10_000_000;
    public long windGenerationMin = 60;
    public long windGenerationMax = 480;
    public int windGenerationMinY = 24;
    public int windGenerationMaxY = DimensionType.MAX_Y;
    public List<ResourceLocation> windGenerationDimBlacklist = new ArrayList<>();

    public double energyPerFissionFuel = 1_000_000d;
    public double fissionCasingHeatCapacity = 1_000D;
    public double fissionSurfaceAreaTarget = 4D;
    public boolean fissionMeltdownsEnabled = true;
    public float fissionMeltdownRadius = 8F;
    public double fissionMeltdownChance = 0.001D;
    public double fissionMeltdownRadiationMultiplier = 50;
    public double fissionPostMeltdownDamage = 0.75 * FissionReactorMultiblockData.MAX_DAMAGE;
    public double defaultBurnRate = 0.1D;
    public long burnPerAssembly = 1L;
    public long maxFuelPerAssembly = 8L * FluidConstants.BUCKET;
    public long fissionCooledCoolantPerTank = 100 * FluidConstants.BUCKET;
    public long fissionHeatedCoolantPerTank = 1_000L * FluidConstants.BUCKET;

    public long hohlraumMaxGas = 10;
    public long hohlraumFillRate = 1;

    public double fusionThermocoupleEfficiency = 0.05D;
    public double fusionCasingThermalConductivity = 0.1D;
    public double fusionWaterHeatingRatio = 0.3D;
    public long fusionFuelCapacity = FluidConstants.BUCKET;
    public long fusionEnergyCapacity = 1_000_000_000;
    public long fusionWaterPerInjection = 1_000 * FluidConstants.BUCKET;
    public long fusionSteamPerInjection = 100L * BASE_MAX_WATER;

    GeneratorsConfig() {
//        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
//        builder.comment("Mekanism Generators Config. This config is synced between server and client.").push("generators");
//
//        energyPerFusionFuel = CachedFloatingLongValue.define(this, builder, "Affects the Injection Rate, Max Temp, and Ignition Temp.",
//              "energyPerFusionFuel", FloatingLong.createConst(10_000_000));
//        solarGeneration = CachedFloatingLongValue.define(this, builder, "Peak output for the Solar Generator. Note: It can go higher than this value in some extreme environments.",
//              "solarGeneration", FloatingLong.createConst(50));
//        advancedSolarGeneration = CachedFloatingLongValue.define(this, builder, "Peak output for the Advanced Solar Generator. Note: It can go higher than this value in some extreme environments.",
//              "advancedSolarGeneration", FloatingLong.createConst(300));
//
//        builder.comment("Bio Generator Settings").push(BIO_CATEGORY);
//        bioGeneration = CachedFloatingLongValue.define(this, builder, "Amount of energy in Joules the Bio Generator produces per tick.",
//              "bioGeneration", FloatingLong.createConst(350));
//        bioTankCapacity = CachedIntValue.wrap(this, builder.comment("The capacity in mB of the fluid tank in the Bio Generator.")
//              .defineInRange("tankCapacity", 24 * FluidType.BUCKET_VOLUME, 1, Integer.MAX_VALUE));
//        builder.pop();
//
//        builder.comment("Heat Generator Settings").push(HEAT_CATEGORY);
//        heatGeneration = CachedFloatingLongValue.define(this, builder, "Amount of energy in Joules the Heat Generator produces per tick. heatGeneration + heatGenerationLava * lavaSides + heatGenerationNether. Note: lavaSides is how many sides are adjacent to lava, this includes the block itself if it is lava logged allowing for a max of 7 \"sides\".",
//              "heatGeneration", FloatingLong.createConst(200));
//        heatGenerationLava = CachedFloatingLongValue.define(this, builder, "Multiplier of effectiveness of Lava that is adjacent to the Heat Generator.",
//              "heatGenerationLava", FloatingLong.createConst(30));
//        heatGenerationNether = CachedFloatingLongValue.define(this, builder, "Add this amount of Joules to the energy produced by a heat generator if it is in an 'ultrawarm' dimension, in vanilla this is just the Nether.",
//              "heatGenerationNether", FloatingLong.createConst(100));
//        heatTankCapacity = CachedIntValue.wrap(this, builder.comment("The capacity in mB of the fluid tank in the Heat Generator.")
//              .defineInRange("tankCapacity", 24 * FluidType.BUCKET_VOLUME, 1, Integer.MAX_VALUE));
//        heatGenerationFluidRate = CachedIntValue.wrap(this, builder.comment("The amount of lava in mB that gets consumed to transfer heatGeneration Joules to the Heat Generator.")
//              .define("heatGenerationFluidRate", 10, value -> value instanceof Integer i && i > 0 && i <= heatTankCapacity.get()));
//        builder.pop();
//
//        builder.comment("Gas-Burning Generator Settings").push(GAS_CATEGORY);
//        gbgTankCapacity = CachedLongValue.wrap(this, builder.comment("The capacity in mB of the gas tank in the Gas-Burning Generator.")
//              .defineInRange("tankCapacity", 18L * FluidType.BUCKET_VOLUME, 1, Long.MAX_VALUE));
//        ethyleneBurnTicks = CachedIntValue.wrap(this, builder.comment("The number of ticks each mB of Ethylene burns for in the Gas-Burning Generator.")
//              .defineInRange("ethyleneBurnTicks", 40, 1, Integer.MAX_VALUE));
//        ethyleneDensityMultiplier = CachedFloatingLongValue.define(this, builder, "Multiplier for calculating the energy density of Ethylene (1 mB Hydrogen + 2 * bioGeneration * densityMultiplier).",
//              "ethyleneDensityMultiplier", FloatingLong.createConst(40), CachedFloatingLongValue.POSITIVE);
//        builder.pop();
//
//        builder.comment("Turbine Settings").push(TURBINE_CATEGORY);
//        turbineBladesPerCoil = CachedIntValue.wrap(this, builder.comment("The number of blades on each turbine coil per blade applied.")
//              .defineInRange("turbineBladesPerCoil", 4, 1, 12));
//        turbineVentGasFlow = CachedDoubleValue.wrap(this, builder.comment("The rate at which steam is vented into the turbine.")
//              .defineInRange("turbineVentGasFlow", 32_000D, 0.1, 1_024_000));
//        turbineDisperserGasFlow = CachedDoubleValue.wrap(this, builder.comment("The rate at which steam is dispersed into the turbine.")
//              .defineInRange("turbineDisperserGasFlow", 1_280D, 0.1, 1_024_000));
//        turbineEnergyCapacityPerVolume = CachedFloatingLongValue.define(this, builder, "Amount of energy (J) that each block of the turbine contributes to the total energy capacity. Max = volume * energyCapacityPerVolume",
//              "energyCapacityPerVolume", FloatingLong.createConst(16_000_000L), CachedFloatingLongValue.greaterZeroLessThan(FloatingLong.createConst(1_000_000_000_000L)));
//        //Note: We use maxVolume as it still is a large number, and we have no reason to go higher even if some things we technically could
//        int maxTurbine = 17 * 17 * 18;
//        turbineGasPerTank = CachedLongValue.wrap(this, builder.comment("Amount of gas (mB) that each block of the turbine's steam cavity contributes to the volume. Max = volume * gasPerTank")
//              .defineInRange("gasPerTank", 64L * FluidType.BUCKET_VOLUME, 1, Long.MAX_VALUE / maxTurbine));
//        condenserRate = CachedIntValue.wrap(this, builder.comment("The rate at which steam is condensed in the turbine.")
//              .defineInRange("condenserRate", 64_000, 1, 2_000_000));
//        builder.pop();
//
//        builder.comment("Wind Generator Settings").push(WIND_CATEGORY);
//        windGenerationMin = CachedFloatingLongValue.define(this, builder, "Minimum base generation value of the Wind Generator.",
//              "windGenerationMin", FloatingLong.createConst(60));
//        //TODO: Should this be capped by the min generator?
//        windGenerationMax = CachedFloatingLongValue.define(this, builder, "Maximum base generation value of the Wind Generator.",
//              "generationMax", FloatingLong.createConst(480));
//        windGenerationMinY = CachedIntValue.wrap(this, builder.comment("The minimum Y value that affects the Wind Generators Power generation. This value gets clamped at the world's min height.")
//              .defineInRange("minY", 24, DimensionType.MIN_Y, DimensionType.MAX_Y - 1));
//        //Note: We just require that the maxY is greater than the minY, nothing goes badly if it is set above the max y of the world though
//        // as it is just used for range clamping
//        windGenerationMaxY = CachedIntValue.wrap(this, builder.comment("The maximum Y value that affects the Wind Generators Power generation. This value gets clamped at the world's logical height.")
//              .define("maxY", DimensionType.MAX_Y, value -> value instanceof Integer && (Integer) value > windGenerationMinY.get()));
//        //Note: We cannot verify the dimension exists as dimensions are dynamic so may not actually exist when we are validating
//        windGenerationDimBlacklist = CachedResourceLocationListValue.define(this, builder.comment("The list of dimension ids that the Wind Generator will not generate power in."),
//              "windGenerationDimBlacklist", ConstantPredicates.alwaysTrue());
//        builder.pop();
//
//        builder.comment("Fusion Settings").push(FUSION_CATEGORY);
//        fusionThermocoupleEfficiency = CachedDoubleValue.wrap(this, builder.comment("The fraction of the heat dissipated from the case that is converted to Joules.")
//              .defineInRange("thermocoupleEfficiency", 0.05D, 0D, 1D));
//        fusionCasingThermalConductivity = CachedDoubleValue.wrap(this, builder.comment("The fraction fraction of heat from the casing that can be transferred to all sources that are not water. Will impact max heat, heat transfer to thermodynamic conductors, and power generation.")
//              .defineInRange("casingThermalConductivity", 0.1D, 0.001D, 1D));
//        fusionWaterHeatingRatio = CachedDoubleValue.wrap(this, builder.comment("The fraction of the heat from the casing that is dissipated to water when water cooling is in use. Will impact max heat, and steam generation.")
//              .defineInRange("waterHeatingRatio", 0.3D, 0D, 1D));
//        fusionFuelCapacity = CachedLongValue.wrap(this, builder.comment("Amount of fuel (mB) that the fusion reactor can store.")
//              .defineInRange("fuelCapacity", (long) FluidType.BUCKET_VOLUME, 2, 1_000L * FluidType.BUCKET_VOLUME));
//        fusionEnergyCapacity = CachedFloatingLongValue.define(this, builder, "Amount of energy (J) the fusion reactor can store.",
//              "energyCapacity", FloatingLong.createConst(1_000_000_000), CachedFloatingLongValue.POSITIVE);
//        int baseMaxWater = 1_000 * FluidType.BUCKET_VOLUME;
//        fusionWaterPerInjection = CachedIntValue.wrap(this, builder.comment("Amount of water (mB) per injection rate that the fusion reactor can store. Max = injectionRate * waterPerInjection")
//              .defineInRange("waterPerInjection", 1_000 * FluidType.BUCKET_VOLUME, 1, Integer.MAX_VALUE / FusionReactorMultiblockData.MAX_INJECTION));
//        fusionSteamPerInjection = CachedLongValue.wrap(this, builder.comment("Amount of steam (mB) per injection rate that the fusion reactor can store. Max = injectionRate * steamPerInjection")
//              .defineInRange("steamPerInjection", 100L * baseMaxWater, 1, Long.MAX_VALUE / FusionReactorMultiblockData.MAX_INJECTION));
//        builder.pop();
//
//        builder.comment("Hohlraum Settings").push(HOHLRAUM_CATEGORY);
//        hohlraumMaxGas = CachedLongValue.wrap(this, builder.comment("Hohlraum capacity in mB.")
//              .defineInRange("maxGas", 10, 1, Long.MAX_VALUE));
//        hohlraumFillRate = CachedLongValue.wrap(this, builder.comment("Amount of DT-Fuel Hohlraum can accept per tick.")
//              .defineInRange("fillRate", 1, 1, Long.MAX_VALUE));
//        builder.pop();
//
//        builder.comment("Fission Reactor Settings").push(FISSION_CATEGORY);
//        energyPerFissionFuel = CachedFloatingLongValue.define(this, builder, "Amount of energy created (in heat) from each whole mB of fission fuel.",
//              "energyPerFissionFuel", FloatingLong.createConst(1_000_000));
//        fissionCasingHeatCapacity = CachedDoubleValue.wrap(this, builder.comment("The heat capacity added to a Fission Reactor by a single casing block. Increase to require more energy to raise the reactor temperature.")
//              .defineInRange("casingHeatCapacity", 1_000D, 1, 1_000_000));
//        fissionSurfaceAreaTarget = CachedDoubleValue.wrap(this, builder.comment("The average surface area of a Fission Reactor's fuel assemblies to reach 100% boil efficiency. Higher values make it harder to cool the reactor.")
//              .defineInRange("surfaceAreaTarget", 4D, 1D, Double.MAX_VALUE));
//        fissionMeltdownsEnabled = CachedBooleanValue.wrap(this, builder.comment("Whether catastrophic meltdowns can occur from Fission Reactors. If disabled instead of melting down the reactor will turn off and not be able to be turned back on until the damage level decreases.")
//              .define("meltdownsEnabled", true));
//        fissionMeltdownRadius = CachedFloatValue.wrap(this, builder.comment("The radius of the explosion that occurs from a meltdown.")
//              .defineInRange("meltdownRadius", 8D, 1, 500));
//        fissionMeltdownChance = CachedDoubleValue.wrap(this, builder.comment("The chance of a meltdown occurring once damage passes 100%. Will linearly scale as damage continues increasing.")
//              .defineInRange("meltdownChance", 0.001D, 0D, 1D));
//        fissionMeltdownRadiationMultiplier = CachedDoubleValue.wrap(this, builder.comment("How much radioactivity of fuel/waste contents are multiplied during a meltdown.")
//              .defineInRange("meltdownRadiationMultiplier", 50, 0.1, 1_000_000));
//        fissionPostMeltdownDamage = CachedDoubleValue.wrap(this, builder.comment("Damage to reset the reactor to after a meltdown.")
//              .defineInRange("postMeltdownDamage", 0.75 * FissionReactorMultiblockData.MAX_DAMAGE, 0, FissionReactorMultiblockData.MAX_DAMAGE));
//        defaultBurnRate = CachedDoubleValue.wrap(this, builder.comment("The default burn rate of the fission reactor.")
//              .defineInRange("defaultBurnRate", 0.1D, 0.001D, 1D));
//        burnPerAssembly = CachedLongValue.wrap(this, builder.comment("The burn rate increase each fuel assembly provides. Max Burn Rate = fuelAssemblies * burnPerAssembly")
//              .defineInRange("burnPerAssembly", 1L, 1, 1_000_000));
//        maxFuelPerAssembly = CachedLongValue.wrap(this, builder.comment("Amount of fuel (mB) that each assembly contributes to the fuel and waste capacity. Max = fuelAssemblies * maxFuelPerAssembly")
//              .defineInRange("maxFuelPerAssembly", 8L * FluidType.BUCKET_VOLUME, 1, Long.MAX_VALUE / 4_096));
//        int maxVolume = 18 * 18 * 18;
//        fissionCooledCoolantPerTank = CachedIntValue.wrap(this, builder.comment("Amount of cooled coolant (mB) that each block of the fission reactor contributes to the volume. Max = volume * cooledCoolantPerTank")
//              .defineInRange("cooledCoolantPerTank", 100 * FluidType.BUCKET_VOLUME, 1, Integer.MAX_VALUE / maxVolume));
//        fissionHeatedCoolantPerTank = CachedLongValue.wrap(this, builder.comment("Amount of heated coolant (mB) that each block of the fission reactor contributes to the volume. Max = volume * heatedCoolantPerTank")
//              .defineInRange("heatedCoolantPerTank", 1_000L * FluidType.BUCKET_VOLUME, 1_000, Long.MAX_VALUE / maxVolume));
//        builder.pop();
//
//        builder.pop();
//        configSpec = builder.build();
    }
}
