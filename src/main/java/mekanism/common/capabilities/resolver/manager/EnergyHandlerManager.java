package mekanism.common.capabilities.resolver.manager;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@NothingNullByDefault
public class EnergyHandlerManager {

    private final Map<Direction, EnergyStorage> cachedCapabilities;
    private final @Nullable EnergyStorage cachedReadOnlyCapabilities;
    private final Map<Direction, IStrictEnergyHandler> handlers;
    //private final ISidedStrictEnergyHandler baseHandler;
    private final boolean canHandle;
    @Nullable
    private IStrictEnergyHandler readOnlyHandler;
    @Nullable
    private final IEnergyContainerHolder holder;

    public EnergyHandlerManager(@Nullable IEnergyContainerHolder holder/*, ISidedStrictEnergyHandler baseHandler*/) {
        this.holder = holder;
        this.canHandle = this.holder != null;
        //this.baseHandler = baseHandler;
        if (this.canHandle) {
            handlers = new EnumMap<>(Direction.class);
            cachedCapabilities = new EnumMap<>(Direction.class);
            cachedReadOnlyCapabilities = null;
        } else {
            handlers = Collections.emptyMap();
            cachedCapabilities = Collections.emptyMap();
            cachedReadOnlyCapabilities = null;
        }
    }

    public boolean canHandle() {
        return canHandle;
    }

    public EnergyStorage getContainer(@Nullable Direction side) {
        return canHandle() ? holder.getEnergyContainers(side) : EnergyStorage.EMPTY;
    }

    public @Nullable IEnergyContainerHolder getHolder() {
        return holder;
    }

    //    /**
//     * Lazily get and cache a handler instance for the given side, and make it be read only if something else is trying to interact with us using the null side
//     *
//     * @apiNote Assumes that {@link #canHandle} has been called before this and that it was {@code true}.
//     */
//    public @Nullable EnergyStorage resolve(@Nullable Direction side) {
//        if (getContainer(side) == null || getContainer(side) == EnergyStorage.EMPTY) {
//            //If we don't have any containers accessible from that side, don't return a handler
//            //TODO: Evaluate moving this somehow into being done via the is disabled check
//            return null;
//        }
//        if (side == null) {
//            if (readOnlyHandler == null) {
//                //Note: Only should enter this if statement if we don't already have a cache,
//                // so we just check it beforehand as it is a quick check and simplifies the code
//                readOnlyHandler = new ProxyStrictEnergyHandler(baseHandler, null, holder);
//            }
//            return EnergyCapabilityResolver.getCachedOrResolve(cachedReadOnlyCapabilities, readOnlyHandler);
//        }
//        //Note: Only should enter this if statement if we don't already have a cache,
//        // so we just check it beforehand as it is a quick check and simplifies the code
//        IStrictEnergyHandler handler = handlers.computeIfAbsent(side, s -> new ProxyStrictEnergyHandler(baseHandler, s, holder));
//        return EnergyCapabilityResolver.getCachedOrResolve(cachedCapabilities.computeIfAbsent(side, key -> new IdentityHashMap<>()), handler);
//    }
//
//    public static EnergyStorage getCachedOrResolve() {
//
//    }
}