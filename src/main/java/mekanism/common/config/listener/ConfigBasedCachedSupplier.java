package mekanism.common.config.listener;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ConfigBasedCachedSupplier<VALUE> implements Supplier<VALUE> {

    private final Supplier<VALUE> resolver;

    public ConfigBasedCachedSupplier(Supplier<VALUE> resolver) {
        this.resolver = resolver;
    }

    @NotNull
    @Override
    public VALUE get() {
        return resolver.get();
    }
}