package mekanism.common.capabilities.holder.slot;

import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.holder.ProxiedHolder;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ProxiedInventorySlotHolder extends ProxiedHolder<IInventorySlot> implements IInventorySlotHolder {

    private final Function<Direction, List<IInventorySlot>> slotFunction;

    public static ProxiedInventorySlotHolder create(Predicate<Direction> insertPredicate, Predicate<Direction> extractPredicate,
          Function<Direction, List<IInventorySlot>> slotFunction) {
        return new ProxiedInventorySlotHolder(insertPredicate, extractPredicate, slotFunction);
    }

    private ProxiedInventorySlotHolder(Predicate<Direction> insertPredicate, Predicate<Direction> extractPredicate,
          Function<Direction, List<IInventorySlot>> slotFunction) {
        super(insertPredicate, extractPredicate);
        this.slotFunction = slotFunction;
    }

    @Override
    public @NotNull Storage<ItemVariant> getInventorySlots(@Nullable Direction side) {
        return new CombinedStorage<>(slotFunction.apply(side));
    }

    @Override
    public List<IInventorySlot> getAll() {
        return slotFunction.apply(null);
    }
}