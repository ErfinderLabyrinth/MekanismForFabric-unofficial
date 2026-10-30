package mekanism.client.mixinhelper;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

public interface CustomPerspectiveModels {
    public BakedModel getPerspectiveModel(ItemDisplayContext context);
}
