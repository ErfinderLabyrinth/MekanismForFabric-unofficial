package mekanism.common.capabilities.chemical;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.*;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.tier.ChemicalTankTier;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.LongSupplier;

@NothingNullByDefault
public abstract class ChemicalTankChemicalTank<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>> extends BasicChemicalTank<CHEMICAL, STACK, TANK> {

    public static MergedChemicalTank create(ChemicalTankTier tier, @Nullable IContentsListener listener) {
        Objects.requireNonNull(tier, "Chemical tank tier cannot be null");
        return MergedChemicalTank.create(
              new GasTankChemicalTank(tier, listener),
              new InfusionTankChemicalTank(tier, listener),
              new PigmentTankChemicalTank(tier, listener),
              new SlurryTankChemicalTank(tier, listener)
        );
    }

    private final boolean isCreative;
    private final LongSupplier rate;

    private ChemicalTankChemicalTank(ChemicalTankTier tier, ChemicalTankBuilder<CHEMICAL, STACK, ?> tankBuilder, @Nullable IContentsListener listener) {
        super(tier.getStorage(), tankBuilder.alwaysTrueBi, tankBuilder.alwaysTrueBi, tankBuilder.alwaysTrue,
              tier == ChemicalTankTier.CREATIVE ? ChemicalAttributeValidator.ALWAYS_ALLOW : null, listener);
        isCreative = tier == ChemicalTankTier.CREATIVE;
        rate = tier::getOutput;
    }

    @Override
    protected long getRate(@Nullable AutomationType automationType) {
        //Only limit the internal rate to change the speed at which this can be filled from an item
        return automationType == AutomationType.INTERNAL ? rate.getAsLong() : super.getRate(automationType);
    }

    @Override
    public long insert(CHEMICAL resource, long maxAmount, TransactionContext transaction) {
        if (isCreative && isEmpty()) {
            //If a player manually inserts into a creative tank (or internally, via a GasInventorySlot), that is empty we need to allow setting the type,
            // Note: We check that it is not external insertion because an empty creative tanks acts as a "void" for automation
            long inserted;
            try(Transaction t=Transaction.openNested(transaction)) {
                inserted = super.insert(resource, maxAmount, t);
            }
            if (inserted == maxAmount) {
                //If we are able to insert it then set perform the action of setting it to full
                setStackUnchecked(createStack(resource, getCapacity()));
            }
            return inserted;
        }

        try(Transaction t=Transaction.openNested(transaction)) {
            long inserted = super.insert(resource, maxAmount, t);
            if (!isCreative)
                t.commit();
            return inserted;
        }
    }

    @Override
    public long extract(CHEMICAL resource, long maxAmount, TransactionContext transaction) {
        try(Transaction t=Transaction.openNested(transaction)) {
            long extracted = super.extract(resource, maxAmount, t);
            if (!isCreative)
                t.commit();
            return extracted;
        }
    }

    /**
     * {@inheritDoc}
     *
     * Note: We are only patching {@link #setStackSize(long)}, as both {@link #growStack(long)} and {@link #shrinkStack(long)} are wrapped through
     * this method.
     */
    @Override
    public long setStackSize(long amount) {
        return super.setStackSize(amount);
    }

    private static class GasTankChemicalTank extends ChemicalTankChemicalTank<Gas, GasStack, IGasTank> implements IGasHandler, IGasTank {

        private GasTankChemicalTank(ChemicalTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.GAS, listener);
        }
    }

    private static class InfusionTankChemicalTank extends ChemicalTankChemicalTank<InfuseType, InfusionStack, IInfusionTank> implements IInfusionHandler, IInfusionTank {

        private InfusionTankChemicalTank(ChemicalTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.INFUSION, listener);
        }
    }

    private static class PigmentTankChemicalTank extends ChemicalTankChemicalTank<Pigment, PigmentStack, IPigmentTank> implements IPigmentHandler, IPigmentTank {

        private PigmentTankChemicalTank(ChemicalTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.PIGMENT, listener);
        }
    }

    private static class SlurryTankChemicalTank extends ChemicalTankChemicalTank<Slurry, SlurryStack, ISlurryTank> implements ISlurryHandler, ISlurryTank {

        private SlurryTankChemicalTank(ChemicalTankTier tier, @Nullable IContentsListener listener) {
            super(tier, ChemicalTankBuilder.SLURRY, listener);
        }
    }
}