package mekanism.common.item.interfaces;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.gas.IGasHandler.IMekanismGasHandler;
import mekanism.common.capabilities.Capabilities;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Optional;

public interface IGasItem {

    @NotNull
    default GasStack useGas(ContainerItemContext context, long amount) {
        IGasHandler gasHandlerItem = context.find(Capabilities.GAS_HANDLER_ITEM);
        if (gasHandlerItem != null) {
            if (gasHandlerItem instanceof IMekanismGasHandler gasHandler) {
                //TODO: If we end up having more tanks than one in any IGasItem's just kill off this if branch
                Iterator<StorageView<Gas>> iterator = gasHandler.iterator();
                StorageView<Gas> gasTank = iterator.hasNext() ? iterator.next() : null;
                if (gasTank != null) {
                    //Should always reach here
                    try(Transaction t=Transaction.openOuter()) {
                        GasStack result = gasTank.getResource().getStack(gasTank.extract(gasTank.getResource(), amount, t));
                        t.commit();
                        return result;
                    }
                }
            }
            Gas resource = null;
            long amountExtracted = 0;
            for (StorageView<Gas> view : gasHandlerItem) {
                if (resource == null || resource == view.getResource()) {
                    try(Transaction t=Transaction.openOuter()) {
                        Gas resource2 = view.getResource();
                        long extract = view.extract(resource2, amount - amountExtracted, t);
                        t.commit();
                        if (extract != 0 && resource == null) {
                            resource = resource2;
                        }
                        amountExtracted += extract;
                    }
                }
            }
            if (resource != null) {
                return resource.getStack(amountExtracted);
            }
        }
        return GasStack.EMPTY;
    }

    default boolean hasGas(ItemStack stack) {
        return Optional.ofNullable(ContainerItemContext.withConstant(stack).find(Capabilities.GAS_HANDLER_ITEM))
              .map(handler -> {
                  for (StorageView<Gas> view:handler) {
                      if (view.getAmount() != 0 && !view.isResourceBlank()) {
                          return true;
                      }
                  }
                  return false;
              }).orElse(false);
    }
}