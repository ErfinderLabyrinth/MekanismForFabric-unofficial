package mekanism.common.tile.qio;

import com.google.common.collect.Iterators;
import mekanism.api.BigItemStack;
import mekanism.api.NBTConstants;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.Mekanism;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.content.transporter.TransporterManager;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableBoolean;
import mekanism.common.lib.inventory.HashedItem;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class TileEntityQIOImporter extends TileEntityQIOFilterHandler {

    private static final int MAX_DELAY = 10;
    private int delay = 0;
    private boolean importWithoutFilter = true;

    public TileEntityQIOImporter(BlockPos pos, BlockState state) {
        super(MekanismBlocks.QIO_IMPORTER, pos, state);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (MekanismUtils.canFunction(this)) {
            if (delay > 0) {
                delay--;
                return;
            }
            tryImport();
            delay = MAX_DELAY;
        }
    }

    private void tryImport() {
        QIOFrequency freq = getQIOFrequency();
        if (freq == null) {
            return;
        }
        Direction direction = getDirection();
        //BlockEntity back = WorldUtils.getTileEntity(getLevel(), worldPosition.relative(direction.getOpposite()));
        Storage<ItemVariant> inventory = ItemStorage.SIDED.find(getLevel(), worldPosition.relative(direction.getOpposite()), direction);
        if (inventory == null) {
            return;
        }

        Predicate<ItemStack> canFilter;
        if (getFilterManager().hasEnabledFilters()) {
            canFilter = stack -> getFilterManager().anyEnabledMatch(filter -> filter.getFinder().modifies(stack));
        } else if (importWithoutFilter) {
            // return true if we don't have any enabled filters installed, and we allow for filterless importing
            canFilter = ConstantPredicates.alwaysTrue();
        } else {
            //If we don't have any enabled filters installed, and we don't allow filterless importing
            return;
        }
        //We know this is present from our earlier checks
        int slots = Iterators.size(inventory.iterator());
        if (slots == 0) {
            //If the inventory has no slots just exit early
            return;
        }
        Set<HashedItem> typesAdded = new HashSet<>();
        int maxTypes = getMaxTransitTypes(), maxCount = getMaxTransitCount(), countAdded = 0;

        for (StorageView<ItemVariant> view:inventory) {
            long amount;
            try(Transaction t = Transaction.openOuter()) {
                amount = view.extract(view.getResource(), maxCount - countAdded, t);
            }
            if (amount == 0) {
                continue;
            }
            ItemStack stack = view.getResource().toStack((int)amount);
            HashedItem type = HashedItem.create(stack);
            // if we don't have room for another item type, skip
            if (!typesAdded.contains(type) && typesAdded.size() == maxTypes) {
                continue;
            }
            // if we can't filter this item type, skip
            if (!canFilter.test(stack)) {
                continue;
            }
            BigItemStack used = TransporterManager.getToUse(BigItemStack.of(stack), BigItemStack.of(freq.addItem(stack)));
            try(Transaction t = Transaction.openOuter()) {
                amount = view.extract(view.getResource(), used.amount(), t);
                t.commit();
            }
            ItemStack ret = view.getResource().toStack((int)amount);
            if (!InventoryUtils.areItemsStackable(used.createStack(), ret) || used.amount() != ret.getCount()) {
                Mekanism.logger.error("QIO insertion error: item handler {} returned {} during simulated extraction, but returned {} during execution. This is wrong!",
                        WorldUtils.getTileEntity(getLevel(), worldPosition.relative(direction.getOpposite())), stack, ret);
            }
            typesAdded.add(type);
            countAdded += used.amount();
        }
    }

    @ComputerMethod
    public boolean getImportWithoutFilter() {
        return importWithoutFilter;
    }

    public void toggleImportWithoutFilter() {
        importWithoutFilter = !importWithoutFilter;
        markForSave();
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableBoolean.create(this::getImportWithoutFilter, value -> importWithoutFilter = value));
    }

    @Override
    public void writeSustainedData(CompoundTag dataMap) {
        super.writeSustainedData(dataMap);
        dataMap.putBoolean(NBTConstants.AUTO, importWithoutFilter);
    }

    @Override
    public void readSustainedData(CompoundTag dataMap) {
        super.readSustainedData(dataMap);
        NBTUtils.setBooleanIfPresent(dataMap, NBTConstants.AUTO, value -> importWithoutFilter = value);
    }

    @Override
    public Map<String, String> getTileDataRemap() {
        Map<String, String> remap = super.getTileDataRemap();
        remap.put(NBTConstants.AUTO, NBTConstants.AUTO);
        return remap;
    }

    //Methods relating to IComputerTile
    @ComputerMethod(requiresPublicSecurity = true)
    void setImportsWithoutFilter(boolean value) throws ComputerException {
        validateSecurityIsPublic();
        if (importWithoutFilter != value) {
            toggleImportWithoutFilter();
        }
    }
    //End methods IComputerTile
}
