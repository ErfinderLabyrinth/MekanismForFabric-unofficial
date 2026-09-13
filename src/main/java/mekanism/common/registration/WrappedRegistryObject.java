package mekanism.common.registration;

import mekanism.api.annotations.NothingNullByDefault;

import java.util.function.Supplier;

@NothingNullByDefault
public class WrappedRegistryObject<T> implements Supplier<T> {

    protected T object;

    protected WrappedRegistryObject(T object) {
        this.object = object;
    }

    @Override
    public T get() {
        return object;
    }
}