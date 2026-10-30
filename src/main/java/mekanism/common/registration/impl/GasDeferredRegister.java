package mekanism.common.registration.impl;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.attribute.ChemicalAttribute;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasBuilder;
import mekanism.common.base.IChemicalConstant;
import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class GasDeferredRegister extends WrappedDeferredRegister<Gas> {
    String modid;
    public GasDeferredRegister(String modid) {
        super(MekanismAPI.GAS_REGISTRY_NAME);
        this.modid = modid;
    }

    public GasDeferredRegister(String modid, Registry<Gas> registry) {
        super(registry);
        this.modid = modid;
    }

    public GasRegistryObject<Gas> register(IChemicalConstant constants, ChemicalAttribute... attributes) {
        return register(constants.getName(), constants.getColor(), attributes);
    }

    public GasRegistryObject<Gas> register(String name, int color, ChemicalAttribute... attributes) {
        return register(name, () -> {
            GasBuilder builder = GasBuilder.builder().tint(color);
            for (ChemicalAttribute attribute : attributes) {
                builder.with(attribute);
            }
            return new Gas(builder);
        });
    }

    public <GAS extends Gas> GasRegistryObject<GAS> register(String name, Supplier<GAS> sup) {
        return register(new ResourceLocation(modid, name), sup, GasRegistryObject::new);
    }
}
