package mekanism.common.content.network.distribution;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.common.lib.distribution.SplitInfo;
import mekanism.common.lib.distribution.Target;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

public class ChemicalHandlerTarget<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, HANDLER extends Storage<CHEMICAL>>
      extends Target<HANDLER, Long, @NotNull STACK> {

    public ChemicalHandlerTarget(@NotNull STACK type) {
        this.extra = type;
    }

    public ChemicalHandlerTarget(@NotNull STACK type, int expectedSize) {
        super(expectedSize);
        this.extra = type;
    }

    @Override
    protected void acceptAmount(HANDLER handler, SplitInfo<Long> splitInfo, Long amount) {
        long inserted;
        try(Transaction t=Transaction.openOuter()) {
            inserted = handler.insert(extra.getType(), amount, t);
            t.commit();
        }
        splitInfo.send(inserted);
    }

    @Override
    protected Long simulate(HANDLER handler, @NotNull STACK stack) {
        try(Transaction t=Transaction.openOuter()) {
            return handler.insert(stack.getType(), stack.getAmount(), t);
        }
    }
}