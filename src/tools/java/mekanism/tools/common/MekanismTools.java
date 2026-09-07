package mekanism.tools.common;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import mekanism.api.MekanismAPI;
import mekanism.common.Mekanism;
import mekanism.common.base.IModModule;
import mekanism.common.lib.Version;
import mekanism.tools.common.config.MekanismToolsConfig;
import mekanism.tools.common.item.tier.MekanismTiers;
import mekanism.tools.common.material.BaseMekanismMaterial;
import mekanism.tools.common.registries.ToolsCreativeTabs;
import mekanism.tools.common.registries.ToolsItems;
import mekanism.tools.common.registries.ToolsRecipeSerializers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

import java.util.Collection;

public class MekanismTools implements IModModule, ModInitializer {

    public static final String MODID = "mekanismtools";

    public static MekanismTools instance;

    /**
     * MekanismTools version number
     */
    public Version versionNumber;

    public void onInitialize() {
        Mekanism.addModule(instance = this);
        MekanismToolsConfig.registerConfig();
        //Register the listener for special mob spawning (mobs with Mekanism armor/tools)

        ToolsItems.register();
        ToolsCreativeTabs.register();
        ToolsRecipeSerializers.register();
        //Set our version number to match the mods.toml file, which matches the one in our build.gradle
        versionNumber = new Version(FabricLoader.getInstance().getModContainer(MekanismAPI.MEKANISM_MODID).get());

        commonSetup();
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MekanismTools.MODID, path);
    }

    private void commonSetup() {
        ToolsTags.init();
        registerTiers(MekanismTiers.BRONZE, MekanismTiers.LAPIS_LAZULI, MekanismTiers.OSMIUM, MekanismTiers.STEEL,
                MekanismTiers.REFINED_OBSIDIAN, MekanismTiers.REFINED_OBSIDIAN);
        Mekanism.logger.info("Loaded 'Mekanism: Tools' module.");
    }

    @SuppressWarnings("deprecation")
    private void registerTiers(BaseMekanismMaterial... tiers) {
        Multimap<Integer, Tier> vanillaTiers = HashMultimap.create();
        for (Tiers vanillaTier : Tiers.values()) {
            vanillaTiers.put(vanillaTier.getLevel(), vanillaTier);
        }
        for (BaseMekanismMaterial tier : tiers) {
            int level = tier.getLevel();
            Collection<Tier> equivalent = vanillaTiers.get(level);
            Collection<Tier> vanillaNext = vanillaTiers.get(level + 1);
            //If the tier is equivalent to another tier then the equivalent one should be placed in the after list
            // and if it is equivalent to a vanilla tier (like all ours are when equivalent), the next tier
            // should also specify the next tier in the before list
            //TierSortingRegistry.registerTier(tier, rl(tier.getRegistryPrefix()), new ArrayList<>(equivalent), new ArrayList<>(vanillaNext));
        }
    }

    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "Tools";
    }

    @Override
    public void resetClient() {
    }
}