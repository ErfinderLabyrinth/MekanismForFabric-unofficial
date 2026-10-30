package mekanism.common.capabilities.energy.item;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.VariableCapacityEnergyContainer;
import mekanism.common.tier.EnergyCubeTier;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Predicate;

@NothingNullByDefault
public class RateLimitEnergyHandler extends ItemStackEnergyHandler {

    public static RateLimitEnergyHandler create(EnergyCubeTier tier) {
        Objects.requireNonNull(tier, "Energy cube tier cannot be null");
        return new RateLimitEnergyHandler(handler -> new EnergyCubeRateLimitEnergyContainer(tier, handler));
    }

    public static RateLimitEnergyHandler create(LongSupplier capacity, Predicate<@NotNull AutomationType> canExtract, Predicate<@NotNull AutomationType> canInsert) {
        return create(() -> (long) (capacity.getAsLong() * 0.005), capacity, canExtract, canInsert);
    }

    public static RateLimitEnergyHandler create(LongSupplier rate, LongSupplier capacity, Predicate<@NotNull AutomationType> canExtract,
                                                Predicate<@NotNull AutomationType> canInsert) {
        Objects.requireNonNull(rate, "Rate supplier cannot be null");
        Objects.requireNonNull(capacity, "Capacity supplier cannot be null");
        Objects.requireNonNull(canExtract, "Extraction validity check cannot be null");
        Objects.requireNonNull(canInsert, "Insertion validity check cannot be null");
        return new RateLimitEnergyHandler(handler -> new RateLimitEnergyContainer(rate, capacity, canExtract, canInsert, handler));
    }

    private final IEnergyContainer energyContainer;

    private RateLimitEnergyHandler(Function<ItemStackEnergyHandler, IEnergyContainer> energyContainerProvider) {
        this.energyContainer = energyContainerProvider.apply(this);
    }

    @Override
    public long getEnergy() {
        return energyContainer.getAmount();
    }

    @Override
    public void setEnergy(long energy, TransactionContext t) {
        energyContainer.setEnergy(energy);
    }

    @Override
    public long getMaxEnergy() {
        return energyContainer.getMaxEnergy();
    }

    @Override
    public long getCapacity() {
        return energyContainer.getCapacity();
    }

    @Override
    protected List<IEnergyContainer> getInitialContainers() {
        return Collections.singletonList(energyContainer);
    }

    private static class RateLimitEnergyContainer extends VariableCapacityEnergyContainer {

        private final LongSupplier rate;

        private RateLimitEnergyContainer(LongSupplier rate, LongSupplier capacity, Predicate<@NotNull AutomationType> canExtract,
              Predicate<@NotNull AutomationType> canInsert, @Nullable IContentsListener listener) {
            super(capacity, canExtract, canInsert, listener);
            this.rate = rate;
        }

        @Override
        protected long getRate(@Nullable AutomationType automationType) {
            //Allow unknown or manual interaction to bypass rate limit for the item
            return automationType == null || automationType == AutomationType.MANUAL ? super.getRate(automationType) : rate.getAsLong();
        }
    }

    private static class EnergyCubeRateLimitEnergyContainer extends RateLimitEnergyContainer {

        private final boolean isCreative;

        private EnergyCubeRateLimitEnergyContainer(EnergyCubeTier tier, @Nullable IContentsListener listener) {
            super(tier::getOutput, tier::getMaxEnergy, BasicEnergyContainer.alwaysTrue, BasicEnergyContainer.alwaysTrue, listener);
            isCreative = tier == EnergyCubeTier.CREATIVE;
        }

        @Override
        public long insert(long amount, TransactionContext t) {
            long inserted;
            try(Transaction t2=Transaction.openOuter()) {
                inserted = super.insert(amount, t2);
                if(!isCreative) {
                    t2.commit();
                }
            }
            return inserted;
        }

        @Override
        public long extract(long amount, TransactionContext t) {
            long extracted;
            try(Transaction t2=Transaction.openOuter()) {
                extracted = super.extract(amount, t2);
                if(!isCreative) {
                    t2.commit();
                }
            }
            return extracted;
        }
    }
}