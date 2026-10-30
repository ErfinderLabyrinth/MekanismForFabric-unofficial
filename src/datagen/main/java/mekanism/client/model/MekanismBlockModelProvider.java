package mekanism.client.model;

import mekanism.common.Mekanism;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.models.BlockModelGenerators;

public class MekanismBlockModelProvider extends BaseBlockModelProvider {

    public MekanismBlockModelProvider(FabricDataOutput output) {
        super(output, Mekanism.MODID);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {

    }
}