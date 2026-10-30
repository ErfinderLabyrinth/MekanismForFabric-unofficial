package mekanism.api.chemical;

import java.util.List;

import com.google.common.collect.Iterators;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public interface IMekanismChemicalHandler<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>>
      extends ISidedChemicalHandler<CHEMICAL, STACK, TANK>, IContentsListener {

    /**
     * Used to check if an instance of {@link IMekanismChemicalHandler} actually has the ability to handle chemicals.
     *
     * @return True if we are actually capable of handling chemicals.
     *
     * @apiNote If for some reason you are comparing to {@link IMekanismChemicalHandler} without having gotten the object via the chemical handler capability, then you
     * must call this method to make sure that it really can handle chemicals. As most mekanism tiles have this class in their hierarchy.
     * @implNote If this returns false the capability should not be exposed AND methods should turn reasonable defaults for not doing anything.
     */
    default boolean canHandle() {
        return true;
    }

    /**
     * Returns the list of TANKs that this chemical handler exposes on the given side.
     *
     * @param side The side we are interacting with the handler from (null for internal).
     *
     * @return The list of all TANKs that this {@link IMekanismChemicalHandler} contains for the given side. If there are no tanks for the side or {@link #canHandle()} is
     * false then it returns an empty list.
     *
     * @implNote When side is null (an internal request), this method <em>MUST</em> return all tanks in the handler. Additionally, if {@link #canHandle()} is false, this
     * <em>MUST</em> return an empty list.
     */
    List<TANK> getChemicalTanks(@Nullable Direction side);

    /**
     * Returns the {@link TANK} that has the given index from the list of tanks on the given side.
     *
     * @param tank The index of the tank to retrieve.
     * @param side The side we are interacting with the handler from (null for internal).
     *
     * @return The {@link TANK} that has the given index from the list of tanks on the given side.
     */
    @Deprecated(forRemoval = true)
    @Nullable
    default STACK getChemicalStack(int tank, @Nullable Direction side) {
        List<TANK> tanks = getChemicalTanks(side);
        if (tank >= 0 && tank < getChemicalTanks(side).size()) {
            StorageView<CHEMICAL> view = getChemicalTanks(side).get(tank);
            return (STACK) view.getResource().getStack(view.getAmount());
        }
        return null;
    }

    @Override
    default List<TANK> getTanks(@Nullable Direction side) {
        return getChemicalTanks(side);
    }

//    @Override
//    @Deprecated(forRemoval = true)
//    default STACK getChemicalInTank(int tank, @Nullable Direction side) {
//        STACK chemicalStack = getChemicalStack(tank, side);
//        return chemicalStack == null ? getEmptyStack() : chemicalStack;
//    }

//    @Override
//    @Deprecated(forRemoval = true)
//    default void setChemicalInTank(int tank, STACK stack, @Nullable Direction side) {
//        List<TANK> tanks = getChemicalTanks(side);
//        if (tank >= 0 && tank < getChemicalTanks(side).size()) {
//            TANK view = getChemicalTanks(side).get(tank);
//            System.out.println("IMekanismChemicalHandler: Try to set the stack, but not supported");
//        }
//    }

//    @Override
//    @Deprecated(forRemoval = true)
//    default long getTankCapacity(int tank, @Nullable Direction side) {
//        List<TANK> tanks = getChemicalTanks(side);
//        if (tank >= 0 && tank < getChemicalTanks(side).size()) {
//            TANK view = getChemicalTanks(side).get(tank);
//            return view.getCapacity();
//        }
//        return 0;
//    }

//    @Override
//    @Deprecated(forRemoval = true)
//    default boolean isValid(int tank, STACK stack, @Nullable Direction side) {
//        STACK chemicalStack = getChemicalStack(tank, side);
//        return chemicalStack != null && chemicalStack.getType().equals(stack.getType());
//    }

//    @Override
//    @Deprecated(forRemoval = true)
//    default STACK insertChemical(int tank, STACK stack, @Nullable Direction side, Action action) {
//        Storage<CHEMICAL> tanks = getChemicalTanks(side);
//        if (tank >= 0 && tank < Iterators.size(getChemicalTanks(side).iterator())) {
//            StorageView<CHEMICAL> view = Iterators.get(getChemicalTanks(side).iterator(), tank);
//            System.out.println("IMekanismChemicalHandler: Try to insert to tank, but not supported");
//        }
//        return null;
//    }

//    @Override
//    @Deprecated(forRemoval = true)
//    default long extract(int tank, long amount, @Nullable Direction side, Action action) {
//        List<TANK> tanks = getChemicalTanks(side);
//        if (tank >= 0 && tank < Iterators.size(getChemicalTanks(side).iterator())) {
//            StorageView<CHEMICAL> view = Iterators.get(getChemicalTanks(side).iterator(), tank);
//            long amountExtracted = 0;
//            try(Transaction transaction = Transaction.openOuter()) {
//                amountExtracted = view.extract(view.getResource(), amount, transaction);
//                transaction.commit();
//            }
//            return (STACK) view.getResource().getStack(amountExtracted);
//        }
//        return null;
//    }
}