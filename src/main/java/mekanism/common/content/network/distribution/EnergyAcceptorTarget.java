package mekanism.common.content.network.distribution;

import mekanism.api.Action;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.lib.distribution.SplitInfo;
import mekanism.common.lib.distribution.Target;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import team.reborn.energy.api.EnergyStorage;

import java.util.Collection;

public class EnergyAcceptorTarget extends Target<EnergyStorage, Long, Long> {

    public EnergyAcceptorTarget() {
    }

    public EnergyAcceptorTarget(Collection<EnergyStorage> allHandlers) {
        super(allHandlers);
    }

    public EnergyAcceptorTarget(int expectedSize) {
        super(expectedSize);
    }

    @Override
    protected void acceptAmount(EnergyStorage handler, SplitInfo<Long> splitInfo, Long amount) {
        try(Transaction t=Transaction.openOuter()) {
            splitInfo.send(amount - handler.insert(amount, t));
            t.commit();
        }
    }

    @Override
    protected Long simulate(EnergyStorage handler, Long energyToSend) {
        try(Transaction t=Transaction.openOuter()) {
            return energyToSend - handler.insert(energyToSend, t);
        }
    }
}