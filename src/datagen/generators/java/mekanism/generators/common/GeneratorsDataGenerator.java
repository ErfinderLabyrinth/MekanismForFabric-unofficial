package mekanism.generators.common;

import mekanism.common.Bas    ePackMetadataGenerator;
import mekanism.common.MekanismDataGenerator;
import mekanism.generators.client.GeneratorsBlockStateProvider;
import mekanism.generators.client.GeneratorsItemModelProvider;
import mekanism.generators.client.GeneratorsLangProvider;
import mekanism.generators.client.GeneratorsSoundProvider;
import mekanism.generators.client.GeneratorsSpriteSourceProvider;
import mekanism.generators.common.loot.GeneratorsLootProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public class GeneratorsDataGenerator implements DataGeneratorEntrypoint {

    private GeneratorsDataGenerator() {
    }

    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider((FabricDataOutput output) -> new BasePackMetadataGenerator(output, GeneratorsLang.PACK_DESCRIPTION));
        //Client side data generators
        MekanismDataGenerator.addProvider(pack, GeneratorsLangProvider::new);
        pack.addProvider(GeneratorsSoundProvider::new);
        pack.addProvider(GeneratorsSpriteSourceProvider::new);
        pack.addProvider(GeneratorsItemModelProvider::new);
        pack.addProvider(GeneratorsBlockStateProvider::new);
        //Server side data generators
        pack.addProvider(GeneratorsTagProvider::new);
        MekanismDataGenerator.addProvider(pack, GeneratorsLootProvider::new);
        pack.addProvider(GeneratorsRecipeProvider::new);
        pack.addProvider(GeneratorsAdvancementProvider::new);
    }
}