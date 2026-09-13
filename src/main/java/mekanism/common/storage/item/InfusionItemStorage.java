package mekanism.common.storage.item;

import mekanism.api.NBTConstants;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;

import java.util.List;
import java.util.function.Supplier;

public class InfusionItemStorage extends AbstractItemStorage<InfuseType, IInfusionTank> {
    public InfusionItemStorage(ContainerItemContext context, Supplier<List<IInfusionTank>> tankCreator) {
        super(context, tankCreator);
    }

    @Override
    protected String getNBTPath() {
        return NBTConstants.INFUSION_TANKS;
    }

    @Override
    protected StorageView<InfuseType> createView(int index) {
        return new InfuseItemStorageView(index);
    }

    class InfuseItemStorageView extends AbstractItemStorageView {

        protected InfuseItemStorageView(int index) {
            super(index);
        }
    }
}
