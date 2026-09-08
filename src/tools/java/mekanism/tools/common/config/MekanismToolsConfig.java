package mekanism.tools.common.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class MekanismToolsConfig {

    private MekanismToolsConfig() {
    }

    public static ToolsConfig tools;
    public static ToolsClientConfig toolsClient;

    public static void registerClientConfigs() {
        AutoConfig.register(ToolsClientConfig.class, GsonConfigSerializer::new);

        toolsClient = AutoConfig.getConfigHolder(ToolsClientConfig.class).getConfig();
    }

    public static void registerConfig() {
        AutoConfig.register(ToolsConfig.class, GsonConfigSerializer::new);

        tools = AutoConfig.getConfigHolder(ToolsConfig.class).getConfig();
    }
}