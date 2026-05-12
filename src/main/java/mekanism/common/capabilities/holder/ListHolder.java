package mekanism.common.capabilities.holder;

import java.util.List;

public class ListHolder<TYPE> implements IHolder<TYPE> {
    List<TYPE> types;
    public ListHolder(List<TYPE> types) {
        this.types = types;
    }

    @Override
    public List<TYPE> getAll() {
        return types;
    }
}
