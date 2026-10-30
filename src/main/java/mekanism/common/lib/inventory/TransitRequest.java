package mekanism.common.lib.inventory;

import com.google.common.collect.Iterators;
import mekanism.api.BigItemStack;
import mekanism.common.Mekanism;
import mekanism.common.content.transporter.TransporterManager;
import mekanism.common.tile.TileEntityLogisticalSorter;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.StackUtils;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


//TODO FIX THIS (TRANSLATE TO TRANSFER API)
public abstract class TransitRequest {


    private final TransitResponse EMPTY = new TransitResponse(BigItemStack.EMPTY, null);

    public static SimpleTransitRequest simple(BigItemStack stack) {
        return new SimpleTransitRequest(stack);
    }

    public static TransitRequest anyItem(Level level, BlockPos pos, Direction side, int amount) {
        return definedItem(level, pos, side, amount, Finder.ANY);
    }

    public static TransitRequest definedItem(Level level, BlockPos pos, Direction side, int amount, Finder finder) {
        return definedItem(level, pos, side, 1, amount, finder);
    }

    public static TransitRequest definedItem(Level level, BlockPos pos, Direction side, int min, int max, Finder finder) {
        TileTransitRequest ret = new TileTransitRequest(level, pos, side);
        Storage<ItemVariant> inventory = InventoryUtils.assertItemHandler("TransitRequest", level, pos, side);
        if (inventory == null) {
            return ret;
        }
        // count backwards- we start from the bottom of the inventory and go back for consistency
        //List<StorageView<ItemVariant>> slots = new ArrayList<>();
        //inventory.forEach(view -> slots.add(0, view));
        for (StorageView<ItemVariant> view:inventory) {
//            ItemStack stack = inventory.extractItem(i, max, true);
//            StorageView<ItemVariant> view = slots.get(i);
//            ItemStack stack = slots.get(i).
//
//            try(Transaction t = Transaction.openOuter()) {
//                ItemStack ret = view.extract(slot, toUse, t);
//            }

            ItemStack stack = view.getResource().toStack((int)Math.min(view.getAmount(), Integer.MAX_VALUE));
            if (!view.isResourceBlank() && view.getAmount() != 0 && finder.modifies(stack)) {
                HashedItem hashed = HashedItem.raw(stack);
                int toUse = (int)Math.min(stack.getCount(), max - ret.getCount(new BigItemStack(view.getResource(), view.getAmount())));
                if (toUse == 0) {
                    continue; // continue if we don't need any more of this item type
                }
                ret.addItem(StackUtils.size(new BigItemStack(view.getResource(), view.getAmount()), toUse), view);
            }
        }
        // remove items that we don't have enough of
        ret.getItemMap().entrySet().removeIf(entry -> entry.getValue().getTotalCount() < min);
        return ret;
    }

    public abstract Collection<? extends ItemData> getItemData();

    @NotNull
    public TransitResponse addToInventory(Level tileLevel, BlockPos tilePos, Direction side, int min, boolean force) {
        if (force && WorldUtils.getTileEntity(tileLevel, tilePos) instanceof TileEntityLogisticalSorter sorter) {
            return sorter.sendHome(this);
        }
        if (isEmpty()) {
            return getEmptyResponse();
        }
        Storage<ItemVariant> inventory = ItemStorage.SIDED.find(tileLevel, tilePos, side.getOpposite());
//        Optional<IItemHandler> capability = CapabilityUtils.getCapability(tile, ForgeCapabilities.ITEM_HANDLER, side.getOpposite()).resolve();
        if (inventory != null) {
            int slots = Iterators.size(inventory.iterator());
            if (slots == 0) {
                //If the inventory has no slots just exit early with the result that we can't send any items
                return getEmptyResponse();
            }
            if (min > 1) {
                //If we have a minimum amount of items we are trying to send, we need to start by simulating
                // to see if we actually have enough room to send the minimum amount of our item. We can
                // skip this step if we don't have a minimum amount being sent, as then whatever we are
                // able to insert will be "good enough"
                TransitResponse response = TransporterManager.getPredictedInsert(inventory, this);
                if (response.isEmpty() || response.getSendingAmount() < min) {
                    //If we aren't able to send any items or are only able to send less than we have room for
                    // return that we aren't able to insert the requested amount
                    return getEmptyResponse();
                }
                // otherwise, continue on to actually sending items to the inventory
            }
            for (ItemData data : getItemData()) {
                BigItemStack origInsert = data.getItemType().copyWithCount(data.getTotalCount());
                BigItemStack toInsert = origInsert.copy();
//                for (int i = 0; i < slots; i++) {
//                    // Do insert, this will handle validating the item is valid for the inventory
//                    toInsert = inventory.insertItem(i, toInsert, false);
//                    // If empty, end
//                    if (toInsert.isEmpty()) {
//                        return createResponse(origInsert, data);
//                    }
//                }
                try (Transaction t = Transaction.openOuter()) {
                    long result = inventory.insert(toInsert.getResource(), toInsert.getAmount(), t);
                    t.commit();
                    toInsert = toInsert.copyWithCount(toInsert.getAmount() - result);
                }
                if (TransporterManager.didEmit(origInsert, toInsert)) {
                    return createResponse(TransporterManager.getToUse(origInsert, toInsert), data);
                }
            }
        }
        return getEmptyResponse();
    }

    public boolean isEmpty() {
        return getItemData().isEmpty();
    }

    @NotNull
    public TransitResponse createResponse(BigItemStack inserted, ItemData data) {
        return new TransitResponse(inserted, data);
    }

    public TransitResponse createSimpleResponse() {
        ItemData data = getItemData().stream().findFirst().orElse(null);
        return data == null ? getEmptyResponse() : createResponse(data.itemType.copyWithCount(data.totalCount), data);
    }

    @NotNull
    public TransitResponse getEmptyResponse() {
        return EMPTY;
    }

    public static class TransitResponse {

        private final BigItemStack inserted;
        private final ItemData slotData;

        public TransitResponse(@NotNull BigItemStack inserted, ItemData slotData) {
            this.inserted = inserted;
            this.slotData = slotData;
        }

        public long getSendingAmount() {
            return inserted.amount();
        }

        public ItemData getSlotData() {
            return slotData;
        }

        public BigItemStack getStack() {
            return inserted;
        }

        public boolean isEmpty() {
            return inserted.isEmpty() || slotData.getTotalCount() == 0;
        }

        @Deprecated(forRemoval = true)
        public ItemStack getRejectedOld() {
            if (isEmpty()) {
                return ItemStack.EMPTY;
            }
            return slotData.getItemType().createStack((int)slotData.getTotalCount() - (int)getSendingAmount());
        }

        public BigItemStack getRejected() {
            if (isEmpty()) {
                return BigItemStack.EMPTY;
            }
            return slotData.getItemType().copyWithCount(slotData.getTotalCount() - getSendingAmount());
        }

        public BigItemStack use(long amount) {
            return slotData.use(amount);
        }

        public BigItemStack useAll() {
            return use(getSendingAmount());
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TransitResponse other = (TransitResponse) o;
            return (inserted == other.inserted || BigItemStack.matches(inserted, other.inserted)) && slotData.equals(other.slotData);
        }

        @Override
        public int hashCode() {
            int code = 1;
            code = 31 * code + inserted.item().hashCode();
            code = 31 * code + Long.hashCode(inserted.getAmount());
            if (inserted.item().hasNbt()) {
                code = 31 * code + inserted.item().getNbt().hashCode();
            }
            code = 31 * code + slotData.hashCode();
            return code;
        }
    }

    public static class ItemData {

        private final BigItemStack itemType;
        protected long totalCount;

        public ItemData(BigItemStack itemType) {
            this.itemType = itemType;
        }

        public BigItemStack getItemType() {
            return itemType;
        }

        public long getTotalCount() {
            return totalCount;
        }

        /**
         *
         * @deprecated ItemStack doesn't support long amount values
         */
        @Deprecated(forRemoval = true)
        public ItemStack getStack() {
            return getItemType().createStack((int)getTotalCount());
        }

        public BigItemStack use(long amount) {
            Mekanism.logger.error("Can't 'use' with this type of TransitResponse: {}", getClass().getName());
            return BigItemStack.EMPTY;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ItemData itemData = (ItemData) o;
            return totalCount == itemData.totalCount && itemType.equals(itemData.itemType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(itemType, totalCount);
        }
    }

    public static class SimpleTransitRequest extends TransitRequest {

        private final List<ItemData> slotData;

        protected SimpleTransitRequest(BigItemStack stack) {
            slotData = Collections.singletonList(new SimpleItemData(stack));
        }

        @Override
        public Collection<ItemData> getItemData() {
            return slotData;
        }

        public static class SimpleItemData extends ItemData {

            public SimpleItemData(BigItemStack stack) {
                //TODO: Can this use raw to avoid a copy? My intuition says yes as I don't think the item data stays around when the stack can mutate
                // but this definitely needs more thought
                super(stack);
                totalCount = stack.amount();
            }
        }
    }
}
