//package mekanism.common.capabilities.proxy;
//
//import mekanism.api.annotations.NothingNullByDefault;
//import mekanism.api.energy.ISidedStrictEnergyHandler;
//import mekanism.api.energy.IStrictEnergyHandler;
//import mekanism.api.math.FloatingLong;
//import mekanism.common.capabilities.holder.IHolder;
//import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
//import net.minecraft.core.Direction;
//import org.jetbrains.annotations.Nullable;
//
//@NothingNullByDefault
//public class ProxyStrictEnergyHandler extends ProxyHandler implements IStrictEnergyHandler {
//
//    private final ISidedStrictEnergyHandler energyHandler;
//
//    public ProxyStrictEnergyHandler(ISidedStrictEnergyHandler energyHandler, @Nullable Direction side, @Nullable IHolder holder) {
//        super(side, holder);
//        this.energyHandler = energyHandler;
//    }
//
//    @Override
//    public int getEnergyContainerCount() {
//        return energyHandler.getEnergyContainerCount(side);
//    }
//
//    @Override
//    public FloatingLong getEnergy(int container) {
//        return energyHandler.getEnergy(container, side);
//    }
//
//    @Override
//    public void setEnergy(int container, FloatingLong energy, TransactionContext t) {
//        if (!readOnly) {
//            energyHandler.setEnergy(container, energy, side, t);
//        }
//    }
//
//    @Override
//    public FloatingLong getMaxEnergy(int container) {
//        return energyHandler.getMaxEnergy(container, side);
//    }
//
//    @Override
//    public FloatingLong getNeededEnergy(int container) {
//        return energyHandler.getNeededEnergy(container, side);
//    }
//
//    @Override
//    public FloatingLong insertEnergy(int container, FloatingLong amount, TransactionContext t) {
//        return readOnly || readOnlyInsert.getAsBoolean() ? amount : energyHandler.insertEnergy(container, amount, side, t);
//    }
//
//    @Override
//    public FloatingLong extractEnergy(int container, FloatingLong amount, TransactionContext t) {
//        return readOnly || readOnlyExtract.getAsBoolean() ? FloatingLong.ZERO : energyHandler.extractEnergy(container, amount, side, t);
//    }
//
//    @Override
//    public FloatingLong insertEnergy(FloatingLong amount, TransactionContext t) {
//        return readOnly || readOnlyInsert.getAsBoolean() ? amount : energyHandler.insertEnergy(amount, side, t);
//    }
//
//    @Override
//    public FloatingLong extractEnergy(FloatingLong amount, TransactionContext t) {
//        return readOnly || readOnlyExtract.getAsBoolean() ? FloatingLong.ZERO : energyHandler.extractEnergy(amount, side, t);
//    }
//}