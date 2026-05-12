package mekanism.common.inventory.container.slot;

import mekanism.common.inventory.container.sync.ISyncableData;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public interface IHasExtraData {

    /**
     * @param player "Owner" of the inventory
     */
    void addTrackers(Player player, Consumer<ISyncableData> tracker);
}