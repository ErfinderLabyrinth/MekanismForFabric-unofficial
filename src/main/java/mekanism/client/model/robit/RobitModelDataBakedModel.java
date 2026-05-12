package mekanism.client.model.robit;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.model.baked.ModelDataBakedModel;
import net.minecraft.client.resources.model.BakedModel;

@NothingNullByDefault
public class RobitModelDataBakedModel extends ModelDataBakedModel {

    public RobitModelDataBakedModel(BakedModel original) {
        super(original);
    }

//    @Override
//    public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous) {
//        //TODO: Handle the original model being layered properly as currently we don't have any way to properly bounce them
//        return RobitSpriteUploader.RENDER_TYPES;
//    }
}