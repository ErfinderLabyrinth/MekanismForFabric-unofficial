package mekanism.common.capabilities.chemical.item;

import mekanism.common.storage.item.ItemStorageHandler;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;

public interface ItemChemicalStorage {
    ItemStorageHandler getChemicalStorage(ContainerItemContext context);
}
