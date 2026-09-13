package mekanism.common.registration.impl;

import com.mojang.serialization.Codec;
import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

import java.util.function.Supplier;

public class IntProviderTypeDeferredRegister extends WrappedDeferredRegister<IntProviderType<?>> {
    String modid;
    public IntProviderTypeDeferredRegister(String modid) {
        super(BuiltInRegistries.INT_PROVIDER_TYPE);
        this.modid = modid;
    }

    public <PROVIDER extends IntProvider> IntProviderTypeRegistryObject<PROVIDER> register(String name, Codec<PROVIDER> codec) {
        return register(name, () -> () -> codec);
    }

    public <PROVIDER extends IntProvider> IntProviderTypeRegistryObject<PROVIDER> register(String name, Supplier<IntProviderType<PROVIDER>> sup) {
        return register(new ResourceLocation(modid, name), sup, IntProviderTypeRegistryObject::new);
    }
}