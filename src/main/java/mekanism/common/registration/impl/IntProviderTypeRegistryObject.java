package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

public class IntProviderTypeRegistryObject<PROVIDER extends IntProvider> extends WrappedRegistryObject<IntProviderType<PROVIDER>> {

    public IntProviderTypeRegistryObject(IntProviderType<PROVIDER> registryObject) {
        super(registryObject);
    }
}