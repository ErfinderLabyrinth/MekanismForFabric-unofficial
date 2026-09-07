package mekanism.common.config;

public interface IMekanismConfig {

    @Deprecated(forRemoval = true)
    String getFileName();

    @Deprecated(forRemoval = true)
    default Object getConfigSpec() {
        throw new UnsupportedOperationException("Not supported on the fabric port");
    }

    @Deprecated()
    default boolean isLoaded() {
        return true;
    }

    @Deprecated(forRemoval = true)
    default Object getConfigType() {
        throw new UnsupportedOperationException("Not supported on the fabric port");
    }

    @Deprecated(forRemoval = true)
    default void save() {
        throw new UnsupportedOperationException("Not supported on the fabric port");
    }

    @Deprecated
    default void clearCache(boolean unloading) {

    };

//    @Deprecated
//    default void addCachedValue(CachedValue<?> configValue) {
//
//    };

    /**
     * Should this config be added to the mods "config" files. Make this return false to only create the config. This will allow it to be tracked, but not override the
     * value that has already been added to this mod's container. As the list is from config type to mod config.
     */
    @Deprecated
    default boolean addToContainer() {
        return true;
    }
}