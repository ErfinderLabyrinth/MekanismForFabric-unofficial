package mekanism.common.content.matrix;

import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.container.sync.ISyncableData;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.container.sync.dynamic.IContainerSyncable;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.lib.multiblock.MultiblockCache.CacheSubstance;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.tile.multiblock.TileEntityInductionCasing;
import mekanism.common.tile.multiblock.TileEntityInductionCell;
import mekanism.common.tile.multiblock.TileEntityInductionProvider;
import mekanism.common.util.MekanismUtils;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class MatrixMultiblockData extends MultiblockData implements IContainerSyncable {

    public static final String STATS_TAB = "stats";

    @NotNull
    private final MatrixEnergyContainer energyContainer;

    private long clientLastOutput = 0;

    private long clientLastInput = 0;

    private long clientEnergy = 0;

    private long clientMaxTransfer = 0;

    private long clientMaxEnergy = 0;

    private int clientProviders;

    private int clientCells;

    @NotNull
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getInputItem", docPlaceholder = "input slot")
    final EnergyInventorySlot energyInputSlot;
    @NotNull
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getOutputItem", docPlaceholder = "output slot")
    final EnergyInventorySlot energyOutputSlot;

    public MatrixMultiblockData(TileEntityInductionCasing tile) {
        super(tile);
        energyContainers.add(energyContainer = new MatrixEnergyContainer(this));
        inventorySlots.add(energyInputSlot = EnergyInventorySlot.drain(energyContainer, this, 146, 21));
        inventorySlots.add(energyOutputSlot = EnergyInventorySlot.fillOrConvert(energyContainer, tile::getLevel, this, 146, 51));
        energyInputSlot.setSlotOverlay(SlotOverlay.PLUS);
        energyOutputSlot.setSlotOverlay(SlotOverlay.MINUS);
    }

    @Override
    protected int getMultiblockRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(getEnergy(), getStorageCap());
    }

    @Override
    protected boolean shouldCap(CacheSubstance<?> type) {
        return type != CacheSubstance.ENERGY;
    }

    public void addCell(TileEntityInductionCell cell) {
        energyContainer.addCell(cell.getBlockPos(), cell);
    }

    public void addProvider(TileEntityInductionProvider provider) {
        energyContainer.addProvider(provider.getBlockPos(), provider);
    }

    @NotNull
    public MatrixEnergyContainer getEnergyContainer() {
        return energyContainer;
    }

    public long getEnergy() {
        return isRemote() ? clientEnergy : energyContainer.getEnergy();
    }

    @Override
    public boolean tick(Level world) {
        boolean ret = super.tick(world);
        energyContainer.tick();
        // We tick the main energy container before adding/draining from the slots, so that we make sure
        // they get first "pickings" at attempting to get or give power, without having to worry about the
        // rate limit of the structure being used up by the ports
        energyInputSlot.drainContainer();
        energyOutputSlot.fillContainerOrConvert();
        if (getLastInput() != 0 || getLastOutput() != 0) {
            // If the stored energy changed, update the comparator
            markDirtyComparator(world);
        }
        return ret;
    }

    @Override
    public void remove(Level world) {
        energyContainer.invalidate();
        super.remove(world);
    }

    public long getStorageCap() {
        return isRemote() ? clientMaxEnergy : energyContainer.getMaxEnergy();
    }

    @ComputerMethod
    public long getTransferCap() {
        return isRemote() ? clientMaxTransfer : energyContainer.getMaxTransfer();
    }

    @ComputerMethod
    public long getLastInput() {
        return isRemote() ? clientLastInput : energyContainer.getLastInput();
    }

    @ComputerMethod
    public long getLastOutput() {
        return isRemote() ? clientLastOutput : energyContainer.getLastOutput();
    }

    @ComputerMethod(nameOverride = "getInstalledCells")
    public int getCellCount() {
        return isRemote() ? clientCells : energyContainer.getCells();
    }

    @ComputerMethod(nameOverride = "getInstalledProviders")
    public int getProviderCount() {
        return isRemote() ? clientProviders : energyContainer.getProviders();
    }

    @Override
    public void addSyncables(Consumer<ISyncableData> acceptor, String tag) {
        if ("default".equals(tag)) {
            // clientLastOutput
            acceptor.accept(SyncableLong.create(this::getLastOutput, newValue -> clientLastOutput = newValue));
            // clientLastInput
            acceptor.accept(SyncableLong.create(this::getLastInput, newValue -> clientLastInput = newValue));
            // clientEnergy
            acceptor.accept(SyncableLong.create(this::getEnergy, newValue -> clientEnergy = newValue));
            // clientMaxTransfer
            acceptor.accept(SyncableLong.create(this::getTransferCap, newValue -> clientMaxTransfer = newValue));
            // clientMaxEnergy
            acceptor.accept(SyncableLong.create(this::getStorageCap, newValue -> clientMaxEnergy = newValue));
        }else if (MatrixMultiblockData.STATS_TAB.equals(tag)) {
            // clientProviders
            acceptor.accept(SyncableInt.create(this::getProviderCount, newValue -> clientProviders = newValue));
            // clientCells
            acceptor.accept(SyncableInt.create(this::getCellCount, newValue -> clientCells = newValue));
        }
    }
}
