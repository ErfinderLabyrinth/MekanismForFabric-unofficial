package mekanism.common.tile.interfaces;

import net.minecraft.nbt.CompoundTag;

import java.util.Map;

public interface ISustainedData {

    void writeSustainedData(CompoundTag dataMap);

    void readSustainedData(CompoundTag dataMap);

    //Key is tile save string, value is sustained data string
    Map<String, String> getTileDataRemap();
}