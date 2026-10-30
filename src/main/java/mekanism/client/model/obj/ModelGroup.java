package mekanism.client.model.obj;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModelGroup {

    private final String name;
    private final Map<String, ModelObject> objects = new LinkedHashMap<>();

    public ModelGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ModelObject getObject(String name) {
        return objects.get(name);
    }

    public ModelObject getOrCreateObject(String name) {
        return objects.computeIfAbsent(name, ModelObject::new);
    }

    public Collection<ModelObject> getObjects() {
        return objects.values();
    }

    public Map<String, ModelObject> getRawObjects() {
        return objects;
    }

    public boolean hasObject(String objectName) {
        return objects.containsKey(objectName);
    }
}