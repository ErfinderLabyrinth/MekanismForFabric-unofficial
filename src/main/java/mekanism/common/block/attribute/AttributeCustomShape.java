package mekanism.common.block.attribute;

import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;

public record AttributeCustomShape(VoxelShape[] bounds) implements Attribute {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof AttributeCustomShape other && Arrays.equals(bounds, other.bounds);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bounds);
    }
}
