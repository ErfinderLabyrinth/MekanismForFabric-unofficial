package mekanism.additions.common.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class MekanismAdditionsConfig {

    private MekanismAdditionsConfig() {
    }

    public static AdditionsConfig additions = new AdditionsConfig();
    public static AdditionsClientConfig additionsClient = new AdditionsClientConfig();

    public static void registerClientConfigs() {
        AutoConfig.register(AdditionsClientConfig.class, GsonConfigSerializer::new);

        additionsClient = AutoConfig.getConfigHolder(AdditionsClientConfig.class).getConfig();
    }

    public static void registerConfig() {
        AutoConfig.register(AdditionsConfig.class, GsonConfigSerializer::new);

        additions = AutoConfig.getConfigHolder(AdditionsConfig.class).getConfig();
    }
}