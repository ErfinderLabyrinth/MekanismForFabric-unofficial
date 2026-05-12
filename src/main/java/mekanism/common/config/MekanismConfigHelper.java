package mekanism.common.config;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class MekanismConfigHelper {

    private MekanismConfigHelper() {
    }

    public static final Path CONFIG_DIR = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("mekanism");

    /**
     * Creates a mod config so that {@link net.minecraftforge.fml.config.ConfigTracker} will track it and sync server configs from server to client.
     */
    /*public static void registerConfig(ModContainer modContainer, IMekanismConfig config) {
        MekanismModConfig modConfig = new MekanismModConfig(modContainer, config);
        if (config.addToContainer()) {
            modContainer.addConfig(modConfig);
        }
    }*/
}