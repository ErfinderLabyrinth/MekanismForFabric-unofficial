package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.network.syncher.EntityDataSerializer;

public class DataSerializerRegistryObject<T> extends WrappedRegistryObject<EntityDataSerializer<T>> {

    public DataSerializerRegistryObject(EntityDataSerializer<T> registryObject) {
        super(registryObject);
    }
}