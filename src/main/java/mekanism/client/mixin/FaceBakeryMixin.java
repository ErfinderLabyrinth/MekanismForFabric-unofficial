package mekanism.client.mixin;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import mekanism.client.mixinhelper.BlockElementExtension;
import mekanism.client.mixinhelper.BlockElementParentSetterGetter;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FaceBakery.class)
public class FaceBakeryMixin {
    @Unique
    private static final int BLOCK_SIZE = DefaultVertexFormat.BLOCK.getIntegerSize();
    @Unique
    private static final int LIGHT_OFFSET = DefaultVertexFormat.BLOCK.offsets.getInt(DefaultVertexFormat.BLOCK.getElements().indexOf(DefaultVertexFormat.ELEMENT_UV2)) / 4;

    @Inject(method = "bakeQuad", at = @At("RETURN"))
    public void applyLight(Vector3f vector3f, Vector3f vector3f2, BlockElementFace blockElementFace, TextureAtlasSprite textureAtlasSprite, Direction direction, ModelState modelState, BlockElementRotation blockElementRotation, boolean bl, ResourceLocation resourceLocation, CallbackInfoReturnable<BakedQuad> cir) {
        BlockElementExtension extension = (BlockElementExtension)blockElementFace;
        int skyLight = extension.mekanism$getSkyLight();
        int blockLight = extension.mekanism$getBlockLight();
        if (skyLight == 0 && blockLight == 0) {
            return;
        }
        int light = LightTexture.pack(blockLight, skyLight);
        var vertices = cir.getReturnValue().getVertices();
        for (int i = 0; i < 4; i++)
            vertices[i * BLOCK_SIZE + LIGHT_OFFSET] = light;
    }
}
