package mekanism.additions.client;

import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.registries.AdditionsSounds;
import mekanism.client.sound.BaseSoundProvider;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AdditionsSoundProvider extends BaseSoundProvider {

    public AdditionsSoundProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, existingFileHelper, MekanismAdditions.MODID);
    }

    @Override
    public void registerSounds(BiConsumer<ResourceLocation, SoundEventBuilder> creator) {
        addSoundEventWithSubtitle(AdditionsSounds.POP, "pop");
    }
}