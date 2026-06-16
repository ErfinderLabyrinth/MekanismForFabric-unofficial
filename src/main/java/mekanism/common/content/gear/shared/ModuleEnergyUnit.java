package mekanism.common.content.gear.shared;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import team.reborn.energy.api.EnergyStorage;

@ParametersAreNotNullByDefault
public class ModuleEnergyUnit implements ICustomModule<ModuleEnergyUnit> {

    public long getEnergyCapacity(IModule<ModuleEnergyUnit> module) {
        long base = module.getContainer().getItem() instanceof ItemMekaSuitArmor ? MekanismConfig.COMMON.gear.mekaSuitBaseEnergyCapacity
                                                                                         : MekanismConfig.COMMON.gear.mekaToolBaseEnergyCapacity;
        return (long) (base * Math.pow(2, module.getInstalledCount()));
    }

    public long getChargeRate(IModule<ModuleEnergyUnit> module) {
        long base = module.getContainer().getItem() instanceof ItemMekaSuitArmor ? MekanismConfig.COMMON.gear.mekaSuitBaseChargeRate
                                                                                         : MekanismConfig.COMMON.gear.mekaToolBaseChargeRate;
        return (long) (base * Math.pow(2, module.getInstalledCount()));
    }

    @Override
    public void onRemoved(IModule<ModuleEnergyUnit> module, boolean last) {
        EnergyStorage energyContainer = module.getEnergyContainer();
        if (energyContainer != null && energyContainer.getAmount() > energyContainer.getCapacity()) {
            try(Transaction t=Transaction.openOuter()) {
                energyContainer.extract(energyContainer.getAmount() - energyContainer.getCapacity(), t);
                t.commit();
            }
        }
    }
}