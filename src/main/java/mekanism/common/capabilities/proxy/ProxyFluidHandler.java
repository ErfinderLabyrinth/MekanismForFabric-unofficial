package mekanism.common.capabilities.proxy;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.fluid.IExtendedFluidHandler;
import mekanism.api.fluid.ISidedFluidHandler;
import mekanism.common.capabilities.holder.IHolder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

@NothingNullByDefault
public class ProxyFluidHandler extends ProxyHandler implements IExtendedFluidHandler {

    private final ISidedFluidHandler fluidHandler;

    public ProxyFluidHandler(ISidedFluidHandler fluidHandler, @Nullable Direction side, @Nullable IHolder holder) {
        super(side, holder);
        this.fluidHandler = fluidHandler;
    }

//    @Override
//    public int getTanks() {
//        return fluidHandler.getTanks(side);
//    }
//
//    @Override
//    public FluidStack getFluidInTank(int tank) {
//        return fluidHandler.getFluidInTank(tank, side);
//    }
//
//    @Override
//    public void setFluidInTank(int tank, FluidStack stack) {
//        if (!readOnly) {
//            fluidHandler.setFluidInTank(tank, stack, side);
//        }
//    }
//
//    @Override
//    public int getTankCapacity(int tank) {
//        return fluidHandler.getTankCapacity(tank, side);
//    }
//
//    @Override
//    public boolean isFluidValid(int tank, FluidStack stack) {
//        return !readOnly || fluidHandler.isFluidValid(tank, stack, side);
//    }

//    @Override
//    public FluidStack insertFluid(int tank, FluidStack stack, Action action) {
//        return readOnly || readOnlyInsert.getAsBoolean() ? stack : fluidHandler.insertFluid(tank, stack, side, action);
//    }
//
//    @Override
//    public FluidStack extractFluid(int tank, int amount, Action action) {
//        return readOnly || readOnlyExtract.getAsBoolean() ? FluidStack.EMPTY : fluidHandler.extractFluid(tank, amount, side, action);
//    }
//
//    @Override
//    public FluidStack insertFluid(FluidStack stack, Action action) {
//        return readOnly || readOnlyInsert.getAsBoolean() ? stack : fluidHandler.insertFluid(stack, side, action);
//    }
//
//    @Override
//    public FluidStack extractFluid(int amount, Action action) {
//        return readOnly || readOnlyExtract.getAsBoolean() ? FluidStack.EMPTY : fluidHandler.extractFluid(amount, side, action);
//    }
//
//    @Override
//    public FluidStack extractFluid(FluidStack stack, Action action) {
//        return readOnly || readOnlyExtract.getAsBoolean() ? FluidStack.EMPTY : fluidHandler.extractFluid(stack, side, action);
//    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return readOnly || readOnlyInsert.getAsBoolean() ? 0 : fluidHandler.getContainers(side).insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return readOnly || readOnlyExtract.getAsBoolean() ? 0 : fluidHandler.getContainers(side).extract(resource, maxAmount, transaction);
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        return fluidHandler.getContainers(side).iterator();
    }
}