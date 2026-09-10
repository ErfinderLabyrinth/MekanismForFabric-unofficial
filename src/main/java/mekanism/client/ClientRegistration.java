package mekanism.client;

import com.google.common.collect.Table.Cell;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.providers.IItemProvider;
import mekanism.api.text.EnumColor;
import mekanism.api.tier.BaseTier;
import mekanism.client.gui.*;
import mekanism.client.gui.item.GuiDictionary;
import mekanism.client.gui.item.GuiPersonalStorageItem;
import mekanism.client.gui.item.GuiPortableTeleporter;
import mekanism.client.gui.item.GuiSeismicReader;
import mekanism.client.gui.machine.*;
import mekanism.client.gui.qio.*;
import mekanism.client.gui.robit.*;
import mekanism.client.key.MekanismKeyHandler;
import mekanism.client.model.*;
import mekanism.client.model.baked.DigitalMinerBakedModel;
import mekanism.client.model.baked.DriveArrayBakedModel;
import mekanism.client.model.baked.ExtensionBakedModel.LightedBakedModel;
import mekanism.client.particle.*;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismShaders;
import mekanism.client.render.RenderTickHandler;
import mekanism.client.render.armor.FreeRunnerArmor;
import mekanism.client.render.armor.JetpackArmor;
import mekanism.client.render.armor.ScubaMaskArmor;
import mekanism.client.render.armor.ScubaTankArmor;
import mekanism.client.render.entity.RenderFlame;
import mekanism.client.render.entity.RenderRobit;
import mekanism.client.render.item.block.RenderEnergyCubeItem;
import mekanism.client.render.item.gear.*;
import mekanism.client.render.layer.MekanismArmorLayer;
import mekanism.client.render.layer.MekanismElytraLayer;
import mekanism.client.render.tileentity.*;
import mekanism.client.render.transmitter.*;
import mekanism.common.Mekanism;
import mekanism.common.base.HolidayManager;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.shared.ModuleColorModulationUnit;
import mekanism.common.integration.MekanismHooks;
import mekanism.common.item.ItemConfigurationCard;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.item.ItemCraftingFormula;
import mekanism.common.item.block.ItemBlockCardboardBox;
import mekanism.common.item.block.machine.ItemBlockFluidTank;
import mekanism.common.lib.Color;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.FluidDeferredRegister;
import mekanism.common.registration.impl.FluidRegistryObject;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.registries.*;
import mekanism.common.resource.IResource;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.resource.ResourceType;
import mekanism.common.tile.qio.TileEntityQIOComponent;
import mekanism.common.tile.transmitter.TileEntityLogisticalTransporter;
import mekanism.common.util.RegistryUtils;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistration {
    public static final ClientTickHandler TICK_HANDLER = new ClientTickHandler();
    public static final RenderTickHandler RENDER_TICK_HANDLER = new RenderTickHandler();

//    private static final FieldReflectionHelper<SeparateTransformsModel.Baked, BakedModel> SEPARATE_PERSPECTIVE_BASE_MODEL =
//          new FieldReflectionHelper<>(SeparateTransformsModel.Baked.class, "baseModel", () -> null);
//    private static final FieldReflectionHelper<SeparateTransformsModel.Baked, ImmutableMap<ItemDisplayContext, BakedModel>> SEPARATE_PERSPECTIVE_PERSPECTIVES =
//          new FieldReflectionHelper<>(SeparateTransformsModel.Baked.class, "perspectives", ImmutableMap::of);
    private static final Map<ResourceLocation, CustomModelRegistryObject> customModels = new ConcurrentHashMap<>();

    public static void init() {
        MekanismConfig.registerClientConfig();

        ClientTickEvents.START_CLIENT_TICK.register(TICK_HANDLER::onTick);
        WorldRenderEvents.END.register(RENDER_TICK_HANDLER::tickEnd);
        WorldRenderEvents.BLOCK_OUTLINE.register(RENDER_TICK_HANDLER::onBlockHover);
//        MinecraftForge.EVENT_BUS.register(new RenderTickHandler());

        if (FabricLoader.getInstance().isModLoaded(MekanismHooks.JEI_MOD_ID)) {
            //Note: We check this directly instead of using our value stored in Mekanism hooks
            // as that is initialized in CommonSetup and I believe that may be fired in parallel
            // to ClientSetup, in which case there would be a chance this gets ran before it is
            // properly initialized and will have the wrong value
        }
        HolidayManager.init();
        IModuleHelper moduleHelper = IModuleHelper.INSTANCE;
        moduleHelper.addMekaSuitModuleModels(Mekanism.rl("models/entity/mekasuit_modules.obj"));
        moduleHelper.addMekaSuitModuleModelSpec("jetpack", MekanismModules.JETPACK_UNIT, EquipmentSlot.CHEST);
        moduleHelper.addMekaSuitModuleModelSpec("modulator", MekanismModules.GRAVITATIONAL_MODULATING_UNIT, EquipmentSlot.CHEST);
        moduleHelper.addMekaSuitModuleModelSpec("elytra", MekanismModules.ELYTRA_UNIT, EquipmentSlot.CHEST, LivingEntity::isFallFlying);

        //Set fluids to a translucent render layer
        for (FluidRegistryObject<?, ?, ?, ?> fluidRO : MekanismFluids.FLUIDS.getAllFluids()) {
            ClientRegistrationUtil.setRenderLayer(RenderType.translucent(), fluidRO);
        }
        ClientRegistrationUtil.setPropertyOverride(MekanismBlocks.CARDBOARD_BOX, Mekanism.rl("storage"),
              (stack, world, entity, seed) -> ((ItemBlockCardboardBox) stack.getItem()).getBlockData(world, stack) == null ? 0 : 1);

        ClientRegistrationUtil.setPropertyOverride(MekanismItems.CRAFTING_FORMULA, Mekanism.rl("invalid"), (stack, world, entity, seed) -> {
            ItemCraftingFormula formula = (ItemCraftingFormula) stack.getItem();
            return formula.hasInventory(stack) && formula.isInvalid(stack) ? 1 : 0;
        });
        ClientRegistrationUtil.setPropertyOverride(MekanismItems.CRAFTING_FORMULA, Mekanism.rl("encoded"), (stack, world, entity, seed) -> {
            ItemCraftingFormula formula = (ItemCraftingFormula) stack.getItem();
            return formula.hasInventory(stack) && !formula.isInvalid(stack) ? 1 : 0;
        });
        ClientRegistrationUtil.setPropertyOverride(MekanismItems.CONFIGURATION_CARD, Mekanism.rl("encoded"),
              (stack, world, entity, seed) -> ((ItemConfigurationCard) stack.getItem()).hasData(stack) ? 1 : 0);
        ClientRegistrationUtil.setPropertyOverride(MekanismItems.CONFIGURATOR, Mekanism.rl("mode"), (stack, world, entity, seed) -> {
            ItemConfigurator.ConfiguratorMode mode = ((ItemConfigurator) stack.getItem()).getMode(stack);
            return switch (mode) {
                default -> 0;
                case EMPTY -> 1;
                case ROTATE -> 2;
                case WRENCH -> 3;
            };
        });

        ClientRegistrationUtil.setPropertyOverride(MekanismItems.ELECTRIC_BOW, Mekanism.rl("pull"),
              (stack, world, entity, seed) -> entity != null && entity.getUseItem() == stack ? (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F : 0);
        ClientRegistrationUtil.setPropertyOverride(MekanismItems.ELECTRIC_BOW, Mekanism.rl("pulling"),
              (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

        ClientRegistrationUtil.setPropertyOverride(MekanismItems.GEIGER_COUNTER, Mekanism.rl("radiation"), (stack, world, entity, seed) -> {
            if (entity instanceof Player) {
                return RadiationManager.get().getClientScale().ordinal();
            }
            return 0;
        });
        //Note: Our implementation allows for a null entity so don't worry about it and pass it
        ClientRegistrationUtil.setPropertyOverride(MekanismItems.HDPE_REINFORCED_ELYTRA, Mekanism.rl("broken"), (stack, world, entity, seed) -> {
            boolean canFly = MekanismItems.HDPE_REINFORCED_ELYTRA.get().isFlyEnabled(stack);
            return canFly ? 0.0F : 1.0F;
        });

        ModelLoadingPlugin.register(new MekanismModelLoadingPlugin());
        ModelBakingCompletedEvent.EVENT.register(MekanismModelCache.INSTANCE::onBake);
        TextureAtlasStitchEvent.EVENT.register(MekanismRenderer::onStitch);

        addCustomModel(MekanismBlocks.QIO_DRIVE_ARRAY, (orig) -> new DriveArrayBakedModel(orig));
        addCustomModel(MekanismBlocks.DIGITAL_MINER, (orig) -> new DigitalMinerBakedModel(orig));

        addLitModel(MekanismItems.MEKA_TOOL);

        registerKeybindings();
        registerLayer();

        registerClientReloadListeners();

        registerContainers();
        registerParticleFactories();
        registerBlockColorHandlers();
        registerItemColorHandlers();

        registerFluidRenderProperties();
        registerRenderers();
        registerItemRenderers();
        registerRenderTypes();

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(ClientRegistration::addLayers);
        CoreShaderRegistrationCallback.EVENT.register(MekanismShaders::registerShaders);
    }


    public static void registerKeybindings() {
        MekanismKeyHandler.registerKeybindings();
    }

    public static void registerRenderers() {
        //Register entity rendering handlers
        EntityRendererRegistry.register(MekanismEntityTypes.ROBIT.get(), RenderRobit::new);
        EntityRendererRegistry.register(MekanismEntityTypes.FLAME.get(), RenderFlame::new);

        //Register TileEntityRenderers
        ClientRegistrationUtil.bindTileEntityRenderer(RenderThermoelectricBoiler::new, MekanismTileEntityTypes.BOILER_CASING, MekanismTileEntityTypes.BOILER_VALVE);
        ClientRegistrationUtil.bindTileEntityRenderer(RenderDynamicTank::new, MekanismTileEntityTypes.DYNAMIC_TANK, MekanismTileEntityTypes.DYNAMIC_VALVE);
        BlockEntityRenderers.register(MekanismTileEntityTypes.DIGITAL_MINER.get(), RenderDigitalMiner::new);
        BlockEntityRenderers.register(MekanismTileEntityTypes.DIMENSIONAL_STABILIZER.get(), RenderDimensionalStabilizer::new);
        BlockEntityRenderers.register(MekanismTileEntityTypes.PERSONAL_CHEST.get(), RenderPersonalChest::new);
        BlockEntityRenderers.register(MekanismTileEntityTypes.NUTRITIONAL_LIQUIFIER.get(), RenderNutritionalLiquifier::new);
        BlockEntityRenderers.register(MekanismTileEntityTypes.PIGMENT_MIXER.get(), RenderPigmentMixer::new);
        BlockEntityRenderers.register(MekanismTileEntityTypes.SEISMIC_VIBRATOR.get(), RenderSeismicVibrator::new);
        BlockEntityRenderers.register(MekanismTileEntityTypes.TELEPORTER.get(), RenderTeleporter::new);
        BlockEntityRenderers.register(MekanismTileEntityTypes.THERMAL_EVAPORATION_CONTROLLER.get(), RenderThermalEvaporationPlant::new);
        BlockEntityRenderers.register(MekanismTileEntityTypes.INDUSTRIAL_ALARM.get(), RenderIndustrialAlarm::new);

        ClientRegistrationUtil.bindTileEntityRenderer(RenderSPS::new, MekanismTileEntityTypes.SPS_CASING, MekanismTileEntityTypes.SPS_PORT);
        ClientRegistrationUtil.bindTileEntityRenderer(RenderBin::new, MekanismTileEntityTypes.BASIC_BIN, MekanismTileEntityTypes.ADVANCED_BIN, MekanismTileEntityTypes.ELITE_BIN,
                MekanismTileEntityTypes.ULTIMATE_BIN, MekanismTileEntityTypes.CREATIVE_BIN);
        ClientRegistrationUtil.bindTileEntityRenderer(RenderEnergyCube::new, MekanismTileEntityTypes.BASIC_ENERGY_CUBE, MekanismTileEntityTypes.ADVANCED_ENERGY_CUBE,
                MekanismTileEntityTypes.ELITE_ENERGY_CUBE, MekanismTileEntityTypes.ULTIMATE_ENERGY_CUBE, MekanismTileEntityTypes.CREATIVE_ENERGY_CUBE);
        ClientRegistrationUtil.bindTileEntityRenderer(RenderFluidTank::new, MekanismTileEntityTypes.BASIC_FLUID_TANK, MekanismTileEntityTypes.ADVANCED_FLUID_TANK,
                MekanismTileEntityTypes.ELITE_FLUID_TANK, MekanismTileEntityTypes.ULTIMATE_FLUID_TANK, MekanismTileEntityTypes.CREATIVE_FLUID_TANK);
        //Transmitters
        ClientRegistrationUtil.bindTileEntityRenderer(RenderLogisticalTransporter::new, MekanismTileEntityTypes.RESTRICTIVE_TRANSPORTER,
                MekanismTileEntityTypes.DIVERSION_TRANSPORTER, MekanismTileEntityTypes.BASIC_LOGISTICAL_TRANSPORTER, MekanismTileEntityTypes.ADVANCED_LOGISTICAL_TRANSPORTER,
                MekanismTileEntityTypes.ELITE_LOGISTICAL_TRANSPORTER, MekanismTileEntityTypes.ULTIMATE_LOGISTICAL_TRANSPORTER);
        ClientRegistrationUtil.bindTileEntityRenderer(RenderMechanicalPipe::new, MekanismTileEntityTypes.BASIC_MECHANICAL_PIPE,
                MekanismTileEntityTypes.ADVANCED_MECHANICAL_PIPE, MekanismTileEntityTypes.ELITE_MECHANICAL_PIPE, MekanismTileEntityTypes.ULTIMATE_MECHANICAL_PIPE);
        ClientRegistrationUtil.bindTileEntityRenderer(RenderPressurizedTube::new, MekanismTileEntityTypes.BASIC_PRESSURIZED_TUBE,
                MekanismTileEntityTypes.ADVANCED_PRESSURIZED_TUBE, MekanismTileEntityTypes.ELITE_PRESSURIZED_TUBE, MekanismTileEntityTypes.ULTIMATE_PRESSURIZED_TUBE);
        ClientRegistrationUtil.bindTileEntityRenderer(RenderUniversalCable::new, MekanismTileEntityTypes.BASIC_UNIVERSAL_CABLE,
                MekanismTileEntityTypes.ADVANCED_UNIVERSAL_CABLE, MekanismTileEntityTypes.ELITE_UNIVERSAL_CABLE, MekanismTileEntityTypes.ULTIMATE_UNIVERSAL_CABLE);
        ClientRegistrationUtil.bindTileEntityRenderer(RenderThermodynamicConductor::new, MekanismTileEntityTypes.BASIC_THERMODYNAMIC_CONDUCTOR,
                MekanismTileEntityTypes.ADVANCED_THERMODYNAMIC_CONDUCTOR, MekanismTileEntityTypes.ELITE_THERMODYNAMIC_CONDUCTOR, MekanismTileEntityTypes.ULTIMATE_THERMODYNAMIC_CONDUCTOR);
    }

    public static void registerRenderTypes() {
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.BASIC_FLUID_TANK.getBlock(), RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ADVANCED_FLUID_TANK.getBlock(), RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ELITE_FLUID_TANK.getBlock(), RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ULTIMATE_FLUID_TANK.getBlock(), RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.CREATIVE_FLUID_TANK.getBlock(), RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.BASIC_ENERGY_CUBE.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ADVANCED_ENERGY_CUBE.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ELITE_ENERGY_CUBE.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ULTIMATE_ENERGY_CUBE.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.CREATIVE_ENERGY_CUBE.getBlock(), RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.CHARGEPAD.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.CHEMICAL_INFUSER.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.CHEMICAL_OXIDIZER.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.CHEMICAL_WASHER.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.DIGITAL_MINER.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ELECTRIC_PUMP.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ELECTROLYTIC_SEPARATOR.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ISOTOPIC_CENTRIFUGE.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.LASER.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.LOGISTICAL_SORTER.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.MODIFICATION_STATION.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.PIGMENT_MIXER.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.RADIOACTIVE_WASTE_BARREL.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.RESISTIVE_HEATER.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ROTARY_CONDENSENTRATOR.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.SEISMIC_VIBRATOR.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.STRUCTURAL_GLASS.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.SUPERCHARGED_COIL.getBlock(), RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.BASIC_INDUCTION_CELL.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ADVANCED_INDUCTION_CELL.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ELITE_INDUCTION_CELL.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ULTIMATE_INDUCTION_CELL.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.BASIC_INDUCTION_PROVIDER.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ADVANCED_INDUCTION_PROVIDER.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ELITE_INDUCTION_PROVIDER.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.ULTIMATE_INDUCTION_PROVIDER.getBlock(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MekanismBlocks.INDUCTION_PORT.getBlock(), RenderType.cutout());

        for(BlockRegistryObject<?,?> block : MekanismBlocks.getFactoryBlocks()) {
            BlockRenderLayerMap.INSTANCE.putBlock(block.getBlock(), RenderType.cutout());
        }
    }

    public static void registerLayer() {
        EntityModelLayerRegistry.registerModelLayer(ModelJetpack.JETPACK_LAYER, ModelJetpack::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(ModelArmoredJetpack.ARMORED_JETPACK_LAYER, ModelArmoredJetpack::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(ModelAtomicDisassembler.DISASSEMBLER_LAYER, ModelAtomicDisassembler::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(ModelEnergyCore.CORE_LAYER, ModelEnergyCore::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(ModelFlamethrower.FLAMETHROWER_LAYER, ModelFlamethrower::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(ModelArmoredFreeRunners.ARMORED_FREE_RUNNER_LAYER, ModelArmoredFreeRunners::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(ModelFreeRunners.FREE_RUNNER_LAYER, ModelFreeRunners::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(ModelIndustrialAlarm.ALARM_LAYER, ModelIndustrialAlarm::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(ModelScubaMask.MASK_LAYER, ModelScubaMask::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(ModelScubaTank.TANK_LAYER, ModelScubaTank::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(ModelTransporterBox.BOX_LAYER, ModelTransporterBox::createLayerDefinition);
    }

    public static void registerItemRenderers() {
        ClientRegistrationUtil.registerISTER(RenderEnergyCubeItem.RENDERER, MekanismBlocks.BASIC_ENERGY_CUBE, MekanismBlocks.ADVANCED_ENERGY_CUBE, MekanismBlocks.ELITE_ENERGY_CUBE, MekanismBlocks.ULTIMATE_ENERGY_CUBE);
        ClientRegistrationUtil.registerISTER(RenderJetpack.ARMORED_RENDERER, MekanismItems.ARMORED_JETPACK);
        ClientRegistrationUtil.registerISTER(RenderAtomicDisassembler.RENDERER, MekanismItems.ATOMIC_DISASSEMBLER);
        ClientRegistrationUtil.registerISTER(RenderFlameThrower.RENDERER, MekanismItems.FLAMETHROWER);
        ClientRegistrationUtil.registerISTER(RenderFreeRunners.RENDERER, MekanismItems.FREE_RUNNERS);
        ClientRegistrationUtil.registerISTER(RenderFreeRunners.ARMORED_RENDERER, MekanismItems.ARMORED_FREE_RUNNERS);
        ClientRegistrationUtil.registerISTER(RenderJetpack.RENDERER, MekanismItems.JETPACK);
        ClientRegistrationUtil.registerISTER(RenderScubaMask.RENDERER, MekanismItems.SCUBA_MASK);
        ClientRegistrationUtil.registerISTER(RenderScubaTank.RENDERER, MekanismItems.SCUBA_TANK);
    }

    public static void registerClientReloadListeners() {
        ResourceManagerHelper clientResource = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);

        //ISTERs
        clientResource.registerReloadListener(RenderEnergyCubeItem.RENDERER);
        clientResource.registerReloadListener(RenderJetpack.ARMORED_RENDERER);
        clientResource.registerReloadListener(RenderAtomicDisassembler.RENDERER);
        clientResource.registerReloadListener(RenderFlameThrower.RENDERER);
        clientResource.registerReloadListener(RenderFreeRunners.RENDERER);
        clientResource.registerReloadListener(RenderFreeRunners.ARMORED_RENDERER);
        clientResource.registerReloadListener(RenderJetpack.RENDERER);
        clientResource.registerReloadListener(RenderScubaMask.RENDERER);
        clientResource.registerReloadListener(RenderScubaTank.RENDERER);
        //Custom Armor
        clientResource.registerReloadListener(JetpackArmor.ARMORED_JETPACK);
        clientResource.registerReloadListener(JetpackArmor.JETPACK);
        clientResource.registerReloadListener(FreeRunnerArmor.ARMORED_FREE_RUNNERS);
        clientResource.registerReloadListener(FreeRunnerArmor.FREE_RUNNERS);
        clientResource.registerReloadListener(ScubaMaskArmor.SCUBA_MASK);
        clientResource.registerReloadListener(ScubaTankArmor.SCUBA_TANK);
    }

    public static void registerContainers() {
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.MODULE_TWEAKER, GuiModuleTweaker::new);

        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.DICTIONARY, GuiDictionary::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.PORTABLE_TELEPORTER, GuiPortableTeleporter::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.SEISMIC_READER, GuiSeismicReader::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.QIO_FREQUENCY_SELECT_ITEM, GuiQIOItemFrequencySelect::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.PORTABLE_QIO_DASHBOARD, GuiPortableQIODashboard::new);

        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.MAIN_ROBIT, GuiRobitMain::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.INVENTORY_ROBIT, GuiRobitInventory::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.SMELTING_ROBIT, GuiRobitSmelting::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.CRAFTING_ROBIT, GuiRobitCrafting::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.REPAIR_ROBIT, GuiRobitRepair::new);

        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.CHEMICAL_CRYSTALLIZER, GuiChemicalCrystallizer::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.CHEMICAL_DISSOLUTION_CHAMBER, GuiChemicalDissolutionChamber::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.CHEMICAL_INFUSER, GuiChemicalInfuser::new);
        ClientRegistrationUtil.registerAdvancedElectricScreen(MekanismContainerTypes.CHEMICAL_INJECTION_CHAMBER);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.CHEMICAL_OXIDIZER, GuiChemicalOxidizer::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.CHEMICAL_WASHER, GuiChemicalWasher::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.COMBINER, GuiCombiner::new);
        ClientRegistrationUtil.registerElectricScreen(MekanismContainerTypes.CRUSHER);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.DIGITAL_MINER, GuiDigitalMiner::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.DYNAMIC_TANK, GuiDynamicTank::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.ELECTRIC_PUMP, GuiElectricPump::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.ELECTROLYTIC_SEPARATOR, GuiElectrolyticSeparator::new);
        ClientRegistrationUtil.registerElectricScreen(MekanismContainerTypes.ENERGIZED_SMELTER);
        ClientRegistrationUtil.registerElectricScreen(MekanismContainerTypes.ENRICHMENT_CHAMBER);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.FLUIDIC_PLENISHER, GuiFluidicPlenisher::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.FORMULAIC_ASSEMBLICATOR, GuiFormulaicAssemblicator::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.FUELWOOD_HEATER, GuiFuelwoodHeater::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.LASER_AMPLIFIER, GuiLaserAmplifier::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.LASER_TRACTOR_BEAM, GuiLaserTractorBeam::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.METALLURGIC_INFUSER, GuiMetallurgicInfuser::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.OREDICTIONIFICATOR, GuiOredictionificator::new);
        ClientRegistrationUtil.registerAdvancedElectricScreen(MekanismContainerTypes.OSMIUM_COMPRESSOR);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.PRECISION_SAWMILL, GuiPrecisionSawmill::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.PRESSURIZED_REACTION_CHAMBER, GuiPRC::new);
        ClientRegistrationUtil.registerAdvancedElectricScreen(MekanismContainerTypes.PURIFICATION_CHAMBER);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.QUANTUM_ENTANGLOPORTER, GuiQuantumEntangloporter::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.RESISTIVE_HEATER, GuiResistiveHeater::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.ROTARY_CONDENSENTRATOR, GuiRotaryCondensentrator::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.SECURITY_DESK, GuiSecurityDesk::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.MODIFICATION_STATION, GuiModificationStation::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.ISOTOPIC_CENTRIFUGE, GuiIsotopicCentrifuge::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.NUTRITIONAL_LIQUIFIER, GuiNutritionalLiquifier::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.ANTIPROTONIC_NUCLEOSYNTHESIZER, GuiAntiprotonicNucleosynthesizer::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.PIGMENT_EXTRACTOR, GuiPigmentExtractor::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.PIGMENT_MIXER, GuiPigmentMixer::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.PAINTING_MACHINE, GuiPaintingMachine::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.SEISMIC_VIBRATOR, GuiSeismicVibrator::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.SOLAR_NEUTRON_ACTIVATOR, GuiSolarNeutronActivator::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.TELEPORTER, GuiTeleporter::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.THERMAL_EVAPORATION_CONTROLLER, GuiThermalEvaporationController::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.QIO_DRIVE_ARRAY, GuiQIODriveArray::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.QIO_DASHBOARD, GuiQIODashboard::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.QIO_IMPORTER, GuiQIOImporter::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.QIO_EXPORTER, GuiQIOExporter::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.QIO_REDSTONE_ADAPTER, GuiQIORedstoneAdapter::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.SPS, GuiSPS::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.DIMENSIONAL_STABILIZER, GuiDimensionalStabilizer::new);

        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.FACTORY, GuiFactory::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.CHEMICAL_TANK, GuiChemicalTank::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.FLUID_TANK, GuiFluidTank::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.ENERGY_CUBE, GuiEnergyCube::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.INDUCTION_MATRIX, GuiInductionMatrix::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.THERMOELECTRIC_BOILER, GuiThermoelectricBoiler::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.PERSONAL_STORAGE_ITEM, GuiPersonalStorageItem::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.PERSONAL_STORAGE_BLOCK, GuiPersonalStorageTile::new);

        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.DIGITAL_MINER_CONFIG, GuiDigitalMinerConfig::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.LOGISTICAL_SORTER, GuiLogisticalSorter::new);

        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.QIO_FREQUENCY_SELECT_TILE, GuiQIOTileFrequencySelect::new);

        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.BOILER_STATS, GuiBoilerStats::new);
        ClientRegistrationUtil.registerScreen(MekanismContainerTypes.MATRIX_STATS, GuiMatrixStats::new);
    }

    public static BakedModel onModelBake(BakedModel model, ModelModifier.AfterBake.Context context) {
        ResourceLocation rl = context.id();
        CustomModelRegistryObject obj = customModels.get(new ResourceLocation(rl.getNamespace(), rl.getPath()));
        model = obj == null ? model : obj.createModel(model);
        return model;
    }

    public static void registerParticleFactories() {
        ParticleFactoryRegistry.getInstance().register(MekanismParticleTypes.LASER.get(), LaserParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(MekanismParticleTypes.JETPACK_FLAME.get(), JetpackFlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(MekanismParticleTypes.JETPACK_SMOKE.get(), JetpackSmokeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(MekanismParticleTypes.SCUBA_BUBBLE.get(), ScubaBubbleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(MekanismParticleTypes.RADIATION.get(), RadiationParticle.Factory::new);
    }

    public static void registerBlockColorHandlers() {
        ClientRegistrationUtil.registerBlockColorHandler((state, world, pos, tintIndex) -> {
                    if (tintIndex == 1) {
                        BaseTier tier = Attribute.getBaseTier(state.getBlock());
                        if (tier != null) {
                            return MekanismRenderer.getColorARGB(tier, 1);
                        }
                    }
                    return -1;
                }, MekanismBlocks.BASIC_FLUID_TANK, MekanismBlocks.ADVANCED_FLUID_TANK, MekanismBlocks.ELITE_FLUID_TANK, MekanismBlocks.ULTIMATE_FLUID_TANK,
                MekanismBlocks.CREATIVE_FLUID_TANK);
        ClientRegistrationUtil.registerBlockColorHandler((state, world, pos, tintIndex) -> {
                    if (pos != null) {
                        TileEntityQIOComponent tile = WorldUtils.getTileEntity(TileEntityQIOComponent.class, world, pos);
                        if (tile != null) {
                            EnumColor color = tile.getColor();
                            return color == null ? -1 : MekanismRenderer.getColorARGB(color, 1);
                        }
                    }
                    return -1;
                }, MekanismBlocks.QIO_DRIVE_ARRAY, MekanismBlocks.QIO_DASHBOARD, MekanismBlocks.QIO_IMPORTER, MekanismBlocks.QIO_EXPORTER,
                MekanismBlocks.QIO_REDSTONE_ADAPTER);
        ClientRegistrationUtil.registerBlockColorHandler((state, world, pos, tintIndex) -> {
                    if (tintIndex == 1 && pos != null) {
                        TileEntityLogisticalTransporter transporter = WorldUtils.getTileEntity(TileEntityLogisticalTransporter.class, world, pos);
                        if (transporter != null) {
                            EnumColor renderColor = transporter.getTransmitter().getColor();
                            if (renderColor != null) {
                                return MekanismRenderer.getColorARGB(renderColor, 1);
                            }
                        }
                    }
                    return -1;
                }, MekanismBlocks.BASIC_LOGISTICAL_TRANSPORTER, MekanismBlocks.ADVANCED_LOGISTICAL_TRANSPORTER, MekanismBlocks.ELITE_LOGISTICAL_TRANSPORTER,
                MekanismBlocks.ULTIMATE_LOGISTICAL_TRANSPORTER);
        for (Map.Entry<IResource, BlockRegistryObject<?, ?>> entry : MekanismBlocks.PROCESSED_RESOURCE_BLOCKS.entrySet()) {
            if (entry.getKey() instanceof PrimaryResource primaryResource) {
                int tint = primaryResource.getTint();
                ClientRegistrationUtil.registerBlockColorHandler((state, world, pos, index) -> index == 1 ? tint : -1, entry.getValue());
            }
        }
    }

    public static void registerItemColorHandlers() {
        ClientRegistrationUtil.registerItemColorHandler((stack, tintIndex) -> {
                  Item item = stack.getItem();
                  if (tintIndex == 1 && item instanceof ItemBlockFluidTank tank) {
                      return MekanismRenderer.getColorARGB(tank.getTier().getBaseTier(), 1);
                  }
                  return -1;
              }, MekanismBlocks.BASIC_FLUID_TANK, MekanismBlocks.ADVANCED_FLUID_TANK, MekanismBlocks.ELITE_FLUID_TANK, MekanismBlocks.ULTIMATE_FLUID_TANK,
              MekanismBlocks.CREATIVE_FLUID_TANK);
        ClientRegistrationUtil.registerBucketColorHandler(MekanismFluids.FLUIDS);
        for (Cell<ResourceType, PrimaryResource, ItemRegistryObject<Item>> item : MekanismItems.PROCESSED_RESOURCES.cellSet()) {
            int tint = item.getColumnKey().getTint();
            ClientRegistrationUtil.registerItemColorHandler((stack, index) -> index == 1 ? tint : -1, item.getValue());
        }
        ClientRegistrationUtil.registerIColoredItemHandler(MekanismItems.PORTABLE_QIO_DASHBOARD, MekanismBlocks.QIO_DRIVE_ARRAY, MekanismBlocks.QIO_DASHBOARD,
              MekanismBlocks.QIO_IMPORTER, MekanismBlocks.QIO_EXPORTER, MekanismBlocks.QIO_REDSTONE_ADAPTER);

        ClientRegistrationUtil.registerItemColorHandler((stack, index) -> {
            if (index == 1) {
                IModule<ModuleColorModulationUnit> colorModulation = IModuleHelper.INSTANCE.load(stack, MekanismModules.COLOR_MODULATION_UNIT);
                if (colorModulation != null) {
                    Color color = colorModulation.getCustomInstance().getColor();
                    //Calculate actual tint from alpha in the same way we do in the shader. Ideally we would expose this somehow for resource packs
                    // like is done for the shader but oh well
                    color = Color.rgbd(color.ad() * color.rd() + (1.0 - color.ad()), color.ad() * color.gd() + (1.0 - color.ad()),
                          color.ad() * color.bd() + (1.0 - color.ad()));
                    return color.argb();
                }
            }
            return -1;
        }, MekanismItems.MEKASUIT_HELMET, MekanismItems.MEKASUIT_BODYARMOR, MekanismItems.MEKASUIT_PANTS, MekanismItems.MEKASUIT_BOOTS);

        for (Map.Entry<IResource, BlockRegistryObject<?, ?>> entry : MekanismBlocks.PROCESSED_RESOURCE_BLOCKS.entrySet()) {
            if (entry.getKey() instanceof PrimaryResource primaryResource) {
                int tint = primaryResource.getTint();
                ClientRegistrationUtil.registerItemColorHandler((stack, index) -> index == 1 ? tint : -1, entry.getValue());
            }
        }
    }

    public static void registerFluidRenderProperties() {
        for(FluidRegistryObject<?,?,?,?> object : MekanismFluids.FLUIDS.getAllFluids()) {
            FluidDeferredRegister.FluidTypeRenderProperties properties = object.getRenderProperties();
            FluidRenderHandlerRegistry.INSTANCE.register(object.getStillFluid(), properties.createRenderHandler());
            FluidRenderHandlerRegistry.INSTANCE.register(object.getFlowingFluid(), properties.createRenderHandler());
        }
    }

//    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
//        event.register(MekanismItems.MEKASUIT_HELMET, MekaSuitBarDecorator.INSTANCE);
//        event.register(MekanismItems.MEKASUIT_BODYARMOR, MekaSuitBarDecorator.INSTANCE);
//        TransmitterTypeDecorator.registerDecorators(event, MekanismBlocks.BASIC_PRESSURIZED_TUBE, MekanismBlocks.ADVANCED_PRESSURIZED_TUBE,
//              MekanismBlocks.ELITE_PRESSURIZED_TUBE, MekanismBlocks.ULTIMATE_PRESSURIZED_TUBE, MekanismBlocks.BASIC_THERMODYNAMIC_CONDUCTOR,
//              MekanismBlocks.ADVANCED_THERMODYNAMIC_CONDUCTOR, MekanismBlocks.ELITE_THERMODYNAMIC_CONDUCTOR, MekanismBlocks.ULTIMATE_THERMODYNAMIC_CONDUCTOR,
//              MekanismBlocks.BASIC_UNIVERSAL_CABLE, MekanismBlocks.ADVANCED_UNIVERSAL_CABLE, MekanismBlocks.ELITE_UNIVERSAL_CABLE, MekanismBlocks.ULTIMATE_UNIVERSAL_CABLE);
//    }

    public static void addLayers(EntityType<?> type, EntityRenderer renderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {
        //Add our own custom armor layer to the various player renderers
        if (!(renderer instanceof LivingEntityRenderer<?,?> livingRenderer)) {
            return;
        }

        addCustomLayers(
                type,
                (LivingEntityRenderer<LivingEntity,HumanoidModel<LivingEntity>>)livingRenderer,
                context.getModelManager()
        );
    }

    private static <T extends LivingEntity, M extends HumanoidModel<T>> void addCustomLayers(EntityType<?> type, @Nullable LivingEntityRenderer<T, M> renderer,
          ModelManager modelManager) {
        if (renderer == null) {
            return;
        }
        HumanoidArmorLayer<T, M, ?> bipedArmorLayer = null;
        boolean hasElytra = false;
        for (RenderLayer<T, M> layerRenderer : renderer.layers) {
            //Validate against the layer render being null, as it seems like some mods do stupid things and add in null layers
            if (layerRenderer != null) {
                //Only allow an exact class match, so we don't add to modded entities that only have a modded extended armor or elytra layer
                Class<?> layerClass = layerRenderer.getClass();
                if (layerClass == HumanoidArmorLayer.class) {
                    bipedArmorLayer = (HumanoidArmorLayer<T, M, ?>) layerRenderer;
                    if (hasElytra) {
                        break;
                    }
                } else if (layerClass == ElytraLayer.class) {
                    hasElytra = true;
                    if (bipedArmorLayer != null) {
                        break;
                    }
                }
            }
        }
        if (bipedArmorLayer != null) {
            renderer.addLayer(new MekanismArmorLayer<>(renderer, bipedArmorLayer.innerModel, bipedArmorLayer.outerModel, modelManager));
            Mekanism.logger.debug("Added Mekanism Armor Layer to entity of type: {}", RegistryUtils.getName(type));
        }
        if (hasElytra) {
            renderer.addLayer(new MekanismElytraLayer<>(renderer, Minecraft.getInstance().getEntityModels()));
            Mekanism.logger.debug("Added Mekanism Elytra Layer to entity of type: {}", RegistryUtils.getName(type));
        }
    }

    public static void addCustomModel(IItemProvider provider, CustomModelRegistryObject object) {
        customModels.put(provider.getRegistryName(), object);
    }

    public static void addLitModel(IItemProvider... providers) {
        for (IItemProvider provider : providers) {
            addCustomModel(provider, (orig) -> lightBakedModel(orig));
        }
    }

    private static BakedModel lightBakedModel(BakedModel orig) {
//        if (orig instanceof SeparateTransformsModel.Baked separatePerspectiveModel) {
//            //Transform inner components of the separate perspective model and then return the original model
//            SEPARATE_PERSPECTIVE_BASE_MODEL.transformValue(separatePerspectiveModel, Objects::nonNull, ClientRegistration::lightBakedModel);
//            SEPARATE_PERSPECTIVE_PERSPECTIVES.transformValue(separatePerspectiveModel, v -> !v.isEmpty(), org -> ImmutableMap.copyOf(Maps.transformValues(org,
//                  ClientRegistration::lightBakedModel)));
//            return orig;
//        }
        return new LightedBakedModel(orig);
    }

    @FunctionalInterface
    public interface CustomModelRegistryObject {

        BakedModel createModel(BakedModel original);
    }
}