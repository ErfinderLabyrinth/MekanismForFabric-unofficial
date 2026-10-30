package mekanism.common.registration.impl;

import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.providers.IPigmentProvider;
import mekanism.common.registration.WrappedRegistryObject;
import org.jetbrains.annotations.NotNull;

public class PigmentRegistryObject<PIGMENT extends Pigment> extends WrappedRegistryObject<PIGMENT> implements IPigmentProvider {

    public PigmentRegistryObject(PIGMENT registryObject) {
        super(registryObject);
    }

    @NotNull
    @Override
    public PIGMENT getChemical() {
        return get();
    }
}