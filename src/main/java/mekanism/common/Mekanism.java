package mekanism.common;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mekanism.api.Coord4D;
import mekanism.api.MekanismAPI;
import mekanism.api.MekanismIMC;
import mekanism.api.providers.IItemProvider;
import mekanism.common.advancements.MekanismCriteriaTriggers;
import mekanism.common.base.IModModule;
import mekanism.common.base.KeySync;
import mekanism.common.base.MekFakePlayer;
import mekanism.common.base.PlayerState;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.command.ChunkCommand;
import mekanism.common.command.CommandMek;
import mekanism.common.command.builders.BuildCommand;
import mekanism.common.command.builders.Builders.*;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.boiler.BoilerMultiblockData;
import mekanism.common.content.boiler.BoilerValidator;
import mekanism.common.content.evaporation.EvaporationMultiblockData;
import mekanism.common.content.evaporation.EvaporationValidator;
import mekanism.common.content.gear.MekaSuitDispenseBehavior;
import mekanism.common.content.gear.ModuleDispenseBehavior;
import mekanism.common.content.matrix.MatrixMultiblockData;
import mekanism.common.content.matrix.MatrixValidator;
import mekanism.common.content.network.BoxedChemicalNetwork.ChemicalTransferEvent;
import mekanism.common.content.network.EnergyNetwork.EnergyTransferEvent;
import mekanism.common.content.network.FluidNetwork.FluidTransferEvent;
import mekanism.common.content.qio.QIOGlobalItemLookup;
import mekanism.common.content.sps.SPSCache;
import mekanism.common.content.sps.SPSMultiblockData;
import mekanism.common.content.sps.SPSValidator;
import mekanism.common.content.tank.TankCache;
import mekanism.common.content.tank.TankMultiblockData;
import mekanism.common.content.tank.TankValidator;
import mekanism.common.content.transporter.PathfinderCache;
import mekanism.common.content.transporter.TransporterManager;
import mekanism.common.integration.MekanismHooks;
import mekanism.common.item.block.machine.ItemBlockFluidTank.BasicCauldronInteraction;
import mekanism.common.item.block.machine.ItemBlockFluidTank.BasicDrainCauldronInteraction;
import mekanism.common.item.block.machine.ItemBlockFluidTank.FluidTankItemDispenseBehavior;
import mekanism.common.item.loot.MekanismLootFunctions;
import mekanism.common.lib.Version;
import mekanism.common.lib.frequency.FrequencyManager;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.inventory.personalstorage.PersonalStorageManager;
import mekanism.common.lib.multiblock.MultiblockCache;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.lib.transmitter.TransmitterNetworkRegistry;
import mekanism.common.mixinhelper.ElytraFlyable;
import mekanism.common.network.PacketHandler;
import mekanism.common.network.to_client.PacketTransmitterUpdate;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.*;
import mekanism.common.storage.item.ItemStorageHandler;
import mekanism.common.tags.MekanismTags;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.world.GenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import team.reborn.energy.api.EnergyStorage;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Mekanism implements ModInitializer {

    public static final String MODID = MekanismAPI.MEKANISM_MODID;
    public static final String MOD_NAME = "Mekanism";
    public static final String LOG_TAG = '[' + MOD_NAME + ']';
    public static final PlayerState playerState = new PlayerState();
    /**
     * Mekanism Packet Pipeline
     */
    private final PacketHandler packetHandler = new PacketHandler();
    /**
     * Mekanism logger instance
     */
    public static final Logger logger = LogUtils.getLogger();

    /**
     * Mekanism mod instance
     */
    public static Mekanism instance;
    /**
     * Mekanism hooks instance
     */
    public static final MekanismHooks hooks = new MekanismHooks();
    /**
     * Mekanism version number
     */
    public Version versionNumber;
    /**
     * MultiblockManagers for various structures
     */
    public static final MultiblockManager<TankMultiblockData> tankManager = new MultiblockManager<>("dynamicTank", TankCache::new, TankValidator::new);
    public static final MultiblockManager<MatrixMultiblockData> matrixManager = new MultiblockManager<>("inductionMatrix", MultiblockCache::new, MatrixValidator::new);
    public static final MultiblockManager<BoilerMultiblockData> boilerManager = new MultiblockManager<>("thermoelectricBoiler", MultiblockCache::new, BoilerValidator::new);
    public static final MultiblockManager<EvaporationMultiblockData> evaporationManager = new MultiblockManager<>("evaporation", MultiblockCache::new, EvaporationValidator::new);
    public static final MultiblockManager<SPSMultiblockData> spsManager = new MultiblockManager<>("sps", SPSCache::new, SPSValidator::new);
    /**
     * List of Mekanism modules loaded
     */
    public static final List<IModModule> modulesLoaded = new ArrayList<>();
    /**
     * The server's world tick handler.
     */
    public static final CommonWorldTickHandler worldTickHandler = new CommonWorldTickHandler();
    /**
     * The GameProfile used by the dummy Mekanism player
     */
    public static final GameProfile gameProfile = new GameProfile(UUID.nameUUIDFromBytes("mekanism.common".getBytes(StandardCharsets.UTF_8)), Mekanism.LOG_TAG);
    public static final KeySync keyMap = new KeySync();
    public static final Set<Coord4D> activeVibrators = new ObjectOpenHashSet<>();

    public final CommonPlayerTracker COMMON_PLAYER_TRACKER = new CommonPlayerTracker();

    private final ReloadListener recipeCacheManager = new ReloadListener();

    public static final CommonPlayerTickHandler commonPlayerTickHandler = new CommonPlayerTickHandler();

    @Override
    public void onInitialize() {
        instance = this;



        //Set our version number to match the mods.toml file, which matches the one in our build.gradle
        versionNumber = new Version(FabricLoader.getInstance().getModContainer(MekanismAPI.MEKANISM_MODID).get());
        //Super early hooks, only reliable thing is for checking dependencies that we declare we are after
        hooks.hookConstructor();

        MekanismConfig.registerCommonConfigs();

        ServerWorldEvents.LOAD.register(this::onWorldLoad);
        ServerWorldEvents.UNLOAD.register(this::onWorldUnload);
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        ServerLifecycleEvents.SERVER_STOPPED.register(this::serverStopped);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(getRecipeCacheManager());
        ServerChunkEvents.CHUNK_LOAD.register(ChunkCommand::onChunkLoad);
        ServerChunkEvents.CHUNK_UNLOAD.register(ChunkCommand::onChunkUnload);
        MekanismModules.MODULES.createAndRegister();
        MekanismItems.register();
        MekanismBlocks.register();
        MekanismFluids.register();
        MekanismContainerTypes.register();
        MekanismCreativeTabs.register();
        MekanismEntityTypes.register();
        MekanismTileEntityTypes.register();
        MekanismGameEvents.register();
        MekanismSounds.register();
        MekanismParticleTypes.register();
        MekanismHeightProviderTypes.register();
        MekanismIntProviderTypes.register();
        MekanismPlacementModifiers.register();
        MekanismFeatures.register();
        MekanismRecipeType.register();
        MekanismRecipeSerializers.register();
        MekanismDataSerializers.register();
        MekanismLootFunctions.register();
        MekanismGases.GASES.createAndRegisterChemical();
        MekanismInfuseTypes.INFUSE_TYPES.createAndRegisterChemical();
        MekanismPigments.PIGMENTS.createAndRegisterChemical();
        MekanismSlurries.SLURRIES.createAndRegisterChemical();
        MekanismRobitSkins.createAndRegisterDatapack();
        imcQueue();

        FluidStorage.GENERAL_COMBINED_PROVIDER.register(this::findFluidStorage);
        EnergyStorage.SIDED.registerFallback(((world, pos, state, blockEntity, context) -> {
            if (blockEntity instanceof TileEntityMekanism tileEntityMekanism) {
                if (tileEntityMekanism.canHandleEnergy()) {
                    return tileEntityMekanism.getEnergyContainer(context);
                }
            }
            return null;
        }));

        EnergyStorage.ITEM.registerFallback((stack, context) -> {
            if(stack.getItem() instanceof ItemStorageHandler itemStorageHandler) {
                return itemStorageHandler.getEnergyStorage(context);
            }
            return null;
        });

        Capabilities.register();

        addEmpty();

        commonSetup();

        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
            for (Map.Entry<BlockPos, BlockEntity> posBlockEntityEntry : chunk.getBlockEntities().entrySet()) {
                BlockEntity te = posBlockEntityEntry.getValue();
                if (te instanceof TileEntityTransmitter tileEntityTransmitter) {
                    tileEntityTransmitter.onChunkUnloaded();
                }
            }
        });

        EntityElytraEvents.CUSTOM.register((entity, tickElytra) -> {
            ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);
            if (chestStack.getItem() instanceof ElytraFlyable elytraFlyable) {
                if (elytraFlyable.canElytraFly(chestStack, entity)) {
                    return elytraFlyable.elytraFlightTick(chestStack, entity, entity.getFallFlyingTicks());
                }
            }
            return false;
        });
    }

    private @Nullable Storage<FluidVariant> findFluidStorage(ContainerItemContext containerItemContext) {
        ItemStack itemStack = containerItemContext.getItemVariant().toStack();
//        if (itemStack.getItem() instanceof ItemBlockFluidTank itemBlockFluidTank) {
//            return RateLimitFluidHandler.create(itemBlockFluidTank.getTier());
//        }else if(itemStack.getItem() instanceof ItemMekaSuitArmor itemMekaSuitArmor) {
//            return RateLimitMultiTankFluidHandler.create(itemStack, itemMekaSuitArmor.getFluidTankSpecs());
        /*}else*/
        if(itemStack.getItem() instanceof ItemStorageHandler itemStorageHandler) {
            return itemStorageHandler.getFluidStorage(containerItemContext);
        }
        return null;
    }

    public static synchronized void addModule(IModModule modModule) {
        modulesLoaded.add(modModule);
    }

    public static PacketHandler packetHandler() {
        return instance.packetHandler;
    }

    private void addEmpty() {
        //Register the empty chemicals
        ResourceLocation emptyName = rl("empty");
        Registry.register(MekanismAPI.gasRegistry(), emptyName, MekanismAPI.EMPTY_GAS);
        Registry.register(MekanismAPI.infuseTypeRegistry(), emptyName, MekanismAPI.EMPTY_INFUSE_TYPE);
        Registry.register(MekanismAPI.pigmentRegistry(), emptyName, MekanismAPI.EMPTY_PIGMENT);
        Registry.register(MekanismAPI.slurryRegistry(), emptyName, MekanismAPI.EMPTY_SLURRY);
//        event.register(MekanismAPI.GAS_REGISTRY_NAME, emptyName, () -> MekanismAPI.EMPTY_GAS);
//        event.register(MekanismAPI.INFUSE_TYPE_REGISTRY_NAME, emptyName, () -> MekanismAPI.EMPTY_INFUSE_TYPE);
//        event.register(MekanismAPI.PIGMENT_REGISTRY_NAME, emptyName, () -> MekanismAPI.EMPTY_PIGMENT);
//        event.register(MekanismAPI.SLURRY_REGISTRY_NAME, emptyName, () -> MekanismAPI.EMPTY_SLURRY);
        //Register our custom serializer condition
//        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
//            CraftingHelper.register(ConditionExistsCondition.Serializer.INSTANCE);
//            CraftingHelper.register(ModVersionLoadedCondition.Serializer.INSTANCE);
//        }
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(Mekanism.MODID, path);
    }

    public ReloadListener getRecipeCacheManager() {
        return recipeCacheManager;
    }

    private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext var2, Commands.CommandSelection var3) {
        BuildCommand.register("boiler", MekanismLang.BOILER, new BoilerBuilder());
        BuildCommand.register("matrix", MekanismLang.MATRIX, new MatrixBuilder());
        BuildCommand.register("tank", MekanismLang.DYNAMIC_TANK, new TankBuilder());
        BuildCommand.register("evaporation", MekanismLang.EVAPORATION_PLANT, new EvaporationBuilder());
        BuildCommand.register("sps", MekanismLang.SPS, new SPSBuilder());
        dispatcher.register(CommandMek.register());
    }

    private void serverStopped(MinecraftServer server) {
        //Clear all cache data, wait until server stopper though so that we make sure saving can use any data it needs
        playerState.clear(false);
        activeVibrators.clear();
        worldTickHandler.resetChunkData();
        FrequencyType.clear();
        BoilerMultiblockData.hotMap.clear();

        //Reset consistent managers
        QIOGlobalItemLookup.INSTANCE.reset();
        RadiationManager.get().reset();
        MultiblockManager.reset();
        FrequencyManager.reset();
        TransporterManager.reset();
        PathfinderCache.reset();
        TransmitterNetworkRegistry.reset();
        GenHandler.reset();
        PersonalStorageManager.reset();
    }

    private void imcQueue() {
        //IMC messages we send to other mods
        //hooks.sendIMCMessages(event);
        //IMC messages that we are sending to ourselves
        MekanismIMC.addModulesToAll(MekanismModules.ENERGY_UNIT);
        MekanismIMC.addMekaSuitModules(MekanismModules.COLOR_MODULATION_UNIT, MekanismModules.LASER_DISSIPATION_UNIT, MekanismModules.RADIATION_SHIELDING_UNIT);
        MekanismIMC.addMekaToolModules(MekanismModules.ATTACK_AMPLIFICATION_UNIT, MekanismModules.SILK_TOUCH_UNIT, MekanismModules.FORTUNE_UNIT, MekanismModules.BLASTING_UNIT, MekanismModules.VEIN_MINING_UNIT,
                MekanismModules.FARMING_UNIT, MekanismModules.SHEARING_UNIT, MekanismModules.TELEPORTATION_UNIT, MekanismModules.EXCAVATION_ESCALATION_UNIT);
        MekanismIMC.addMekaSuitHelmetModules(MekanismModules.ELECTROLYTIC_BREATHING_UNIT, MekanismModules.INHALATION_PURIFICATION_UNIT,
                MekanismModules.VISION_ENHANCEMENT_UNIT, MekanismModules.NUTRITIONAL_INJECTION_UNIT);
        MekanismIMC.addMekaSuitBodyarmorModules(MekanismModules.JETPACK_UNIT, MekanismModules.GRAVITATIONAL_MODULATING_UNIT, MekanismModules.CHARGE_DISTRIBUTION_UNIT,
                MekanismModules.DOSIMETER_UNIT, MekanismModules.GEIGER_UNIT, MekanismModules.ELYTRA_UNIT);
        MekanismIMC.addMekaSuitPantsModules(MekanismModules.LOCOMOTIVE_BOOSTING_UNIT, MekanismModules.GYROSCOPIC_STABILIZATION_UNIT,
                MekanismModules.HYDROSTATIC_REPULSOR_UNIT, MekanismModules.MOTORIZED_SERVO_UNIT);
        MekanismIMC.addMekaSuitBootsModules(MekanismModules.HYDRAULIC_PROPULSION_UNIT, MekanismModules.MAGNETIC_ATTRACTION_UNIT, MekanismModules.FROST_WALKER_UNIT);
    }

    private void commonSetup() {
        //Initialization notification
        logger.info("Version {} initializing...", versionNumber);
        hooks.hookCommonSetup();

        //Ensure our tags are all initialized
        MekanismTags.init();
        //Collect annotation scan data
        //MekAnnotationScanner.collectScanData();
        //Register advancement criteria
        MekanismCriteriaTriggers.init();
        //Add chunk loading callbacks
        //TODO ForgeChunkManager.setForcedChunkLoadingCallback(Mekanism.MODID, ChunkValidationCallback.INSTANCE);
        //Register dispenser behaviors
        MekanismFluids.FLUIDS.registerBucketDispenserBehavior();
        registerFluidTankBehaviors(MekanismBlocks.BASIC_FLUID_TANK, MekanismBlocks.ADVANCED_FLUID_TANK, MekanismBlocks.ELITE_FLUID_TANK,
                MekanismBlocks.ULTIMATE_FLUID_TANK, MekanismBlocks.CREATIVE_FLUID_TANK);
        registerDispenseBehavior(new ModuleDispenseBehavior(), MekanismItems.MEKA_TOOL);
        registerDispenseBehavior(new MekaSuitDispenseBehavior(), MekanismItems.MEKASUIT_HELMET, MekanismItems.MEKASUIT_BODYARMOR, MekanismItems.MEKASUIT_PANTS,
                MekanismItems.MEKASUIT_BOOTS);
//        //Register custom item predicates
//        Registry.register(BuiltInRegistries.PROV, FullCanteenItemPredicate.ID, FullCanteenItemPredicate.TYPE);
//        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, MaxedModuleContainerLootItemCondition.ID, MaxedModuleContainerLootItemCondition.TYPE);
        //Add any extra game event frequencies
        MekanismGameEvents.addFrequencies();

        //Register player tracker
        ;
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            server.getPlayerList().getPlayers().forEach(commonPlayerTickHandler::tickEnd);
        });

        //ServerPlayConnectionEvents.JOIN.register(worldTickHandler.);
        PlayerBlockBreakEvents.BEFORE.register(worldTickHandler::onBlockBreak);
        ServerChunkEvents.CHUNK_UNLOAD.register(worldTickHandler::chunkUnloadEvent);
        ServerWorldEvents.LOAD.register(worldTickHandler::worldLoadEvent);
        ServerWorldEvents.UNLOAD.register(worldTickHandler::worldUnloadEvent);
        ServerTickEvents.END_SERVER_TICK.register(worldTickHandler::serverTick);
        ServerTickEvents.END_WORLD_TICK.register(worldTickHandler::tickEnd);


        //Register with TransmitterNetworkRegistry
        TransmitterNetworkRegistry.initiate();
        ServerTickEvents.END_SERVER_TICK.register(TransmitterNetworkRegistry.getInstance()::onTick);

        //Packet registrations
        packetHandler.initialize();

        //Fake player info
        logger.info("Fake player readout: UUID = {}, name = {}", gameProfile.getId(), gameProfile.getName());
        logger.info("Mod loaded.");
    }

    private static void registerDispenseBehavior(DispenseItemBehavior behavior, IItemProvider... itemProviders) {
        for (IItemProvider itemProvider : itemProviders) {
            DispenserBlock.registerBehavior(itemProvider.asItem(), behavior);
        }
    }

    private static void registerFluidTankBehaviors(IItemProvider... itemProviders) {
        registerDispenseBehavior(FluidTankItemDispenseBehavior.INSTANCE);
        for (IItemProvider itemProvider : itemProviders) {
            Item item = itemProvider.asItem();
            CauldronInteraction.EMPTY.put(item, BasicCauldronInteraction.EMPTY);
            CauldronInteraction.WATER.put(item, BasicDrainCauldronInteraction.WATER);
            CauldronInteraction.LAVA.put(item, BasicDrainCauldronInteraction.LAVA);
        }
    }

    public void onEnergyTransferred(EnergyTransferEvent event) {
        packetHandler.sendToReceivers(new PacketTransmitterUpdate(event.network), event.network, event.network.getWorld().getServer());
    }

    public void onChemicalTransferred(ChemicalTransferEvent event) {
        packetHandler.sendToReceivers(new PacketTransmitterUpdate(event.network, event.transferType), event.network, event.network.getWorld().getServer());
    }

    public void onLiquidTransferred(FluidTransferEvent event) {
        packetHandler.sendToReceivers(new PacketTransmitterUpdate(event.network, event.fluidType), event.network, event.network.getWorld().getServer());
    }

    private void onWorldLoad(MinecraftServer server, ServerLevel level) {
        playerState.init(level);
    }

    private void onWorldUnload(MinecraftServer server, ServerLevel level) {
        // Make sure the global fake player drops its reference to the World
        // when the server shuts down
        MekFakePlayer.releaseInstance(level);
    }
}