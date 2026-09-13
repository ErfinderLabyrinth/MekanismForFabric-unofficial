package mekanism.generators.client;

import mekanism.client.sound.BaseSoundProvider;
import mekanism.generators.common.MekanismGenerators;
import mekanism.generators.common.registries.GeneratorsSounds;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class GeneratorsSoundProvider extends BaseSoundProvider {

    public GeneratorsSoundProvider(FabricDataOutput output) {
        super(output, MekanismGenerators.MODID);
    }

    @Override
    public void registerSounds(BiConsumer<ResourceLocation, SoundEventBuilder> creator) {
        addSoundEventWithSubtitle(creator, GeneratorsSounds.FUSION_REACTOR, "fusion_reactor");
        addSoundEventWithSubtitle(creator, GeneratorsSounds.FISSION_REACTOR, "fission_reactor");
        addGeneratorSoundEvents(creator);
    }

    private void addGeneratorSoundEvents(BiConsumer<ResourceLocation, SoundEventBuilder> creator) {
        String basePath = "generator/";
        addSoundEventWithSubtitle(creator, GeneratorsSounds.BIO_GENERATOR, basePath + "bio");
        addSoundEventWithSubtitle(creator, GeneratorsSounds.GAS_BURNING_GENERATOR, basePath + "gas_burning");
        addSoundEventWithSubtitle(creator, GeneratorsSounds.HEAT_GENERATOR, basePath + "heat");
        //Use a reduced attenuation range for passive generators
        addSoundEventWithSubtitle(creator, GeneratorsSounds.SOLAR_GENERATOR, basePath + "solar", sound -> sound.withAttenuationDistance(8));
        addSoundEventWithSubtitle(creator, GeneratorsSounds.WIND_GENERATOR, basePath + "wind", sound -> sound.withAttenuationDistance(8));
    }
}