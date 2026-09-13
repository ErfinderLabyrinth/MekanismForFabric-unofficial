package mekanism.common.storage.item;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import team.reborn.energy.api.EnergyStorage;

public interface ItemStorageHandler {
    default Storage<Gas> getGasStorage(ContainerItemContext context) {
        return null;
    }

    default Storage<InfuseType> getInfusionStorage(ContainerItemContext context) {
        return null;
    }

    default Storage<Pigment> getPigmentStorage(ContainerItemContext context) {
        return null;
    }

    default Storage<Slurry> getSlurryStorage(ContainerItemContext context) {
        return null;
    }

    default Storage<FluidVariant> getFluidStorage(ContainerItemContext context) {
        return null;
    }

    default EnergyStorage getEnergyStorage(ContainerItemContext context) {
        return null;
    }
}
