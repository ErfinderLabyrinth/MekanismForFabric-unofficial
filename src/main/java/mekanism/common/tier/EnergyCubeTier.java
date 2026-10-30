package mekanism.common.tier;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.LongSupplier;

@NothingNullByDefault
public enum EnergyCubeTier implements ITier, StringRepresentable {
    BASIC(BaseTier.BASIC, 4_000_000, 4_000),
    ADVANCED(BaseTier.ADVANCED, 16_000_000, 16_000),
    ELITE(BaseTier.ELITE, 64_000_000, 64_000),
    ULTIMATE(BaseTier.ULTIMATE, 256_000_000, 256_000),
    CREATIVE(BaseTier.CREATIVE, Long.MAX_VALUE, Long.MAX_VALUE);

    private final long baseMaxEnergy;
    private final long baseOutput;
    private final BaseTier baseTier;
    @Nullable
    private LongSupplier storageReference;
    @Nullable
    private LongSupplier outputReference;

    EnergyCubeTier(BaseTier tier, long max, long out) {
        baseMaxEnergy = max;
        baseOutput = out;
        baseTier = tier;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public long getMaxEnergy() {
        return storageReference == null ? getBaseMaxEnergy() : storageReference.getAsLong();
    }

    public long getOutput() {
        return outputReference == null ? getBaseOutput() : outputReference.getAsLong();
    }

    public long getBaseMaxEnergy() {
        return baseMaxEnergy;
    }

    public long getBaseOutput() {
        return baseOutput;
    }

    /**
     * ONLY CALL THIS FROM TierConfig. It is used to give the EnergyCubeTier a reference to the actual config value object
     */
    public void setConfigReference(LongSupplier storageReference, LongSupplier outputReference) {
        this.storageReference = storageReference;
        this.outputReference = outputReference;
    }
}