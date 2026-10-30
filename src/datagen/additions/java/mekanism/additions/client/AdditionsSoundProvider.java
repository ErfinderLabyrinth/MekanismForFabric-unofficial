package mekanism.additions.client;

import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.registries.AdditionsSounds;
import mekanism.client.sound.BaseSoundProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class AdditionsSoundProvider extends BaseSoundProvider {

    public AdditionsSoundProvider(FabricDataOutput output) {
        super(output, MekanismAdditions.MODID);
    }

    @Override
    public void registerSounds(BiConsumer<ResourceLocation, SoundEventBuilder> creator) {
        addSoundEventWithSubtitle(creator, AdditionsSounds.POP, "pop");
    }
}