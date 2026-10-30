package mekanism.additions.common;

import mekanism.additions.client.AdditionsBlockStateProvider;
import mekanism.additions.client.AdditionsLangProvider;
import mekanism.additions.client.AdditionsSoundProvider;
import mekanism.additions.client.AdditionsSpriteSourceProvider;
import mekanism.additions.common.loot.AdditionsLootProvider;
import mekanism.additions.common.recipe.AdditionsRecipeProvider;
import mekanism.common.MekanismDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class AdditionsDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        //MekanismDataGenerator.bootstrapConfigs(MekanismAdditions.MODID);
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        //gen.addProvider(true, new BasePackMetadataGenerator(output, AdditionsLang.PACK_DESCRIPTION));
        //Client side data generators
        MekanismDataGenerator.addProvider(pack, AdditionsLangProvider::new);
        pack.addProvider(AdditionsSoundProvider::new);
        pack.addProvider(AdditionsSpriteSourceProvider::new);
        pack.addProvider(AdditionsBlockStateProvider::new);
        //Server side data generators
        pack.addProvider(AdditionsTagProvider::new);
        MekanismDataGenerator.addProvider(pack, AdditionsLootProvider::new);
        pack.addProvider(AdditionsRecipeProvider::new);
        pack.addProvider(AdditionsAdvancementProvider::new);
    }
}