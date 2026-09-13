package mekanism.common;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import mekanism.api.MekanismAPI;
import mekanism.client.lang.MekanismLangProvider;
import mekanism.client.model.MekanismItemModelProvider;
import mekanism.client.sound.MekanismSoundProvider;
import mekanism.client.state.MekanismBlockStateProvider;
import mekanism.client.texture.MekanismSpriteSourceProvider;
import mekanism.client.texture.PrideRobitTextureProvider;
import mekanism.common.advancements.MekanismAdvancementProvider;
import mekanism.common.advancements.MekanismCriteriaTriggers;
import mekanism.common.config.MekanismConfig;
import mekanism.common.loot.table.MekanismBlockLootTables;
import mekanism.common.recipe.impl.MekanismRecipeProvider;
import mekanism.common.registries.MekanismDatapackRegistryProvider;
import mekanism.common.tag.MekanismTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.Util;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

public class MekanismDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        MekanismDatapackRegistryProvider drProvider = pack.addProvider(MekanismDatapackRegistryProvider::new);
        //Bootstrap our advancement triggers as common setup doesn't run
        MekanismCriteriaTriggers.init();
//        pack.addProvider((FabricDataOutput output) -> new BasePackMetadataGenerator(output, MekanismLang.PACK_DESCRIPTION));
        //Client side data generators
        addProvider(pack, MekanismLangProvider::new);
        pack.addProvider(PrideRobitTextureProvider::new);
        pack.addProvider((FabricDataOutput output) -> new MekanismSoundProvider(output));
        pack.addProvider((FabricDataOutput output) -> new MekanismSpriteSourceProvider(output));
        pack.addProvider(MekanismItemModelProvider::new);
        pack.addProvider(MekanismBlockStateProvider::new);
        //Server side data generators
        pack.addProvider(MekanismTagProvider::new);

        //Loot Tables
        pack.addProvider(MekanismBlockLootTables::new);
        //pack.addProvider(MekanismEntityLootTables::new);

        MekanismRecipeProvider recipeProvider = pack.addProvider(MekanismRecipeProvider::new);
        pack.addProvider(MekanismAdvancementProvider::new);
//        pack.addProvider(MekanismCustomConversions::new);
//        pack.addProvider(new MekanismCrTExampleProvider(output, existingFileHelper));
//        pack.addProvider(new ComputerHelpProvider(output, Mekanism.MODID));
        //Data generator to help with persisting data when porting across MC versions when optional deps aren't updated yet
        // DO NOT ADD OTHERS AFTER THIS ONE
        pack.addProvider((FabricDataOutput output) -> new PersistingDisabledProvidersProvider(output, recipeProvider.getDisabledCompats()));
    }

    //@SubscribeEvent
    public static void gatherData(/*GatherDataEvent event*/) {
        bootstrapConfigs(Mekanism.MODID);
//        DataGenerator gen = event.getGenerator();
//        PackOutput output = gen.getPackOutput();
//        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
//        MekanismDatapackRegistryProvider drProvider = new MekanismDatapackRegistryProvider(output, event.getLookupProvider());
//        CompletableFuture<HolderLookup.Provider> lookupProvider = drProvider.getRegistryProvider();
        //Bootstrap our advancement triggers as common setup doesn't run
//        MekanismCriteriaTriggers.init();
//        gen.addProvider(true, new BasePackMetadataGenerator(output, MekanismLang.PACK_DESCRIPTION));
        //Client side data generators
//        addProvider(gen, event.includeClient(), MekanismLangProvider::new);
//        gen.addProvider(event.includeClient(), new PrideRobitTextureProvider(output));
//        gen.addProvider(event.includeClient(), new MekanismSoundProvider(output, existingFileHelper));
//        gen.addProvider(event.includeClient(), new MekanismSpriteSourceProvider(output, existingFileHelper));
//        gen.addProvider(event.includeClient(), new MekanismItemModelProvider(output, existingFileHelper));
//        gen.addProvider(event.includeClient(), new MekanismBlockStateProvider(output, existingFileHelper));
//        //Server side data generators
//        gen.addProvider(event.includeServer(), new MekanismTagProvider(output, lookupProvider, existingFileHelper));
//        addProvider(gen, event.includeServer(), MekanismLootProvider::new);
//        gen.addProvider(event.includeServer(), drProvider);
//        MekanismRecipeProvider recipeProvider = new MekanismRecipeProvider(output, existingFileHelper);
//        gen.addProvider(event.includeServer(), recipeProvider);
//        gen.addProvider(event.includeServer(), new MekanismAdvancementProvider(output, existingFileHelper));
//        gen.addProvider(event.includeServer(), new MekanismCustomConversions(output, lookupProvider));
//        gen.addProvider(event.includeServer(), new MekanismCrTExampleProvider(output, existingFileHelper));
//        gen.addProvider(event.includeServer(), new ComputerHelpProvider(output, Mekanism.MODID));
//        //Data generator to help with persisting data when porting across MC versions when optional deps aren't updated yet
//        // DO NOT ADD OTHERS AFTER THIS ONE
//        gen.addProvider(true, new PersistingDisabledProvidersProvider(output, recipeProvider.getDisabledCompats()));
    }

    public static <PROVIDER extends DataProvider> void addProvider(FabricDataGenerator.Pack pack, FabricDataGenerator.Pack.Factory<PROVIDER> factory) {
        pack.addProvider(factory);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(MekanismAPI.ROBIT_SKIN_REGISTRY_NAME, (unused) -> {});
    }

    /**
     * Used to bootstrap configs to their default values so that if we are querying if things exist we don't have issues with it happening to early or in cases we have
     * fake tiles.
     */
    public static void bootstrapConfigs(String modid) {
        MekanismConfig.registerClientConfig();
        MekanismConfig.registerCommonConfigs();
    }

    /**
     * Basically a copy of {@link DataProvider#saveStable(CachedOutput, JsonElement, Path)} but it takes a consumer of the output stream instead of serializes json using GSON.
     * Use it to write arbitrary files.
     */
    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public static CompletableFuture<?> save(CachedOutput cache, IOConsumer<OutputStream> osConsumer, Path path) {
        return CompletableFuture.runAsync(() -> {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), outputStream)) {
                osConsumer.accept(hashingOutputStream);
                cache.writeIfNeeded(path, outputStream.toByteArray(), hashingOutputStream.hash());
            } catch (IOException ioexception) {
                DataProvider.LOGGER.error("Failed to save file to {}", path, ioexception);
            }
        }, Util.backgroundExecutor());
    }

    @FunctionalInterface
    public interface IOConsumer<T> {
        void accept(T value) throws IOException;
    }
}