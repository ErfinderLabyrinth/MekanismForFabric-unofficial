package mekanism.common.storage.item;

import mekanism.api.FluidStack;
import mekanism.api.NBTConstants;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.util.ItemDataUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.function.Supplier;

public class FluidItemStorage extends AbstractItemStorage<FluidVariant, IExtendedFluidTank> {
    public FluidItemStorage(ContainerItemContext context, Supplier<List<IExtendedFluidTank>> tankCreator) {
        super(context, tankCreator);
    }

    @Override
    protected String getNBTPath() {
        return NBTConstants.FLUID_TANKS;
    }

    @Override
    protected StorageView<FluidVariant> createView(int index) {
        return new FluidItemStorageView(index);
    }

    class FluidItemStorageView extends AbstractItemStorageView implements IExtendedFluidTank {

        protected FluidItemStorageView(int index) {
            super(index);
        }

        @Override
        public void setStack(FluidStack stack) {
            List<IExtendedFluidTank> tanks = tankCreator.get();
            ItemDataUtils.readContainers(context.getItemVariant().toStack((int)context.getAmount()), getNBTPath(), tanks);
            tanks.get(index).setStack(stack);
            try(Transaction t=Transaction.openOuter()) {
                save(t, tanks);
                t.commit();
            }
        }

        @Override
        public void setStackUnchecked(FluidStack stack) {
            List<IExtendedFluidTank> tanks = tankCreator.get();
            ItemDataUtils.readContainers(context.getItemVariant().toStack((int)context.getAmount()), getNBTPath(), tanks);
            tanks.get(index).setStackUnchecked(stack);
            try(Transaction t=Transaction.openOuter()) {
                save(t, tanks);
                t.commit();
            }
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            List<IExtendedFluidTank> tanks = tankCreator.get();
            ItemDataUtils.readContainers(context.getItemVariant().toStack((int)context.getAmount()), getNBTPath(), tanks);
            return tanks.get(index).isFluidValid(stack);
        }

        @Override
        public FluidStack getFluid() {
            List<IExtendedFluidTank> tanks = tankCreator.get();
            ItemDataUtils.readContainers(context.getItemVariant().toStack((int)context.getAmount()), getNBTPath(), tanks);
            return tanks.get(index).getFluid();
        }

        @Override
        public void onContentsChanged() {

        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {

        }

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            List<IExtendedFluidTank> tanks = tankCreator.get();
            ItemDataUtils.readContainers(context.getItemVariant().toStack((int)context.getAmount()), getNBTPath(), tanks);

            long amountInserted = tanks.get(index).insert(resource, maxAmount, transaction);

            if (amountInserted == 0) {
                return 0;
            }

            if (save(transaction, tanks)) {
                return amountInserted;
            }
            return 0;
        }
    }
}
