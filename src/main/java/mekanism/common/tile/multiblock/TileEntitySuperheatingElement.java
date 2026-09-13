package mekanism.common.tile.multiblock;

import mekanism.common.content.boiler.BoilerMultiblockData;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.prefab.TileEntityInternalMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class TileEntitySuperheatingElement extends TileEntityInternalMultiblock {

    public TileEntitySuperheatingElement(BlockPos pos, BlockState state) {
        super(MekanismBlocks.SUPERHEATING_ELEMENT, pos, state);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        UUID multiblockUUID = getMultiblockUUID();
        setActive(multiblockUUID != null && BoilerMultiblockData.hotMap.getBoolean(multiblockUUID));
    }
}