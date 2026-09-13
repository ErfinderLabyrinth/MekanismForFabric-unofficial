package mekanism.common.integration;

import mekanism.common.integration.computer.FactoryRegistry;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;

/**
 * Hooks for Mekanism. Use to grab items or blocks out of different mods.
 *
 * @author AidanBrady
 */
public final class MekanismHooks {

    public static final String CC_MOD_ID = "computercraft";
    public static final String CRAFTTWEAKER_MOD_ID = "crafttweaker";
    public static final String CURIOS_MODID = "curios";
    public static final String DARK_MODE_EVERYWHERE_MODID = "darkmodeeverywhere";
    public static final String FLUX_NETWORKS_MOD_ID = "fluxnetworks";
    public static final String IC2_MOD_ID = "ic2";
    public static final String JEI_MOD_ID = "jei";
    public static final String JEITWEAKER_MOD_ID = "jeitweaker";
    public static final String JSON_THINGS_MOD_ID = "jsonthings";
    public static final String OC2_MOD_ID = "oc2";
    public static final String PROJECTE_MOD_ID = "projecte";
    public static final String RECIPE_STAGES_MOD_ID = "recipestages";
    public static final String TOP_MOD_ID = "theoneprobe";
    public static final String WILDFIRE_GENDER_MOD_ID = "wildfire_gender";

    public boolean CCLoaded;
    public boolean CraftTweakerLoaded;
    public boolean CuriosLoaded;
    public boolean DMELoaded;
    public boolean FluxNetworksLoaded;
    public boolean IC2Loaded;
    public boolean JEILoaded;
    public boolean JsonThingsLoaded;
    public boolean OC2Loaded;
    public boolean ProjectELoaded;
    public boolean RecipeStagesLoaded;
    public boolean TOPLoaded;
    public boolean WildfireGenderModLoaded;

    public void hookConstructor() {
        FabricLoader loader = FabricLoader.getInstance();
        CraftTweakerLoaded = loader.isModLoaded(CRAFTTWEAKER_MOD_ID);
        CuriosLoaded = loader.isModLoaded(CURIOS_MODID);
        JsonThingsLoaded = loader.isModLoaded(JSON_THINGS_MOD_ID);
        if (CuriosLoaded) {
            //TODO add support
            //CuriosIntegration.addListeners();
        }
        if (CraftTweakerLoaded) {
            //Attempt to grab the mod event bus for CraftTweaker so that we can register our custom content in their namespace
            // to make it clearer which chemicals were added by CraftTweaker, and which are added by actual mods.
            // Gracefully fallback to our event bus if something goes wrong with getting CrT's and just then have the log have
            // warnings about us registering things in their namespace.

            //Register our CrT listener at lowest priority to try and ensure they get later ids than our normal registries

            //TODO add support
            //crtModEventBus.addListener(EventPriority.LOWEST, CrTContentUtils::registerCrTContent);
        }
        if (JsonThingsLoaded) {
            //TODO add support
            //JsonThingsIntegration.hook();
        }
    }

    public void hookCommonSetup() {
        FabricLoader loader = FabricLoader.getInstance();
        CCLoaded = loader.isModLoaded(CC_MOD_ID);
        DMELoaded = loader.isModLoaded(DARK_MODE_EVERYWHERE_MODID);
        IC2Loaded = loader.isModLoaded(IC2_MOD_ID);
        JEILoaded = loader.isModLoaded(JEI_MOD_ID);
        OC2Loaded = loader.isModLoaded(OC2_MOD_ID);
        ProjectELoaded = loader.isModLoaded(PROJECTE_MOD_ID);
        RecipeStagesLoaded = loader.isModLoaded(RECIPE_STAGES_MOD_ID);
        TOPLoaded = loader.isModLoaded(TOP_MOD_ID);
        FluxNetworksLoaded = loader.isModLoaded(FLUX_NETWORKS_MOD_ID);
        WildfireGenderModLoaded = loader.isModLoaded(WILDFIRE_GENDER_MOD_ID);
        if (computerCompatEnabled()) {
            FactoryRegistry.load();
            if (CCLoaded) {
                //TODO add Support
                //CCCapabilityHelper.registerApis();
            }
        }
        //EnergyCompatUtils.initLoadedCache();

        //TODO - 1.20: Move this out of here and back to always being registered whenever it gets fixed in Neo.
        // Modifying the result doesn't apply properly when "quick crafting"
        if (loader.isModLoaded("fastbench")) {
            //TODO add support
            //MinecraftForge.EVENT_BUS.addListener(BinInsertRecipe::onCrafting);
        }
    }

    public void sendIMCMessages() {
        //TODO add support
//        if (DMELoaded) {
//            //Note: While it is only strings, so it is safe to call and IMC validates the mods are loaded
//            // we add this check here, so we can skip iterating the list of things we want to blacklist when it is not present
//            sendDarkModeEverywhereIMC();
//        }
//        if (ProjectELoaded) {
//            NSSHelper.init();
//        }
//        if (TOPLoaded) {
//            InterModComms.sendTo(TOP_MOD_ID, "getTheOneProbe", TOPProvider::new);
//        }
    }

    public boolean computerCompatEnabled() {
        return CCLoaded || OC2Loaded;
    }

    /**
     * @apiNote DME only uses strings in IMC, so we can safely just include them here without worrying about classloading issues
     */
    private void sendDarkModeEverywhereIMC() {
        List<String> methodBlacklist = List.of(
              //Used for drawing fluids and chemicals in various GUIs including JEI as well as similar styled things
              "mekanism.client.gui.GuiUtils:drawTiledSprite",
              //MekaSuit HUD rendering (already configurable by the user)
              "mekanism.client.render.HUDRenderer:renderCompass",
              "mekanism.client.render.HUDRenderer:renderHUDElement"
        );
        for (String method : methodBlacklist) {
            //InterModComms.sendTo(DARK_MODE_EVERYWHERE_MODID, "dme-shaderblacklist", () -> method);
        }
    }
}
