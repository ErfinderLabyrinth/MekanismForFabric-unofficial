package mekanism.common.registration.impl;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.providers.IGasProvider;
import mekanism.common.registration.WrappedRegistryObject;
import org.jetbrains.annotations.NotNull;

public class GasRegistryObject<GAS extends Gas> extends WrappedRegistryObject<GAS> implements IGasProvider {

    public GasRegistryObject(GAS registryObject) {
        super(registryObject);
    }

    @NotNull
    @Override
    public GAS getChemical() {
        return get();
    }
}