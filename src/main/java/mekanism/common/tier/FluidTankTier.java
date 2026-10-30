package mekanism.common.tier;

import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;

import java.util.function.IntSupplier;

public enum FluidTankTier implements ITier {
    BASIC(BaseTier.BASIC, 32_000 * 81, 1_000 * 81),
    ADVANCED(BaseTier.ADVANCED, 64_000 * 81, 4_000 * 81),
    ELITE(BaseTier.ELITE, 128_000 * 81, 16_000 * 81),
    ULTIMATE(BaseTier.ULTIMATE, 256_000 * 81, 64_000 * 81),
    CREATIVE(BaseTier.CREATIVE, Integer.MAX_VALUE, Integer.MAX_VALUE / 2);

    private final int baseStorage;
    private final int baseOutput;
    private final BaseTier baseTier;
    private IntSupplier storageReference;
    private IntSupplier outputReference;

    FluidTankTier(BaseTier tier, int s, int o) {
        baseStorage = s;
        baseOutput = o;
        baseTier = tier;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    public int getStorage() {
        return storageReference == null ? getBaseStorage() : storageReference.getAsInt();
    }

    public int getOutput() {
        return outputReference == null ? getBaseOutput() : outputReference.getAsInt();
    }

    public int getBaseStorage() {
        return baseStorage;
    }

    public int getBaseOutput() {
        return baseOutput;
    }

    /**
     * ONLY CALL THIS FROM TierConfig. It is used to give the FluidTankTier a reference to the actual config value object
     */
    public void setConfigReference(IntSupplier storageReference, IntSupplier outputReference) {
        this.storageReference = storageReference;
        this.outputReference = outputReference;
    }
}