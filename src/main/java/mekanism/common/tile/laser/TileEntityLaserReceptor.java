package mekanism.common.tile.laser;

import mekanism.api.AutomationType;
import mekanism.api.lasers.ILaserReceptor;
import mekanism.api.providers.IBlockProvider;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TileEntityLaserReceptor extends TileEntityBasicLaser implements ILaserReceptor {

    public TileEntityLaserReceptor(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
//        addCapabilityResolver(BasicCapabilityResolver.constant(Capabilities.LASER_RECEPTOR, this));
    }

    @Override
    public void receiveLaserEnergy(long energy) {
        try(Transaction t = Transaction.openOuter()) {
            energyContainer.insert(energy, t, AutomationType.INTERNAL);
            t.commit();
        }
    }

    @Override
    public boolean canLasersDig() {
        return false;
    }
}