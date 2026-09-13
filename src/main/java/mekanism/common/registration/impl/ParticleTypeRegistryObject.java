package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public class ParticleTypeRegistryObject<PARTICLE extends ParticleOptions, TYPE extends ParticleType<PARTICLE>> extends WrappedRegistryObject<TYPE> {

    public ParticleTypeRegistryObject(TYPE registryObject) {
        super(registryObject);
    }
}