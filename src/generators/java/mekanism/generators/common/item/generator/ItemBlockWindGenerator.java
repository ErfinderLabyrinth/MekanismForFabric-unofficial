package mekanism.generators.common.item.generator;

import java.util.function.Consumer;

import mekanism.client.render.RenderPropertiesProvider;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.item.block.machine.ItemBlockMachine;
import mekanism.generators.client.render.GeneratorsRenderPropertiesProvider;

public class ItemBlockWindGenerator extends ItemBlockMachine implements RenderPropertiesProvider.MekRenderPropertiesGetter {

    public ItemBlockWindGenerator(BlockTile<?, ?> block) {
        super(block);
    }

    @Override
    public RenderPropertiesProvider.MekRenderProperties getRenderProperties() {
        return GeneratorsRenderPropertiesProvider.wind();
    }
}