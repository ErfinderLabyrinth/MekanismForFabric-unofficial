package mekanism.common.capabilities.resolver;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.capabilities.holder.IHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@NothingNullByDefault
public class BasicSidedCapabilityResolver<HANDLER, SIDED_HANDLER> {

    private final ProxyCreator<HANDLER, SIDED_HANDLER> proxyCreator;
    private final Map<Direction, HANDLER> handlers;
    //private final SIDED_HANDLER baseHandler;
    @Nullable
    private HANDLER readOnlyHandler;

    public BasicSidedCapabilityResolver(/*SIDED_HANDLER baseHandler,*/ BasicProxyCreator<HANDLER, SIDED_HANDLER> proxyCreator) {
        this(/*baseHandler,*/ proxyCreator, true);
    }

    protected BasicSidedCapabilityResolver(/*SIDED_HANDLER baseHandler,*/ ProxyCreator<HANDLER, SIDED_HANDLER> proxyCreator,
          boolean canHandle) {
        //this.baseHandler = baseHandler;
        this.proxyCreator = proxyCreator;
        if (canHandle) {
            handlers = new EnumMap<>(Direction.class);
        } else {
            handlers = Collections.emptyMap();
        }
    }

    //public SIDED_HANDLER getInternal() {
    //    return baseHandler;
    //}

    @Nullable
    protected IHolder getHolder() {
        return null;
    }

    /**
     * Lazily get and cache a handler instance for the given side, and make it be read only if something else is trying to interact with us using the null side
     */
//    public @Nullable HANDLER resolve(@Nullable Direction side) {
//        if (side == null) {
//            if (readOnlyHandler == null) {
//                readOnlyHandler = proxyCreator.create(baseHandler, null, getHolder());
//            }
//            return readOnlyHandler;
//        }
//        HANDLER cachedCapability = handlers.get(side);
//        if (cachedCapability == null) {
//            handlers.put(side, cachedCapability = proxyCreator.create(baseHandler, side, getHolder()));
//        }
//        return cachedCapability;
//    }

    @FunctionalInterface
    public interface ProxyCreator<HANDLER, SIDED_HANDLER> {

        HANDLER create(SIDED_HANDLER handler, @Nullable Direction side, @Nullable IHolder holder);
    }

    @FunctionalInterface
    public interface BasicProxyCreator<HANDLER, SIDED_HANDLER> extends ProxyCreator<HANDLER, SIDED_HANDLER> {

        HANDLER create(SIDED_HANDLER handler, @Nullable Direction side);

        @Override
        default HANDLER create(SIDED_HANDLER handler, @Nullable Direction side, @Nullable IHolder holder) {
            return create(handler, side);
        }
    }
}