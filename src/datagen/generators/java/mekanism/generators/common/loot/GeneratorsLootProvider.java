package mekanism.generators.common.loot;

import java.util.List;
import mekanism.common.loot.BaseLootProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class GeneratorsLootProvider extends BaseLootProvider {

    public GeneratorsLootProvider(FabricDataOutput output) {
        super(output, List.of(
              new SubProviderEntry(() -> new GeneratorsBlockLootTables(output), LootContextParamSets.BLOCK)
        ));
    }
}