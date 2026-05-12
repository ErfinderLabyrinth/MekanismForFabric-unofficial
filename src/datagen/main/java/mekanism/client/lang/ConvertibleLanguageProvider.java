package mekanism.client.lang;

import java.util.List;
import mekanism.client.lang.FormatSplitter.Component;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.PackOutput;

public abstract class ConvertibleLanguageProvider extends FabricLanguageProvider {

    public ConvertibleLanguageProvider(FabricDataOutput dataGenerator, String locale) {
        super(dataGenerator, locale);
    }

    public abstract void convert(String key, List<Component> splitEnglish);

    @Override
    protected void addTranslations() {
    }
}