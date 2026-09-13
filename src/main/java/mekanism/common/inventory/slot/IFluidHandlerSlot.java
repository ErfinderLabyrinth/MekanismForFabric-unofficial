package mekanism.common.inventory.slot;

import com.google.common.collect.Iterators;
import mekanism.api.FluidStack;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.SimpleSingleStackStorage;
import mekanism.common.tile.interfaces.IFluidContainerManager.ContainerEditMode;
import mekanism.common.util.MekanismUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
//import net.minecraftforge.fluids.FluidStack;
//import net.minecraftforge.fluids.FluidUtil;
//import net.minecraftforge.fluids.capability.IFluidHandler;
//import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
//import net.minecraftforge.fluids.capability.IFluidHandlerItem;
//import net.minecraftforge.items.ItemHandlerHelper;

public interface IFluidHandlerSlot extends IInventorySlot {

    IExtendedFluidTank getFluidTank();

    boolean isDraining();

    boolean isFilling();

    void setDraining(boolean draining);

    void setFilling(boolean filling);

    default void handleTank(IInventorySlot outputSlot, ContainerEditMode editMode) {
        if (!isEmpty()) {
            if (editMode == ContainerEditMode.FILL) {
                drainTank(outputSlot);
            } else if (editMode == ContainerEditMode.EMPTY) {
                fillTank(outputSlot);
            } else if (editMode == ContainerEditMode.BOTH) {
                ItemStack stack = getStack();
                //If we have more than one item in the input, check against a single item of it
                // The fluid handler for buckets returns false about being able to accept fluids if they are stacked
                // though we have special handling to only move one item at a time anyway
//                Optional<IFluidHandlerItem> cap = FluidUtil.getFluidHandler(stack.getCount() > 1 ? stack.copyWithCount(1) : stack).resolve();
                ContainerItemContext context = ContainerItemContext.withConstant(stack);
                Storage<FluidVariant> fluidHandlerItem = context.find(FluidStorage.ITEM);
                if (fluidHandlerItem != null) {
                    boolean hasEmpty = false;
                    for (StorageView<FluidVariant> view : fluidHandlerItem) {
                        FluidStack fluidInTank = new FluidStack(view.getResource(), view.getAmount());
                        if (fluidInTank.isEmpty()) {
                            hasEmpty = true;
                        } else if (!isDraining()) {
                            boolean canInsert;
                            try(Transaction t=Transaction.openOuter()) {
                                canInsert = getFluidTank().insert(fluidInTank.variant(), fluidInTank.amount(), t) > 0;
                            }
                            if (canInsert) {
                                //If we support either mode and our container is not empty or currently being filled, then drain the item into the tank
                                fillTank(outputSlot);
                                return;
                            }
                        }
                    }
                    if (isFilling()) {
                        //if we were filling, but can no longer fill the tank, attempt to move the item to the output slot
                        if (moveItem(outputSlot, stack)) {
                            setFilling(false);
                        }
                    }
                    //If we have no valid fluids/can't fill the tank with it, we return if there is at least
                    // one empty tank in the item so that we can then drain into it
                    else {
                        boolean shouldDrain = false;
                        try (Transaction t = Transaction.openOuter()) {
                            if (getFluidTank().isEmpty() && hasEmpty || isDraining() || fluidHandlerItem.insert(getFluidTank().getFluid().variant(), getFluidTank().getFluid().amount(), t) > 0) {
                                //we return if there is at least one empty tank in the item so that we can then drain into it
                                shouldDrain = true;
                            }
                        }
                        if (shouldDrain) {
                            drainTank(outputSlot);
                        }
                    }
                }
            }
        }
    }

    /**
     * Fills tank from slot
     *
     * @param outputSlot The slot to move our container to after draining the item.
     */
    default void fillTank(IInventorySlot outputSlot) {
        if (!isEmpty()) {
            //Try filling from the tank's item
            ContainerItemContext context = ContainerItemContext.withConstant(getStack());
            Storage<FluidVariant> fluidHandlerItem = context.find(FluidStorage.ITEM);
            if (fluidHandlerItem != null) {
                int itemTanks = Iterators.size(fluidHandlerItem.iterator());
                if (itemTanks == 1) {
                    //If we only have one tank just directly check against that fluid instead of performing extra calculations to properly handle multiple tanks
                    StorageView<FluidVariant> view = fluidHandlerItem.iterator().next();
                    FluidStack fluidInItem = new FluidStack(view.getResource(), view.getAmount());
                    if (!fluidInItem.isEmpty() && getFluidTank().isFluidValid(fluidInItem)) {
                        //If we have a fluid that is valid for our fluid handler, attempt to drain it into our fluid handler
                        drainItemAndMove(outputSlot, fluidInItem);
                    }
                } else if (itemTanks > 1) {
                    //If we have more than one tank in our item then handle calculating the different drains that will occur for filling our fluid handler
                    // We start by gathering all the fluids in the item that we are able to drain and are valid for the tank,
                    // combining same fluid types into a single fluid stack
                    Set<FluidStack> knownFluids = gatherKnownFluids(fluidHandlerItem, itemTanks);
                    //If we found any fluids that we can drain, attempt to drain them into our item
                    for (FluidStack knownFluid : knownFluids) {
                        if (drainItemAndMove(outputSlot, knownFluid) && isEmpty()) {
                            //If we moved the item after draining it and we now don't have an item to try and fill
                            // then just exit instead of checking the other types of fluids
                            //TODO: Eventually fix the case where the item we are draining has multiple
                            // types of fluids so we may not actually want to move it immediately
                            // Note: Not sure what a good middle ground is because if the item can stack like buckets
                            // then how do we know when to move it
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Drains tank into slot
     *
     * @param outputSlot The slot to move our container to after draining the tank.
     */
    default void drainTank(IInventorySlot outputSlot) {
        //Verify we have an item, we have tanks that may need to be drained, and that our item is a fluid handler
        if (!isEmpty() && ContainerItemContext.withConstant(getStack()).find(FluidStorage.ITEM) != null) {
            FluidStack fluidInTank = getFluidTank().getFluid();
            if (!fluidInTank.isEmpty()) {
                //If we have a fluid attempt to drain it into our item
                long simulatedDrain;
                try(Transaction t=Transaction.openOuter()) {
                    simulatedDrain = getFluidTank().extract(fluidInTank.variant(), fluidInTank.amount(), t);
                }
                if (simulatedDrain == 0) {
                    //If we cannot actually drain from our fluid handler then just exit early
                    return;
                }
                ItemStack input = getStack().copyWithCount(1);
                SimpleSingleStackStorage stackStorage = new SimpleSingleStackStorage(input);
                Storage<FluidVariant> storage = ContainerItemContext.ofSingleSlot(stackStorage).find(FluidStorage.ITEM);
                if (storage != null) {
                    //The capability should be present based on checks that happen before this method, but verify to make sure it is present
                    //Fill the stack, note our stack is a copy so this is how we simulate to get the proper "container" item,
                    // and it does not actually matter that we are directly executing on the item
                    long toDrain;
                    try(Transaction t = Transaction.openOuter()) {
                        toDrain = storage.insert(fluidInTank.variant(), simulatedDrain, t);
                        t.commit();
                    }
                    if (getCount() == 1) {
                        try(Transaction t = Transaction.openOuter()) {
                            if (storage.insert(fluidInTank.variant(), toDrain, t) > 0) {
                                //If we have a single item in the input slot, and we can continue to fill it after
                                // our current fill, then mark that we don't want to move it to the output slot, yet
                                // Additionally we replace our input item with its container
                                setStack(stackStorage.getStack());
                                //Mark that we are currently draining
                                setDraining(true);
                                //Actually remove the fluid from our handler
                                MekanismUtils.logMismatchedStackSize(getFluidTank().shrinkStack(toDrain), toDrain);
                                return;
                            }
                        }
                    }
                    //If we can move it to the output slot then actually drain our tank
                    if (moveItem(outputSlot, stackStorage.getStack())) {
                        //Actually remove the fluid from our handler
                        MekanismUtils.logMismatchedStackSize(getFluidTank().shrinkStack(toDrain), toDrain);
                        //Mark we are no longer draining (as we have moved the item to the output slot)
                        setDraining(false);
                    }
                }
            }
        }
    }

    /**
     * Fills our fluid handler from the item and then moves the item to the given output slot. If it won't be able to move to the output slot, then we do not move it or
     * drain our item into the fluid handler.
     *
     * @param outputSlot      The slot our item will be moved to afterwards
     * @param fluidToTransfer The fluid we are draining from the item. This should be known to not be empty, and to have passed any validity checks.
     *
     * @return True if we can drain the fluid from the item and the item after being drained can (and was) moved to the output slot, false otherwise
     */
    private boolean drainItemAndMove(IInventorySlot outputSlot, FluidStack fluidToTransfer) {
        long inserted;
        try(Transaction t=Transaction.openOuter()) {
            inserted = getFluidTank().insert(fluidToTransfer.variant(), fluidToTransfer.amount(), t);
        }
        long toTransfer = fluidToTransfer.amount();
        if (inserted == 0) {
            //If we cannot actually fill our fluid handler then just exit early
            return false;
        }

        ItemStack input = getStack().copyWithCount(1);
        SimpleSingleStackStorage stackStorage = new SimpleSingleStackStorage(input);
        Storage<FluidVariant> storage = ContainerItemContext.ofSingleSlot(stackStorage).find(FluidStorage.ITEM);
        if (storage == null) {
            //The capability should be present based on checks that happen before this method, but if for some reason it isn't just exit
            return false;
        }
        //Drain the stack, note our stack is a copy so this is how we simulate to get the proper "container" item,
        // and it does not actually matter that we are directly executing on the item
        FluidStack drained;
        try(Transaction t = Transaction.openOuter()) {
            drained = new FluidStack(fluidToTransfer, storage.extract(fluidToTransfer.variant(), inserted, t));
            t.commit();
        }
        if (drained.isEmpty()) {
            //If we cannot actually drain from the item then just exit early
            return false;
        }
        if (getCount() == 1) {
            boolean canExtract;
            try(Transaction t = Transaction.openOuter()) {
                canExtract = storage.extract(fluidToTransfer.variant(), Integer.MAX_VALUE, t) != 0;
            }
            if (canExtract) {
                //If we have a single item in the input slot, and we can continue to drain from it
                // after our current drain, then we allow for draining and actually fill our handler
                // Additionally we replace our input item with its container
                setStack(stackStorage.getStack());
                try(Transaction t=Transaction.openOuter()) {
                    getFluidTank().insert(drained.variant(), drained.amount(), t);
                    t.commit();
                }
                //Mark that we are currently filling
                setFilling(true);
                return true;
            }
        }
        //Otherwise, we try to move the item to the output and then actually fill it
        if (moveItem(outputSlot, stackStorage.getStack())) {
            //Actually fill our handler with the fluid
            try(Transaction t=Transaction.openOuter()) {
                getFluidTank().insert(drained.variant(), drained.amount(), t);
                t.commit();
            }
            return true;
        }
        return false;
    }

    /**
     * Tries to move a stack from our slot to the output slot
     *
     * @param outputSlot  The slot we are trying to move our item to
     * @param stackToMove The stack we are moving, this is our container
     *
     * @return True if we are able to move the stack and did so, false otherwise
     */
    private boolean moveItem(IInventorySlot outputSlot, ItemStack stackToMove) {
        if (outputSlot.isEmpty()) {
            outputSlot.setStack(stackToMove);
        } else {
            ItemStack outputStack = outputSlot.getStack();
            if (!ItemEntity.areMergable(outputStack, stackToMove) || outputStack.getCount() >= outputSlot.getLimit(outputStack)) {
                //We won't be able to move our container to the output slot so exit
                return false;
            }
            MekanismUtils.logMismatchedStackSize(outputSlot.growStack(1), 1);
        }
        //Note: We do not need to call onContentsChanged, because it will be done due to the stack changing from calling shrinkStack
        MekanismUtils.logMismatchedStackSize(shrinkStack(1), 1);
        return true;
    }

    /**
     * Fills tank from slot, ensuring the stack's count is one, and does not move it to an output slot afterwards
     */
    default boolean fillTank() {
        if (getCount() == 1) {
            //Try filling from the tank's item
            SimpleSingleStackStorage stackSlot = new SimpleSingleStackStorage(getStack());
            Storage<FluidVariant> storage = ContainerItemContext.ofSingleSlot(stackSlot).find(FluidStorage.ITEM);
            if (storage != null) {
                int tanks = Iterators.size(storage.iterator());
                if (tanks == 1) {
                    //If we only have one tank just directly check against that fluid instead of performing extra calculations to properly handle multiple tanks
                    StorageView<FluidVariant> view = storage.iterator().next();
                    FluidStack fluidInItem = new FluidStack(view.getResource(), view.getAmount());
                    if (!fluidInItem.isEmpty() && getFluidTank().isFluidValid(fluidInItem)) {
                        //If we have a fluid that is valid for our fluid handler, attempt to drain it into our fluid handler
                        if (fillHandlerFromOther(getFluidTank(), storage, fluidInItem)) {
                            //Update the stack to the empty container
                            setStack(stackSlot.getStack());
                            return true;
                        }
                    }
                } else if (tanks > 1) {
                    //If we have more than one tank in our item then handle calculating the different drains that will occur for filling our fluid handler
                    // We start by gathering all the fluids in the item that we are able to drain and are valid for the tank,
                    // combining same fluid types into a single fluid stack
                    Set<FluidStack> knownFluids = gatherKnownFluids(storage, tanks);
                    if (!knownFluids.isEmpty()) {
                        //If we found any fluids that we can drain, attempt to drain them into our item
                        boolean changed = false;
                        for (FluidStack knownFluid : knownFluids) {
                            if (fillHandlerFromOther(getFluidTank(), storage, knownFluid)) {
                                changed = true;
                            }
                        }
                        if (changed) {
                            //Update the stack to the empty container
                            setStack(stackSlot.getStack());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Set<FluidStack> gatherKnownFluids(Storage<FluidVariant> storage, int tanks) {
        Map<FluidStack, FluidStack> knownFluids = new HashMap<>();
        for (StorageView<FluidVariant> view:storage) {
            FluidStack fluidInItem = new FluidStack(view.getResource(), view.getAmount());
            if (!fluidInItem.isEmpty()) {
                //Note: We use fluid directly for looking it up as a key as they only compare on equals and hashcode
                FluidStack knownFluid = knownFluids.get(fluidInItem);
                //If we have a fluid that can be drained from the item and is valid then we add it to our known fluids
                if (knownFluid == null) {
                    try(Transaction t = Transaction.openOuter()) {
                        if (view.extract(fluidInItem.variant(), fluidInItem.amount(), t) != 0 && getFluidTank().isFluidValid(fluidInItem)) {
                            //Note: While theoretically we could store the initial fluidInItem as they key as we don't mutate it...
                            // doing it this way allows for us to return the keySet from this method as the only thing we change (the amount)
                            // is not part of the hashCode or equals, so it will not cause things to break by mutating the key as well
                            FluidStack copy = fluidInItem.copy();
                            knownFluids.put(copy, copy);
                        }
                    }
                } else {
                    knownFluid.grow(fluidInItem.amount());
                }
            }
        }
        return knownFluids.keySet();
    }

    /**
     * Tries to drain the specified fluid from one fluid handler, while filling another fluid handler.
     *
     * @param handlerToFill  The fluid handler to fill
     * @param storage        The fluid handler to drain
     * @param fluid          The fluid to attempt to transfer
     *
     * @return True if we managed to transfer any contents, false otherwise
     */
    private boolean fillHandlerFromOther(IExtendedFluidTank handlerToFill, Storage<FluidVariant> storage, FluidStack fluid) {
        //Check how much of this fluid type we are actually able to drain from the handler we are draining
        FluidStack simulatedDrain;
        try(Transaction t = Transaction.openOuter()) {
            simulatedDrain = new FluidStack(fluid.variant(), storage.extract(fluid.variant(), fluid.amount(), t));
        }
        if (!simulatedDrain.isEmpty()) {
            //Check how much of it we will be able to put into the handler we are filling
            long inserted;
            try(Transaction t=Transaction.openOuter()) {
                inserted = getFluidTank().insert(simulatedDrain.variant(), simulatedDrain.amount(), t);
            }
            if (inserted > 0) {
                //Drain the handler to drain, filling the handler to fill while we are at it
                long extracted;
                try(Transaction t=Transaction.openOuter()) {
                    extracted = storage.extract(fluid.variant(), inserted, t);
                    t.commit();
                }

                try(Transaction t = Transaction.openOuter()) {
                    handlerToFill.insert(fluid.variant(), extracted, t);
                    t.commit();
                }
                return true;
            }
        }
        return false;
    }
}