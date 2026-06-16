package mekanism.client.mixinhelper;

import net.minecraft.client.renderer.block.model.BlockElement;

public interface BlockElementParentSetterGetter {
    void mekanism$setParent(BlockElement element);
    BlockElement mekanism$getParent();
}
