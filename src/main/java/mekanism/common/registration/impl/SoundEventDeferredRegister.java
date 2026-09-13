package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEventDeferredRegister extends WrappedDeferredRegister<SoundEvent> {

    //We need to store the modid because the deferred register doesn't let you get the modid back out
    private final String modid;

    public SoundEventDeferredRegister(String modid) {
        super(BuiltInRegistries.SOUND_EVENT);
        this.modid = modid;
    }

    public SoundEventRegistryObject<SoundEvent> register(String name) {
        ResourceLocation rl = new ResourceLocation(modid, name);
        return register(rl, () -> SoundEvent.createVariableRangeEvent(rl), sound -> new SoundEventRegistryObject((SoundEvent) sound, rl));
    }
}