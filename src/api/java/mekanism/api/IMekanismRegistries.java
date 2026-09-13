package mekanism.api;

import com.mojang.serialization.Codec;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.gear.ModuleData;
import mekanism.api.robit.RobitSkin;
import net.minecraft.core.Registry;

public interface IMekanismRegistries {
    Registry<Gas> getGasRegistry();
    Registry<InfuseType> getInfuseTypeRegistry();
    Registry<Pigment> getPigmentRegistry();
    Registry<Slurry> getSlurryRegistry();
    Registry<ModuleData<?>> getModuleRegistry();
    Registry<Codec<? extends RobitSkin>> getRobitSkinSerializerRegistry();
}
