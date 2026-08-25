package mekanism.tools.client;

import mekanism.client.texture.BaseSpriteSourceProvider;
import mekanism.tools.common.MekanismTools;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ToolsSpriteSourceProvider extends BaseSpriteSourceProvider {
    protected static final ResourceLocation SHIELD_PATTERNS_ATLAS = new ResourceLocation("shield_patterns");
    public ToolsSpriteSourceProvider(FabricDataOutput output) {
        super(output, MekanismTools.MODID);
    }

    @Override
    protected void addSources() {
        List<SpriteSource> atlas = atlas(SHIELD_PATTERNS_ATLAS);
        for (ShieldTextures textures : ShieldTextures.values()) {
            addFiles(atlas, textures.getBase().texture());
        }
    }
}