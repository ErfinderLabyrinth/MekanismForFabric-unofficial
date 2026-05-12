package mekanism.common.content.network.distribution;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.lib.distribution.SplitInfo;
import mekanism.common.lib.distribution.Target;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import team.reborn.energy.api.EnergyStorage;

import java.util.Collection;

public class EnergySaveTarget extends Target<EnergySaveTarget.SaveHandler, FloatingLong, FloatingLong> {

    public EnergySaveTarget() {
    }

    public EnergySaveTarget(Collection<EnergySaveTarget.SaveHandler> allHandlers) {
        super(allHandlers);
    }

    public EnergySaveTarget(int expectedSize) {
        super(expectedSize);
    }

    @Override
    protected void acceptAmount(EnergySaveTarget.SaveHandler handler, SplitInfo<FloatingLong> splitInfo, FloatingLong amount) {
        handler.acceptAmount(splitInfo, amount);
    }

    @Override
    protected FloatingLong simulate(EnergySaveTarget.SaveHandler handler, FloatingLong energyToSend) {
        return handler.simulate(energyToSend);
    }

    public void save() {
        for (SaveHandler handler : handlers) {
            handler.save();
        }
    }

    public void addDelegate(EnergyStorage delegate) {
        this.addHandler(new SaveHandler(delegate));
    }

    @NothingNullByDefault
    public static class SaveHandler {

        private final EnergyStorage delegate;
        private FloatingLong currentStored = FloatingLong.ZERO;

        public SaveHandler(EnergyStorage delegate) {
            this.delegate = delegate;
        }

        protected void acceptAmount(SplitInfo<FloatingLong> splitInfo, FloatingLong amount) {
            amount = amount.min(FloatingLong.create(delegate.getCapacity()).subtract(currentStored));
            currentStored = currentStored.plusEqual(amount);
            splitInfo.send(amount);
        }

        protected FloatingLong simulate(FloatingLong energyToSend) {
            return energyToSend.copy().min(FloatingLong.create(delegate.getCapacity()).subtract(currentStored));
        }

        protected void save() {
            if (delegate instanceof IEnergyContainer energyContainer) {
                try(Transaction t=Transaction.openOuter()) {
                    energyContainer.setEnergy(currentStored.longValue(), t);
                    t.commit();
                }
            }
            long dif = currentStored.longValue() - delegate.getAmount();
            if (dif > 0) {
                try(Transaction t=Transaction.openOuter()) {
                    delegate.insert(dif, t);
                    t.commit();
                }
            }else if(dif < 0) {
                try(Transaction t=Transaction.openOuter()) {
                    delegate.extract(-dif, t);
                    t.commit();
                }
            }
        }
    }
}