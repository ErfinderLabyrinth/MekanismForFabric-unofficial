package mekanism.tools.common;

import mekanism.common.MekanismDataGenerator;
import mekanism.tools.client.ToolsItemModelProvider;
import mekanism.tools.client.ToolsLangProvider;
import mekanism.tools.client.ToolsSpriteSourceProvider;
import mekanism.tools.common.recipe.ToolsRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ToolsDataGenerator implements DataGeneratorEntrypoint {

    private ToolsDataGenerator() {
    }

    public void onInitializeDataGenerator(FabricDataGenerator gen) {
        MekanismDataGenerator.bootstrapConfigs(MekanismTools.MODID);
        FabricDataGenerator.Pack pack = gen.createPack();
        //pack.addProvider(output -> new BasePackMetadataGenerator(output, ToolsLang.PACK_DESCRIPTION));
        //Client side data generators
        pack.addProvider(ToolsLangProvider::new);
        pack.addProvider(ToolsSpriteSourceProvider::new);
        pack.addProvider(ToolsItemModelProvider::new);
        //Server side data generators
        pack.addProvider(ToolsTagProvider::new);
        pack.addProvider(ToolsRecipeProvider::new);
        pack.addProvider(ToolsAdvancementProvider::new);
    }
}