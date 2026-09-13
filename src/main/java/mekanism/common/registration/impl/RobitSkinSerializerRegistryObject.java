package mekanism.common.registration.impl;

import com.mojang.serialization.Codec;
import mekanism.api.robit.RobitSkin;
import mekanism.common.registration.WrappedRegistryObject;

public class RobitSkinSerializerRegistryObject<ROBIT_SKIN extends RobitSkin> extends WrappedRegistryObject<Codec<ROBIT_SKIN>> {

    public RobitSkinSerializerRegistryObject(Codec<ROBIT_SKIN> codec) {
        super(codec);
    }
}