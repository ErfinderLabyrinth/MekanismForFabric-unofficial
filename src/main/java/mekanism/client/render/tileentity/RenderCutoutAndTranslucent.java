package mekanism.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.profiling.ProfilerFiller;

public class RenderCutoutAndTranslucent extends MekanismTileEntityRenderer<TileEntityMekanism> {
    BakedModel cutout;
    BakedModel translucent;
    public RenderCutoutAndTranslucent(BlockEntityRendererProvider.Context context, BakedModel cutout, BakedModel translucent) {
        super(context);
        this.cutout = cutout;
        this.translucent = translucent;
    }

    @Override
    protected void render(TileEntityMekanism tileEntityMekanism, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler) {

    }

    @Override
    protected String getProfilerSection() {
        return "render_cutout_and_translucent";
    }
}
