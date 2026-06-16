package mekanism.client.mixin;

import com.mojang.datafixers.util.Either;
import mekanism.client.mixinhelper.BlockElementExtension;
import mekanism.client.mixinhelper.BlockElementParentSetterGetter;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(BlockElement.class)
public class BlockElementMixin implements BlockElementExtension {
    @Unique
    private int blockLight;
    @Unique
    private int skyLight;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void setFaceParent(Vector3f vector3f, Vector3f vector3f2, Map<Direction, BlockElementFace> map, BlockElementRotation blockElementRotation, boolean bl, CallbackInfo ci) {
        for (BlockElementFace face : map.values()) {
            ((BlockElementParentSetterGetter)face).mekanism$setParent((BlockElement) (Object) this);
        }
    }

    @Override
    public int mekanism$getBlockLight() {
        return blockLight;
    }

    @Override
    public int mekanism$getSkyLight() {
        return skyLight;
    }

    @Override
    public void mekanism$setLight(int blockLight, int skyLight) {
        this.blockLight = blockLight;
        this.skyLight = skyLight;
    }
}
