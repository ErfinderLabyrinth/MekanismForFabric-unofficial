package mekanism.client.lang;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import mekanism.client.lang.FormatSplitter.Component;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public abstract class ConvertibleLanguageProvider implements DataProvider {
    protected final FabricDataOutput dataOutput;
    private final String languageCode;

    private final Map<String, String> translations = new TreeMap<>();

    public ConvertibleLanguageProvider(FabricDataOutput dataGenerator, String locale) {
        this.dataOutput = dataGenerator;
        this.languageCode = locale;
    }

    public abstract void convert(String key, List<Component> splitEnglish);

    public void add(String key, String translation) {
        translations.put(key, translation);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        JsonObject langEntryJson = new JsonObject();

        for (Map.Entry<String, String> entry : translations.entrySet()) {
            langEntryJson.addProperty(entry.getKey(), entry.getValue());
        }

        return DataProvider.saveStable(writer, langEntryJson, getLangFilePath(this.languageCode));
    }

    private Path getLangFilePath(String code) {
        return dataOutput
                .createPathProvider(PackOutput.Target.RESOURCE_PACK, "lang")
                .json(new ResourceLocation(dataOutput.getModId(), code));
    }

    @Override
    public String getName() {
        return "Mekanism Convertible Language (%s)".formatted(languageCode);
    }
}