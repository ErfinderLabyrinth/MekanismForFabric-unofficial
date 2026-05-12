package mekanism.common.tile.interfaces;

import mekanism.common.capabilities.IOffsetCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

/**
 * Internal interface.  A bounding block is not actually a 'bounding' block, it is really just a fake block that is used to mimic actual block bounds.
 *
 * @author AidanBrady
 */
public interface IBoundingBlock extends IComparatorSupport, IOffsetCapability, IUpgradeTile {

//    Set<Capability<?>> ALWAYS_PROXY = Set.of(
//          Capabilities.CONFIG_CARD,
//          Capabilities.OWNER_OBJECT,
//          Capabilities.SECURITY_OBJECT
//    );

    default void onBoundingBlockPowerChange(BlockPos boundingPos, int oldLevel, int newLevel) {
    }

    default int getBoundingComparatorSignal(Vec3i offset) {
        return 0;
    }

    default boolean triggerBoundingEvent(Vec3i offset, int id, int param) {
        return false;
    }
}