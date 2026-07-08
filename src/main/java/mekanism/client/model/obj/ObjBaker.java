package mekanism.client.model.obj;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Function;

public final class ObjBaker {
    private static final Material MISSING = new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());

    public static BakedObjModel bake(
            ObjModel model,
            BlockModel owner,
            Function<Material, TextureAtlasSprite> sprites,
            List<Vec3> positions,
            List<Vec2> uvs
    ) {

        Map<String, List<BakedQuad>> result = new HashMap<>();

        for (ModelGroup group : model.getGroups()) {

            for (ModelObject obj : group.getObjects()) {

                List<BakedQuad> quads = new ArrayList<>();

                for (Face face : obj.getFaces()) {

                    mekanism.client.model.obj.Material mat = model.getMaterial(face.material);
                    TextureAtlasSprite sprite = sprites.apply(
                            owner == null ? MISSING : owner.getMaterial(mat != null && mat.mapKd != null
                                    ? mat.mapKd
                                    : face.material)
                    );

                    if (face.vertices.size() < 3)
                        continue;

                    // TRIANGULATION (fan)
                    for (int i = 1; i < face.vertices.size() - 1; i++) {

                        VertexRef v0 = face.vertices.get(0);
                        VertexRef v1 = face.vertices.get(i);
                        VertexRef v2 = face.vertices.get(i + 1);

                        quads.add(buildQuad(
                                v0, v1, v2,
                                positions,
                                uvs,
                                sprite
                        ));
                    }
                }

                result.put(obj.getName(), quads);
            }
        }

        return new BakedObjModel(result);
    }

    private static BakedQuad buildQuad(
            VertexRef a,
            VertexRef b,
            VertexRef c,
            List<Vec3> pos,
            List<Vec2> uv,
            TextureAtlasSprite sprite
    ) {

        Vec3 p0 = pos.get(a.v);
        Vec3 p1 = pos.get(b.v);
        Vec3 p2 = pos.get(c.v);

        float u0 = getU(uv, a.vt);
        float v0 = getV(uv, a.vt);

        float u1 = getU(uv, b.vt);
        float v1 = getV(uv, b.vt);

        float u2 = getU(uv, c.vt);
        float v2 = getV(uv, c.vt);

        return QuadFactory.build(
                p0, p1, p2,
                sprite,
                u0, v0,
                u1, v1,
                u2, v2
        );
    }

    private static float getU(List<Vec2> uv, int i) {
        if (i < 0 || i >= uv.size()) return 0f;
        return uv.get(i).x;
    }

    private static float getV(List<Vec2> uv, int i) {
        if (i < 0 || i >= uv.size()) return 0f;
        return uv.get(i).y;
    }
}