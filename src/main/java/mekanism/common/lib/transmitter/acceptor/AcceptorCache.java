package mekanism.common.lib.transmitter.acceptor;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.content.network.transmitter.Transmitter;
import mekanism.common.lib.transmitter.acceptor.AcceptorCache.AcceptorInfo;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

//TODO remove and replace?
@NothingNullByDefault
public class AcceptorCache<ACCEPTOR> extends AbstractAcceptorCache<ACCEPTOR, AcceptorInfo<ACCEPTOR>> {
    BlockApiLookup<ACCEPTOR, Direction> lookup;
    public AcceptorCache(Transmitter<ACCEPTOR, ?, ?> transmitter, TileEntityTransmitter transmitterTile, BlockApiLookup<ACCEPTOR, Direction> lookup) {
        super(transmitter, transmitterTile);
        this.lookup = lookup;
    }

    protected void updateCachedAcceptorAndListen(Direction side, Level acceptorLevel, BlockPos acceptorPos, Optional<ACCEPTOR> acceptor) {
        updateCachedAcceptorAndListen(side, acceptorLevel, acceptorPos, acceptor, acceptor, true);
    }

    //@Deprecated(forRemoval = true)
    protected void updateCachedAcceptorAndListen(Direction side, Level acceptorLevel, BlockPos acceptorPos, Optional<ACCEPTOR> acceptor, Optional<?> sourceAcceptor,
          boolean sourceIsSame) {
        boolean dirtyAcceptor = false;
        if (cachedAcceptors.containsKey(side)) {
            AcceptorInfo<ACCEPTOR> acceptorInfo = cachedAcceptors.get(side);
            if (acceptorLevel != acceptorInfo.getLevel() || !acceptorPos.equals(acceptorInfo.getPos())) {
                //The tile changed, fully invalidate it
                cachedAcceptors.put(side, new AcceptorInfo<>(acceptorLevel, acceptorPos, sourceAcceptor, acceptor));
                dirtyAcceptor = true;
            } else if (sourceAcceptor != acceptorInfo.sourceAcceptor) {
                //The source acceptor is different, make sure we update it and the actual acceptor
                // This allows us to make sure we only mark the acceptor as dirty if it actually changed
                // Use case: Wrapped energy acceptors
                acceptorInfo.updateAcceptor(sourceAcceptor, acceptor);
                dirtyAcceptor = true;
            }
        } else {
            cachedAcceptors.put(side, new AcceptorInfo<>(acceptorLevel, acceptorPos, sourceAcceptor, acceptor));
            dirtyAcceptor = true;
        }
        if (dirtyAcceptor) {
            transmitter.markDirtyAcceptor(side);
            //If the capability is present, and we want to add the listener, add a listener so that once it gets invalidated
            // we recheck that side assuming that the world and position is still loaded and our tile has not been removed
            Consumer<Optional<ACCEPTOR>> refreshListener = getRefreshListener(side);
            if (sourceIsSame) {
                //Add it to the actual acceptor as it is the same as the source, and we can do so without any unchecked warnings
                //acceptor.addListener(refreshListener);
            } else {
                //Otherwise, use unchecked generics to add the listener to the source acceptor
                //CapabilityUtils.addListener(sourceAcceptor, refreshListener);
            }
        }
    }

    /**
     * @implNote Grabs the acceptors from cache
     */
    @Override
    public Optional<ACCEPTOR> getConnectedAcceptor(Direction side) {
        if (cachedAcceptors.containsKey(side)) {
            AcceptorInfo<ACCEPTOR> acceptorInfo = cachedAcceptors.get(side);
            if (isAcceptorAndListen(acceptorInfo.getLevel(), acceptorInfo.getPos(), side, lookup)) {
                return acceptorInfo.acceptor;
            }
            //TODO: If the tile has been removed should we force an invalidation/recheck?
        }
        return Optional.empty();
    }

    @Nullable
    public BlockEntity getConnectedAcceptorTile(Direction side) {
        if (cachedAcceptors.containsKey(side)) {
            AcceptorInfo<ACCEPTOR> acceptorInfo = cachedAcceptors.get(side);
            if (isAcceptorAndListen(acceptorInfo.getLevel(), acceptorInfo.getPos(), side, lookup)) {
                return acceptorInfo.getLevel().getBlockEntity(acceptorInfo.getPos());
            }
        }
        return null;
    }

    /**
     * @apiNote Only call this from the server side
     */
    public boolean isItemAcceptorAndListen(@Nullable Level level, BlockPos pos, Direction side) {
        Storage<ItemVariant> acceptor = ItemStorage.SIDED.find(level, pos, side.getOpposite());
        if (acceptor != null) {
            //Update the cached acceptor and if it changed, add a listener to it to listen for invalidation
            updateCachedAcceptorAndListen(side, level, pos, Optional.of((ACCEPTOR) acceptor));
            return true;
        }
        return false;
    }

    public boolean isAcceptorAndListen(@Nullable Level level, BlockPos pos, Direction side, BlockApiLookup<ACCEPTOR, Direction> lookup) {
        ACCEPTOR acceptor = lookup.find(level, pos, side.getOpposite());
        if (acceptor != null) {
            //Update the cached acceptor and if it changed, add a listener to it to listen for invalidation
            updateCachedAcceptorAndListen(side, level, pos, Optional.of(acceptor));
            return true;
        }
        return false;
    }

    public static class AcceptorInfo<ACCEPTOR> extends AbstractAcceptorInfo {

        private Optional<?> sourceAcceptor;
        private Optional<ACCEPTOR> acceptor;

        private AcceptorInfo(Level level, BlockPos pos, Optional<?> sourceAcceptor, Optional<ACCEPTOR> acceptor) {
            super(level, pos);
            this.acceptor = acceptor;
            this.sourceAcceptor = sourceAcceptor;
        }

        private void updateAcceptor(Optional<?> sourceAcceptor, Optional<ACCEPTOR> acceptor) {
            this.sourceAcceptor = sourceAcceptor;
            this.acceptor = acceptor;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return o instanceof AcceptorInfo<?> other && getLevel().equals(other.getLevel()) && getPos().equals(other.getPos()) && sourceAcceptor.equals(other.sourceAcceptor) && acceptor.equals(other.acceptor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLevel(), getPos(), sourceAcceptor, acceptor);
        }
    }
}