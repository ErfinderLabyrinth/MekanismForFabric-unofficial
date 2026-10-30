package mekanism.client.mixinhelper;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

public class MixinData {
    // For VertexConsumerMixin
    public static final int LIGHT_OFFSET = DefaultVertexFormat.BLOCK.offsets.getInt(DefaultVertexFormat.BLOCK.getElements().indexOf(DefaultVertexFormat.ELEMENT_UV2));
}
