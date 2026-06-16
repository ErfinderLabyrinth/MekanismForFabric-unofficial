package mekanism.client.sound;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mekanism.api.text.ILangEntry;
import mekanism.common.registration.impl.SoundEventRegistryObject;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class BaseSoundProvider implements DataProvider {

    private final String modid;
    private final PackOutput.PathProvider soundPathProvider;

    protected BaseSoundProvider(PackOutput output, String modid) {
        //super(output, modid, existingFileHelper);
        this.modid = modid;
        soundPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "");
    }

    @NotNull
    @Override
    public String getName() {
        return "Mekanism.BaseSoundProvider: " + modid;
    }

    protected void addSoundEventWithSubtitle(BiConsumer<ResourceLocation, SoundEventBuilder> creator, SoundEventRegistryObject<?> soundEventRO, String path) {
        addSoundEventWithSubtitle(creator, soundEventRO, path, UnaryOperator.identity());
    }

    protected void addSoundEventWithSubtitle(BiConsumer<ResourceLocation, SoundEventBuilder> creator, SoundEventRegistryObject<?> soundEventRO, String path, UnaryOperator<SoundBuilder> soundModifier) {
        addSoundEvent(creator, soundEventRO, path, definition -> definition.withSubtitle(soundEventRO.getTranslationKey()), soundModifier);
    }

    protected void addSoundEvent(BiConsumer<ResourceLocation, SoundEventBuilder> creator, SoundEventRegistryObject<?> soundEventRO, String path, ILangEntry subtitle) {
        addSoundEvent(creator, soundEventRO, path, definition -> definition.withSubtitle(subtitle.getTranslationKey()), UnaryOperator.identity());
    }

    protected void addSoundEvent(BiConsumer<ResourceLocation, SoundEventBuilder> creator, SoundEventRegistryObject<?> soundEventRO, String path, UnaryOperator<SoundEventBuilder> definitionModifier,
          UnaryOperator<SoundBuilder> soundModifier) {
        creator.accept(soundEventRO.get().getLocation(), definitionModifier.apply(new SoundEventBuilder()).withSound(soundModifier.apply(new SoundBuilder(new ResourceLocation(modid, path)))));
        //add(soundEventRO.get(), definitionModifier.apply(definition()).with(soundModifier.apply(sound(new ResourceLocation(modid, path)))));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        Map<ResourceLocation, SoundEventBuilder> soundEventRegistrations = new HashMap<>();
        registerSounds(soundEventRegistrations::put);

        JsonElement json = new JsonArray();//TODO

        return DataProvider.saveStable(cachedOutput, json, soundPathProvider.json(new ResourceLocation(modid, "sounds")));
    }

    protected abstract void registerSounds(BiConsumer<ResourceLocation, SoundEventBuilder> creator);

    public class SoundEventBuilder {
        Optional<String> subtitle = Optional.empty();
        Optional<Boolean> replace = Optional.empty();
        List<SoundBuilder> sounds = new ArrayList<>();

        public SoundEventBuilder withReplace(boolean replace) {
            this.replace = Optional.of(replace);
            return this;
        }

        public SoundEventBuilder withSubtitle(String subtitle) {
            this.subtitle = Optional.of(subtitle);
            return this;
        }

        public SoundEventBuilder withSound(SoundBuilder sound) {
            this.sounds.add(sound);
            return this;
        }

        public JsonElement toJson() {
            JsonObject json = new JsonObject();
            if (replace.isPresent()) {
                json.addProperty("replace", replace.get());
            }
            if (subtitle.isPresent()) {
                json.addProperty("subtitle", subtitle.get());
            }
            JsonArray soundsJson = new JsonArray();
            sounds.stream().map(SoundBuilder::toJson).forEach(s -> soundsJson.add(s));
            json.add("sounds", soundsJson);
            return json;
        }
    }

    public class SoundBuilder {
        ResourceLocation name;
        Optional<Float> volume = Optional.empty();
        Optional<Float> pitch = Optional.empty();
        Optional<Integer> weight = Optional.empty();
        Optional<Boolean> stream = Optional.empty();
        Optional<Integer> attenuationDistance = Optional.empty();
        Optional<Boolean> preload = Optional.empty();
        Optional<Sound.Type> type = Optional.empty();

        public SoundBuilder(ResourceLocation name) {
            this.name = name;
        }

        public SoundBuilder withVolume(float volume) {
            this.volume = Optional.of(volume);
            return this;
        }

        public SoundBuilder withPitch(float pitch) {
            this.pitch = Optional.of(pitch);
            return this;
        }

        public SoundBuilder withWeight(int weight) {
            this.weight = Optional.of(weight);
            return this;
        }

        public SoundBuilder withAttenuationDistance(int attenuationDistance) {
            this.attenuationDistance = Optional.of(attenuationDistance);
            return this;
        }

        public SoundBuilder withStream(boolean stream) {
            this.stream = Optional.of(stream);
            return this;
        }

        public SoundBuilder withPreload(boolean preload) {
            this.preload = Optional.of(preload);
            return this;
        }

        public SoundBuilder withType(Sound.Type type) {
            this.type = Optional.of(type);
            return this;
        }

        public JsonElement toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("name", name.toString());
            if (volume.isPresent()) {
                json.addProperty("volume", volume.get());
            }
            if (pitch.isPresent()) {
                json.addProperty("pitch", pitch.get());
            }
            if (weight.isPresent()) {
                json.addProperty("weight", weight.get());
            }
            if (stream.isPresent()) {
                json.addProperty("stream", stream.get());
            }
            if (attenuationDistance.isPresent()) {
                json.addProperty("attenuation_distance", attenuationDistance.get());
            }
            if (preload.isPresent()) {
                json.addProperty("preload", preload.get());
            }
            if (type.isPresent()) {
                json.addProperty("preload", type.get().name());
            }
            return json;
        }
    }
}