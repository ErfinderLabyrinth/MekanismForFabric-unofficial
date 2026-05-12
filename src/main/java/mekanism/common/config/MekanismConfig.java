package mekanism.common.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

public class MekanismConfig {

    private MekanismConfig() {}

    // Define config instances
    public static ClientConfig client;
    public static CommonConfig common;
    public static GeneralConfig general;
    public static GearConfig gear;
    public static StorageConfig storage;
    public static TierConfig tiers;
    public static UsageConfig usage;
    public static WorldConfig world;

    public static void registerClientConfig() {
        AutoConfig.register(ClientConfig.class, Toml4jConfigSerializer::new);

        client = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();
    }

    public static void registerCommonConfigs() {
        // Register each config with AutoConfig
        AutoConfig.register(CommonConfig.class, Toml4jConfigSerializer::new);
        AutoConfig.register(GeneralConfig.class, Toml4jConfigSerializer::new);
        AutoConfig.register(GearConfig.class, Toml4jConfigSerializer::new);
        AutoConfig.register(StorageConfig.class, Toml4jConfigSerializer::new);
        AutoConfig.register(TierConfig.class, Toml4jConfigSerializer::new);
        AutoConfig.register(UsageConfig.class, Toml4jConfigSerializer::new);
        AutoConfig.register(WorldConfig.class, Toml4jConfigSerializer::new);

        // Get instances
        common = AutoConfig.getConfigHolder(CommonConfig.class).getConfig();
        general = AutoConfig.getConfigHolder(GeneralConfig.class).getConfig();
        gear = AutoConfig.getConfigHolder(GearConfig.class).getConfig();
        storage = AutoConfig.getConfigHolder(StorageConfig.class).getConfig();
        tiers = AutoConfig.getConfigHolder(TierConfig.class).getConfig();
        usage = AutoConfig.getConfigHolder(UsageConfig.class).getConfig();
        world = AutoConfig.getConfigHolder(WorldConfig.class).getConfig();
    }
}