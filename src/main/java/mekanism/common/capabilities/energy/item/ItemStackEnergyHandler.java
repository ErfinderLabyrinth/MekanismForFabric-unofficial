package mekanism.common.capabilities.energy.item;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.energy.IEnergyContainer;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.MethodsReturnNonnullByDefault;

import java.util.List;

/**
 * Helper class for implementing fluid handlers for items
 */
@ParametersAreNotNullByDefault
@MethodsReturnNonnullByDefault
public abstract class ItemStackEnergyHandler implements IEnergyContainer {

    protected List<IEnergyContainer> energyContainers;

    protected abstract List<IEnergyContainer> getInitialContainers();



    @Override
    public void updateSnapshots(TransactionContext t) {
        //Not necessary when handeling with items
    }

//    @Override
//    protected void init() {
//        super.init();
//        this.energyContainers = getInitialContainers();
//    }
//
//    @Override
//    protected void load() {
//        super.load();
//        ItemDataUtils.readContainers(getStack(), NBTConstants.ENERGY_CONTAINERS, getEnergyContainers(null));
//    }

    @Override
    public void onContentsChanged() {
        //ItemDataUtils.writeContainers(getStack(), NBTConstants.ENERGY_CONTAINERS, energyContainers);
    }

//    @Override
//    protected void gatherCapabilityResolvers(Consumer<ICapabilityResolver> consumer) {
//        consumer.accept(new EnergyCapabilityResolver(this));
//    }
}