package mekanism.common.capabilities.holder;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IHolder<TYPE> {

    default boolean canInsert(@Nullable Direction direction) {
        return true;
    }

    default boolean canExtract(@Nullable Direction direction) {
        return true;
    }

    default int indexOf(TYPE type) {
        return getAll().indexOf(type);
    }

    default TYPE get(int index) {
        return index < 0 || index >= getAll().size() ? null : getAll().get(index);
    }

    List<TYPE> getAll();
}