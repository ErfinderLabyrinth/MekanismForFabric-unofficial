package mekanism.common.inventory.slot.chemical;

import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.common.capabilities.Capabilities;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

@NothingNullByDefault
public class SlurryInventorySlot extends ChemicalInventorySlot<Slurry, SlurryStack> {

    @Nullable
    public static Storage<Slurry> getCapability(ContainerItemContext stack) {
        return stack.find(Capabilities.SLURRY_HANDLER_ITEM);
    }

    /**
     * Accepts any items that can be filled with the current contents of the slurry tank, or if it is a slurry tank container and the tank is currently empty
     * <p>
     * Drains the tank into this item.
     */
    public static SlurryInventorySlot drain(ISlurryTank slurryTank, @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(slurryTank, "Slurry tank cannot be null");
        Predicate<@NotNull ItemStack> insertPredicate = getDrainInsertPredicate(slurryTank, SlurryInventorySlot::getCapability);
        return new SlurryInventorySlot(slurryTank, insertPredicate.negate(), insertPredicate, stack -> ContainerItemContext.withConstant(stack).find(Capabilities.SLURRY_HANDLER_ITEM) != null,
              listener, x, y);
    }

    //TODO: Implement creators as needed
    private SlurryInventorySlot(ISlurryTank slurryTank, Predicate<@NotNull ItemStack> canExtract, Predicate<@NotNull ItemStack> canInsert,
          Predicate<@NotNull ItemStack> validator, @Nullable IContentsListener listener, int x, int y) {
        this(slurryTank, () -> null, canExtract, canInsert, validator, listener, x, y);
    }

    private SlurryInventorySlot(ISlurryTank slurryTank, Supplier<Level> worldSupplier, Predicate<@NotNull ItemStack> canExtract,
          Predicate<@NotNull ItemStack> canInsert, Predicate<@NotNull ItemStack> validator, @Nullable IContentsListener listener, int x, int y) {
        super(slurryTank, worldSupplier, canExtract, canInsert, validator, listener, x, y);
    }

    @Nullable
    @Override
    protected Storage<Slurry> getCapability() {
        return getCapability(ContainerItemContext.ofSingleSlot(current));
    }
}