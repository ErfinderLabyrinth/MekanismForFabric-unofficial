package mekanism.client.lang;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import mekanism.api.gear.ModuleData;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.providers.IModuleDataProvider;
import mekanism.api.text.IHasTranslationKey;
import mekanism.client.lang.FormatSplitter.Component;
import mekanism.common.Mekanism;
import mekanism.common.advancements.MekanismAdvancement;
import mekanism.common.base.IModModule;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeGui;
import mekanism.common.registration.impl.FluidRegistryObject;
import mekanism.common.util.RegistryUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public abstract class BaseLanguageProvider extends FabricLanguageProvider {

    private final ConvertibleLanguageProvider[] altProviders;
    protected final String modName;
    protected final String basicModName;

    protected BaseLanguageProvider(FabricDataOutput dataGenerator) {
        this(dataGenerator, Mekanism.MOD_NAME);
    }

    protected BaseLanguageProvider(FabricDataOutput dataGenerator, IModModule module) {
        this(dataGenerator, Mekanism.MOD_NAME + ": " + module.getName());
    }

    private BaseLanguageProvider(FabricDataOutput dataGenerator, String modName) {
        super(dataGenerator, "en_us");
        this.modName = modName;
        this.basicModName = modName.replaceAll(":", "");
        altProviders = new ConvertibleLanguageProvider[]{
              new UpsideDownLanguageProvider(dataGenerator),
              new NonAmericanLanguageProvider(dataGenerator, "en_au"),
              new NonAmericanLanguageProvider(dataGenerator, "en_gb")
        };
    }

    @NotNull
    @Override
    public String getName() {
        return super.getName() + ": " + modName;
    }

    protected void addPackData(TranslationBuilder builder, IHasTranslationKey name, IHasTranslationKey packDescription) {
        add(builder, name, modName);
        add(builder, packDescription, "Resources used for " + modName);
    }

    protected void add(TranslationBuilder builder, IHasTranslationKey key, String value) {
        if (key instanceof IBlockProvider blockProvider) {
            Block block = blockProvider.getBlock();
            if (Attribute.matches(block, AttributeGui.class, attribute -> !attribute.hasCustomName())) {
                add(builder, Util.makeDescriptionId("container", RegistryUtils.getName(block)), value);
            }
        }
        add(builder, key.getTranslationKey(), value);
    }

    protected void add(TranslationBuilder builder, IBlockProvider blockProvider, String value, String containerName) {
        Block block = blockProvider.getBlock();
        if (Attribute.matches(block, AttributeGui.class, attribute -> !attribute.hasCustomName())) {
            add(builder, Util.makeDescriptionId("container", RegistryUtils.getName(block)), containerName);
            add(builder, blockProvider.getTranslationKey(), value);
        } else {
            throw new IllegalArgumentException("Block " + blockProvider.getRegistryName() + " does not have a container name set.");
        }
    }

    protected void add(TranslationBuilder builder, IModuleDataProvider<?> moduleDataProvider, String name, String description) {
        ModuleData<?> moduleData = moduleDataProvider.getModuleData();
        add(builder, moduleData.getTranslationKey(), name);
        add(builder, moduleData.getDescriptionTranslationKey(), description);
    }

    protected void addFluid(TranslationBuilder builder, FluidRegistryObject<?, ?, ?, ?> fluidRO, String name) {
        add(builder, fluidRO.getBlock().getDescriptionId(), name);
        add(builder, fluidRO.getBucket().getDescriptionId(), name + " Bucket");
    }

    protected void add(TranslationBuilder builder, MekanismAdvancement advancement, String title, String description) {
        add(builder, advancement.title(), title);
        add(builder, advancement.description(), description);
    }

    public void add(TranslationBuilder builder, @NotNull String key, @NotNull String value) {
        if (value.contains("%s")) {
            throw new IllegalArgumentException("Values containing substitutions should use explicit numbered indices: " + key + " - " + value);
        }
        builder.add(key, value);
        if (altProviders.length > 0) {
            List<Component> splitEnglish = FormatSplitter.split(value);
            for (ConvertibleLanguageProvider provider : altProviders) {
                provider.convert(key, splitEnglish);
            }
        }
    }

    @NotNull
    @Override
    public CompletableFuture<?> run(@NotNull CachedOutput cache) {
        CompletableFuture<?> future = super.run(cache);
        if (altProviders.length > 0) {
            CompletableFuture<?>[] futures = new CompletableFuture[altProviders.length + 1];
            futures[0] = future;
            for (int i = 0; i < altProviders.length; i++) {
                futures[i + 1] = altProviders[i].run(cache);
            }
            return CompletableFuture.allOf(futures);
        }
        return future;
    }
}
