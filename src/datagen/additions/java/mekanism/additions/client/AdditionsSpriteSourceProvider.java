package mekanism.additions.client;

import mekanism.additions.common.MekanismAdditions;
import mekanism.client.texture.BaseSpriteSourceProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;

import java.util.List;

public class AdditionsSpriteSourceProvider extends BaseSpriteSourceProvider {

    public AdditionsSpriteSourceProvider(FabricDataOutput output) {
        super(output, MekanismAdditions.MODID);
    }

    @Override
    protected void addSources() {
        List<SpriteSource> atlas = atlas(BLOCKS_ATLAS);
        addFiles(atlas, MekanismAdditions.rl("entity/balloon"), MekanismAdditions.rl("entity/balloon_string"));
    }
}