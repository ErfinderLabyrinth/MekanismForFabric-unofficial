package mekanism.common.registration.impl;

import com.mojang.serialization.Codec;
import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.function.Supplier;

public class PlacementModifierDeferredRegister extends WrappedDeferredRegister<PlacementModifierType<?>> {
    String modid;
    public PlacementModifierDeferredRegister(String modid) {
        super(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE);
        this.modid = modid;
    }

    public <PROVIDER extends PlacementModifier> PlacementModifierRegistryObject<PROVIDER> register(String name, Codec<PROVIDER> codec) {
        return register(name, () -> () -> codec);
    }

    public <PROVIDER extends PlacementModifier> PlacementModifierRegistryObject<PROVIDER> register(String name, Supplier<PlacementModifierType<PROVIDER>> sup) {
        return register(new ResourceLocation(modid, name), sup, PlacementModifierRegistryObject::new);
    }
}