package mekanism.client.model.obj;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.io.*;
import java.util.*;

public final class ObjParser {

    private ObjParser() {}

    public static ObjModel load(InputStream in, ResourceLocation path) throws IOException {

        ObjModel model = new ObjModel();

        ModelGroup currentGroup = model.getOrCreateGroup("default");
        ModelObject currentObject = currentGroup.getOrCreateObject("default");

        String currentMaterial = null;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            String line;

            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] s = line.split("\\s+");

                switch (s[0]) {

                    case "v":
                        model.positions.add(new Vec3(
                                Float.parseFloat(s[1]),
                                Float.parseFloat(s[2]),
                                Float.parseFloat(s[3])
                        ));
                        break;

                    case "vt":
                        model.uvs.add(new Vec2(
                                Float.parseFloat(s[1]),
                                Float.parseFloat(s[2])
                        ));
                        break;

                    case "vn":
                        model.normals.add(new Vec3(
                                Float.parseFloat(s[1]),
                                Float.parseFloat(s[2]),
                                Float.parseFloat(s[3])
                        ));
                        break;

                    case "g":
                        currentGroup = model.getOrCreateGroup(s[1]);
                        currentObject = currentGroup.getOrCreateObject("default");
                        break;

                    case "o":
                        currentObject = currentGroup.getOrCreateObject(s[1]);
                        break;

                    case "usemtl":
                        currentMaterial = s[1];
                        break;

                    case "f":
                        Face face = new Face();
                        face.material = currentMaterial;

                        for (int i = 1; i < s.length; i++) {

                            String[] p = s[i].split("/");

                            int vi = parseIndex(p[0], model.positions.size());
                            int vti = p.length > 1 && !p[1].isEmpty()
                                    ? parseIndex(p[1], model.uvs.size())
                                    : -1;

                            int vni = p.length > 2
                                    ? parseIndex(p[2], model.normals.size())
                                    : -1;

                            face.vertices.add(new VertexRef(vi, vti, vni));
                        }

                        currentObject.addFace(face);
                        break;

                    case "mtllib": {
                        String file = s[1];
                        MtlParser.load(model, inResolver(file, path));
                        break;
                    }
                }
            }
        }

        return model;
    }

    private static InputStream inResolver(String file, ResourceLocation path) {
        try {
            return Minecraft.getInstance().getResourceManager().getResource(path.withSuffix("/" + file)).get().open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int parseIndex(String s, int size) {
        int i = Integer.parseInt(s);
        return (i < 0) ? size + i : i - 1;
    }
}