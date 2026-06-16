package mekanism.common.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

public class MekanismConfig {

    private MekanismConfig() {}

    // Define config instances
//    public static ClientConfig client;
//    public static CommonConfig common;
//    public static GeneralConfig general;
//    public static GearConfig gear;
//    public static StorageConfig storage;
//    public static TierConfig tiers;
//    public static UsageConfig usage;
//    public static WorldConfig world;
    public static Common COMMON;
    public static Client CLIENT;

    public static void registerClientConfig() {
        AutoConfig.register(Client.class, PartitioningSerializer.wrap(GsonConfigSerializer::new));

        CLIENT = AutoConfig.getConfigHolder(Client.class).getConfig();
    }

    public static void registerCommonConfigs() {
        // Register each config with AutoConfig
//        AutoConfig.register(CommonConfig.class, GsonConfigSerializer::new);
//        AutoConfig.register(GeneralConfig.class, GsonConfigSerializer::new);
//        AutoConfig.register(GearConfig.class, GsonConfigSerializer::new);
//        AutoConfig.register(StorageConfig.class, GsonConfigSerializer::new);
//        AutoConfig.register(TierConfig.class, GsonConfigSerializer::new);
//        AutoConfig.register(UsageConfig.class, GsonConfigSerializer::new);
//        AutoConfig.register(WorldConfig.class, GsonConfigSerializer::new);
//
//        // Get instances
//        common = AutoConfig.getConfigHolder(CommonConfig.class).getConfig();
//        general = AutoConfig.getConfigHolder(GeneralConfig.class).getConfig();
//        gear = AutoConfig.getConfigHolder(GearConfig.class).getConfig();
//        storage = AutoConfig.getConfigHolder(StorageConfig.class).getConfig();
//        tiers = AutoConfig.getConfigHolder(TierConfig.class).getConfig();
//        usage = AutoConfig.getConfigHolder(UsageConfig.class).getConfig();
//        world = AutoConfig.getConfigHolder(WorldConfig.class).getConfig();

        AutoConfig.register(Common.class, PartitioningSerializer.wrap(GsonConfigSerializer::new));

        COMMON = AutoConfig.getConfigHolder(Common.class).getConfig();
    }

    @Config(name = "mekanism")
    public static class Common extends PartitioningSerializer.GlobalData {
        public CommonConfig common;
        public GeneralConfig general;
        public GearConfig gear;
        public StorageConfig storage;
        public TierConfig tiers;
        public UsageConfig usage;
        public WorldConfig world;
    }

    @Config(name = "mekanism")
    public static class Client extends PartitioningSerializer.GlobalData {
        public ClientConfig client;
    }
}