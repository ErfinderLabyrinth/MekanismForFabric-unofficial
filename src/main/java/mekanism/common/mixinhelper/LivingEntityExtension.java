package mekanism.common.mixinhelper;

public interface LivingEntityExtension {
    ThreadLocal<Boolean> SUPPRESS_SOUND = ThreadLocal.withInitial(() -> false);

    float mekanism$getSwimSpeedModifier();

    void mekanism$setSwimSpeedModifier(float swimSpeedModifier);
}
