package mekanism.client.model;

import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelState;

public class SimpleModelState implements ModelState {
    Transformation rotation;
    boolean uvLocked;

    public SimpleModelState(Transformation rotation, boolean uvLocked) {
        this.rotation = rotation;
        this.uvLocked = uvLocked;
    }

    @Override
    public Transformation getRotation() {
        return rotation;
    }

    @Override
    public boolean isUvLocked() {
        return uvLocked;
    }
}
