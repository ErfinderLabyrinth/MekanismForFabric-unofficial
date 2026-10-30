package mekanism.additions.common.loot;

import mekanism.common.loot.BaseLootProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;

public class AdditionsLootProvider extends BaseLootProvider {

    public AdditionsLootProvider(FabricDataOutput output) {
        super(output, List.of(
              new SubProviderEntry(() -> new AdditionsBlockLootTables(output), LootContextParamSets.BLOCK),
              new SubProviderEntry(() -> new AdditionsEntityLootTables(output), LootContextParamSets.ENTITY)
        ));
    }
}