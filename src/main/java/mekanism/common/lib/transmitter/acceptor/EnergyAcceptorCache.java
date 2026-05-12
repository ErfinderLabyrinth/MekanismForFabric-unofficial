package mekanism.common.lib.transmitter.acceptor;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.content.network.transmitter.Transmitter;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.integration.energy.IEnergyCompat;
import mekanism.common.integration.energy.StrictEnergyCompat;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.Optional;

@NothingNullByDefault
public class EnergyAcceptorCache extends AcceptorCache<IStrictEnergyHandler> {


    public EnergyAcceptorCache(Transmitter<IStrictEnergyHandler, ?, ?> transmitter, TileEntityTransmitter transmitterTile) {
        super(transmitter, transmitterTile, Capabilities.STRICT_ENERGY_BLOCK);
    }

    /**
     * @apiNote Only call this from the server side
     */
    public boolean hasStrictEnergyHandlerAndListen(Level level, BlockPos pos, Direction side) {
        Direction opposite = side.getOpposite();
        for (IEnergyCompat energyCompat : EnergyCompatUtils.getCompats()) {
            if (energyCompat.isUsable()) {
                IStrictEnergyHandler handler = energyCompat.getStrictEnergyHandler(level, pos, side);
                if (handler != null) {
                    if (energyCompat instanceof StrictEnergyCompat) {
                        //Our lazy optional is already the proper type
                        updateCachedAcceptorAndListen(side, level, pos, Optional.empty());
                    } else {
                        //Update the cache with the strict energy lazy optional as that is the one we interact with
                        IStrictEnergyHandler wrappedAcceptor = energyCompat.getStrictEnergyHandler(level, pos, opposite);
                        //Note: The wrapped acceptor should always be present, but double check just in case
                        if (wrappedAcceptor != null) {
                            updateCachedAcceptorAndListen(side, level, pos, Optional.of(wrappedAcceptor), Optional.empty(), false);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
}