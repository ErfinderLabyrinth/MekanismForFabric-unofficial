package mekanism.common.util;

import mekanism.api.FluidStack;
import mekanism.api.NBTConstants;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.providers.IFluidProvider;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.content.network.distribution.FluidHandlerTarget;
import mekanism.common.inventory.SimpleSingleStackStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.IntSupplier;

public final class FluidUtils {

    private FluidUtils() {
    }

    public static ItemStack getFilledVariant(ItemStack toFill, IntSupplier capacity, IFluidProvider provider) {
        return getFilledVariant(toFill, capacity.getAsInt(), provider);
    }

    public static ItemStack getFilledVariant(ItemStack toFill, int capacity, IFluidProvider provider) {
        SimpleSingleStackStorage itemStorage = new SimpleSingleStackStorage(toFill);
        Storage<FluidVariant> fluidStorage = ContainerItemContext.ofSingleSlot(itemStorage).find(FluidStorage.ITEM);
        if (fluidStorage != null) {
            try (Transaction t = Transaction.openOuter()) {
                fluidStorage.insert(FluidVariant.of(provider.getFluid()), Long.MAX_VALUE, t);
                t.commit();
            }
        }
        return itemStorage.getStack();

//        IExtendedFluidTank dummyTank = BasicFluidTank.create(capacity, null);
//        //Manually handle filling it as capabilities are not necessarily loaded yet (at least not on the first call to this, which is made via fillItemGroup)
//        dummyTank.setStack(provider.getFluidStack(dummyTank.getCapacity()));
//        ItemDataUtils.writeContainers(toFill, NBTConstants.FLUID_TANKS, Collections.singletonList(dummyTank));
//        //The item is now filled return it for convenience
//        return toFill;
    }

    public static ItemStack getForceFilledVariant(ItemStack toFill, int capacity, IFluidProvider provider) {
        IExtendedFluidTank dummyTank = BasicFluidTank.create(capacity, null);
        //Manually handle filling it as capabilities are not necessarily loaded yet (at least not on the first call to this, which is made via fillItemGroup)
        dummyTank.setStack(provider.getFluidStack(dummyTank.getCapacity()));
        ItemDataUtils.writeContainers(toFill, NBTConstants.FLUID_TANKS, Collections.singletonList(dummyTank));
        //The item is now filled return it for convenience
        return toFill;
    }

    public static OptionalInt getRGBDurabilityForDisplay(ItemStack stack) {
        return getRGBDurabilityForDisplay(StorageUtils.getStoredFluidFromNBT(stack));
    }

    public static OptionalInt getRGBDurabilityForDisplay(FluidStack stack) {
        if (!stack.isEmpty()) {
            //TODO: Technically doesn't support things where the color is part of the texture such as lava
            // for chemicals it is supported via allowing people to override getColorRepresentation in their
            // chemicals
            if (stack.getFluid().isSame(Fluids.LAVA)) {//Special case lava
                return OptionalInt.of(0xFFDB6B19);
            } else if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                //Note: We can only return an accurate result on the client side. This method should never be called from the server
                // but in case it is make sure we only run on the client side
                FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(stack.getFluid());
                if (handler != null) {
                    return OptionalInt.of(handler.getFluidColor(null, null, stack.getFluid().defaultFluidState()));
                }
            }
        }
        return OptionalInt.empty();
    }

    public static void emit(IExtendedFluidTank tank, BlockEntity from) {
        emit(EnumSet.allOf(Direction.class), tank, from);
    }

    public static void emit(Set<Direction> outputSides, IExtendedFluidTank tank, BlockEntity from) {
        emit(outputSides, tank, from, tank.getCapacity());
    }

    public static void emit(Set<Direction> outputSides, IExtendedFluidTank tank, BlockEntity from, long maxOutput) {
        if (!tank.isEmpty() && maxOutput > 0) {
            long simulatedExtract;
            try(Transaction t = Transaction.openOuter()) {
                simulatedExtract = tank.extract(tank.getResource(), maxOutput, t);
            }
            long extractingAmount = emit(outputSides, new FluidStack(tank.getResource(), simulatedExtract), from);
            try(Transaction t=Transaction.openOuter()) {
                tank.extract(tank.getResource(), extractingAmount, t);
            }
        }
    }

    /**
     * Emits fluid from a central block by splitting the received stack among the sides given.
     *
     * @param sides - the list of sides to output from
     * @param stack - the stack to output
     * @param from  - the TileEntity to output from
     *
     * @return the amount of fluid emitted
     */
    public static long emit(Set<Direction> sides, @NotNull FluidStack stack, BlockEntity from) {
        if (stack.isEmpty() || sides.isEmpty()) {
            return 0;
        }
        FluidStack toSend = stack.copy();
        FluidHandlerTarget target = new FluidHandlerTarget(stack, 6);
        EmitUtils.forEachSide(from.getLevel(), from.getBlockPos(), sides, (acceptorLevel, acceptorPos, side) -> {
            //Insert to access side and collect the cap if it is present, and we can insert the type of the stack into it
            Storage<FluidVariant> storage = FluidStorage.SIDED.find(acceptorLevel, acceptorPos, side.getOpposite());
            if (storage != null) {
                if (canFill(storage, toSend)) {
                    target.addHandler(storage);
                }
            }
        });
        if (target.getHandlerCount() > 0) {
            return EmitUtils.sendToAcceptors(target, stack.amount(), toSend);
        }
        return 0;
    }

    public static boolean canFill(Storage<FluidVariant> handler, @NotNull FluidStack stack) {
        try(Transaction t = Transaction.openOuter()) {
            return handler.insert(stack.variant(), 1, t) > 0;
        }
    }

    public static boolean handleTankInteraction(Player player, InteractionHand hand, ItemStack itemStack, IExtendedFluidTank fluidTank) {
        ItemStack copyStack = itemStack.copyWithCount(1);
        ContainerItemContext context = ContainerItemContext.forPlayerInteraction(player, hand);
        Storage<FluidVariant> handler = context.find(FluidStorage.ITEM);
        if (handler != null) {
            FluidStack fluidInItem = FluidStack.EMPTY;
            if (fluidTank.isEmpty()) {
                //If we don't have a fluid stored try draining in general
                Iterator<StorageView<FluidVariant>> views = handler.nonEmptyIterator();
                if (views.hasNext()) {
                    StorageView<FluidVariant> view = views.next();
                    try(Transaction t=Transaction.openOuter()) {
                        FluidVariant variant = view.getResource();
                        long amountInItem = handler.extract(variant, Integer.MAX_VALUE, t);
                        fluidInItem = new FluidStack(variant, amountInItem);
                    }
                }
            } else {
                //Otherwise, try draining the same type of fluid we have stored
                // We do this to better support multiple tanks in case the fluid we have stored we could pull out of a block's
                // second tank but just asking to drain a specific amount
                try(Transaction t=Transaction.openOuter()) {
                    FluidVariant variant = fluidTank.getFluid().variant();
                    long amountInItem = handler.extract(variant, Integer.MAX_VALUE, t);
                    fluidInItem = new FluidStack(variant, amountInItem);
                }
            }
            if (fluidInItem.amount() == 0) {
                if (!fluidTank.isEmpty()) {
                    long filled;
                    try(Transaction t=Transaction.openOuter()) {
                        filled = handler.insert(fluidTank.getFluid().variant(), fluidTank.getFluid().amount(), t);
                        if (!player.isCreative()) {
                            t.commit();
                        }
                    }
                    if (filled > 0) {
                        try(Transaction t=Transaction.openOuter()) {
                            fluidTank.extract(fluidTank.getResource(), filled, t);
                            t.commit();
                        }
                        return true;
                    }
                }
            } else {
                long amountInserted;
                try(Transaction t=Transaction.openOuter()) {
                    amountInserted = fluidTank.insert(fluidInItem.variant(), fluidInItem.amount(), t);
                }
                long storedAmount = fluidInItem.amount();
                if (amountInserted != 0) {
                    boolean filled = false;
                    long drained;
                    try(Transaction t=Transaction.openOuter()) {
                        drained = handler.extract(fluidInItem.variant(), amountInserted, t);
                        if (!player.isCreative()) {
                            t.commit();
                        }
                    }
                    if (drained != 0) {
                        try(Transaction t=Transaction.openOuter()) {
                            fluidTank.insert(fluidInItem.variant(), drained, t);
                            t.commit();
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}