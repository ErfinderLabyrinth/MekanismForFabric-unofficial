package mekanism.common.capabilities.merged;

import mekanism.api.NBTSerializable;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.common.capabilities.chemical.dynamic.DynamicChemicalHandler.DynamicGasHandler;
import mekanism.common.capabilities.chemical.dynamic.DynamicChemicalHandler.DynamicInfusionHandler;
import mekanism.common.capabilities.chemical.dynamic.DynamicChemicalHandler.DynamicPigmentHandler;
import mekanism.common.capabilities.chemical.dynamic.DynamicChemicalHandler.DynamicSlurryHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.MethodsReturnNonnullByDefault;

import java.util.List;

/**
 * @apiNote Make sure to set the merged tank, and dynamic handlers
 */
@ParametersAreNotNullByDefault
@MethodsReturnNonnullByDefault
public abstract class MergedTankContentsHandler<MERGED extends MergedChemicalTank> implements Storage<ItemVariant>, IMergedHandler {

    protected MERGED mergedTank;
    protected DynamicGasHandler gasHandler;
    protected DynamicInfusionHandler infusionHandler;
    protected DynamicPigmentHandler pigmentHandler;
    protected DynamicSlurryHandler slurryHandler;

    protected List<ISlurryTank> slurryTanks;
    protected List<IPigmentTank> pigmentTanks;
    protected List<IInfusionTank> infusionTanks;
    protected List<IGasTank> gasTanks;

//    @Override
//    protected void init() {
//        super.init();
//        this.gasTanks = Collections.singletonList(mergedTank.getGasTank());
//        this.infusionTanks = Collections.singletonList(mergedTank.getInfusionTank());
//        this.pigmentTanks = Collections.singletonList(mergedTank.getPigmentTank());
//        this.slurryTanks = Collections.singletonList(mergedTank.getSlurryTank());
//    }
//
//    @Override
//    protected void load() {
//        super.load();
//        ItemStack stack = getStack();
//        if (!stack.isEmpty()) {
//            ItemDataUtils.readContainers(stack, NBTConstants.GAS_TANKS, gasTanks);
//            ItemDataUtils.readContainers(stack, NBTConstants.INFUSION_TANKS, infusionTanks);
//            ItemDataUtils.readContainers(stack, NBTConstants.PIGMENT_TANKS, pigmentTanks);
//            ItemDataUtils.readContainers(stack, NBTConstants.SLURRY_TANKS, slurryTanks);
//        }
//    }

    protected void onContentsChanged(String key, List<? extends NBTSerializable> containers) {
        //ItemDataUtils.writeContainers(getStack(), key, containers);
    }


    @Override
    public DynamicGasHandler getGasHandler() {
        return gasHandler;
    }

    @Override
    public DynamicInfusionHandler getInfusionHandler() {
        return infusionHandler;
    }

    @Override
    public DynamicPigmentHandler getPigmentHandler() {
        return pigmentHandler;
    }

    @Override
    public DynamicSlurryHandler getSlurryHandler() {
        return slurryHandler;
    }
}