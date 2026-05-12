package mekanism.common.lib.inventory;

import mekanism.api.BigItemStack;
import mekanism.common.Mekanism;
import mekanism.common.util.InventoryUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.*;

public class TileTransitRequest extends TransitRequest {

    private final Level level;
    private final BlockPos pos;
    private final Direction side;
    private final Map<BigItemStack, TileItemData> itemMap = new LinkedHashMap<>();

    public TileTransitRequest(Level level, BlockPos pos, Direction side) {
        this.level = level;
        this.pos = pos;
        this.side = side;
    }

    public void addItem(BigItemStack stack, StorageView<ItemVariant> slot) {
        itemMap.computeIfAbsent(stack, TileItemData::new).addSlot(slot, stack);
    }

    public long getCount(BigItemStack itemType) {
        ItemData data = itemMap.get(itemType);
        return data == null ? 0 : data.getTotalCount();
    }

    protected Direction getSide() {
        return side;
    }

    public Map<BigItemStack, TileItemData> getItemMap() {
        return itemMap;
    }

    @Override
    public Collection<TileItemData> getItemData() {
        return itemMap.values();
    }

    public class TileItemData extends ItemData {

        private final Map<StorageView<ItemVariant>, Long> slotMap = new HashMap<>();

        public TileItemData(BigItemStack itemType) {
            super(itemType);
        }

        public void addSlot(StorageView<ItemVariant> slot, BigItemStack stack) {
            slotMap.put(slot, stack.amount());
            totalCount += stack.amount();
        }

        @Override
        public BigItemStack use(long amount) {
            Direction side = getSide();
            Storage<ItemVariant> handler = InventoryUtils.assertItemHandler("TileTransitRequest", level, pos, side);
            if (handler != null && !slotMap.isEmpty()) {
                BigItemStack itemType = getItemType();
                ItemStack itemStack = itemType.createStack();
                Iterator<Map.Entry<StorageView<ItemVariant>, Long>> iterator = slotMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<StorageView<ItemVariant>, Long> entry = iterator.next();
                    StorageView<ItemVariant> slot = entry.getKey();
                    long currentCount = entry.getValue();
                    long toUse = Math.min(amount, currentCount);
                    long ret = 0;
                    try(Transaction t = Transaction.openOuter()) {
                        ret = handler.extract(slot.getResource(), toUse, t);
                    }
                    boolean stackable = InventoryUtils.areItemsStackable(itemStack, slot.getResource().toStack((int)ret));
                    if (!stackable || ret != toUse) { // be loud if an InvStack's prediction doesn't line up
                        Mekanism.logger.warn("An inventory's returned content {} does not line up with TileTransitRequest's prediction.", stackable ? "count" : "type");
                        Mekanism.logger.warn("TileTransitRequest item: {}, toUse: {}, ret: {}, slot: {}", itemStack, toUse, ret, slot);
                        //Mekanism.logger.warn("Tile: {} {} {}", RegistryUtils.getName(tile.getType()), tile.getBlockPos(), side);
                    }
                    amount -= toUse;
                    totalCount -= toUse;
                    if (totalCount == 0) {
                        itemMap.remove(itemType);
                    }
                    currentCount = currentCount - toUse;
                    if (currentCount == 0) {
                        //If we removed all items from this slot, remove the slot
                        iterator.remove();
                    } else {
                        // otherwise, update the amount in it
                        entry.setValue(currentCount);
                    }
                    if (amount == 0) {
                        break;
                    }
                }
            }
            return getItemType();
        }
    }
}