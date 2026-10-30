package mekanism.common.capabilities.chemical.item;

import mekanism.api.chemical.infuse.IInfusionHandler.IMekanismInfusionHandler;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;

/**
 * Helper class for implementing infusion handlers for items
 */
public abstract class ItemStackMekanismInfusionHandler extends ItemStackMekanismChemicalHandler<InfuseType, InfusionStack, IInfusionTank> implements IMekanismInfusionHandler {

//    @NotNull
//    @Override
//    protected String getNbtKey() {
//        return NBTConstants.INFUSION_TANKS;
//    }

//    @Override
//    protected void gatherCapabilityResolvers(Consumer<ICapabilityResolver> consumer) {
//        consumer.accept(BasicCapabilityResolver.constant(Capabilities.INFUSION_HANDLER, this));
//    }
}