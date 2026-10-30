package mekanism.common.registries;

import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.NotNull;

public abstract class BaseDatapackRegistryProvider extends FabricDynamicRegistryProvider {

    private final String modid;

    protected BaseDatapackRegistryProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid) {
        super(output, registries);
        this.modid = modid;
    }

    @NotNull
    @Override
    public String getName() {
        return "Datapack registries: " + modid;
    }

    protected static PlacedFeaturesHolder registerPlacedFeature(Entries entries, HolderLookup.Provider registries, ResourceLocation name,
          Boolean2ObjectFunction<List<PlacementModifier>> placementModifiers) {
        return registerPlacedFeature(entries, registries, name, name, placementModifiers);
    }

    protected static PlacedFeaturesHolder registerPlacedFeature(Entries entries, HolderLookup.Provider registries, ResourceLocation name, ResourceLocation retrogenName,
          Boolean2ObjectFunction<List<PlacementModifier>> placementModifiers) {
//        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = entries.getLookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> configuredFeature = entries.ref(configuredFeature(name));
        Holder<ConfiguredFeature<?, ?>> retrogenConfiguredFeature = entries.ref(configuredFeature(retrogenName));
        return new PlacedFeaturesHolder(
                entries.add(placedFeature(name), new PlacedFeature(configuredFeature, placementModifiers.get(false))),
                entries.add(placedFeature(name.withSuffix("_retrogen")), new PlacedFeature(retrogenConfiguredFeature, placementModifiers.get(true)))
        );
    }

    protected static ResourceKey<ConfiguredFeature<?, ?>> configuredFeature(ResourceLocation name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, name);
    }

    protected static ResourceKey<PlacedFeature> placedFeature(ResourceLocation name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, name);
    }

    protected record PlacedFeaturesHolder(Holder<PlacedFeature> feature, Holder<PlacedFeature> retrogen) {

    }
}