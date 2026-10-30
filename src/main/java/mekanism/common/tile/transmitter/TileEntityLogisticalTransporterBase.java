package mekanism.common.tile.transmitter;

import mekanism.api.providers.IBlockProvider;
import mekanism.common.content.network.transmitter.LogisticalTransporterBase;
import mekanism.common.content.transporter.TransporterStack;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.util.TransporterUtils;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public abstract class TileEntityLogisticalTransporterBase extends TileEntityTransmitter implements SidedStorageBlockEntity {

    protected TileEntityLogisticalTransporterBase(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
//        addCapabilityResolver(new TransporterCapabilityResolver());
    }

    @Override
    protected abstract LogisticalTransporterBase createTransmitter(IBlockProvider blockProvider);

    @Override
    public LogisticalTransporterBase getTransmitter() {
        return (LogisticalTransporterBase) super.getTransmitter();
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, TileEntityLogisticalTransporterBase transmitter) {
        transmitter.getTransmitter().onUpdateClient();
    }

    @Override
    public void onUpdateServer() {
        super.onUpdateServer();
        getTransmitter().onUpdateServer();
    }

    @Override
    public void blockRemoved() {
        super.blockRemoved();
        if (!isRemote()) {
            LogisticalTransporterBase transporter = getTransmitter();
            if (!transporter.isUpgrading()) {
                //If the transporter is not currently being upgraded, drop the contents
                for (TransporterStack stack : transporter.getTransit()) {
                    TransporterUtils.drop(transporter, stack);
                }
            }
        }
    }

    @Override
    public void sideChanged(@NotNull Direction side, @NotNull ConnectionType old, @NotNull ConnectionType type) {
        super.sideChanged(side, old, type);
        //Note: We don't expose a cap for when the connection type is none or push and this method only gets called if type != old,
        // so we can check to ensure that if we are one of the two that the other isn't the other one we don't have a cap for
        if (type == ConnectionType.NONE && old != ConnectionType.PUSH ||
            type == ConnectionType.PUSH && old != ConnectionType.NONE) {
            //Notify the neighbor on that side our state changed and we no longer have a capability
            WorldUtils.notifyNeighborOfChange(level, side, worldPosition);
        } else if (old == ConnectionType.NONE && type != ConnectionType.PUSH ||
                   old == ConnectionType.PUSH && type != ConnectionType.NONE) {
            //Notify the neighbor on that side our state changed, and we now do have a capability
            WorldUtils.notifyNeighborOfChange(level, side, worldPosition);
        }
    }

//    private final Map<Direction, CursedTransporterItemHandler> cursedHandlers = new EnumMap<>(Direction.class);
    private final Map<Direction, Storage<ItemVariant>> handlers = new EnumMap<>(Direction.class);

    @Override
    public @Nullable Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
//        if (handlers.containsKey(side))
//            return handlers.get(side);
//        if (getTransmitter().exposesInsertCap(side)) {
//            handlers.put(side, cursedHandlers.computeIfAbsent(side, s -> new CursedTransporterItemHandler(getTransmitter(), worldPosition.relative(s),
//                  () -> level == null ? -1 : level.getGameTime())));
//        }
//        return null;

        if (getTransmitter().canConnect(side)) {
            ConnectionType connectionType = getTransmitter().getConnectionType(side);
            if (connectionType != ConnectionType.NONE) {
                return new LogisticalTransporterSideStorage(this, worldPosition.relative(side), connectionType);
            }

        }
        return null;
    }

    //    @NothingNullByDefault
//    private class TransporterCapabilityResolver implements ICapabilityResolver {
//
//
//        private final Map<Direction, CursedTransporterItemHandler> cursedHandlers = new EnumMap<>(Direction.class);
////        private final Map<Direction, LazyOptional<IItemHandler>> handlers = new EnumMap<>(Direction.class);
//
//        /**
//         * Lazily get and cache a handler instance for the given side, and make it be read only if something else is trying to interact with us using the null side
//         */
//        @Override
//        public <T> Optional<T> resolve(Capability<T> capability, @Nullable Direction side) {
//            if (side == null) {
//                //We provide no readonly item handler view
//                return Optional.empty();
//            }
//            LazyOptional<IItemHandler> cachedCapability = handlers.get(side);
//            if (cachedCapability == null || !cachedCapability.isPresent()) {
//                LogisticalTransporterBase transporter = getTransmitter();
//                //Note: We check here whether it exposes the cap rather than in the cap itself as we invalidate the cached cap whenever this changes
//                if (transporter.exposesInsertCap(side)) {
//                    handlers.put(side, cachedCapability = LazyOptional.of(() ->
//                          cursedHandlers.computeIfAbsent(side, s -> new CursedTransporterItemHandler(transporter, worldPosition.relative(s),
//                          () -> level == null ? -1 : level.getGameTime()))));
//                } else {
//                    return LazyOptional.empty();
//                }
//            }
//            return cachedCapability.cast();
//        }
//
//        @Override
//        public void invalidate(Capability<?> capability, @Nullable Direction side) {
//            if (side != null) {
//                invalidate(handlers.get(side));
//            }
//        }
//
//        @Override
//        public void invalidateAll() {
//            handlers.values().forEach(this::invalidate);
//        }
//
//        protected void invalidate(@Nullable LazyOptional<?> cachedCapability) {
//            if (cachedCapability != null && cachedCapability.isPresent()) {
//                cachedCapability.invalidate();
//            }
//        }
//    }
}