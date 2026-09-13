package mekanism.common.capabilities.fluid.item;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.fluid.IMekanismFluidHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

/**
 * Helper class for implementing fluid handlers for items
 */
@ParametersAreNotNullByDefault
@MethodsReturnNonnullByDefault
public abstract class ItemStackMekanismFluidHandler extends SnapshotParticipant<List<CompoundTag>> implements IMekanismFluidHandler, Storage<FluidVariant> {

    protected List<IExtendedFluidTank> tanks;

    protected abstract List<IExtendedFluidTank> getInitialTanks();

    protected void init() {
        this.tanks = getInitialTanks();
    }

    @Override
    public Storage<FluidVariant> getFluidTanks(@Nullable Direction side) {
        return new CombinedStorage<>(tanks);
    }

    public List<IExtendedFluidTank> getTanks() {
        return tanks;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        updateSnapshots(transaction);

        long amountInserted = 0;
        for (IExtendedFluidTank tank : tanks) {
            amountInserted += tank.insert(resource, maxAmount - amountInserted, transaction);
            if (amountInserted == maxAmount)
                break;
        }

        return amountInserted;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        updateSnapshots(transaction);

        long amountExtracted = 0;
        for (IExtendedFluidTank tank : tanks) {
            amountExtracted += tank.extract(resource, maxAmount - amountExtracted, transaction);
            if (amountExtracted == maxAmount)
                break;
        }

        return amountExtracted;
    }

    @Override
    protected List<CompoundTag> createSnapshot() {
        return tanks.stream().map(tank -> tank.serializeNBT()).toList();
    }

    @Override
    protected void readSnapshot(List<CompoundTag> snapshot) {
        for (int i = 0; i < snapshot.size(); i++) {
            tanks.get(i).deserializeNBT(snapshot.get(i));
        }
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        return tanks.stream().map(tank -> (StorageView<FluidVariant>) tank).iterator();
    }

    //    @Override
//    protected void gatherCapabilityResolvers(Consumer<ICapabilityResolver> consumer) {
//        consumer.accept(BasicCapabilityResolver.constant(ForgeCapabilities.FLUID_HANDLER_ITEM, this));
//    }


    @Override
    public void onContentsChanged() {

    }
}