package mekanism.defense.common;

import mekanism.common.Mekanism;
import mekanism.common.base.IModModule;
import mekanism.common.config.MekanismModConfig;
import mekanism.common.lib.Version;
import mekanism.defense.common.config.MekanismDefenseConfig;
import mekanism.defense.common.network.DefensePacketHandler;
import mekanism.defense.common.registries.DefenseContainerTypes;
import mekanism.defense.common.registries.DefenseCreativeTabs;
import mekanism.defense.common.registries.DefenseItems;
import mekanism.defense.common.registries.DefenseTileEntityTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class MekanismDefense implements IModModule, ModInitializer {

    public static final String MODID = "mekanismdefense";

    public static MekanismDefense instance;

    /**
     * MekanismDefense version number
     */
    public Version versionNumber;
    /**
     * Mekanism Defense Packet Pipeline
     */
    private DefensePacketHandler packetHandler;

    @Override
    public void onInitialize() {
        Mekanism.addModule(instance = this);
        MekanismDefenseConfig.registerConfigs(ModLoadingContext.get());

        DefenseItems.ITEMS.register();
        //DefenseBlocks.BLOCKS.register();
        DefenseCreativeTabs.CREATIVE_TABS.register();
        DefenseContainerTypes.CONTAINER_TYPES.register();
        DefenseTileEntityTypes.TILE_ENTITY_TYPES.register();

        //Set our version number to match the mods.toml file, which matches the one in our build.gradle
        versionNumber = new Version(FabricLoader.getInstance().getModContainer(MODID).get().getMetadata().getVersion().getFriendlyString());
        packetHandler = new DefensePacketHandler();

        commonSetup();
    }

    public void commonSetup() {
        packetHandler.initialize();

        //Finalization
        Mekanism.logger.info("Loaded 'Mekanism: Defense' module.");
    }

    public static DefensePacketHandler packetHandler() {
        return instance.packetHandler;
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MekanismDefense.MODID, path);
    }

    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "Defense";
    }

    @Override
    public void resetClient() {
    }

    private void onConfigLoad(ModConfigEvent configEvent) {
        //Note: We listen to both the initial load and the reload, to make sure that we fix any accidentally
        // cached values from calls before the initial loading
        ModConfig config = configEvent.getConfig();
        //Make sure it is for the same modid as us
        if (config.getModId().equals(MODID) && config instanceof MekanismModConfig mekConfig) {
            mekConfig.clearCache(configEvent);
        }
    }
}