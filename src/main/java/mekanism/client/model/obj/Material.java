package mekanism.client.model.obj;

public final class Material {

    public String name;

    // diffuse texture
    public String mapKd;

    public float[] kd = new float[]{1f, 1f, 1f}; // diffuse color

    @Override
    public String toString() {
        return "Material{name='" + name + "', mapKd='" + mapKd + "'}";
    }
}