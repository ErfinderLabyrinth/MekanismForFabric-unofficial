package mekanism.common.inventory.container.item;

import mekanism.api.Coord4D;
import mekanism.common.content.teleporter.TeleporterFrequency;
import mekanism.common.inventory.container.IEmptyContainer;
import mekanism.common.inventory.container.sync.SyncableByte;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.registries.MekanismContainerTypes;
import mekanism.common.tile.TileEntityTeleporter;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

public class PortableTeleporterContainer extends FrequencyItemContainer<TeleporterFrequency> implements IEmptyContainer {

    private byte status;

    public PortableTeleporterContainer(int id, Inventory inv, InteractionHand hand, ItemStack stack) {
        super(MekanismContainerTypes.PORTABLE_TELEPORTER, id, inv, hand, stack);
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public FrequencyType<TeleporterFrequency> getFrequencyType() {
        return FrequencyType.TELEPORTER;
    }

    public byte getStatus() {
        return status;
    }

    @Override
    protected void addContainerTrackers() {
        super.addContainerTrackers();
        //Relies on super being called first
        if (isRemote()) {
            //Client side sync handling
            track(SyncableByte.create(() -> status, value -> status = value));
        } else {
            //Server side sync handling
            //Note: It is important these are in the same order as the client side trackers
            track(SyncableByte.create(() -> {
                byte status = 3;
                TeleporterFrequency freq = getFrequency();
                if (freq != null && !freq.getActiveCoords().isEmpty()) {
                    status = 1;
                    if (!inv.player.isCreative()) {
                        EnergyStorage energyStorage = ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM);
                        if (energyStorage == null) {
                            status = 4;
                        } else {
                            Coord4D coords = freq.getClosestCoords(new Coord4D(inv.player));
                            if (coords != null) {
                                long energyNeeded = TileEntityTeleporter.calculateEnergyCost(inv.player, coords);
                                try(Transaction t=Transaction.openOuter()) {
                                    if (energyNeeded != -1 && energyStorage.extract(energyNeeded, t) < energyNeeded) {
                                        status = 4;
                                    }
                                }
                            }
                        }
                    }
                }
                return status;
            }, value -> status = value));
        }
    }
}