package mekanism.common.registration.impl;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentBuilder;
import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class PigmentDeferredRegister extends WrappedDeferredRegister<Pigment> {
    String modid;
    public PigmentDeferredRegister(String modid) {
        super(MekanismAPI.PIGMENT_REGISTRY_NAME);
        this.modid = modid;
    }

    public PigmentRegistryObject<Pigment> register(String name, int tint) {
        return register(name, () -> new Pigment(PigmentBuilder.builder().tint(tint)));
    }

    public PigmentRegistryObject<Pigment> register(String name, ResourceLocation texture) {
        return register(name, () -> new Pigment(PigmentBuilder.builder(texture)));
    }

    public <PIGMENT extends Pigment> PigmentRegistryObject<PIGMENT> register(String name, Supplier<PIGMENT> sup) {
        return register(new ResourceLocation(modid, name), sup, PigmentRegistryObject::new);
    }
}