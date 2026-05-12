package mekanism.common.integration.energy.teamreborn;

import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.util.UnitDisplayUtils;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import team.reborn.energy.api.EnergyStorage;

public class TeamRebornStrictEnergyHandler implements IStrictEnergyHandler {
    final EnergyStorage storage;

    private TeamRebornStrictEnergyHandler(EnergyStorage storage) {
        this.storage = storage;
    }

    public static IStrictEnergyHandler createWrapper(EnergyStorage storage) {
        if(storage instanceof TeamRebornEnergyIntegration integration) {
            return integration.handler;
        } else {
            return new TeamRebornStrictEnergyHandler(storage);
        }
    }

    @Override
    public int getEnergyContainerCount() {
        return 1;
    }

    @Override
    public FloatingLong getEnergy(int container) {
        return container == 0 ? UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(FloatingLong.create(storage.getAmount())) : FloatingLong.ZERO;
    }

    @Override
    public void setEnergy(int container, long energy, TransactionContext t) {
        //Not implemented or directly needed
    }

    @Override
    public FloatingLong getMaxEnergy(int container) {
        return container == 0 ? UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(FloatingLong.create(storage.getCapacity())) : FloatingLong.ZERO;
    }

    @Override
    public FloatingLong getNeededEnergy(int container) {
        return container == 0 ? UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(FloatingLong.create(Math.max(0, storage.getCapacity() - storage.getAmount()))) : FloatingLong.ZERO;
    }

    @Override
    public FloatingLong insertEnergy(int container, FloatingLong amount, TransactionContext t) {
        if (container == 0 && storage.supportsInsertion()) {
            int toInsert = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertToAsInt(amount.longValue());
            if (toInsert > 0) {
                long inserted = storage.insert(toInsert, t);
                if (inserted > 0) {
                    //Only bother converting back if any was inserted
                    return amount.subtract(UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(inserted));
                }
            }
        }
        return amount;
    }

    @Override
    public FloatingLong extractEnergy(int container, FloatingLong amount, TransactionContext t) {
        if (container == 0 && storage.supportsExtraction()) {
            int toExtract = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertToAsInt(amount.longValue());
            if (toExtract > 0) {
                long extracted = extracted = storage.extract(toExtract, t);
                return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(FloatingLong.create(extracted));
            }
        }
        return FloatingLong.ZERO;
    }
}
