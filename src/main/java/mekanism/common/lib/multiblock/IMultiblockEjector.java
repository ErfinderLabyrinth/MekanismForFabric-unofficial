package mekanism.common.lib.multiblock;

import net.minecraft.core.Direction;

import java.util.Set;

public interface IMultiblockEjector {

    void setEjectSides(Set<Direction> sides);
}