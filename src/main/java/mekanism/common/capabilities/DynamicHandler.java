package mekanism.common.capabilities;

import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

@NothingNullByDefault
public abstract class DynamicHandler<CHEMICAL> implements IContentsListener {

    protected final Function<Direction, List<CHEMICAL>> containerSupplier;
    protected final InteractPredicate canExtract;
    protected final InteractPredicate canInsert;
    @Nullable
    private final IContentsListener listener;

    protected DynamicHandler(Function<Direction, List<CHEMICAL>> containerSupplier, InteractPredicate canExtract, InteractPredicate canInsert,
          @Nullable IContentsListener listener) {
        this.containerSupplier = containerSupplier;
        this.canExtract = canExtract;
        this.canInsert = canInsert;
        this.listener = listener;
    }

    @Override
    public void onContentsChanged() {
        if (listener != null) {
            listener.onContentsChanged();
        }
    }

    @FunctionalInterface
    public interface InteractPredicate {

        InteractPredicate ALWAYS_TRUE = (side) -> true;

        boolean test(@Nullable Direction side);
    }
}