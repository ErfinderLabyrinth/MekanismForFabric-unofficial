package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class ParticleTypeDeferredRegister extends WrappedDeferredRegister<ParticleType<?>> {
    String modid;
    public ParticleTypeDeferredRegister(String modid) {
        super(BuiltInRegistries.PARTICLE_TYPE);
        this.modid = modid;
    }

    public ParticleTypeRegistryObject<SimpleParticleType, SimpleParticleType> registerBasicParticle(String name) {
        return register(name, () -> new SimpleParticleType(false));
    }

    public <PARTICLE extends ParticleOptions, TYPE extends ParticleType<PARTICLE>> ParticleTypeRegistryObject<PARTICLE, TYPE> register(String name, Supplier<TYPE> sup) {
        return register(new ResourceLocation(modid, name), sup, ParticleTypeRegistryObject::new);
    }
}