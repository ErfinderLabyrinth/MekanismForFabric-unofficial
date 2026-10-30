package mekanism.common.config.listener;

import mekanism.api.math.FloatingLong;
import mekanism.api.math.FloatingLongSupplier;

import java.util.function.Supplier;

public class ConfigBasedCachedFLSupplier extends ConfigBasedCachedSupplier<FloatingLong> implements FloatingLongSupplier {

    public ConfigBasedCachedFLSupplier(Supplier<FloatingLong> resolver) {
        super(resolver);
    }
}