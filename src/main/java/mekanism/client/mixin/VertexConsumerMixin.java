package mekanism.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mekanism.client.mixinhelper.MixinData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.nio.ByteBuffer;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin {

    @ModifyVariable(method = "putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFF[IIZ)V", at = @At("STORE"), ordinal = 4)
    default int fixLightning(int arg0, @Local ByteBuffer buffer) {
        int bl = Math.max(arg0 & 0xFFFF, Short.toUnsignedInt(buffer.getShort(MixinData.LIGHT_OFFSET)));
        int sl = Math.max((arg0 >> 16) & 0xFFFF, Short.toUnsignedInt(buffer.getShort(MixinData.LIGHT_OFFSET + 2)));
        return bl | (sl << 16);
    }
}
