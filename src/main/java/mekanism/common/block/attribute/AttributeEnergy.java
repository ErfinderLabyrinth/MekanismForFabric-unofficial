package mekanism.common.block.attribute;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.LongSupplier;

//TODO: Eventually we may want to make these suppliers be used more like suppliers in that:
// if the config updates it doesn't require a server restart (or chunk reload to take effect
public class AttributeEnergy implements Attribute {

    private LongSupplier energyUsage = () -> 0;
    // 2 operations (20 secs) worth of ticks * usage
    private LongSupplier energyStorage = () -> energyUsage.getAsLong() * 400;

    public AttributeEnergy(@Nullable LongSupplier energyUsage, @Nullable LongSupplier energyStorage) {
        if (energyUsage != null) {
            this.energyUsage = energyUsage;
        }
        if (energyStorage != null) {
            this.energyStorage = energyStorage;
        }
    }

    @NotNull
    public long getUsage() {
        return energyUsage.getAsLong();
    }

    @NotNull
    public long getConfigStorage() {
        return energyStorage.getAsLong();
    }

    @NotNull
    public long getStorage() {
        return Long.max(getConfigStorage(), getUsage());
    }
}
