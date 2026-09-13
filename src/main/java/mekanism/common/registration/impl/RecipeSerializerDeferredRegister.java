package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public class RecipeSerializerDeferredRegister extends WrappedDeferredRegister<RecipeSerializer<?>> {
    String modid;
    public RecipeSerializerDeferredRegister(String modid) {
        super(BuiltInRegistries.RECIPE_SERIALIZER);
        this.modid = modid;
    }

    public <RECIPE extends Recipe<?>> RecipeSerializerRegistryObject<RECIPE> register(String name, Supplier<RecipeSerializer<RECIPE>> sup) {
        return register(new ResourceLocation(modid, name), sup, RecipeSerializerRegistryObject::new);
    }
}