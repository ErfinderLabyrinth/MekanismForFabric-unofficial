package mekanism.common.storage.item;

import mekanism.api.NBTConstants;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.IGasTank;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;

import java.util.List;
import java.util.function.Supplier;

public class GasItemStorage extends AbstractItemStorage<Gas, IGasTank> {
    public GasItemStorage(ContainerItemContext context, Supplier<List<IGasTank>> tankCreator) {
        super(context, tankCreator);
    }

    @Override
    protected String getNBTPath() {
        return NBTConstants.GAS_TANKS;
    }

    @Override
    protected StorageView<Gas> createView(int index) {
        return new GasItemStorageView(index);
    }

    class GasItemStorageView extends AbstractItemStorageView {

        protected GasItemStorageView(int index) {
            super(index);
        }
    }
}
