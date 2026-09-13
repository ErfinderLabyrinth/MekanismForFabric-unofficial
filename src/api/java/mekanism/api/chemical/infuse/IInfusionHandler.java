package mekanism.api.chemical.infuse;

import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IMekanismChemicalHandler;
import mekanism.api.chemical.ISidedChemicalHandler;

//@AutoRegisterCapability //TODO
public interface IInfusionHandler extends IChemicalHandler<InfuseType, InfusionStack, IInfusionTank>, IEmptyInfusionProvider {

    /**
     * A sided variant of {@link IInfusionHandler}
     */
    interface ISidedInfusionHandler extends ISidedChemicalHandler<InfuseType, InfusionStack, IInfusionTank>, IInfusionHandler {
    }

    interface IMekanismInfusionHandler extends IMekanismChemicalHandler<InfuseType, InfusionStack, IInfusionTank>, ISidedInfusionHandler {
    }
}