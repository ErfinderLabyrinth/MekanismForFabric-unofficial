package mekanism.client.model.robit;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.model.baked.ModelDataBakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@NothingNullByDefault
public class RobitModelDataBakedModel extends ModelDataBakedModel {
    ResourceLocation texture;
    public RobitModelDataBakedModel(BakedModel original, ResourceLocation texture) {
        super(original);
        this.texture = texture;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        if (original instanceof RobitBakedModel robitBakedModel) {
            return robitBakedModel.getQuads(state, side, rand, texture);
        }
        return super.getQuads(state, side, rand);
    }
}