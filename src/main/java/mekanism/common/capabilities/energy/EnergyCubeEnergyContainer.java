package mekanism.common.capabilities.energy;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.tier.EnergyCubeTier;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.LongSupplier;

@NothingNullByDefault
public class EnergyCubeEnergyContainer extends BasicEnergyContainer {

    public static EnergyCubeEnergyContainer create(EnergyCubeTier tier, @Nullable IContentsListener listener) {
        Objects.requireNonNull(tier, "Energy cube tier cannot be null");
        return new EnergyCubeEnergyContainer(tier, listener);
    }

    private final boolean isCreative;
    private final LongSupplier rate;

    private EnergyCubeEnergyContainer(EnergyCubeTier tier, @Nullable IContentsListener listener) {
        super(tier.getMaxEnergy(), alwaysTrue, alwaysTrue, listener);
        isCreative = tier == EnergyCubeTier.CREATIVE;
        rate = tier::getOutput;
    }

    @Override
    protected long getRate(@Nullable AutomationType automationType) {
        //Only limit the internal rate to change the speed at which this can be filled from an item
        return automationType == AutomationType.INTERNAL ? rate.getAsLong() : super.getRate(automationType);
    }

    @Override
    public long insert(long amount, TransactionContext t) {
        try(Transaction t2=Transaction.openOuter()) {
            return super.insert(amount, t2);
        }
    }

    @Override
    public long extract(long amount, TransactionContext t) {
        try(Transaction t2=Transaction.openOuter()) {
            return super.extract(amount, t2);
        }
    }
}