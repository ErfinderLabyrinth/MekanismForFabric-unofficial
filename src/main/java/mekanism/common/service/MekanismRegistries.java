package mekanism.common.service;

import com.mojang.serialization.Codec;
import mekanism.api.IMekanismRegistries;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.gear.ModuleData;
import mekanism.api.robit.RobitSkin;
import mekanism.common.registries.*;
import net.minecraft.core.Registry;

public class MekanismRegistries implements IMekanismRegistries {
    public static final MekanismRegistries INSTANCE = new MekanismRegistries();

    @Override
    public Registry<Gas> getGasRegistry() {
        return MekanismGases.GASES.getRegistry();
    }

    @Override
    public Registry<InfuseType> getInfuseTypeRegistry() {
        return MekanismInfuseTypes.INFUSE_TYPES.getRegistry();
    }

    @Override
    public Registry<Pigment> getPigmentRegistry() {
        return MekanismPigments.PIGMENTS.getRegistry();
    }

    @Override
    public Registry<Slurry> getSlurryRegistry() {
        return MekanismSlurries.SLURRIES.getRegistry();
    }

    @Override
    public Registry<ModuleData<?>> getModuleRegistry() {
        return MekanismModules.MODULES.getRegistry();
    }

    @Override
    public Registry<Codec<? extends RobitSkin>> getRobitSkinSerializerRegistry() {
        return MekanismRobitSkins.ROBIT_SKINS.getRegistry();
    }
}
