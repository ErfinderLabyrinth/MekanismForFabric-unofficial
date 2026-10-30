package mekanism.common.item.block.machine;

import mekanism.api.AutomationType;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ItemBlockLaserTractorBeam extends ItemBlockMachine {

    public ItemBlockLaserTractorBeam(BlockTile<?, ?> block) {
        super(block);
    }

    @Override
    protected Predicate<@NotNull AutomationType> getEnergyCapInsertPredicate() {
        //Don't allow charging laser tractor beams inside of energy storage devices
        return BasicEnergyContainer.manualOnly;
    }
}