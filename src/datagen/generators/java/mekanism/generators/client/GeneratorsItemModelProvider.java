package mekanism.generators.client;

import mekanism.client.model.BaseItemModelProvider;
import mekanism.generators.common.MekanismGenerators;
import mekanism.generators.common.registries.GeneratorsFluids;
import mekanism.generators.common.registries.GeneratorsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.models.ItemModelGenerators;

public class GeneratorsItemModelProvider extends BaseItemModelProvider {

    public GeneratorsItemModelProvider(FabricDataOutput output) {
        super(output, MekanismGenerators.MODID);
    }

    @Override
    public void generateItemModels(ItemModelGenerators gen) {
        registerBuckets(gen, GeneratorsFluids.FLUIDS);
        registerModules(gen, GeneratorsItems.ITEMS);
        registerGenerated(gen, GeneratorsItems.HOHLRAUM, GeneratorsItems.SOLAR_PANEL, GeneratorsItems.TURBINE_BLADE);
    }
}