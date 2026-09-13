package mekanism.common.registration.impl;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryBuilder;
import mekanism.common.registration.WrappedDeferredRegister;
import mekanism.common.resource.PrimaryResource;
import net.minecraft.core.Registry;

import java.util.function.UnaryOperator;

public class SlurryDeferredRegister extends WrappedDeferredRegister<Slurry> {
    String modid;

    public SlurryDeferredRegister(String modid) {
        super(MekanismAPI.SLURRY_REGISTRY_NAME);
        this.modid = modid;
    }

    public SlurryRegistryObject<Slurry, Slurry> register(PrimaryResource resource) {
        return register(resource.getRegistrySuffix(), builder -> builder.tint(resource.getTint()).ore(resource.getOreTag()));
    }

    public SlurryRegistryObject<Slurry, Slurry> register(String baseName, UnaryOperator<SlurryBuilder> builderModifier) {
        return new SlurryRegistryObject<>(Registry.register(internal, "dirty_" + baseName, new Slurry(builderModifier.apply(SlurryBuilder.dirty()))),
                Registry.register(internal, "clean_" + baseName, new Slurry(builderModifier.apply(SlurryBuilder.clean()))));
    }
}
