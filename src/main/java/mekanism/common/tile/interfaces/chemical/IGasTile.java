package mekanism.common.tile.interfaces.chemical;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.common.capabilities.chemical.dynamic.IGasTracker;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.GasHandlerManager;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@MethodsReturnNonnullByDefault
public interface IGasTile extends IGasTracker {

    GasHandlerManager getGasManager();

    /**
     * @apiNote This should not be overridden, or directly called except for initial creation
     */
    default GasHandlerManager getInitialGasManager(IContentsListener listener) {
        return new GasHandlerManager(getInitialGasTanks(listener));
    }

    /**
     * @apiNote Do not call directly, only override implementation
     */
    @Nullable
    default IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener) {
        return null;
    }

    /**
     * @apiNote This should not be overridden
     */
    default boolean canHandleGas() {
        return getGasManager().canHandle();
    }

    /**
     * @apiNote This should not be overridden
     */
    @Override
    default Storage<Gas> getGasStorage(@Nullable Direction side) {
        return getGasManager().getContainers(side);
    }

    @Override
    default List<IGasTank> getGasTanks() {
        if (getGasManager().canHandle()) {
            return getGasManager().getHolder().getAll();
        }
        return List.of();
    }

    default boolean extractGasCheck(int tank, @Nullable Direction side) {
        return true;
    }

    default boolean insertGasCheck(int tank, @Nullable Direction side) {
        return true;
    }
}