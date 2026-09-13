package mekanism.common.capabilities.fluid.item;

import mekanism.api.AutomationType;
import mekanism.api.FluidStack;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.functions.TriPredicate;
import mekanism.common.capabilities.GenericTankSpec;
import mekanism.common.capabilities.fluid.item.RateLimitFluidHandler.RateLimitFluidTank;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.LongSupplier;
import java.util.function.Predicate;

@NothingNullByDefault
public class RateLimitMultiTankFluidHandler extends ItemStackMekanismFluidHandler {

    public static RateLimitMultiTankFluidHandler create(ItemStack itemStack, @NotNull Collection<FluidTankSpec> fluidTanks) {
        return new RateLimitMultiTankFluidHandler(itemStack, fluidTanks);
    }

    private final List<IExtendedFluidTank> tanks;

    private RateLimitMultiTankFluidHandler(ItemStack stack, @NotNull Collection<FluidTankSpec> fluidTanks) {
        List<IExtendedFluidTank> tankProviders = new ArrayList<>();
        for (FluidTankSpec spec : fluidTanks) {
            tankProviders.add(new RateLimitFluidTank(spec.rate, spec.capacity, spec.canExtract,
                  (fluid, automationType) -> spec.canInsert.test(fluid, automationType, stack), spec.isValid, this));
        }
        tanks = Collections.unmodifiableList(tankProviders);
        init();
    }

    @Override
    protected List<IExtendedFluidTank> getInitialTanks() {
        return tanks;
    }

    public static class FluidTankSpec extends GenericTankSpec<FluidStack> {

        final LongSupplier rate;
        final LongSupplier capacity;

        public FluidTankSpec(LongSupplier rate, LongSupplier capacity, BiPredicate<@NotNull FluidStack, @NotNull AutomationType> canExtract,
                             TriPredicate<@NotNull FluidStack, @NotNull AutomationType, @NotNull ItemStack> canInsert, Predicate<@NotNull FluidStack> isValid,
                             Predicate<@NotNull ItemStack> supportsStack) {
            super(canExtract, canInsert, isValid, supportsStack);
            this.rate = rate;
            this.capacity = capacity;
        }

        public static FluidTankSpec create(LongSupplier rate, LongSupplier capacity) {
            return new FluidTankSpec(rate, capacity, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrueTri(), ConstantPredicates.alwaysTrue(),
                  ConstantPredicates.alwaysTrue());
        }

        public static FluidTankSpec createFillOnly(LongSupplier rate, LongSupplier capacity, Predicate<@NotNull FluidStack> isValid) {
            return createFillOnly(rate, capacity, isValid, ConstantPredicates.alwaysTrue());
        }

        public static FluidTankSpec createFillOnly(LongSupplier rate, LongSupplier capacity, Predicate<@NotNull FluidStack> isValid,
              Predicate<@NotNull ItemStack> supportsStack) {
            return new FluidTankSpec(rate, capacity, ConstantPredicates.notExternal(), (chemical, automation, stack) -> supportsStack.test(stack), isValid, supportsStack);
        }
    }
}
