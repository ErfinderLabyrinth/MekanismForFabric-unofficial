package mekanism.common.capabilities.merged;

import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;

public interface IMergedHandler {
    IGasHandler getGasHandler();
    IInfusionHandler getInfusionHandler();
    IPigmentHandler getPigmentHandler();
    ISlurryHandler getSlurryHandler();
}
