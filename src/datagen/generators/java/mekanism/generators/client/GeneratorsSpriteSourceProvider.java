package mekanism.generators.client;

import mekanism.client.texture.BaseSpriteSourceProvider;
import mekanism.generators.common.MekanismGenerators;
import mekanism.generators.common.registries.GeneratorsFluids;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.data.PackOutput;

import java.util.List;

public class GeneratorsSpriteSourceProvider extends BaseSpriteSourceProvider {

    public GeneratorsSpriteSourceProvider(PackOutput output) {
        super(output, MekanismGenerators.MODID);
    }

    @Override
    protected void addSources() {
        List<SpriteSource> atlas = atlas(BLOCKS_ATLAS);
        addChemicalSprites(atlas);
        addFluids(atlas, GeneratorsFluids.FLUIDS);
    }
}