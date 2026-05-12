package mekanism.common.storage.item;

import mekanism.api.DataHandlerUtils;
import mekanism.api.NBTSerializable;
import mekanism.common.util.ItemDataUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractItemStorage<T, TANK extends SingleSlotStorage<T> & NBTSerializable<CompoundTag>> implements Storage<T> {
    protected Supplier<List<TANK>> tankCreator;
    protected ContainerItemContext context;

    public AbstractItemStorage(ContainerItemContext context, Supplier<List<TANK>> tankCreator) {
        this.context = context;
        this.tankCreator = tankCreator;
    }

    abstract protected String getNBTPath();

    protected boolean save(TransactionContext t, List<TANK> tanks) {
        CompoundTag oldNBT = context.getItemVariant().copyOrCreateNbt();
        oldNBT.put(getNBTPath(), DataHandlerUtils.writeContainers(tanks, NBTSerializable::serializeNBT));
        return context.exchange(ItemVariant.of(context.getItemVariant().getItem(), oldNBT), 1, t) == 1;
    }

    @Override
    public long insert(T resource, long maxAmount, TransactionContext transaction) {
        List<TANK> tanks = tankCreator.get();
        ItemDataUtils.readContainers(context.getItemVariant().toStack((int)context.getAmount()), getNBTPath(), tanks);
        long amountInserted = 0;
        for(TANK tank:tanks) {
            amountInserted += tank.insert(resource, maxAmount - amountInserted, transaction);
        }

        if (amountInserted == 0) {
            return 0;
        }

        if (save(transaction, tanks)) {
            return amountInserted;
        }
        return 0;
    }

    @Override
    public long extract(T resource, long maxAmount, TransactionContext transaction) {
        List<TANK> tanks = tankCreator.get();
        ItemDataUtils.readContainers(context.getItemVariant().toStack((int)context.getAmount()), getNBTPath(), tanks);
        long amountExtracted = 0;
        for(TANK tank:tanks) {
            amountExtracted += tank.extract(resource, maxAmount - amountExtracted, transaction);
        }

        if (amountExtracted == 0) {
            return 0;
        }

        if (save(transaction, tanks)) {
            return amountExtracted;
        }
        return 0;
    }

    @Override
    public Iterator<StorageView<T>> iterator() {
        List<StorageView<T>> views = new ArrayList<>();
        for (int i = 0; i < tankCreator.get().size(); i++) {
            views.add(createView(i));
        }
        return views.iterator();
    }

    abstract protected StorageView<T> createView(int index);

    abstract class AbstractItemStorageView implements StorageView<T> {
        protected final int index;
        protected AbstractItemStorageView(int index) {
            this.index = index;
        }


        @Override
        public long extract(T resource, long maxAmount, TransactionContext transaction) {
            List<TANK> tanks = tankCreator.get();
            ItemDataUtils.readContainers(context.getItemVariant().toStack((int)context.getAmount()), getNBTPath(), tanks);

            long amountExtracted = tanks.get(index).extract(resource, maxAmount, transaction);

            if (amountExtracted == 0) {
                return 0;
            }

            if (save(transaction, tanks)) {
                return amountExtracted;
            }
            return 0;
        }

        @Override
        public boolean isResourceBlank() {
            List<TANK> tanks = tankCreator.get();
            ItemDataUtils.readContainers(context.getItemVariant().toStack((int)context.getAmount()), getNBTPath(), tanks);
            return tanks.get(index).isResourceBlank();
        }

        @Override
        public T getResource() {
            List<TANK> tanks = tankCreator.get();
            ItemDataUtils.readContainers(context.getItemVariant().toStack((int)context.getAmount()), getNBTPath(), tanks);
            return tanks.get(index).getResource();
        }

        @Override
        public long getAmount() {
            List<TANK> tanks = tankCreator.get();
            ItemDataUtils.readContainers(context.getItemVariant().toStack((int)context.getAmount()), getNBTPath(), tanks);
            return tanks.get(index).getAmount();
        }

        @Override
        public long getCapacity() {
            List<TANK> tanks = tankCreator.get();
            ItemDataUtils.readContainers(context.getItemVariant().toStack((int)context.getAmount()), getNBTPath(), tanks);
            return tanks.get(index).getCapacity();
        }
    }
}
