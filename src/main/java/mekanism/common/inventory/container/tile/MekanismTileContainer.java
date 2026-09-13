package mekanism.common.inventory.container.tile;

import mekanism.api.inventory.IInventorySlot;
import mekanism.api.security.ISecurityObject;
import mekanism.common.inventory.container.IEmptyContainer;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.slot.VirtualInventoryContainerSlot;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MekanismTileContainer<TILE extends TileEntityMekanism> extends MekanismContainer {

    private VirtualInventoryContainerSlot upgradeSlot;
    private VirtualInventoryContainerSlot upgradeOutputSlot;
    @NotNull
    protected final TILE tile;

    public MekanismTileContainer(ContainerTypeRegistryObject<?> type, int id, Inventory inv, @NotNull TILE tile) {
        super(type, id, inv);
        this.tile = tile;
        addContainerTrackers();
        addSlotsAndOpen();
    }

    protected void addContainerTrackers() {
        tile.addContainerTrackers(this);
    }

    public TILE getTileEntity() {
        return tile;
    }

    @Nullable
    @Override
    public ISecurityObject getSecurityObject() {
        return tile;
    }

    @Override
    protected void openInventory(@NotNull Inventory inv) {
        super.openInventory(inv);
        tile.open(inv.player);
    }

    @Override
    protected void closeInventory(@NotNull Player player) {
        super.closeInventory(player);
        tile.close(player);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        //prevent Containers from remaining valid after the chunk has unloaded;
        return tile.hasGui() && !tile.isRemoved() && WorldUtils.isBlockLoaded(tile.getLevel(), tile.getBlockPos());
    }

    @Override
    protected void addSlots() {
        super.addSlots();
        if (this instanceof IEmptyContainer) {
            //Don't include the inventory slots
            return;
        }
        if (tile.supportsUpgrades()) {
            //Add the virtual slot for the upgrade (add them before the main inventory to make sure they take priority in targeting)
            addSlot(upgradeSlot = tile.getComponent().getUpgradeSlot().createContainerSlot());
            addSlot(upgradeOutputSlot = tile.getComponent().getUpgradeOutputSlot().createContainerSlot());
        }
        if (tile.hasInventory()) {
            //Get all the inventory slots the tile has
            Storage<ItemVariant> inventorySlots = tile.getItemStorage(null);
            for (StorageView<ItemVariant> view : inventorySlots) {
                if (view instanceof IInventorySlot inventorySlot) {
                    Slot containerSlot = inventorySlot.createContainerSlot();
                    if (containerSlot != null) {
                        addSlot(containerSlot);
                    }
                }
            }
        }
    }

    @Nullable
    public VirtualInventoryContainerSlot getUpgradeSlot() {
        return upgradeSlot;
    }

    @Nullable
    public VirtualInventoryContainerSlot getUpgradeOutputSlot() {
        return upgradeOutputSlot;
    }
}