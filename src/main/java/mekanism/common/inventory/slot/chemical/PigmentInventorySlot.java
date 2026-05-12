package mekanism.common.inventory.slot.chemical;

import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.common.capabilities.Capabilities;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

@NothingNullByDefault
public class PigmentInventorySlot extends ChemicalInventorySlot<Pigment, PigmentStack> {

    @Nullable
    public static IPigmentHandler getCapability(ContainerItemContext stack) {
        return stack.find(Capabilities.PIGMENT_HANDLER_ITEM);
    }

    /**
     * Fills the tank from this item
     */
    public static PigmentInventorySlot fill(IPigmentTank pigmentTank, @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(pigmentTank, "Pigment tank cannot be null");
        return new PigmentInventorySlot(pigmentTank, getFillExtractPredicate(pigmentTank, PigmentInventorySlot::getCapability),
              stack -> fillInsertCheck(pigmentTank, getCapability(ContainerItemContext.withConstant(stack))), stack -> ContainerItemContext.withConstant(stack).find(Capabilities.PIGMENT_HANDLER_ITEM) != null,
              listener, x, y);
    }

    /**
     * Accepts any items that can be filled with the current contents of the pigment tank, or if it is a pigment tank container and the tank is currently empty
     * <p>
     * Drains the tank into this item.
     */
    public static PigmentInventorySlot drain(IPigmentTank pigmentTank, @Nullable IContentsListener listener, int x, int y) {
        Objects.requireNonNull(pigmentTank, "Pigment tank cannot be null");
        Predicate<@NotNull ItemStack> insertPredicate = getDrainInsertPredicate(pigmentTank, PigmentInventorySlot::getCapability);
        return new PigmentInventorySlot(pigmentTank, insertPredicate.negate(), insertPredicate,
              stack -> ContainerItemContext.withConstant(stack).find(Capabilities.PIGMENT_HANDLER_ITEM) != null, listener, x, y);
    }

    private PigmentInventorySlot(IPigmentTank pigmentTank, Predicate<@NotNull ItemStack> canExtract, Predicate<@NotNull ItemStack> canInsert,
          Predicate<@NotNull ItemStack> validator, @Nullable IContentsListener listener, int x, int y) {
        this(pigmentTank, () -> null, canExtract, canInsert, validator, listener, x, y);
    }

    private PigmentInventorySlot(IPigmentTank pigmentTank, Supplier<Level> worldSupplier, Predicate<@NotNull ItemStack> canExtract,
          Predicate<@NotNull ItemStack> canInsert, Predicate<@NotNull ItemStack> validator, @Nullable IContentsListener listener, int x, int y) {
        super(pigmentTank, worldSupplier, canExtract, canInsert, validator, listener, x, y);
    }

    @Nullable
    @Override
    protected IChemicalHandler<Pigment, PigmentStack, IPigmentTank> getCapability() {
        return getCapability(ContainerItemContext.ofSingleSlot(current));
    }
}