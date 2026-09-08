package mekanism.client.model.obj;

import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.List;
import java.util.Set;

public final class ObjectRenderState {

    private final List<BakedQuad> quads;

    public ObjectRenderState(
            BakedObjModel model,
            Set<String> objects
    ) {
        this.quads = model.getQuads(objects);
    }

    public List<BakedQuad> quads() {
        return quads;
    }
}
