package mekanism.common.integration.energy.teamreborn;

import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.util.UnitDisplayUtils;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import team.reborn.energy.api.EnergyStorage;

public class TeamRebornEnergyIntegration implements EnergyStorage {
    final IStrictEnergyHandler handler;

    public TeamRebornEnergyIntegration(IStrictEnergyHandler handler) {
        this.handler = handler;
    }

    public static EnergyStorage createWrapper(IStrictEnergyHandler storage) {
        if(storage instanceof TeamRebornStrictEnergyHandler integration) {
            return integration.storage;
        } else {
            return new TeamRebornEnergyIntegration(storage);
        }
    }

    @Override
    public long insert(long maxReceive, TransactionContext t) {
        if (maxReceive <= 0) {
            return 0;
        }
        long toInsert = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(maxReceive);
        try(Transaction inner = t.openNested()) {
            //Before we can actually execute it we need to simulate to calculate how much we can actually insert
            FloatingLong simulatedRemainder = handler.insertEnergy(FloatingLong.create(toInsert), t);
            if (simulatedRemainder.equals(toInsert)) {
                //Nothing can be inserted at all, just exit quickly
                return 0;
            }
            FloatingLong simulatedInserted = FloatingLong.create(toInsert).subtract(simulatedRemainder);
            //Convert how much we could insert back to FE so that it gets appropriately clamped so that for example 1.5 FE gets treated
            // as trying to insert 1 FE for how much we actually will accept, and then convert that clamped value to go back to Joules
            // so that we don't allow inserting a tiny bit of extra for "free" and end up creating power from nowhere
            toInsert = convertToAndBack(simulatedInserted).longValue();
            if (toInsert == 0) {
                //If converting back and forth between FE and Joules causes us to be clamped at zero, that means we can't accept anything or could only
                // accept a partial amount; we need to exit early returning that we couldn't insert anything
                return 0;
            }
            inner.abort();
        }
        FloatingLong remainder = handler.insertEnergy(FloatingLong.create(toInsert), t);
        if (remainder.equals(toInsert)) {
            //Nothing can be inserted at all, just exit quickly
            return 0;
        }
        FloatingLong inserted = FloatingLong.create(toInsert).subtract(remainder);
        return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertToAsInt(inserted.longValue());
    }

    @Override
    public long extract(long maxExtract, TransactionContext t) {
        if (maxExtract <= 0) {
            return 0;
        }
        FloatingLong toExtract = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(FloatingLong.create(maxExtract));
        try(Transaction inner = t.openNested()) {
            //Before we can actually execute it we need to simulate to calculate how much we can actually extract in our other units
            FloatingLong simulatedExtracted = handler.extractEnergy(toExtract, inner);
            //Convert how much we could extract back to FE so that it gets appropriately clamped so that for example 1.5 FE gets treated
            // as trying to extract 1 FE for how much we can actually provide, and then convert that clamped value to go back to Joules
            // so that we don't allow extracting a tiny bit into nowhere causing some power to be voided
            // This is important as otherwise if we can have 1.5 FE extracted, we will reduce our amount by 1.5 FE but the caller will only receive 1 FE
            toExtract = convertToAndBack(simulatedExtracted);
            if (toExtract.isZero()) {
                //If converting back and forth between FE and Joules causes us to be clamped at zero, that means we can't provide anything or could only
                // provide a partial amount; we need to exit early returning that nothing could be extracted
                return 0;
            }
            inner.abort();
        }
        FloatingLong extracted = handler.extractEnergy(toExtract, t);
        return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertToAsInt(extracted.longValue());
    }

    private FloatingLong convertToAndBack(FloatingLong value) {
        return FloatingLong.create(UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(value.longValue())));
    }

    @Override
    public long getAmount() {
        long energy = 0;
        for (int container = 0, containers = handler.getEnergyContainerCount(); container < containers; container++) {
            long total = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(handler.getEnergy(container).longValue());
            if (total > Long.MAX_VALUE - energy) {
                //Ensure we don't overflow
                return Long.MAX_VALUE;
            }
            energy += total;
        }
        return energy;
    }

    @Override
    public long getCapacity() {
        long maxEnergy = 0;
        for (int container = 0, containers = handler.getEnergyContainerCount(); container < containers; container++) {
            long max = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(handler.getMaxEnergy(container).longValue());
            if (max > Long.MAX_VALUE - maxEnergy) {
                //Ensure we don't overflow
                return Long.MAX_VALUE;
            }
            maxEnergy += max;
        }
        return maxEnergy;
    }

    @Override
    public boolean supportsExtraction() {
        //Mark that we can provide energy if we can extract energy
        try(Transaction t = Transaction.openOuter()) {
            if (!handler.extractEnergy(FloatingLong.ONE, t).isZero()) {
                return true;
            }
        }
        //Or all our containers are empty. This isn't fully accurate but will give the best
        // accuracy to other mods of if we may be able to extract given we are predicate based
        // instead of having strict can receive checks
        for (int container = 0, containers = handler.getEnergyContainerCount(); container < containers; container++) {
            if (!handler.getEnergy(container).isZero()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean supportsInsertion() {
        //Mark that we can receive energy if we can insert energy
        try(Transaction t = Transaction.openOuter()) {
            if (handler.insertEnergy(FloatingLong.ONE, t).smallerThan(FloatingLong.ONE)) {
                return true;
            }
        }
        //Or all our containers are full. This isn't fully accurate but will give the best
        // accuracy to other mods of if we may be able to receive given we are predicate based
        // instead of having strict can receive checks
        for (int container = 0, containers = handler.getEnergyContainerCount(); container < containers; container++) {
            if (!handler.getNeededEnergy(container).isZero()) {
                return false;
            }
        }
        return true;
    }

    public IStrictEnergyHandler getHandler() {
        return handler;
    }
}
