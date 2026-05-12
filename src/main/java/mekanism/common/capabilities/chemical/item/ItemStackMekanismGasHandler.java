package mekanism.common.capabilities.chemical.item;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler.IMekanismGasHandler;
import mekanism.api.chemical.gas.IGasTank;

/**
 * Helper class for implementing gas handlers for items
 */
public abstract class ItemStackMekanismGasHandler extends ItemStackMekanismChemicalHandler<Gas, GasStack, IGasTank> implements IMekanismGasHandler {

//    @NotNull
//    @Override
//    protected String getNbtKey() {
//        return NBTConstants.GAS_TANKS;
//    }
}