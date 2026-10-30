package mekanism.common.inventory.slot;

import mekanism.api.FluidStack;
import mekanism.api.IContentsListener;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

@NothingNullByDefault
public class FluidInventorySlot extends BasicInventorySlot implements IFluidHandlerSlot {

    //TODO: Rename this maybe? It is basically used as an "input" slot where it accepts either an empty container to try and take stuff
    // OR accepts a fluid container tha that has contents that match the handler for purposes of filling the handler

    /**
     * Fills/Drains the tank depending on if this item has any contents in it
     */
    public static FluidInventorySlot input(IExtendedFluidTank fluidTank, @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(fluidTank, "Fluid tank cannot be null");
        return new FluidInventorySlot(fluidTank, alwaysFalse, getInputPredicate(fluidTank), stack -> ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null, listener, x, y);
    }

    protected static Predicate<ItemStack> getInputPredicate(IExtendedFluidTank fluidTank) {
        return stack -> {
            //If we have more than one item in the input, check if we can fill a single item of it
            // The fluid handler for buckets returns false about being able to accept fluids if they are stacked
            // though we have special handling to only move one item at a time anyway
            ItemStack usingStack = stack.getCount() > 1 ? stack.copyWithCount(1) : stack;
            ContainerItemContext context = ContainerItemContext.withConstant(usingStack);
            Storage<FluidVariant> storage = context.find(FluidStorage.ITEM);
//            Optional<IFluidHandlerItem> cap = FluidUtil.getFluidHandler(stack.getCount() > 1 ? stack.copyWithCount(1) : stack).resolve();
            if (storage != null) {
                boolean hasEmpty = false;
                for (StorageView<FluidVariant> view:storage) {
                    if (view.getAmount() == 0) {
                        hasEmpty = true;
                    } else {
                        try(Transaction t = Transaction.openNested(Transaction.getCurrentUnsafe())) { //TODO replace with better method (currantly unsave)
                            if (fluidTank.insert(view.getResource(), view.getAmount(), t) > 0) {
                                //True if the items contents are valid, and we can fill the tank with any of our contents
                                return true;
                            }
                        }
                    }
                }
                //If we have no valid fluids/can't fill the tank with it
                if (fluidTank.isEmpty()) {
                    //we return if there is at least one empty tank in the item so that we can then drain into it
                    return hasEmpty;
                }

                try(Transaction t = Transaction.openNested(Transaction.getCurrentUnsafe())) { //TODO replace with better method (currantly unsave)
                    return storage.insert(fluidTank.getFluid().variant(), fluidTank.getAmount(), t) > 0;
                }
            }
            return false;
        };
    }

    /**
     * Fills/Drains the tank depending on if this item has any contents in it AND if the supplied boolean's mode supports it
     */
    public static FluidInventorySlot rotary(IExtendedFluidTank fluidTank, BooleanSupplier modeSupplier, @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(fluidTank, "Fluid tank cannot be null");
        Objects.requireNonNull(modeSupplier, "Mode supplier cannot be null");
        return new FluidInventorySlot(fluidTank, alwaysFalse, stack -> {
            Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
            if (storage != null) {
                boolean mode = modeSupplier.getAsBoolean();
                //Mode == true if fluid to gas
                boolean allEmpty = true;
                for (StorageView<FluidVariant> view:storage) {
                    FluidStack fluidInTank = new FluidStack(view.getResource(), view.getAmount());
                    if (!fluidInTank.isEmpty()) {
                        try(Transaction t=Transaction.openOuter()) {
                            if (fluidTank.insert(view.getResource(), view.getAmount(), t) > 0) {
                                //True if we are the input tank and the items contents are valid and can fill the tank with any of our contents
                                return mode;
                            }
                        }
                        allEmpty = false;
                    }
                }
                //We want to try and drain the tank AND we are not the input tank
                return allEmpty && !mode;
            }
            return false;
        }, stack -> {
            Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
            if (storage != null) {
                if (modeSupplier.getAsBoolean()) {
                    //Input tank, so we want to fill it
                    for (StorageView<FluidVariant> view:storage) {
                        FluidStack fluidInTank = new FluidStack(view.getResource(), view.getAmount());
                        if (!fluidInTank.isEmpty() && fluidTank.isFluidValid(fluidInTank)) {
                            return true;
                        }
                    }
                    return false;
                }
                //Output tank, so we want to drain
                //Allow for any fluid containers, but we have a more restrictive canInsert so that we don't insert all items
                // as otherwise when we drain and replace with the container we might have issues
                return true;
            }
            return false;
        }, listener, x, y);
    }

    /**
     * Fills the tank from this item
     */
    public static FluidInventorySlot fill(IExtendedFluidTank fluidTank, @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(fluidTank, "Fluid tank cannot be null");
        return new FluidInventorySlot(fluidTank, alwaysFalse, stack -> {
            Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
            if (storage != null) {
                for (StorageView<FluidVariant> view:storage) {
                    FluidStack fluidInTank = new FluidStack(view.getResource(), view.getAmount());
                    try(Transaction t=Transaction.openOuter()) {
                        if (!fluidInTank.isEmpty() && fluidTank.insert(fluidInTank.variant(), fluidInTank.amount(), t) > 0) {
                            //True if we can fill the tank with any of our contents
                            // Note: We need to recheck the fact the fluid is not empty and that it is valid,
                            // in case the item has multiple tanks and only some of the fluids are valid
                            return true;
                        }
                    }
                }
            }
            return false;
        }, stack -> {
            //Allow for any fluid containers, but we have a more restrictive canInsert so that we don't insert all items
            //TODO: Check the other ones to see if we need something like this for them
            return ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null;
        }, listener, x, y);
    }

    /**
     * Accepts any items that can be filled with the current contents of the fluid tank, or if it is a fluid container and the tank is currently empty
     * <p>
     * Drains the tank into this item.
     */
    public static FluidInventorySlot drain(IExtendedFluidTank fluidTank, @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(fluidTank, "Fluid handler cannot be null");
        return new FluidInventorySlot(fluidTank, alwaysFalse, stack -> {
            //If we have more than one item in the input, check if we can fill a single item of it
            // The fluid handler for buckets returns false about being able to accept fluids if they are stacked
            // though we have special handling to only move one item at a time anyway
            Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
            if (storage != null) {
                FluidStack fluidInTank = fluidTank.getFluid();
                if (fluidInTank.isEmpty()) {
                    return true;
                }
                //True if the tanks contents are valid, and we can fill the item with any of the contents
                try (Transaction t = Transaction.openOuter()) {
                    return storage.insert(fluidInTank.variant(), fluidTank.getAmount(), t) > 0;
                }
            }
            return false;
        }, stack -> isNonFullFluidContainer(ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM)), listener, x, y);
    }

    //TODO: Should we make this also have the fluid type have to match a desired type???
    private static boolean isNonFullFluidContainer(Storage<FluidVariant> storage) {
        if (storage != null) {
            for (StorageView<FluidVariant> view:storage) {
                if (view.getAmount() < view.getCapacity()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    protected final IExtendedFluidTank fluidTank;
    private boolean isDraining;
    private boolean isFilling;

    protected FluidInventorySlot(IExtendedFluidTank fluidTank, Predicate<@NotNull ItemStack> canExtract, Predicate<@NotNull ItemStack> canInsert,
          Predicate<@NotNull ItemStack> validator, @Nullable IContentsListener listener, int x, int y) {
        super(canExtract, canInsert, validator, listener, x, y);
        setSlotType(ContainerSlotType.EXTRA);
        this.fluidTank = fluidTank;
    }

    @Override
    public void setStack(ItemStack stack) {
        super.setStack(stack);
        //Reset the cache of if we are currently draining or filling
        isDraining = false;
        isFilling = false;
    }

    @Override
    public IExtendedFluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public boolean isDraining() {
        return isDraining;
    }

    @Override
    public boolean isFilling() {
        return isFilling;
    }

    @Override
    public void setDraining(boolean draining) {
        isDraining = draining;
    }

    @Override
    public void setFilling(boolean filling) {
        isFilling = filling;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        if (isDraining) {
            nbt.putBoolean(NBTConstants.DRAINING, true);
        }
        if (isFilling) {
            nbt.putBoolean(NBTConstants.FILLING, true);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        //Grab the booleans regardless if they are present as if they aren't that means they are false
        isDraining = nbt.getBoolean(NBTConstants.DRAINING);
        isFilling = nbt.getBoolean(NBTConstants.FILLING);
        super.deserializeNBT(nbt);
    }
}