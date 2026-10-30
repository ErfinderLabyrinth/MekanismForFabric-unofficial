package mekanism.additions.client;

import mekanism.additions.common.MekanismAdditions;
import mekanism.client.model.BaseBlockModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.models.BlockModelGenerators;

public class AdditionsBlockModelProvider extends BaseBlockModelProvider {

    //TODO: Add helpers for the color block stuff
    public AdditionsBlockModelProvider(FabricDataOutput output) {
        super(output, MekanismAdditions.MODID);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {

    }
}