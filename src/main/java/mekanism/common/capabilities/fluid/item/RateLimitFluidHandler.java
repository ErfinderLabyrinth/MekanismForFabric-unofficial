package mekanism.common.capabilities.fluid.item;

import mekanism.api.AutomationType;
import mekanism.api.FluidStack;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.tier.FluidTankTier;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Predicate;

@NothingNullByDefault
public class RateLimitFluidHandler extends ItemStackMekanismFluidHandler {

    public static RateLimitFluidHandler create(LongSupplier rate, LongSupplier capacity) {
        return create(rate, capacity, BasicFluidTank.alwaysTrueBi, BasicFluidTank.alwaysTrueBi, BasicFluidTank.alwaysTrue);
    }

    public static RateLimitFluidHandler create(LongSupplier rate, LongSupplier capacity, BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canExtract,
          BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canInsert, Predicate<@NotNull FluidStack> isValid) {
        Objects.requireNonNull(rate, "Rate supplier cannot be null");
        Objects.requireNonNull(capacity, "Capacity supplier cannot be null");
        Objects.requireNonNull(canExtract, "Extraction validity check cannot be null");
        Objects.requireNonNull(canInsert, "Insertion validity check cannot be null");
        Objects.requireNonNull(isValid, "Gas validity check cannot be null");
        return new RateLimitFluidHandler(listener -> new RateLimitFluidTank(rate, capacity, canExtract, canInsert, isValid, listener));
    }

    public static RateLimitFluidHandler create(FluidTankTier tier) {
        Objects.requireNonNull(tier, "Fluid tank tier cannot be null");
        return new RateLimitFluidHandler(listener -> new FluidTankRateLimitFluidTank(tier, listener));
    }

    private final IExtendedFluidTank tank;

    private RateLimitFluidHandler(Function<IContentsListener, IExtendedFluidTank> tankProvider) {
        tank = tankProvider.apply(this);
        init();
    }

    @Override
    protected List<IExtendedFluidTank> getInitialTanks() {
        return Collections.singletonList(tank);
    }

    public static class RateLimitFluidTank extends VariableCapacityFluidTank {

        private final LongSupplier rate;

        public RateLimitFluidTank(LongSupplier rate, LongSupplier capacity, @Nullable IContentsListener listener) {
            this(rate, capacity, alwaysTrueBi, alwaysTrueBi, alwaysTrue, listener);
        }

        public RateLimitFluidTank(LongSupplier rate, LongSupplier capacity, BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canExtract,
              BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canInsert, Predicate<@NotNull FluidStack> isValid, @Nullable IContentsListener listener) {
            super(capacity, canExtract, canInsert, isValid, listener);
            this.rate = rate;
        }

        @Override
        protected long getRate(@Nullable AutomationType automationType) {
            //Allow unknown or manual interaction to bypass rate limit for the item
            return automationType == null || automationType == AutomationType.MANUAL ? super.getRate(automationType) : rate.getAsLong();
        }
    }

    private static class FluidTankRateLimitFluidTank extends RateLimitFluidTank {

        private final boolean isCreative;

        private FluidTankRateLimitFluidTank(FluidTankTier tier, @Nullable IContentsListener listener) {
            super(tier::getOutput, tier::getStorage, BasicFluidTank.alwaysTrueBi, BasicFluidTank.alwaysTrueBi, BasicFluidTank.alwaysTrue, listener);
            isCreative = tier == FluidTankTier.CREATIVE;
        }

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            long amountInserted;
            try(Transaction t=Transaction.openNested(transaction)) {
                amountInserted = super.insert(resource, maxAmount, t);
                if (!isCreative)
                    t.commit();
            }
            return amountInserted;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            long amountExtracted;
            try(Transaction t=Transaction.openNested(transaction)) {
                amountExtracted = super.extract(resource, maxAmount, t);
                if (!isCreative)
                    t.commit();
            }
            return amountExtracted;
        }

        /**
         * {@inheritDoc}
         *
         * Note: We are only patching {@link #setStackSize(long)}, as both {@link #growStack(long)} and {@link #shrinkStack(long)} are wrapped
         * through this method.
         */
        @Override
        public long setStackSize(long amount) {
            if (isCreative) {
                if (isEmpty() || amount <= 0) {
                    return 0;
                }
                return Math.min(getCapacity(), amount);
            }
            return super.setStackSize(amount);
        }
    }
}