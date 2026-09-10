package mekanism.generators.common;

import mekanism.api.MekanismIMC;
import mekanism.api.chemical.gas.attribute.GasAttributes.Fuel;
import mekanism.api.math.FloatingLong;
import mekanism.common.Mekanism;
import mekanism.common.base.IModModule;
import mekanism.common.command.builders.BuildCommand;
import mekanism.common.config.MekanismConfig;
import mekanism.common.config.listener.ConfigBasedCachedFLSupplier;
import mekanism.common.config.listener.ConfigBasedCachedSupplier;
import mekanism.common.lib.Version;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.registries.MekanismGases;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.fission.FissionReactorCache;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import mekanism.generators.common.content.fission.FissionReactorValidator;
import mekanism.generators.common.content.fusion.FusionReactorCache;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import mekanism.generators.common.content.fusion.FusionReactorValidator;
import mekanism.generators.common.content.turbine.TurbineCache;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import mekanism.generators.common.content.turbine.TurbineValidator;
import mekanism.generators.common.network.GeneratorsPacketHandler;
import mekanism.generators.common.registries.GeneratorsBlocks;
import mekanism.generators.common.registries.GeneratorsBuilders.FissionReactorBuilder;
import mekanism.generators.common.registries.GeneratorsBuilders.FusionReactorBuilder;
import mekanism.generators.common.registries.GeneratorsBuilders.TurbineBuilder;
import mekanism.generators.common.registries.GeneratorsContainerTypes;
import mekanism.generators.common.registries.GeneratorsCreativeTabs;
import mekanism.generators.common.registries.GeneratorsFluids;
import mekanism.generators.common.registries.GeneratorsGases;
import mekanism.generators.common.registries.GeneratorsItems;
import mekanism.generators.common.registries.GeneratorsModules;
import mekanism.generators.common.registries.GeneratorsSounds;
import mekanism.generators.common.registries.GeneratorsTileEntityTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

public class MekanismGenerators implements ModInitializer, IModModule {

    public static final String MODID = "mekanismgenerators";
    private static final ConfigBasedCachedSupplier<Long> ETHENE_ENERGY_DENSITY = new ConfigBasedCachedSupplier<>(() -> {
        long energy = MekanismGeneratorsConfig.generators.bioGeneration * 2
              * MekanismGeneratorsConfig.generators.ethyleneDensityMultiplier;
        return energy + MekanismConfig.COMMON.general.FROM_H2;
    });

    public static MekanismGenerators instance;

    /**
     * MekanismGenerators version number
     */
    public Version versionNumber;
    /**
     * Mekanism Generators Packet Pipeline
     */
    private GeneratorsPacketHandler packetHandler;

    public static final MultiblockManager<TurbineMultiblockData> turbineManager = new MultiblockManager<>("industrialTurbine", TurbineCache::new, TurbineValidator::new);
    public static final MultiblockManager<FissionReactorMultiblockData> fissionReactorManager = new MultiblockManager<>("fissionReactor", FissionReactorCache::new, FissionReactorValidator::new);
    public static final MultiblockManager<FusionReactorMultiblockData> fusionReactorManager = new MultiblockManager<>("fusionReactor", FusionReactorCache::new, FusionReactorValidator::new);

    public void onInitialize() {
        packetHandler = new GeneratorsPacketHandler();
        Mekanism.addModule(instance = this);
        MekanismGeneratorsConfig.registerConfigs();


        versionNumber = new Version(FabricLoader.getInstance().getModContainer(MODID).get());
    }

    @Override
    public void launchCommon() {
        commonSetup();

        GeneratorsModules.register();
        GeneratorsItems.register();
        GeneratorsBlocks.register();
        GeneratorsFluids.register();
        GeneratorsCreativeTabs.register();
        GeneratorsSounds.register();
        GeneratorsContainerTypes.register();
        GeneratorsTileEntityTypes.register();
        GeneratorsGases.register();
        //Set our version number to match the mods.toml file, which matches the one in our build.gradle
        imcQueue();
    }

    public static GeneratorsPacketHandler packetHandler() {
        return instance.packetHandler;
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MekanismGenerators.MODID, path);
    }

    private void commonSetup() {
        //Ensure our tags are all initialized
        GeneratorTags.init();
        //Add fuel attribute to ethene
        MekanismGases.ETHENE.get().addAttribute(new Fuel(() -> MekanismGeneratorsConfig.generators.ethyleneBurnTicks, ETHENE_ENERGY_DENSITY::get));
        //Register dispenser behaviors
        GeneratorsFluids.FLUIDS.registerBucketDispenserBehavior();
        //Register extended build commands (in enqueue as it is not thread safe)
        BuildCommand.register("turbine", GeneratorsLang.TURBINE, new TurbineBuilder());
        BuildCommand.register("fission", GeneratorsLang.FISSION_REACTOR, new FissionReactorBuilder());
        BuildCommand.register("fusion", GeneratorsLang.FUSION_REACTOR, new FusionReactorBuilder());

        packetHandler.initialize();

        //Finalization
        Mekanism.logger.info("Loaded 'Mekanism: Generators' module.");
    }

    private void imcQueue() {
        MekanismIMC.addMekaSuitHelmetModules(GeneratorsModules.SOLAR_RECHARGING_UNIT);
        MekanismIMC.addMekaSuitPantsModules(GeneratorsModules.GEOTHERMAL_GENERATOR_UNIT);
    }

    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "Generators";
    }

    @Override
    public void resetClient() {
        TurbineMultiblockData.clientRotationMap.clear();
    }
}
