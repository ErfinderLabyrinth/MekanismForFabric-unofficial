package mekanism.common.registration.impl;

import com.mojang.serialization.Codec;
import mekanism.api.MekanismAPI;
import mekanism.api.robit.RobitSkin;
import mekanism.common.registration.WrappedDatapackDeferredRegister;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class RobitSkinDeferredRegister extends WrappedDatapackDeferredRegister<RobitSkin> {
    String modid;
    public RobitSkinDeferredRegister(String modid) {
        super(modid, MekanismAPI.ROBIT_SKIN_SERIALIZER_REGISTRY_NAME, MekanismAPI.ROBIT_SKIN_REGISTRY_NAME);
        this.modid = modid;
    }

    public <ROBIT_SKIN extends RobitSkin> RobitSkinSerializerRegistryObject<ROBIT_SKIN> registerSerializer(String name, Supplier<Codec<ROBIT_SKIN>> sup) {
        return register(new ResourceLocation(modid, name), sup, RobitSkinSerializerRegistryObject::new);
    }
}