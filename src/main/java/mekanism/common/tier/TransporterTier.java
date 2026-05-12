package mekanism.common.tier;

import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;
import mekanism.common.util.EnumUtils;

import java.util.function.IntSupplier;

public enum TransporterTier implements ITier {
    BASIC(BaseTier.BASIC, 1, 5),
    ADVANCED(BaseTier.ADVANCED, 16, 10),
    ELITE(BaseTier.ELITE, 32, 20),
    ULTIMATE(BaseTier.ULTIMATE, 64, 50);

    private final int basePull;
    private final int baseSpeed;
    private final BaseTier baseTier;
    private IntSupplier pullReference;
    private IntSupplier speedReference;

    TransporterTier(BaseTier tier, int pull, int s) {
        basePull = pull;
        baseSpeed = s;
        baseTier = tier;
    }

    public static TransporterTier get(BaseTier tier) {
        for (TransporterTier transmitter : EnumUtils.TRANSPORTER_TIERS) {
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

    public int getPullAmount() {
        return pullReference == null ? getBasePull() : pullReference.getAsInt();
    }

    public int getSpeed() {
        return speedReference == null ? getBaseSpeed() : speedReference.getAsInt();
    }

    public int getBasePull() {
        return basePull;
    }

    public int getBaseSpeed() {
        return baseSpeed;
    }

    /**
     * ONLY CALL THIS FROM TierConfig. It is used to give the TransporterTier a reference to the actual config value object
     */
    public void setConfigReference(IntSupplier pullReference, IntSupplier speedReference) {
        this.pullReference = pullReference;
        this.speedReference = speedReference;
    }
}