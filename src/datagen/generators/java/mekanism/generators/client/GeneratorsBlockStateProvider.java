package mekanism.generators.client;

import mekanism.client.state.BaseBlockStateProvider;
import mekanism.generators.common.MekanismGenerators;
import mekanism.generators.common.registries.GeneratorsFluids;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraftforge.common.data.ExistingFileHelper;

public class GeneratorsBlockStateProvider extends BaseBlockStateProvider<GeneratorsBlockModelProvider> {

    public GeneratorsBlockStateProvider(FabricDataOutput output) {
        super(output, MekanismGenerators.MODID, GeneratorsBlockModelProvider::new);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        registerFluidBlockStates(generators, GeneratorsFluids.FLUIDS.getAllFluids());
    }
}