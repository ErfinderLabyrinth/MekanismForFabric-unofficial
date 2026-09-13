package mekanism.generators.common.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class MekanismGeneratorsConfig {

    private MekanismGeneratorsConfig() {
    }

    public static GeneratorsConfig generators;
    public static GeneratorsGearConfig gear;
    public static GeneratorsStorageConfig storageConfig;

    public static void registerConfigs() {
        AutoConfig.register(GeneratorsConfig.class, GsonConfigSerializer::new);
        AutoConfig.register(GeneratorsGearConfig.class, GsonConfigSerializer::new);
        AutoConfig.register(GeneratorsStorageConfig.class, GsonConfigSerializer::new);

        generators = AutoConfig.getConfigHolder(GeneratorsConfig.class).getConfig();
        gear = AutoConfig.getConfigHolder(GeneratorsGearConfig.class).getConfig();
        storageConfig = AutoConfig.getConfigHolder(GeneratorsStorageConfig.class).getConfig();
    }
}