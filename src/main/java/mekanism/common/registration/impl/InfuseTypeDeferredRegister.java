package mekanism.common.registration.impl;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfuseTypeBuilder;
import mekanism.common.registration.WrappedDeferredRegister;
import mekanism.common.util.ChemicalUtil;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class InfuseTypeDeferredRegister extends WrappedDeferredRegister<InfuseType> {
    String modid;
    public InfuseTypeDeferredRegister(String modid) {
        super(MekanismAPI.INFUSE_TYPE_REGISTRY_NAME);
        this.modid = modid;
    }

    public InfuseTypeRegistryObject<InfuseType> register(String name, int tint) {
        return register(name, () -> new InfuseType(InfuseTypeBuilder.builder().tint(tint)));
    }

    public InfuseTypeRegistryObject<InfuseType> register(String name, ResourceLocation texture, int barColor) {
        return register(name, () -> ChemicalUtil.infuseType(InfuseTypeBuilder.builder(texture), barColor));
    }

    public <INFUSE_TYPE extends InfuseType> InfuseTypeRegistryObject<INFUSE_TYPE> register(String name, Supplier<INFUSE_TYPE> sup) {
        return register(new ResourceLocation(modid, name), sup, InfuseTypeRegistryObject::new);
    }
}