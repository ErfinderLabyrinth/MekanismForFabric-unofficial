package mekanism.client.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import mekanism.api.providers.IItemProvider;
import mekanism.common.Mekanism;
import mekanism.common.item.ItemModule;
import mekanism.common.registration.impl.FluidDeferredRegister;
import mekanism.common.registration.impl.FluidRegistryObject;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.util.RegistryUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public abstract class BaseItemModelProvider extends FabricModelProvider {
    //private final FieldReflectionHelper<ModelBuilder, Map<String, String>> MODEL_TEXTURES = new FieldReflectionHelper<>(ModelBuilder.class, "textures", HashMap::new);
//    private static final TrimModelDataHelper<?> TRIM_HELPER = new TrimModelDataHelper<>();
    private final String modid;
    private final FabricDataOutput output;

    protected BaseItemModelProvider(FabricDataOutput output, String modid) {
        super(output);
        this.output = output;
        this.modid = modid;
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        //Unused
    }

    @NotNull
    @Override
    public String getName() {
        return "Item model provider: " + modid;
    }

    public ResourceLocation modLoc(String path) {
        return new ResourceLocation(modid, path);
    }

    public boolean textureExists(ResourceLocation texture) {
        return output.getModContainer().findPath("assets/" + texture.getNamespace() + "/textures/" + texture.getPath() + ".png").isPresent();
        //return output.exists(texture, PackType.CLIENT_RESOURCES, ".png", "textures");
    }

    protected ResourceLocation itemTexture(IItemProvider itemProvider) {
        return new ResourceLocation(modid, "item/" + itemProvider.getName());
    }

    protected void registerGenerated(ItemModelGenerators generators, IItemProvider... itemProviders) {
        for (IItemProvider itemProvider : itemProviders) {
            generated(generators, itemProvider.asItem());
        }
    }

    protected void registerModules(ItemModelGenerators generators, ItemDeferredRegister register) {
        for (IItemProvider itemProvider : register.getAllItems()) {
            Item item = itemProvider.asItem();
            if (item instanceof ItemModule) {
                generated(generators, item);
            }
        }
    }

    protected void registerBuckets(ItemModelGenerators generators, FluidDeferredRegister register) {
        for (FluidRegistryObject<?, ?, ?, ?> fluidRegistryObject : register.getAllFluids()) {
            registerBucket(generators, fluidRegistryObject);
        }
    }

    protected void generated(ItemModelGenerators generators, Item item) {
        generators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
    }

//    protected ItemModelBuilder generated(ItemModelGenerators generators, Item item, ResourceLocation texture) {
//        return generators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);withExistingParent(itemProvider.getName(), "item/generated").texture("layer0", texture);
//    }

    protected void resource(ItemModelGenerators generators, IItemProvider itemProvider, String type) {
        //TODO: Try to come up with a better solution to this. Currently we have an empty texture for layer zero so that we can set
        // the tint only on layer one so that we only end up having the tint show for this fallback texture
        TextureMapping mapping;

        ResourceLocation overlay = new ResourceLocation(modid, "item/" + type + "_overlay");
        if(textureExists(overlay)) {
            mapping = TextureMapping.layered(new ResourceLocation(modid, "item/empty"), new ResourceLocation(modid, "item/" + type), overlay);
        } else {
            mapping = TextureMapping.layered(new ResourceLocation(modid, "item/empty"), new ResourceLocation(modid, "item/" + type));
        }

        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(itemProvider.asItem()), mapping, generators.output);
    }

    protected void registerHandheld(ItemModelGenerators generators, IItemProvider... itemProviders) {
        for (IItemProvider itemProvider : itemProviders) {
            handheld(generators, itemProvider.asItem());
        }
    }

    protected void handheld(ItemModelGenerators generators, Item item) {
        generators.generateFlatItem(item, ModelTemplates.FLAT_HANDHELD_ITEM);
    }

//    protected ItemModelBuilder handheld(IItemProvider itemProvider, ResourceLocation texture) {
//        return withExistingParent(itemProvider.getName(), "item/handheld").texture("layer0", texture);
//    }

    protected void armorWithTrim(ItemModelGenerators generators, ArmorItem armor) {
        generators.generateArmorTrims(armor);
    }

    //Note: This isn't the best way to do this in terms of model file validation, but it works
    protected void registerBucket(ItemModelGenerators generators, FluidRegistryObject<?, ?, ?, ?> fluidRO) {
        generators.generateLayeredItem(ModelLocationUtils.getModelLocation(fluidRO.getBucket()), new ResourceLocation("item/bucket"), Mekanism.rl("item/bucket_overlay"));
        //generated(generators, fluidRO.getBucket());
//        withExistingParent(RegistryUtils.getPath(fluidRO.getBucket()), new ResourceLocation("forge", "item/bucket"))
//              .customLoader(DynamicFluidContainerModelBuilder::begin)
//              .fluid(fluidRO.getStillFluid());
    }

//    private static class TrimModelDataHelper<TMD_CLASS> {
//
//        private final FieldReflectionHelper<ItemModelGenerators, List<TMD_CLASS>> generatedTrimModels = new FieldReflectionHelper<>(ItemModelGenerators.class, "f_265952_", Collections::emptyList);
//        private final FieldReflectionHelper<TMD_CLASS, String> name;
//        private final FieldReflectionHelper<TMD_CLASS, Float> itemModelIndex;
//
//        public TrimModelDataHelper() {
//            Class<TMD_CLASS> tmdClass;
//            try {
//                tmdClass = (Class<TMD_CLASS>) Class.forName("net.minecraft.data.models.ItemModelGenerators$TrimModelData");
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//            name = new FieldReflectionHelper<>(tmdClass, "f_265890_", () -> null);
//            itemModelIndex = new FieldReflectionHelper<>(tmdClass, "f_265849_", () -> null);
//        }
//
//        public void forEachTrim(BiConsumer<String, Float> consumer) {
//            List<TMD_CLASS> trims = generatedTrimModels.getValue(null);
//            for (TMD_CLASS trim : trims) {
//                String trimName = name.getValue(trim);
//                Float modelIndex = itemModelIndex.getValue(trim);
//                consumer.accept(trimName, modelIndex);
//            }
//        }
//    }
}