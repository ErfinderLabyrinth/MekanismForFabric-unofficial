package mekanism.common.tile.interfaces.chemical;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.common.capabilities.chemical.dynamic.IInfusionTracker;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.InfusionHandlerManager;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@MethodsReturnNonnullByDefault
public interface IInfusionTile extends IInfusionTracker {

    InfusionHandlerManager getInfusionManager();

    /**
     * @apiNote This should not be overridden, or directly called except for initial creation
     */
    default InfusionHandlerManager getInitialInfusionManager(IContentsListener listener) {
        return new InfusionHandlerManager(getInitialInfusionTanks(listener));
    }

    /**
     * @apiNote Do not call directly, only override implementation
     */
    @Nullable
    default IChemicalTankHolder<InfuseType, InfusionStack, IInfusionTank> getInitialInfusionTanks(IContentsListener listener) {
        return null;
    }

    /**
     * @apiNote This should not be overridden
     */
    default boolean canHandleInfusion() {
        return getInfusionManager().canHandle();
    }

    /**
     * @apiNote This should not be overridden
     */
    @Override
    default Storage<InfuseType> getInfusionStorage(@Nullable Direction side) {
        return getInfusionManager().getContainers(side);
    }

    @Override
    default List<IInfusionTank> getInfusionTanks() {
        if (getInfusionManager().canHandle()) {
            return getInfusionManager().getHolder().getAll();
        }
        return List.of();
    }

    default boolean extractInfusionCheck(int tank, @Nullable Direction side) {
        return true;
    }

    default boolean insertInfusionCheck(int tank, @Nullable Direction side) {
        return true;
    }
}