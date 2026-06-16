package mekanism.client.mixin;


import mekanism.client.mixinhelper.BlockElementExtension;
import mekanism.client.mixinhelper.BlockElementParentSetterGetter;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockElementFace.class)
public class BlockElementFaceMixin implements BlockElementParentSetterGetter, BlockElementExtension {
    @Unique
    private BlockElement parent;
    @Unique
    private boolean lightSet;
    @Unique
    private int skyLight;
    @Unique
    private int blockLight;

    public void mekanism$setParent(BlockElement element) {
        this.parent = element;
    }

    @Override
    public BlockElement mekanism$getParent() {
        return parent;
    }

    @Override
    public int mekanism$getSkyLight() {
        return lightSet ? skyLight : parent != null ? ((BlockElementExtension)parent).mekanism$getSkyLight() : 0;
    }

    @Override
    public int mekanism$getBlockLight() {
        return lightSet ? blockLight : parent != null ? ((BlockElementExtension)parent).mekanism$getBlockLight() : 0;
    }

    @Override
    public void mekanism$setLight(int blockLight, int skyLight) {
        lightSet = true;
        this.blockLight = blockLight;
        this.skyLight = skyLight;
    }
}
