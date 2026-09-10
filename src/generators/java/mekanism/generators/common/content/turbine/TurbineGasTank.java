package mekanism.generators.common.content.turbine;

import mekanism.api.IContentsListener;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.gas.Gas;
import mekanism.common.capabilities.chemical.multiblock.MultiblockChemicalTankBuilder.MultiblockGasTank;
import mekanism.common.registries.MekanismGases;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class TurbineGasTank extends MultiblockGasTank {

    private final TurbineMultiblockData multiblock;

    public TurbineGasTank(TurbineMultiblockData multiblock, @Nullable IContentsListener listener) {
        super(multiblock::getSteamCapacity, multiblock.notExternalFormedBiPred(), multiblock.formedBiPred(), gas -> gas == MekanismGases.STEAM.getChemical(),
              null, listener);
        this.multiblock = multiblock;
    }

    @Override
    public long insert(Gas resource, long maxAmount, TransactionContext transaction) {
        long inserted = super.insert(resource, maxAmount, transaction);

        if(multiblock.isFormed()) {
            transaction.addOuterCloseCallback(result -> {
                if (result.wasCommitted()) {
                    multiblock.newSteamInput += inserted;
                }
            });
        }

        return inserted;
    }
}