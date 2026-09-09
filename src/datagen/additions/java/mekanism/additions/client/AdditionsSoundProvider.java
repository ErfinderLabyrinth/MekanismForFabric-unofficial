package mekanism.additions.client;

import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.registries.AdditionsSounds;
import mekanism.client.sound.BaseSoundProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class AdditionsSoundProvider extends BaseSoundProvider {

    public AdditionsSoundProvider(PackOutput output) {
        super(output, MekanismAdditions.MODID);
    }

    @Override
    public void registerSounds(BiConsumer<ResourceLocation, SoundEventBuilder> creator) {
        addSoundEventWithSubtitle(creator, AdditionsSounds.POP, "pop");
    }
}