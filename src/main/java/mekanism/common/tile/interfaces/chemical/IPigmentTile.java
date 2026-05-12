package mekanism.common.tile.interfaces.chemical;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.common.capabilities.chemical.dynamic.IPigmentTracker;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.PigmentHandlerManager;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@MethodsReturnNonnullByDefault
public interface IPigmentTile extends IPigmentTracker {

    PigmentHandlerManager getPigmentManager();

    /**
     * @apiNote This should not be overridden, or directly called except for initial creation
     */
    default PigmentHandlerManager getInitialPigmentManager(IContentsListener listener) {
        return new PigmentHandlerManager(getInitialPigmentTanks(listener));
    }

    /**
     * @apiNote Do not call directly, only override implementation
     */
    @Nullable
    default IChemicalTankHolder<Pigment, PigmentStack, IPigmentTank> getInitialPigmentTanks(IContentsListener listener) {
        return null;
    }

    /**
     * @apiNote This should not be overridden
     */
    default boolean canHandlePigment() {
        return getPigmentManager().canHandle();
    }

    /**
     * @apiNote This should not be overridden
     */
    @Override
    default Storage<Pigment> getPigmentStorage(@Nullable Direction side) {
        return getPigmentManager().getContainers(side);
    }

    @Override
    default List<IPigmentTank> getPigmentTanks() {
        if (getPigmentManager().canHandle()) {
            return getPigmentManager().getHolder().getAll();
        }
        return List.of();
    }

    default boolean extractPigmentCheck(int tank, @Nullable Direction side) {
        return true;
    }

    default boolean insertPigmentCheck(int tank, @Nullable Direction side) {
        return true;
    }
}