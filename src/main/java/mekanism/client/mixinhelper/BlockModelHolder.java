package mekanism.client.mixinhelper;

import net.minecraft.client.renderer.block.model.BlockModel;

public interface BlockModelHolder {
    BlockModel mekanism$getBlockModel();
    void mekanism$setBlockModel(BlockModel model);
}
