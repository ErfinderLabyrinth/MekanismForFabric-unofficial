package mekanism.common.registration;

import mekanism.api.annotations.NothingNullByDefault;

@NothingNullByDefault
public class DoubleWrappedRegistryObject<PRIMARY, SECONDARY> {

    protected final PRIMARY primary;
    protected final SECONDARY secondary;

    public DoubleWrappedRegistryObject(PRIMARY primary, SECONDARY secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public PRIMARY getPrimary() {
        return primary;
    }

    public SECONDARY getSecondary() {
        return secondary;
    }
}