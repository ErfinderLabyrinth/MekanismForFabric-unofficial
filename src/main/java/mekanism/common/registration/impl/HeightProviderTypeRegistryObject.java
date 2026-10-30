package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;

public class HeightProviderTypeRegistryObject<PROVIDER extends HeightProvider> extends WrappedRegistryObject<HeightProviderType<PROVIDER>> {

    public HeightProviderTypeRegistryObject(HeightProviderType<PROVIDER> registryObject) {
        super(registryObject);
    }
}