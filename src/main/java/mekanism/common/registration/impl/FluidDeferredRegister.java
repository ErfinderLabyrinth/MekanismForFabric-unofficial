package mekanism.common.registration.impl;

import mekanism.common.Mekanism;
import mekanism.common.base.IChemicalConstant;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.UnaryOperator;

public class FluidDeferredRegister {

    private static final ResourceLocation OVERLAY = new ResourceLocation("block/water_overlay");
    private static final ResourceLocation RENDER_OVERLAY = new ResourceLocation("misc/underwater");
    private static final ResourceLocation LIQUID = Mekanism.rl("liquid/liquid");
    private static final ResourceLocation LIQUID_FLOW = Mekanism.rl("liquid/liquid_flow");
    //Copy of/based off of vanilla's lava/water bucket dispense behavior
    private static final DispenseItemBehavior BUCKET_DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior() {
        @NotNull
        @Override
        public ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
            Level world = source.getLevel();
            DispensibleContainerItem bucket = (DispensibleContainerItem) stack.getItem();
            BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
            if (bucket.emptyContents(null, world, pos, null)) {
                bucket.checkExtraContent(null, world, stack, pos);
                return new ItemStack(Items.BUCKET);
            }
            return super.execute(source, stack);
        }
    };

    private final List<FluidRegistryObject<?, ?, ?, ?>> allFluids = new ArrayList<>();

    private final Registry<Fluid> fluidRegister;
    private final Registry<Block> blockRegister;
    private final Registry<Item> itemRegister;
    private final String modid;

    public FluidDeferredRegister(String modid) {
        this.modid = modid;
        blockRegister = BuiltInRegistries.BLOCK;
        fluidRegister = BuiltInRegistries.FLUID;
        itemRegister = BuiltInRegistries.ITEM;
    }

    public FluidRegistryObject<MekanismFluid.Source, MekanismFluid.Flowing, LiquidBlock, BucketItem> registerLiquidChemical(IChemicalConstant constants) {
        int density = Math.round(constants.getDensity());
        return register(constants.getName(), properties -> properties
              .temperature(Math.round(constants.getTemperature()))
              .density(density)
              .viscosity(density)
              .lightLevel(constants.getLightLevel()), renderProperties -> renderProperties
              .tint(constants.getColor())
        );
    }

    public FluidRegistryObject<MekanismFluid.Source, MekanismFluid.Flowing, LiquidBlock, BucketItem> register(String name, UnaryOperator<FluidTypeRenderProperties> renderProperties) {
        return register(name, UnaryOperator.identity(), renderProperties);
    }

    public FluidRegistryObject<MekanismFluid.Source, MekanismFluid.Flowing, LiquidBlock, BucketItem> register(String name, UnaryOperator<MekanismFluidVariantAttributeHandler.Properties> properties,
          UnaryOperator<FluidTypeRenderProperties> renderProperties) {
        return register(name, BucketItem::new, properties, renderProperties);
    }

    public <BUCKET extends BucketItem> FluidRegistryObject<MekanismFluid.Source, MekanismFluid.Flowing, LiquidBlock, BUCKET> register(String name, BucketCreator<BUCKET> bucketCreator,
          UnaryOperator<MekanismFluidVariantAttributeHandler.Properties> fluidProperties, UnaryOperator<FluidTypeRenderProperties> renderProperties) {
        return register(name, new MekanismFluidVariantAttributeHandler(fluidProperties.apply(new MekanismFluidVariantAttributeHandler.Properties())), renderProperties.apply(FluidTypeRenderProperties.builder()), bucketCreator);
    }

    public <BUCKET extends BucketItem> FluidRegistryObject<MekanismFluid.Source, MekanismFluid.Flowing, LiquidBlock, BUCKET> register(
            String name,
            FluidVariantAttributeHandler handler,
            FluidTypeRenderProperties renderProperties,
            BucketCreator<BUCKET> bucketCreator
    ) {
        String flowingName = "flowing_" + name;
        String bucketName = name + "_bucket";
        //Set the translation string to the same as the block
        //properties.descriptionId(Util.makeDescriptionId("block", new ResourceLocation(modid, name)));
        //Create the registry object and let the values init to null as before we actually call get on them, we will update the backing values
        FluidRegistryObject<MekanismFluid.Source, MekanismFluid.Flowing, LiquidBlock, BUCKET> fluidRegistryObject = new FluidRegistryObject<>();
        //Pass in suppliers that are wrapped instead of direct references to the registry objects, so that when we update the registry object to
        // point to a new object it gets updated properly.
        //Update the references to objects that are retrieved from the deferred registers
        fluidRegistryObject.updateStill(Registry.register(BuiltInRegistries.FLUID, new ResourceLocation(Mekanism.MODID, name), new MekanismFluid.Source(fluidRegistryObject)));
        fluidRegistryObject.updateFlowing(Registry.register(BuiltInRegistries.FLUID, new ResourceLocation(Mekanism.MODID, flowingName), new MekanismFluid.Flowing(fluidRegistryObject)));
        fluidRegistryObject.updateBucket(Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Mekanism.MODID, bucketName), bucketCreator.create(fluidRegistryObject.getStillFluid(),
              new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET))));
        MapColor color = getClosestColor(renderProperties.color);
        //Note: The block properties used here is a copy of the ones for water
        fluidRegistryObject.updateBlock(Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Mekanism.MODID, name), new LiquidBlock(fluidRegistryObject.getStillFluid(), BlockBehaviour.Properties.of()
              .noCollission().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid().mapColor(color))));
        fluidRegistryObject.updateRenderProperties(renderProperties);
        FluidVariantAttributes.register(fluidRegistryObject.getStillFluid(), handler);
        FluidVariantAttributes.register(fluidRegistryObject.getFlowingFluid(), handler);
        allFluids.add(fluidRegistryObject);
        return fluidRegistryObject;
    }

    private static MapColor getClosestColor(int tint) {
        if (tint == 0xFFFFFFFF) {
            return MapColor.NONE;
        }
        int red = FastColor.ARGB32.red(tint);
        int green = FastColor.ARGB32.green(tint);
        int blue = FastColor.ARGB32.blue(tint);
        MapColor color = MapColor.NONE;
        double minDistance = Double.MAX_VALUE;
        for (MapColor toTest : MapColor.MATERIAL_COLORS) {
            if (toTest != null && toTest != MapColor.NONE) {
                int testRed = FastColor.ARGB32.red(toTest.col);
                int testGreen = FastColor.ARGB32.green(toTest.col);
                int testBlue = FastColor.ARGB32.blue(toTest.col);
                double distanceSquare = perceptualColorDistanceSquared(red, green, blue, testRed, testGreen, testBlue);
                if (distanceSquare < minDistance) {
                    minDistance = distanceSquare;
                    color = toTest;
                }
            }
        }
        return color;
    }

    /**
     * <a href="http://www.compuphase.com/cmetric.htm">Color Metric</a>
     * <a href="http://stackoverflow.com/a/6334454">Stack Overflow</a>
     * Returns 0 for equal colors, nonzero for colors that look different. The return value is farther from 0 the more different the colors look.
     */
    private static double perceptualColorDistanceSquared(int red1, int green1, int blue1, int red2, int green2, int blue2) {
        int redMean = (red1 + red2) >> 1;
        int r = red1 - red2;
        int g = green1 - green2;
        int b = blue1 - blue2;
        return (((512 + redMean) * r * r) >> 8) + 4 * g * g + (((767 - redMean) * b * b) >> 8);
    }

    public List<FluidRegistryObject<?, ?, ?, ?>> getAllFluids() {
        return Collections.unmodifiableList(allFluids);
    }

    public void registerBucketDispenserBehavior() {
        for (FluidRegistryObject<?, ?, ?, ?> fluidRO : getAllFluids()) {
            DispenserBlock.registerBehavior(fluidRO.getBucket(), BUCKET_DISPENSE_BEHAVIOR);
        }
    }

    @FunctionalInterface
    public interface BucketCreator<BUCKET extends BucketItem> {

        BUCKET create(Fluid fluid, Properties builder);
    }

    public static class FluidTypeRenderProperties {

        private ResourceLocation stillTexture = LIQUID;
        private ResourceLocation flowingTexture = LIQUID_FLOW;
        //For now all our fluids use the same "overlay" for being against glass as vanilla water.
        private ResourceLocation overlayTexture = OVERLAY;
        private ResourceLocation renderOverlayTexture = RENDER_OVERLAY;
        private int color = 0xFFFFFFFF;

        private FluidTypeRenderProperties() {
        }

        public static FluidTypeRenderProperties builder() {
            return new FluidTypeRenderProperties();
        }

        public FluidTypeRenderProperties texture(ResourceLocation still, ResourceLocation flowing) {
            this.stillTexture = still;
            this.flowingTexture = flowing;
            return this;
        }

        public FluidTypeRenderProperties texture(ResourceLocation still, ResourceLocation flowing, ResourceLocation overlay) {
            this.stillTexture = still;
            this.flowingTexture = flowing;
            this.overlayTexture = overlay;
            return this;
        }

        public FluidTypeRenderProperties renderOverlay(ResourceLocation renderOverlay) {
            this.renderOverlayTexture = renderOverlay;
            return this;
        }

        public FluidTypeRenderProperties tint(int color) {
            this.color = color;
            return this;
        }

        public int getColor() {
            return color;
        }

        public SimpleFluidRenderHandler createRenderHandler() {
            return new SimpleFluidRenderHandler(stillTexture, flowingTexture, overlayTexture, color);
        }
    }

    public static abstract class MekanismFluid extends FlowingFluid {
        FluidRegistryObject<?,?,?,?> registryObject;

        public MekanismFluid(FluidRegistryObject<?,?,?,?> registryObject) {
            this.registryObject = registryObject;
        }

        @Override
        public Fluid getFlowing() {
            return registryObject.getFlowingFluid();
        }

        @Override
        public Fluid getSource() {
            return registryObject.getFluid();
        }

        @Override
        public Item getBucket() {
            return registryObject.getBucket();
        }

        protected boolean canConvertToSource(Level level) {
            return false;
        }

        protected void beforeDestroyingBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
            BlockEntity blockEntity = blockState.hasBlockEntity() ? levelAccessor.getBlockEntity(blockPos) : null;
            Block.dropResources(blockState, levelAccessor, blockPos, blockEntity);
        }

        public int getSlopeFindDistance(LevelReader levelReader) {
            return 4;
        }

        public BlockState createLegacyBlock(FluidState fluidState) {
            return registryObject.getBlock().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
        }

        public boolean isSame(Fluid fluid) {
            return fluid == registryObject.getFluid() || fluid == registryObject.getFlowingFluid();
        }

        public int getDropOff(LevelReader levelReader) {
            return 1;
        }

        public int getTickDelay(LevelReader levelReader) {
            return 5;
        }

        public boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, Direction direction) {
            return false;
        }

        protected float getExplosionResistance() {
            return 100.0F;
        }

        public Optional<SoundEvent> getPickupSound() {
            return Optional.of(SoundEvents.BUCKET_FILL);
        }

        public static class Source extends MekanismFluid {
            public Source(FluidRegistryObject<?,?,?,?> registryObject) {
                super(registryObject);
            }

            public int getAmount(FluidState fluidState) {
                return 8;
            }

            public boolean isSource(FluidState fluidState) {
                return true;
            }
        }

        public static class Flowing extends MekanismFluid {
            public Flowing(FluidRegistryObject<?,?,?,?> registryObject) {
                super(registryObject);
            }

            protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
                super.createFluidStateDefinition(builder);
                builder.add(LEVEL);
            }

            public int getAmount(FluidState fluidState) {
                return fluidState.getValue(LEVEL);
            }

            public boolean isSource(FluidState fluidState) {
                return false;
            }
        }
    }

    public static class MekanismFluidVariantAttributeHandler implements FluidVariantAttributeHandler {
        private final Properties properties;
        public MekanismFluidVariantAttributeHandler(Properties properties) {
            this.properties = properties;
        }

        public Component getName(FluidVariant fluidVariant) {
            return properties.name.orElseGet(() -> FluidVariantAttributeHandler.super.getName(fluidVariant));
        }

        public int getLuminance(FluidVariant variant) {
            return properties.luminance.isPresent() ? properties.luminance.getAsInt() : FluidVariantAttributeHandler.super.getLuminance(variant);
        }

        public int getTemperature(FluidVariant variant) {
            return properties.temperature.isPresent() ? properties.temperature.getAsInt() : FluidVariantAttributeHandler.super.getTemperature(variant);
        }

        public int getViscosity(FluidVariant variant, @Nullable Level world) {
            return properties.viscosity.isPresent() ? properties.viscosity.getAsInt() : FluidVariantAttributeHandler.super.getViscosity(variant, world);
        }

        public boolean isLighterThanAir(FluidVariant variant) {
            return properties.lighterThanAir.orElseGet(() -> FluidVariantAttributeHandler.super.isLighterThanAir(variant));
        }

        public static class Properties {
            private Optional<Component> name = Optional.empty();
            private OptionalInt luminance = OptionalInt.empty();
            private OptionalInt temperature = OptionalInt.empty();
            private OptionalInt viscosity = OptionalInt.empty();
            private Optional<Boolean> lighterThanAir = Optional.empty();

            public Properties descriptionId(String descriptionId)
            {
                this.name = Optional.of(Component.translatable(descriptionId));
                return this;
            }

            public Properties lightLevel(int lightLevel)
            {
                if (lightLevel < 0 || lightLevel > 15)
                    throw new IllegalArgumentException("The light level should be between [0,15].");
                this.luminance = OptionalInt.of(lightLevel);
                return this;
            }

            public Properties density(int density)
            {
                lighterThanAir = Optional.of(density <= 0);
                return this;
            }

            public Properties temperature(int temperature)
            {
                this.temperature = OptionalInt.of(temperature);
                return this;
            }

            public Properties viscosity(int viscosity)
            {
                if (viscosity < 0)
                    throw new IllegalArgumentException("The viscosity should never be negative.");
                this.viscosity = OptionalInt.of(viscosity);
                return this;
            }
        }
    }

//    public static class MekanismFluidType extends FluidType {
//
//        public final ResourceLocation stillTexture;
//        public final ResourceLocation flowingTexture;
//        public final ResourceLocation overlayTexture;
//        public final ResourceLocation renderOverlayTexture;
//        private final int color;
//
//        public MekanismFluidType(FluidType.Properties properties, FluidTypeRenderProperties renderProperties) {
//            super(properties);
//            this.stillTexture = renderProperties.stillTexture;
//            this.flowingTexture = renderProperties.flowingTexture;
//            this.overlayTexture = renderProperties.overlayTexture;
//            this.renderOverlayTexture = renderProperties.renderOverlayTexture;
//            this.color = renderProperties.color;
//        }
//
//        @Override
//        public boolean isVaporizedOnPlacement(Level level, BlockPos pos, FluidStack stack) {
//            //TODO - 1.19: Decide on this for our fluids for now default to not vaporizing
//            return false;
//        }
//
//        @Override
//        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
//            consumer.accept(new IClientFluidTypeExtensions() {
//                @Override
//                public ResourceLocation getStillTexture() {
//                    return stillTexture;
//                }
//
//                @Override
//                public ResourceLocation getFlowingTexture() {
//                    return flowingTexture;
//                }
//
//                @Override
//                public ResourceLocation getOverlayTexture() {
//                    return overlayTexture;
//                }
//
//                @Nullable
//                @Override
//                public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
//                    return renderOverlayTexture;
//                }
//
//                @Override
//                public int getTintColor() {
//                    return color;
//                }
//            });
//        }
//    }
}