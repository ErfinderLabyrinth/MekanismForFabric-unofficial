package mekanism.common.registration.impl;

import com.mojang.serialization.Codec;
import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;

import java.util.function.Supplier;

public class HeightProviderTypeDeferredRegister extends WrappedDeferredRegister<HeightProviderType<?>> {
    String modid;
    public HeightProviderTypeDeferredRegister(String modid) {
        super(BuiltInRegistries.HEIGHT_PROVIDER_TYPE);
        this.modid = modid;
    }

    public <PROVIDER extends HeightProvider> HeightProviderTypeRegistryObject<PROVIDER> register(String name, Codec<PROVIDER> codec) {
        return register(name, () -> () -> codec);
    }

    public <PROVIDER extends HeightProvider> HeightProviderTypeRegistryObject<PROVIDER> register(String name, Supplier<HeightProviderType<PROVIDER>> sup) {
        return register(new ResourceLocation(modid, name), sup, HeightProviderTypeRegistryObject::new);
    }
}