package mekanism.common.lib.inventory.personalstorage;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.inventory.IMekanismInventory;
import mekanism.common.inventory.slot.BasicInventorySlot;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NothingNullByDefault
public abstract class AbstractPersonalStorageItemInventory implements IMekanismInventory {

    protected final List<IInventorySlot> slots = Util.make(new ArrayList<>(), lst -> PersonalStorageManager.createSlots(lst::add, BasicInventorySlot.alwaysTrueBi, this));

    @Override
    public Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        return new CombinedStorage<>(slots);
    }

    public IInventorySlot getSlot(int id) {
        return slots.get(id);
    }

    public List<IInventorySlot> getSlots() {
        return slots;
    }
}