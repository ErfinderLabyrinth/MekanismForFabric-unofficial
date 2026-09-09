package mekanism.generators.common.config;

import me.shedaniel.autoconfig.annotation.Config;
import mekanism.api.math.FloatingLong;
import mekanism.common.config.BaseMekanismConfig;

@Config(name = "mekanism/generators-gear")

public class GeneratorsGearConfig extends BaseMekanismConfig {

    private static final String MEKASUIT_CATEGORY = "mekasuit";
    private static final String MEKASUIT_DAMAGE_CATEGORY = "damage_absorption";
    //MekaSuit
    public double mekaSuitGeothermalChargingRate = 10.5;
    public float mekaSuitHeatDamageReductionRatio = 0.8F;

    GeneratorsGearConfig() {
//        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
//        builder.comment("Mekanism Generators Gear Config. This config is synced from server to client.").push("generators-gear");
//
//        builder.comment("MekaSuit Settings").push(MEKASUIT_CATEGORY);
//        mekaSuitGeothermalChargingRate = CachedFloatingLongValue.define(this, builder, "Geothermal charging rate (Joules) of pants per tick, per degree above ambient, per upgrade installed. This value scales down based on how much of the MekaSuit Pants is submerged. Fire is treated as having a temperature of ~200K above ambient, lava has a temperature of 1,000K above ambient.",
//              "geothermalChargingRate", FloatingLong.createConst(10.5));
//        builder.push(MEKASUIT_DAMAGE_CATEGORY);
//        mekaSuitHeatDamageReductionRatio = CachedFloatValue.wrap(this, builder.comment("Percent of heat damage negated by MekaSuit Pants with maximum geothermal generator units installed. This number scales down linearly based on how many units are actually installed.")
//              .defineInRange("heatDamageReductionRatio", 0.8, 0, 1));
//        builder.pop(2);
//
//        builder.pop();
//        configSpec = builder.build();
    }
}