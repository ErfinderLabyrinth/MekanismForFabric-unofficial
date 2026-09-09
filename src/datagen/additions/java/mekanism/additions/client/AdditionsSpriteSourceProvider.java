package mekanism.additions.client;

import mekanism.additions.common.MekanismAdditions;
import mekanism.client.texture.BaseSpriteSourceProvider;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.data.PackOutput;

import java.util.List;

public class AdditionsSpriteSourceProvider extends BaseSpriteSourceProvider {

    public AdditionsSpriteSourceProvider(PackOutput output) {
        super(output, MekanismAdditions.MODID);
    }

    @Override
    protected void addSources() {
        List<SpriteSource> atlas = atlas(BLOCKS_ATLAS);
        addFiles(atlas, MekanismAdditions.rl("entity/balloon"), MekanismAdditions.rl("entity/balloon_string"));
    }
}