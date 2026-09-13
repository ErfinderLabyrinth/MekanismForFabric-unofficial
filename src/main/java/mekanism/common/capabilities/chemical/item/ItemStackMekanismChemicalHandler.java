package mekanism.common.capabilities.chemical.item;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.IMekanismChemicalHandler;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Helper class for implementing chemical handlers for items
 */
@ParametersAreNotNullByDefault
@MethodsReturnNonnullByDefault
public abstract class ItemStackMekanismChemicalHandler<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>,
      TANK extends IChemicalTank<CHEMICAL, STACK>> extends SnapshotParticipant<List<TANK>> implements IMekanismChemicalHandler<CHEMICAL, STACK, TANK> {

    protected List<TANK> tanks;

    public ItemStackMekanismChemicalHandler() {
        this.tanks = getInitialTanks();
    }

//    protected void init() {
//        this.tanks = getInitialTanks();
//    }
//
//    protected void load() {
//        ItemDataUtils.readContainers(getStack(), getNbtKey(), tanks);
//    }

//    @Override
//    public void onContentsChanged() {
//        ItemDataUtils.writeContainers(getStack(), getNbtKey(), tanks);
//    }

    @Override
    public List<TANK> getChemicalTanks(@Nullable Direction side) {
        return tanks;
    }

    protected abstract List<TANK> getInitialTanks();

//    protected abstract String getNbtKey();

//    protected abstract ItemStack getStack();


    @Override
    protected List<TANK> createSnapshot() {
        return tanks;
    }

    @Override
    protected void readSnapshot(List<TANK> snapshot) {
        tanks = snapshot;
    }
}