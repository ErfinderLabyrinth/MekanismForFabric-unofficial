package mekanism.common.capabilities.chemical.item;

import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.chemical.variable.RateLimitChemicalTank;
import mekanism.common.tier.ChemicalTankTier;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public abstract class ChemicalTankRateLimitChemicalTank<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>>
      extends RateLimitChemicalTank<CHEMICAL, STACK, TANK> {

    private final boolean isCreative;

    private ChemicalTankRateLimitChemicalTank(ChemicalTankTier tier, ChemicalTankBuilder<CHEMICAL, STACK, ?> tankBuilder, @Nullable IContentsListener listener) {
        super(tier::getOutput, tier::getStorage, tankBuilder.alwaysTrueBi, tankBuilder.alwaysTrueBi, tankBuilder.alwaysTrue,
              tier == ChemicalTankTier.CREATIVE ? ChemicalAttributeValidator.ALWAYS_ALLOW : null, listener);
        isCreative = tier == ChemicalTankTier.CREATIVE;
    }

//    @Deprecated(forRemoval = true)
//    @Override
//    public STACK insert(STACK stack, Action action, AutomationType automationType) {
//        return super.insert(stack, action.combine(!isCreative), automationType);
//    }
//
//    @Deprecated(forRemoval = true)
//    @Override
//    public STACK extract(long amount, Action action, AutomationType automationType) {
//        return super.extract(amount, action.combine(!isCreative), automationType);
//    }

    @Override
    public long insert(CHEMICAL resource, long maxAmount, TransactionContext transaction) {
        long inserted;
        try(Transaction t2=Transaction.openOuter()) {
            inserted = super.insert(resource, maxAmount, t2);
            if(!isCreative) {
                t2.commit();
            }
        }
        return inserted;
    }

    @Override
    public long extract(CHEMICAL resource, long maxAmount, TransactionContext transaction) {
        long extracted;
        try(Transaction t2=Transaction.openOuter()) {
            extracted = super.extract(resource, maxAmount, t2);
            if(!isCreative) {
                t2.commit();
            }
        }
        return extracted;
    }

    /**
     * {@inheritDoc}
     *
     * Note: We are only patching {@link #setStackSize(long)}, as both {@link #growStack(long)} and {@link #shrinkStack(long)} are wrapped through
     * this method.
     */
    @Override
    public long setStackSize(long amount) {
        if (isCreative) {
            return isEmpty() ? 0 : Math.min(amount, 0);
        }else {
            return super.setStackSize(amount);
        }
    }

    public static class GasTankRateLimitChemicalTank extends ChemicalTankRateLimitChemicalTank<Gas, GasStack, IGasTank> implements IGasHandler, IGasTank {

        public GasTankRateLimitChemicalTank(ChemicalTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.GAS, listener);
        }
    }

    public static class InfusionTankRateLimitChemicalTank extends ChemicalTankRateLimitChemicalTank<InfuseType, InfusionStack, IInfusionTank> implements IInfusionHandler, IInfusionTank {

        public InfusionTankRateLimitChemicalTank(ChemicalTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.INFUSION, listener);
        }
    }

    public static class PigmentTankRateLimitChemicalTank extends ChemicalTankRateLimitChemicalTank<Pigment, PigmentStack, IPigmentTank> implements IPigmentHandler, IPigmentTank {

        public PigmentTankRateLimitChemicalTank(ChemicalTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.PIGMENT, listener);
        }
    }

    public static class SlurryTankRateLimitChemicalTank extends ChemicalTankRateLimitChemicalTank<Slurry, SlurryStack, ISlurryTank> implements ISlurryHandler, ISlurryTank {

        public SlurryTankRateLimitChemicalTank(ChemicalTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.SLURRY, listener);
        }
    }
}