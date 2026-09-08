package mekanism.defense.common.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class MekanismDefenseConfig {

    private MekanismDefenseConfig() {
    }

    public static DefenseConfig defense;

    public static void registerConfigs() {
        AutoConfig.register(DefenseConfig.class, GsonConfigSerializer::new);

        defense = AutoConfig.getConfigHolder(DefenseConfig.class).getConfig();
    }
}