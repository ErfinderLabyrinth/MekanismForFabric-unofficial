package mekanism.common.capabilities.resolver.manager;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.capabilities.holder.IHolder;
import mekanism.common.capabilities.resolver.BasicSidedCapabilityResolver;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

@NothingNullByDefault
public class CapabilityHandlerManager<HOLDER extends IHolder<TYPE>, VARIANT, HANDLER, SIDED_HANDLER, TYPE> extends BasicSidedCapabilityResolver<HANDLER, SIDED_HANDLER>
      implements ICapabilityHandlerManager<VARIANT> {

    private final BiFunction<HOLDER, Direction, Storage<VARIANT>> containerGetter;
    private final boolean canHandle;
    @Nullable
    protected final HOLDER holder;

    protected CapabilityHandlerManager(@Nullable HOLDER holder, /*SIDED_HANDLER baseHandler,*/
          ProxyCreator<HANDLER, SIDED_HANDLER> proxyCreator, BiFunction<HOLDER, Direction, Storage<VARIANT>> containerGetter) {
        super(/*baseHandler, */proxyCreator, holder != null);
        this.holder = holder;
        this.canHandle = this.holder != null;
        this.containerGetter = containerGetter;
    }

    @Override
    public boolean canHandle() {
        return canHandle;
    }

    @Override
    public Storage<VARIANT> getContainers(@Nullable Direction side) {
        return canHandle() ? containerGetter.apply(holder, side) : Storage.empty();
    }

    @Nullable
    @Override
    public IHolder<TYPE> getHolder() {
        return holder;
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote Assumes that {@link #canHandle} has been called before this and that it was {@code true}.
     */
//    @Override
//    public @Nullable HANDLER resolve(@Nullable Direction side) {
//        if (!getContainers(side).iterator().hasNext()) {
//            //If we don't have any containers accessible from that side, don't return a handler
//            //TODO: Evaluate moving this somehow into being done via the is disabled check
//            return null;
//        }
//        return super.resolve(side);
//    }
}