package mekanism.common.tile.multiblock;

import mekanism.api.providers.IBlockProvider;
import mekanism.common.Mekanism;
import mekanism.common.content.matrix.MatrixMultiblockData;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismContainerTypes;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityInductionCasing extends TileEntityMultiblock<MatrixMultiblockData> {

    public TileEntityInductionCasing(BlockPos pos, BlockState state) {
        this(MekanismBlocks.INDUCTION_CASING, pos, state);
        //Disable item handler caps if we are the induction casing, don't disable it for the subclassed port though
    }

    public TileEntityInductionCasing(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    public @Nullable Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        return null;
    }

    @NotNull
    @Override
    public MatrixMultiblockData createMultiblock() {
        return new MatrixMultiblockData(this);
    }

    @Override
    public MultiblockManager<MatrixMultiblockData> getManager() {
        return Mekanism.matrixManager;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        if (container.getType() == MekanismContainerTypes.MATRIX_STATS.get()) {
            getMultiblock().addSyncables(container::track, MatrixMultiblockData.STATS_TAB);
        }
    }
}