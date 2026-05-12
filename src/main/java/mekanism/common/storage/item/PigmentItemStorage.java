package mekanism.common.storage.item;

import mekanism.api.NBTConstants;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;

import java.util.List;
import java.util.function.Supplier;

public class PigmentItemStorage extends AbstractItemStorage<Pigment, IPigmentTank> {
    public PigmentItemStorage(ContainerItemContext context, Supplier<List<IPigmentTank>> tankCreator) {
        super(context, tankCreator);
    }

    @Override
    protected String getNBTPath() {
        return NBTConstants.PIGMENT_TANKS;
    }

    @Override
    protected StorageView<Pigment> createView(int index) {
        return new PigmentItemStorageView(index);
    }

    class PigmentItemStorageView extends AbstractItemStorageView {

        protected PigmentItemStorageView(int index) {
            super(index);
        }
    }
}
