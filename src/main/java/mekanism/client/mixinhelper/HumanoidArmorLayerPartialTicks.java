package mekanism.client.mixinhelper;

public class HumanoidArmorLayerPartialTicks {
    public static final ThreadLocal<Float> THREAD_LOCAL = ThreadLocal.withInitial(() -> 0F);
}
