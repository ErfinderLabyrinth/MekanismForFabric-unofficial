package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class FeatureRegistryObject<CONFIG extends FeatureConfiguration, FEATURE extends Feature<CONFIG>> extends WrappedRegistryObject<FEATURE> {

    public FeatureRegistryObject(FEATURE registryObject) {
        super(registryObject);
    }
}