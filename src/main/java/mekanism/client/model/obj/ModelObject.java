package mekanism.client.model.obj;


import java.util.ArrayList;
import java.util.List;

public final class ModelObject {

    private final String name;
    private final List<Face> faces = new ArrayList<>();

    public ModelObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Face> getFaces() {
        return faces;
    }

    public void addFace(Face face) {
        faces.add(face);
    }
}