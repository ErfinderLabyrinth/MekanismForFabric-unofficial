package mekanism.api.chemical.pigment;

import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.IMekanismChemicalHandler;
import mekanism.api.chemical.ISidedChemicalHandler;

//@AutoRegisterCapability //TODO
public interface IPigmentHandler extends IChemicalHandler<Pigment, PigmentStack, IPigmentTank>, IEmptyPigmentProvider {

    /**
     * A sided variant of {@link IPigmentHandler}
     */
    interface ISidedPigmentHandler extends ISidedChemicalHandler<Pigment, PigmentStack, IPigmentTank>, IPigmentHandler {
    }

    interface IMekanismPigmentHandler extends IMekanismChemicalHandler<Pigment, PigmentStack, IPigmentTank>, ISidedPigmentHandler {
    }
}