package mekanism.client.model.obj;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public final class QuadFactory {

    public static BakedQuad build(
            Vec3 a,
            Vec3 b,
            Vec3 c,
            TextureAtlasSprite sprite,
            float u0, float v0,
            float u1, float v1,
            float u2, float v2
    ) {

        int[] data = new int[32];

        putVertex(data, 0,
                (float) a.x, (float) a.y, (float) a.z,
                sprite, u0, v0);

        putVertex(data, 8,
                (float) b.x, (float) b.y, (float) b.z,
                sprite, u1, v1);

        putVertex(data, 16,
                (float) c.x, (float) c.y, (float) c.z,
                sprite, u2, v2);

        // 4th vertex = duplicate for Minecraft quad format
        putVertex(data, 24,
                (float) c.x, (float) c.y, (float) c.z,
                sprite, u2, v2);

        return new BakedQuad(
                data,
                -1,
                Direction.UP,
                sprite,
                false
        );
    }

    private static void putVertex(
            int[] data,
            int offset,
            float x, float y, float z,
            TextureAtlasSprite sprite,
            float u, float v
    ) {
        data[offset]     = Float.floatToRawIntBits(x);
        data[offset + 1] = Float.floatToRawIntBits(y);
        data[offset + 2] = Float.floatToRawIntBits(z);

        data[offset + 3] = -1; // ARGB color (default: white = no tint)

        float tu = sprite.getU0() + (sprite.getU1() - sprite.getU0()) * u;
        float tv = sprite.getV0() + (sprite.getV1() - sprite.getV0()) * v;

        data[offset + 4] = Float.floatToRawIntBits(tu);
        data[offset + 5] = Float.floatToRawIntBits(tv);

        data[offset + 6] = 0; // lightmap (optional)
        data[offset + 7] = 0; // normal (packed later if needed)
    }
}