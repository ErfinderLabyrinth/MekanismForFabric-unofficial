package mekanism.common.capabilities.fluid;

import mekanism.api.AutomationType;
import mekanism.api.FluidStack;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.tier.FluidTankTier;
import mekanism.common.tile.TileEntityFluidTank;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.IntSupplier;

@NothingNullByDefault
public class FluidTankFluidTank extends BasicFluidTank {

    public static FluidTankFluidTank create(TileEntityFluidTank tile, @Nullable IContentsListener listener) {
        Objects.requireNonNull(tile, "Fluid tank tile entity cannot be null");
        return new FluidTankFluidTank(tile, listener);
    }

    private final TileEntityFluidTank tile;
    private final boolean isCreative;
    private final IntSupplier rate;

    private FluidTankFluidTank(TileEntityFluidTank tile, @Nullable IContentsListener listener) {
        super(tile.tier.getStorage(), alwaysTrueBi, alwaysTrueBi, alwaysTrue, listener);
        this.tile = tile;
        rate = tile.tier::getOutput;
        isCreative = tile.tier == FluidTankTier.CREATIVE;
    }

    @Override
    protected long getRate(@Nullable AutomationType automationType) {
        //Only limit the internal rate to change the speed at which this can be filled from an item
        return automationType == AutomationType.INTERNAL ? rate.getAsInt() * 81 : super.getRate(automationType);
    }

    @Override
    public long insert(FluidVariant resource, long amount, TransactionContext t) {
        long amountInserted = 0;
        FluidStack remainder;
        if (isCreative && isEmpty()) {
            //If a player manually inserts into a creative tank (or internally, via a FluidInventorySlot), that is empty we need to allow setting the type,
            // Note: We check that it is not external insertion because an empty creative tanks acts as a "void" for automation
            try(Transaction t2 = Transaction.openNested(t)) {
                amountInserted = super.insert(resource, amount, t2);
            }
            if (amount - amountInserted == 0) {
                //If we are able to insert it then set perform the action of setting it to full
                setStackUnchecked(new FluidStack(resource, getCapacity()));
            }
        } else {
            try(Transaction t2=Transaction.openNested(t)) {
                amountInserted = super.insert(resource, amount, t2);
                if (!isCreative) {
                    t2.commit();
                }
            }
        }
        if (amount - amountInserted > 0) {
            //If we have any leftover check if we can send it to the tank that is above
            TileEntityFluidTank tileAbove = WorldUtils.getTileEntity(TileEntityFluidTank.class, this.tile.getLevel(), this.tile.getBlockPos().above());
            if (tileAbove != null) {
                //Note: We do external so that it is not limited by the internal rate limits
                amountInserted += tileAbove.fluidTank.insert(resource, amount - amountInserted, t);
            }
        }
        return amountInserted;
    }

    @Override
    public long growStack(long amount) {
        long grownAmount = super.growStack(amount);
        if (amount > 0 && grownAmount < amount) {
            //If we grew our stack less than we tried to, and we were actually growing and not shrinking it
            // try inserting into above tiles
            if (!tile.getActive()) {
                TileEntityFluidTank tileAbove = WorldUtils.getTileEntity(TileEntityFluidTank.class, this.tile.getLevel(), this.tile.getBlockPos().above());
                if (tileAbove != null) {
                    long leftOverToInsert = amount - grownAmount;
                    //Note: We do external so that it is not limited by the internal rate limits
                    try(Transaction t=Transaction.openOuter()) {
                        grownAmount += tileAbove.fluidTank.insert(stored.variant(), leftOverToInsert, t);
                        t.commit();
                    }
                }
            }
        }
        return grownAmount;
    }

    @Override
    public long extract(FluidVariant resource, long amount, TransactionContext t) {
        try(Transaction t2=Transaction.openNested(t)) {
            long result = super.extract(resource, amount, t2);
            if (!isCreative) {
                t2.commit();
            }
            return result;
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
        if (isCreative) {
            if (isEmpty() || amount <= 0) {
                return 0;
            }
            return Math.min(amount, getCapacity());
        }
        return super.setStackSize(amount);
    }
}