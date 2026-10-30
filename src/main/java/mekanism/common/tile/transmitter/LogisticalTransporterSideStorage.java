package mekanism.common.tile.transmitter;

import mekanism.api.BigItemStack;
import mekanism.common.content.network.transmitter.LogisticalTransporterBase;
import mekanism.common.content.transporter.TransporterStack;
import mekanism.common.lib.inventory.TransitRequest;
import mekanism.common.lib.transmitter.ConnectionType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LogisticalTransporterSideStorage extends SnapshotParticipant<List<Pair<TransporterStack, TransitRequest.TransitResponse>>> implements Storage<ItemVariant> {
    final TileEntityLogisticalTransporterBase tileEntityLogisticalTransporterBase;
    final BlockPos fromPos;
    List<Pair<TransporterStack, TransitRequest.TransitResponse>> pendingItems = new ArrayList<>();

    public LogisticalTransporterSideStorage(TileEntityLogisticalTransporterBase tileEntityLogisticalTransporterBase, BlockPos fromPos, ConnectionType connectionType) {
        this.tileEntityLogisticalTransporterBase = tileEntityLogisticalTransporterBase;
        this.fromPos = fromPos;
    }

    private TransitRequest getRequest(int limit, BigItemStack stack) {
        //If the stack is already the correct size skip copying it and resizing by using the source stack
        // as our simple transit request won't have the stack get mutated
        if (stack.amount() <= limit) {
            return TransitRequest.simple(stack);
        }
        return TransitRequest.simple(stack.copyWithCount(limit));
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        updateSnapshots(transaction);
        LogisticalTransporterBase transmitter = tileEntityLogisticalTransporterBase.getTransmitter();
        TransitRequest request = getRequest(transmitter.tier.getPullAmount(), new BigItemStack(resource, maxAmount));
        TransporterStack stack = transmitter.createInsertStack(fromPos, transmitter.getColor());
        TransitRequest.TransitResponse response = stack.recalculatePath(request, transmitter, 1);
        if (response.isEmpty()) {
            return 0;
        }
        pendingItems.add(new Pair<>(stack, response));
        return response.getSendingAmount();
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public boolean supportsExtraction() {
        return false;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        return pendingItems.stream().map(pendingItemStack -> (StorageView<ItemVariant>) pendingItemStack).iterator();
    }

    @Override
    protected List<Pair<TransporterStack, TransitRequest.TransitResponse>> createSnapshot() {
        return new ArrayList<>(pendingItems);
    }

    @Override
    protected void readSnapshot(List<Pair<TransporterStack, TransitRequest.TransitResponse>> snapshot) {
        pendingItems = snapshot;
    }

    @Override
    protected void onFinalCommit() {
        super.onFinalCommit();

        LogisticalTransporterBase transmitter = tileEntityLogisticalTransporterBase.getTransmitter();
        for (Pair<TransporterStack, TransitRequest.TransitResponse> pendingItem : pendingItems) {
            transmitter.updateTransit(true, pendingItem.getA(), pendingItem.getB());
        }

        pendingItems.clear();
    }
}
