package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.function.Supplier;

public class FeatureDeferredRegister extends WrappedDeferredRegister<Feature<?>> {
    String modid;
    public FeatureDeferredRegister(String modid) {
        super(BuiltInRegistries.FEATURE);
        this.modid = modid;
    }

    public <CONFIG extends FeatureConfiguration, FEATURE extends Feature<CONFIG>> FeatureRegistryObject<CONFIG, FEATURE> register(String name, Supplier<FEATURE> sup) {
        return register(new ResourceLocation(modid, name), sup, FeatureRegistryObject::new);
    }
}