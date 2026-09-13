package mekanism.generators.common.config;

import me.shedaniel.autoconfig.annotation.Config;
import mekanism.api.math.FloatingLong;
import mekanism.common.config.BaseMekanismConfig;

@Config(name = "mekanism/generators-storage")

public class GeneratorsStorageConfig extends BaseMekanismConfig {
    public long heatGenerator = 160_000;
    public long bioGenerator = 160_000;
    public long solarGenerator = 96_000;
    public long advancedSolarGenerator = 200_000;
    public long windGenerator = 200_000;

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