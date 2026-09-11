package mekanism.generators.common.loot;

import mekanism.common.loot.table.BaseBlockLootTables;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public class GeneratorsBlockLootTables extends BaseBlockLootTables {
    public GeneratorsBlockLootTables(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate() {
        dropSelfWithContents(GeneratorsBlocks.BLOCKS.getAllBlocks());
    }
}