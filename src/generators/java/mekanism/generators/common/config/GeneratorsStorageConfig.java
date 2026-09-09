package mekanism.generators.common.config;

import mekanism.api.math.FloatingLong;
import mekanism.common.config.BaseMekanismConfig;

public class GeneratorsStorageConfig extends BaseMekanismConfig {
    public FloatingLong heatGenerator = FloatingLong.createConst(160_000);
    public FloatingLong bioGenerator = FloatingLong.createConst(160_000);
    public FloatingLong solarGenerator = FloatingLong.createConst(96_000);
    public FloatingLong advancedSolarGenerator = FloatingLong.createConst(200_000);
    public FloatingLong windGenerator = FloatingLong.createConst(200_000);

    GeneratorsStorageConfig() {
//        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
//        builder.comment("Generator Energy Storage Config. This config is synced from server to client.").push("storage");
//
//        heatGenerator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "heatGenerator",
//              FloatingLong.createConst(160_000));
//        bioGenerator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "bioGenerator",
//              FloatingLong.createConst(160_000));
//        solarGenerator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "solarGenerator",
//              FloatingLong.createConst(96_000));
//        advancedSolarGenerator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "advancedSolarGenerator",
//              FloatingLong.createConst(200_000));
//        windGenerator = CachedFloatingLongValue.define(this, builder, "Base energy storage (Joules).", "windGenerator",
//              FloatingLong.createConst(200_000));
//
//        builder.pop();
//        configSpec = builder.build();
    }
}