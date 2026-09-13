package mekanism.common.content.network.distribution;

import mekanism.api.FluidStack;
import mekanism.common.lib.distribution.SplitInfo;
import mekanism.common.lib.distribution.Target;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class FluidHandlerTarget extends Target<Storage<FluidVariant>, Long, @NotNull FluidStack> {

    public FluidHandlerTarget(@NotNull FluidStack type) {
        this.extra = type;
    }

    public FluidHandlerTarget(@NotNull FluidStack type, Collection<Storage<FluidVariant>> allHandlers) {
        super(allHandlers);
        this.extra = type;
    }

    public FluidHandlerTarget(@NotNull FluidStack type, int expectedSize) {
        super(expectedSize);
        this.extra = type;
    }

    @Override
    protected void acceptAmount(Storage<FluidVariant> handler, SplitInfo<Long> splitInfo, Long amount) {
        try(Transaction t = Transaction.openOuter()) {
            splitInfo.send(handler.insert(extra.variant(), amount, t));
            t.commit();
        }
    }

    @Override
    protected Long simulate(Storage<FluidVariant> handler, @NotNull FluidStack fluidStack) {
        try(Transaction t = Transaction.openOuter()) {
            return handler.insert(fluidStack.variant(), fluidStack.amount(), t);
        }
    }
}