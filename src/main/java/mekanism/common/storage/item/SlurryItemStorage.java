package mekanism.common.storage.item;

import mekanism.api.NBTConstants;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;

import java.util.List;
import java.util.function.Supplier;

public class SlurryItemStorage extends AbstractItemStorage<Slurry, ISlurryTank> {
    public SlurryItemStorage(ContainerItemContext context, Supplier<List<ISlurryTank>> tankCreator) {
        super(context, tankCreator);
    }

    @Override
    protected String getNBTPath() {
        return NBTConstants.SLURRY_TANKS;
    }

    @Override
    protected StorageView<Slurry> createView(int index) {
        return new SlurryItemStorageView(index);
    }

    class SlurryItemStorageView extends AbstractItemStorageView {

        protected SlurryItemStorageView(int index) {
            super(index);
        }
    }
}
