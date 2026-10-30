package mekanism.client.model.obj;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Function;

public final class ObjModel {

    public final List<Vec3> positions = new ArrayList<>();
    public final List<Vec2> uvs = new ArrayList<>();
    public final List<Vec3> normals = new ArrayList<>();

    public final Map<String, Material> materials = new HashMap<>();

    private final Map<String, ModelGroup> groups = new LinkedHashMap<>();

    public ModelGroup getGroup(String name) {
        return groups.get(name);
    }

    public Collection<ModelGroup> getGroups() {
        return groups.values();
    }

    public Material getMaterial(String name) {
        return materials.get(name);
    }

    public ModelGroup getOrCreateGroup(String name) {
        return groups.computeIfAbsent(name, ModelGroup::new);
    }

    public BakedObjModel bake(BlockModel owner, Function<net.minecraft.client.resources.model.Material, TextureAtlasSprite> sprites) {
        return ObjBaker.bake(this, owner, sprites, positions, uvs);
    }

    public Map<String, ModelGroup> getRawGroups() {
        return groups;
    }

    public boolean hasObject(String objectName) {
        for (ModelGroup group : groups.values()) {
            if (group.hasObject(objectName)) {
                return true;
            }
        }
        return false;
    }
}