package mekanism.common.tier;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;
import org.jetbrains.annotations.Nullable;

import java.util.function.LongSupplier;

@NothingNullByDefault
public enum InductionProviderTier implements ITier {
    BASIC(BaseTier.BASIC, 256_000),
    ADVANCED(BaseTier.ADVANCED, 2_048_000),
    ELITE(BaseTier.ELITE, 16_384_000),
    ULTIMATE(BaseTier.ULTIMATE, 131_072_000);

    private final long baseOutput;
    private final BaseTier baseTier;
    @Nullable
    private LongSupplier outputReference;

    InductionProviderTier(BaseTier tier, long out) {
        baseOutput = out;
        baseTier = tier;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    public long getOutput() {
        return outputReference == null ? getBaseOutput() : outputReference.getAsLong();
    }

    public long getBaseOutput() {
        return baseOutput;
    }

    /**
     * ONLY CALL THIS FROM TierConfig. It is used to give the InductionProviderTier a reference to the actual config value object
     */
    public void setConfigReference(LongSupplier outputReference) {
        this.outputReference = outputReference;
    }
}