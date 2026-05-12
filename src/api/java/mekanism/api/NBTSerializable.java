package mekanism.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public interface NBTSerializable<T extends Tag> {
    T serializeNBT();
    void deserializeNBT(T nbt);
}
