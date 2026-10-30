package mekanism.common.inventory.slot;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.util.MekanismUtils;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

@NothingNullByDefault
public class FuelInventorySlot extends BasicInventorySlot {

    public static FuelInventorySlot forFuel(ToIntFunction<@NotNull ItemStack> fuelValue, @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(fuelValue, "Fuel value calculator cannot be null");
        return new FuelInventorySlot(stack -> fuelValue.applyAsInt(stack) == 0, stack -> fuelValue.applyAsInt(stack) > 0, alwaysTrue, listener, x, y);
    }

    private FuelInventorySlot(Predicate<@NotNull ItemStack> canExtract, Predicate<@NotNull ItemStack> canInsert, Predicate<@NotNull ItemStack> validator,
          @Nullable IContentsListener listener, int x, int y) {
        super((stack, automationType) -> automationType == AutomationType.MANUAL || canExtract.test(stack), (stack, automationType) -> canInsert.test(stack), validator,
              listener, x, y);
    }

    public int burn() {
        if (isEmpty()) {
            return 0;
        }
        int burnTime = FuelRegistry.INSTANCE.get(current.getStack().getItem()) / 2;
        if (burnTime > 0) {
            if (current.getStack().getItem().hasCraftingRemainingItem()) {
                if (current.getStack().getCount() > 1) {
                    //If we have a container but have more than a single stack of it somehow just exit
                    return 0;
                }
                //If the item has a container, then replace it with the container
                setStack(current.getStack().getItem().getCraftingRemainingItem().getDefaultInstance());
            } else {
                //Otherwise, shrink the size of the stack by one
                MekanismUtils.logMismatchedStackSize(shrinkStack(1), 1);
            }
        }
        return burnTime;
    }
}