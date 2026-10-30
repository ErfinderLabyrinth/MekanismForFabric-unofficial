package mekanism.generators.client;

import mekanism.client.model.BaseBlockModelProvider;
import mekanism.generators.common.MekanismGenerators;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.models.BlockModelGenerators;

public class GeneratorsBlockModelProvider extends BaseBlockModelProvider {

    public GeneratorsBlockModelProvider(FabricDataOutput output) {
        super(output, MekanismGenerators.MODID);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {

    }
}