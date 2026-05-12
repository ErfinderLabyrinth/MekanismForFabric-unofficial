package mekanism.common.registration.impl;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.text.ILangEntry;
import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

@NothingNullByDefault
public class SoundEventRegistryObject<SOUND extends SoundEvent> extends WrappedRegistryObject<SOUND> implements ILangEntry {

    private final String translationKey;

    public SoundEventRegistryObject(SOUND registryObject, ResourceLocation rl) {
        super(registryObject);
        translationKey = Util.makeDescriptionId("sound_event", rl);
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }
}