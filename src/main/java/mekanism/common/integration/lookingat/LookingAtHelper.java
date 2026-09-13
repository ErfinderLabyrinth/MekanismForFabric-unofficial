package mekanism.common.integration.lookingat;

import mekanism.api.FluidStack;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.math.FloatingLong;
import net.minecraft.network.chat.Component;

public interface LookingAtHelper {

    void addText(Component text);

    void addEnergyElement(FloatingLong energy, FloatingLong maxEnergy);

    void addFluidElement(FluidStack stored, int capacity);

    void addChemicalElement(ChemicalStack<?> stored, long capacity);
}