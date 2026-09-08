package mekanism.common.lib.transmitter.acceptor;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.chemical.BoxedChemicalHandler;
import mekanism.common.content.network.transmitter.BoxedPressurizedTube;
import mekanism.common.lib.transmitter.acceptor.BoxedChemicalAcceptorCache.BoxedChemicalAcceptorInfo;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

//TODO - V11: Improve this so it only invalidates the types needed instead of doing all chemical types at once
@NothingNullByDefault
public class BoxedChemicalAcceptorCache extends AbstractAcceptorCache<BoxedChemicalHandler, BoxedChemicalAcceptorInfo> {

    public BoxedChemicalAcceptorCache(BoxedPressurizedTube transmitter, TileEntityTransmitter transmitterTile) {
        super(transmitter, transmitterTile);
    }

    private void updateCachedAcceptorAndListen(Direction side, BlockEntity acceptorTile, BoxedChemicalHandler acceptor) {
        boolean dirtyAcceptor = false;
        if (cachedAcceptors.containsKey(side)) {
            BoxedChemicalAcceptorInfo acceptorInfo = cachedAcceptors.get(side);
            if (acceptorTile.getLevel() != acceptorInfo.getLevel() || !acceptorTile.getBlockPos().equals(acceptorInfo.getPos())) {
                //The tile changed, fully invalidate it
                cachedAcceptors.put(side, new BoxedChemicalAcceptorInfo(acceptorTile.getLevel(), acceptorTile.getBlockPos(), acceptor));
                dirtyAcceptor = true;
            } else if (!acceptor.sameHandlers(acceptorInfo.boxedHandler)) {
                //The source acceptor is different, make sure we update it and the actual acceptor
                // This allows us to make sure we only mark the acceptor as dirty if it actually changed
                // Use case: Wrapped energy acceptors
                acceptorInfo.updateAcceptor(acceptor);
                dirtyAcceptor = true;
            }
        } else {
            cachedAcceptors.put(side, new BoxedChemicalAcceptorInfo(acceptorTile.getLevel(), acceptorTile.getBlockPos(), acceptor));
            dirtyAcceptor = true;
        }
        if (dirtyAcceptor) {
            transmitter.markDirtyAcceptor(side);
            //If the capability is present, and we want to add the listener, add a listener to all the types so that once it gets invalidated
            // we recheck that side assuming that the world and position is still loaded and our tile has not been removed
            acceptor.addRefreshListeners(getRefreshListener(side));
        }
    }

    public boolean isChemicalAcceptorAndListen(@Nullable BlockEntity tile, Direction side) {
        //TODO: Improve this to make it easier to add more chemical types
        Direction opposite = side.getOpposite();
        Storage<Gas> gasAcceptor = Capabilities.GAS_HANDLER_BLOCK.find(tile.getLevel(), tile.getBlockPos(), opposite);
        Storage<InfuseType> infusionAcceptor = Capabilities.INFUSION_HANDLER_BLOCK.find(tile.getLevel(), tile.getBlockPos(), opposite);
        Storage<Pigment> pigmentAcceptor = Capabilities.PIGMENT_HANDLER_BLOCK.find(tile.getLevel(), tile.getBlockPos(), opposite);
        Storage<Slurry> slurryAcceptor = Capabilities.SLURRY_HANDLER_BLOCK.find(tile.getLevel(), tile.getBlockPos(), opposite);
        if (gasAcceptor != null || infusionAcceptor != null || pigmentAcceptor != null || slurryAcceptor != null) {
            BoxedChemicalHandler chemicalHandler = new BoxedChemicalHandler();
            if (gasAcceptor != null) {
                chemicalHandler.addGasHandler(Optional.of(gasAcceptor));
            }
            if (infusionAcceptor != null) {
                chemicalHandler.addInfusionHandler(Optional.of(infusionAcceptor));
            }
            if (pigmentAcceptor != null) {
                chemicalHandler.addPigmentHandler(Optional.of(pigmentAcceptor));
            }
            if (slurryAcceptor != null) {
                chemicalHandler.addSlurryHandler(Optional.of(slurryAcceptor));
            }
            //Update the cached acceptor and if it changed, add a listener to it to listen for invalidation
            updateCachedAcceptorAndListen(side, tile, chemicalHandler);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote We override this method to skip wrapping it in a lazy optional before resolving them
     */
    @Override
    public List<BoxedChemicalHandler> getConnectedAcceptors(Set<Direction> sides) {
        List<BoxedChemicalHandler> acceptors = new ArrayList<>(sides.size());
        for (Direction side : sides) {
            if (cachedAcceptors.containsKey(side)) {
                BoxedChemicalAcceptorInfo acceptorInfo = cachedAcceptors.get(side);
                BlockEntity tile = acceptorInfo.getLevel().getBlockEntity(acceptorInfo.getPos());
                if (tile != null && isChemicalAcceptorAndListen(tile, side)) {
                    acceptors.add(acceptorInfo.boxedHandler);
                }
            }
        }
        return acceptors;
    }

    @Override
    public Optional<BoxedChemicalHandler> getConnectedAcceptor(Direction side) {
        if (cachedAcceptors.containsKey(side)) {
            BoxedChemicalAcceptorInfo acceptorInfo = cachedAcceptors.get(side);
            BlockEntity tile = acceptorInfo.getLevel().getBlockEntity(acceptorInfo.getPos());
            if (tile != null && isChemicalAcceptorAndListen(tile, side)) {
                return acceptorInfo.getAsOptional();
            }
        }
        return Optional.empty();
    }

    public static class BoxedChemicalAcceptorInfo extends AbstractAcceptorInfo {

        private BoxedChemicalHandler boxedHandler;
        @Nullable
        private Optional<BoxedChemicalHandler> asOptional;

        private BoxedChemicalAcceptorInfo(Level level, BlockPos pos, BoxedChemicalHandler boxedHandler) {
            super(level, pos);
            this.boxedHandler = boxedHandler;
        }

        public void updateAcceptor(BoxedChemicalHandler acceptor) {
            boxedHandler = acceptor;
            asOptional = null;
        }

        private Optional<BoxedChemicalHandler> getAsOptional() {
            if (asOptional == null) {
                //Lazily calculate the lazy optional value of the boxed chemical handler
                asOptional = Optional.of(boxedHandler);
            }
            return asOptional;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BoxedChemicalAcceptorInfo other = (BoxedChemicalAcceptorInfo) o;
            return boxedHandler.equals(other.boxedHandler);
        }

        @Override
        public int hashCode() {
            return boxedHandler.hashCode();
        }
    }
}