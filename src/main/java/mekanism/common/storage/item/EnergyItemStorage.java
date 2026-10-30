package mekanism.common.storage.item;

import mekanism.api.NBTConstants;
import mekanism.api.energy.IEnergyContainer;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.Supplier;

public class EnergyItemStorage implements EnergyStorage {
    private Supplier<IEnergyContainer> tankCreator;
    private ContainerItemContext context;

    public EnergyItemStorage(ContainerItemContext context, Supplier<IEnergyContainer> tankCreator) {
        this.context = context;
        this.tankCreator = tankCreator;
    }

    private boolean save(TransactionContext t, IEnergyContainer container) {
        CompoundTag oldNBT = context.getItemVariant().copyOrCreateNbt();
        CompoundTag mekData = oldNBT.getCompound(NBTConstants.MEK_DATA).copy();
        mekData.put(NBTConstants.ENERGY_CONTAINER, container.serializeNBT());
        oldNBT.put(NBTConstants.MEK_DATA, mekData);
        return context.exchange(ItemVariant.of(context.getItemVariant().getItem(), oldNBT), 1, t) == 1;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        IEnergyContainer container = tankCreator.get();
        container.deserializeNBT(context.getItemVariant().copyOrCreateNbt().getCompound(NBTConstants.MEK_DATA).getCompound(NBTConstants.ENERGY_CONTAINER));

        long amountInserted = container.insert(maxAmount, transaction);

        if (amountInserted == 0) {
            return 0;
        }

        if (save(transaction, container)) {
            return amountInserted;
        }
        return 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        IEnergyContainer container = tankCreator.get();
        container.deserializeNBT(context.getItemVariant().copyOrCreateNbt().getCompound(NBTConstants.MEK_DATA).getCompound(NBTConstants.ENERGY_CONTAINER));

        long amountExtracted = container.extract(maxAmount, transaction);

        if (amountExtracted == 0) {
            return 0;
        }

        if (save(transaction, container)) {
            return amountExtracted;
        }
        return 0;
    }

    @Override
    public long getAmount() {
        IEnergyContainer container = tankCreator.get();
        container.deserializeNBT(context.getItemVariant().copyOrCreateNbt().getCompound(NBTConstants.MEK_DATA).getCompound(NBTConstants.ENERGY_CONTAINER));

        return container.getAmount();
    }

    @Override
    public long getCapacity() {
        IEnergyContainer container = tankCreator.get();
        container.deserializeNBT(context.getItemVariant().copyOrCreateNbt().getCompound(NBTConstants.MEK_DATA).getCompound(NBTConstants.ENERGY_CONTAINER));

        return container.getCapacity();
    }
}
