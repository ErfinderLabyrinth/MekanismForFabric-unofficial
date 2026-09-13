package mekanism.common.capabilities.proxy;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.*;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.gas.IGasHandler.ISidedGasHandler;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.IInfusionHandler.ISidedInfusionHandler;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.IPigmentHandler.ISidedPigmentHandler;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.ISlurryHandler.ISidedSlurryHandler;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.holder.IHolder;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

@NothingNullByDefault
public abstract class ProxyChemicalHandler<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>,
      SIDED_HANDLER extends ISidedChemicalHandler<CHEMICAL, STACK, TANK>> extends ProxyHandler implements IChemicalHandler<CHEMICAL, STACK, TANK> {

    private final SIDED_HANDLER sidedHandler;

    public ProxyChemicalHandler(SIDED_HANDLER sidedHandler, @Nullable Direction side, @Nullable IHolder holder) {
        super(side, holder);
        this.sidedHandler = sidedHandler;
    }

    /**
     * @apiNote This is only for use in the TOP integration to allow us to properly handle hiding merged chemical tanks, and <strong>SHOULD NOT</strong> be called from
     * anywhere else. It is also important to not use this to bypass write access the proxy may limit.
     */
    public <TANK extends IChemicalTank<CHEMICAL, STACK>> Storage<CHEMICAL> getTanksIfMekanism() {
        if (sidedHandler instanceof IMekanismChemicalHandler) {
            return new CombinedStorage<>(((IMekanismChemicalHandler<CHEMICAL, STACK, TANK>) sidedHandler).getChemicalTanks(null));
        }
        return Storage.empty();
    }

    @Override
    public List<TANK> getTanks() {
        return sidedHandler.getTanks(side);
    }

//    @Override
//    public STACK getChemicalInTank(int tank) {
//        return sidedHandler.getChemicalInTank(tank, side);
//    }
//
//    @Override
//    public void setChemicalInTank(int tank, STACK stack) {
//        if (!readOnly) {
//            sidedHandler.setChemicalInTank(tank, stack, side);
//        }
//    }
//
//    @Override
//    public long getTankCapacity(int tank) {
//        return sidedHandler.getTankCapacity(tank, side);
//    }
//
//    @Override
//    public boolean isValid(int tank, STACK stack) {
//        return !readOnly || sidedHandler.isValid(tank, stack, side);
//    }
//
//    @Override
//    public STACK insertChemical(int tank, STACK stack, Action action) {
//        return readOnly || readOnlyInsert.getAsBoolean() ? stack : sidedHandler.insertChemical(tank, stack, side, action);
//    }
//
//    @Override
//    public STACK extractChemical(int tank, long amount, Action action) {
//        return readOnly || readOnlyExtract.getAsBoolean() ? getEmptyStack() : sidedHandler.extractChemical(tank, amount, side, action);
//    }
//
//    @Override
//    public STACK insertChemical(STACK stack, Action action) {
//        return readOnly || readOnlyInsert.getAsBoolean() ? stack : sidedHandler.insertChemical(stack, side, action);
//    }
//
//    @Override
//    public STACK extractChemical(long amount, Action action) {
//        return readOnly || readOnlyExtract.getAsBoolean() ? getEmptyStack() : sidedHandler.extractChemical(amount, side, action);
//    }
//
//    @Override
//    public STACK extractChemical(STACK stack, Action action) {
//        return readOnly || readOnlyExtract.getAsBoolean() ? getEmptyStack() : sidedHandler.extractChemical(stack, side, action);
//    }

    @Override
    public Iterator<StorageView<CHEMICAL>> iterator() {
        return sidedHandler.iterator();
    }

    @Override
    public long insert(CHEMICAL resource, long maxAmount, TransactionContext transaction) {
        return readOnly || readOnlyInsert.getAsBoolean() ? 0 : sidedHandler.insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(CHEMICAL resource, long maxAmount, TransactionContext transaction) {
        return readOnly || readOnlyExtract.getAsBoolean() ? 0 : sidedHandler.extract(resource, maxAmount, transaction);
    }

    public static class ProxyGasHandler extends ProxyChemicalHandler<Gas, GasStack, IGasTank, ISidedGasHandler> implements IGasHandler {

        public ProxyGasHandler(@NotNull ISidedGasHandler gasHandler, @Nullable Direction side, @Nullable IHolder holder) {
            super(gasHandler, side, holder);
        }
    }

    public static class ProxyInfusionHandler extends ProxyChemicalHandler<InfuseType, InfusionStack, IInfusionTank, ISidedInfusionHandler> implements IInfusionHandler {

        public ProxyInfusionHandler(@NotNull ISidedInfusionHandler infusionHandler, @Nullable Direction side, @Nullable IHolder holder) {
            super(infusionHandler, side, holder);
        }
    }

    public static class ProxyPigmentHandler extends ProxyChemicalHandler<Pigment, PigmentStack, IPigmentTank, ISidedPigmentHandler> implements IPigmentHandler {

        public ProxyPigmentHandler(@NotNull ISidedPigmentHandler pigmentHandler, @Nullable Direction side, @Nullable IHolder holder) {
            super(pigmentHandler, side, holder);
        }
    }

    public static class ProxySlurryHandler extends ProxyChemicalHandler<Slurry, SlurryStack, ISlurryTank, ISidedSlurryHandler> implements ISlurryHandler {

        public ProxySlurryHandler(@NotNull ISidedSlurryHandler slurryHandler, @Nullable Direction side, @Nullable IHolder holder) {
            super(slurryHandler, side, holder);
        }
    }
}