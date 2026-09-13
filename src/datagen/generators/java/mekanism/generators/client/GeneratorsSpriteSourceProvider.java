package mekanism.generators.client;

import mekanism.client.texture.BaseSpriteSourceProvider;
import mekanism.generators.common.MekanismGenerators;
import mekanism.generators.common.registries.GeneratorsFluids;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;

import java.util.List;

public class GeneratorsSpriteSourceProvider extends BaseSpriteSourceProvider {

    public GeneratorsSpriteSourceProvider(FabricDataOutput output) {
        super(output, MekanismGenerators.MODID);
    }

    @Override
    protected void addSources() {
        List<SpriteSource> atlas = atlas(BLOCKS_ATLAS);
        addChemicalSprites(atlas);
        addFluids(atlas, GeneratorsFluids.FLUIDS);
    }
}