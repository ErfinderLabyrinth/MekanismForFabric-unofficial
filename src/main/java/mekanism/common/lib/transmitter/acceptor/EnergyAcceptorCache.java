package mekanism.common.lib.transmitter.acceptor;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.content.network.transmitter.Transmitter;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import team.reborn.energy.api.EnergyStorage;

import java.util.Optional;

@NothingNullByDefault
public class EnergyAcceptorCache extends AcceptorCache<EnergyStorage> {


    public EnergyAcceptorCache(Transmitter<EnergyStorage, ?, ?> transmitter, TileEntityTransmitter transmitterTile) {
        super(transmitter, transmitterTile, EnergyStorage.SIDED);
    }

    /**
     * @apiNote Only call this from the server side
     */
    public boolean hasStrictEnergyHandlerAndListen(Level level, BlockPos pos, Direction side) {
        Direction opposite = side.getOpposite();
        EnergyStorage storage = lookup.find(level, pos, opposite);

        if (storage != null) {
            updateCachedAcceptorAndListen(side, level, pos, Optional.of(storage));
            return true;
        }

//        for (IEnergyCompat energyCompat : EnergyCompatUtils.getCompats()) {
//            if (energyCompat.isUsable()) {
//                IStrictEnergyHandler handler = energyCompat.getStrictEnergyHandler(level, pos, opposite);
//                if (handler != null) {
//                    if (energyCompat instanceof StrictEnergyCompat) {
//                        //Our lazy optional is already the proper type
//                        updateCachedAcceptorAndListen(side, level, pos, Optional.empty());
//                    } else {
//                        //Update the cache with the strict energy lazy optional as that is the one we interact with
//                        IStrictEnergyHandler wrappedAcceptor = energyCompat.getStrictEnergyHandler(level, pos, opposite);
//                        //Note: The wrapped acceptor should always be present, but double check just in case
//                        if (wrappedAcceptor != null) {
//                            updateCachedAcceptorAndListen(side, level, pos, Optional.of(wrappedAcceptor), Optional.empty(), false);
//                        }
//                    }
//                    return true;
//                }
//            }
//        }
        return false;
    }
}