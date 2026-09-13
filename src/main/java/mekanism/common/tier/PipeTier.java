package mekanism.common.tier;

import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;
import mekanism.common.util.EnumUtils;

import java.util.function.IntSupplier;

public enum PipeTier implements ITier {
    BASIC(BaseTier.BASIC, 2_000 * 81, 250 * 81),
    ADVANCED(BaseTier.ADVANCED, 8_000 * 81, 1_000 * 81),
    ELITE(BaseTier.ELITE, 32_000 * 81, 8_000 * 81),
    ULTIMATE(BaseTier.ULTIMATE, 128_000 * 81, 32_000 * 81);

    private final int baseCapacity;
    private final int basePull;
    private final BaseTier baseTier;
    private IntSupplier capacityReference;
    private IntSupplier pullReference;

    PipeTier(BaseTier tier, int capacity, int pullAmount) {
        baseCapacity = capacity;
        basePull = pullAmount;
        baseTier = tier;
    }

    public static PipeTier get(BaseTier tier) {
        for (PipeTier transmitter : EnumUtils.PIPE_TIERS) {
            if (transmitter.getBaseTier() == tier) {
                return transmitter;
            }
        }
        return BASIC;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    public int getPipeCapacity() {
        return capacityReference == null ? getBaseCapacity() : capacityReference.getAsInt();
    }

    public int getPipePullAmount() {
        return pullReference == null ? getBasePull() : pullReference.getAsInt();
    }

    public int getBaseCapacity() {
        return baseCapacity;
    }

    public int getBasePull() {
        return basePull;
    }

    /**
     * ONLY CALL THIS FROM TierConfig. It is used to give the PipeTier a reference to the actual config value object
     */
    public void setConfigReference(IntSupplier capacityReference, IntSupplier pullReference) {
        this.capacityReference = capacityReference;
        this.pullReference = pullReference;
    }
}