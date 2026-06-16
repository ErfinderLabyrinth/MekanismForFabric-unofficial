package mekanism.common.recipe.upgrade.chemical;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.gas.IGasHandler.IMekanismGasHandler;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.base.TileEntityMekanism;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@NothingNullByDefault
public class GasRecipeData extends ChemicalRecipeData<Gas, GasStack, IGasTank, Storage<Gas>> {

    public GasRecipeData(ListTag tanks) {
        super(tanks);
    }

    private GasRecipeData(List<IGasTank> tanks) {
        super(tanks);
    }

    @Override
    protected GasRecipeData create(List<IGasTank> tanks) {
        return new GasRecipeData(tanks);
    }

    @Override
    protected SubstanceType getSubstanceType() {
        return SubstanceType.GAS;
    }

    @Override
    protected ChemicalTankBuilder<Gas, GasStack, IGasTank> getTankBuilder() {
        return ChemicalTankBuilder.GAS;
    }

    @Override
    protected IGasHandler getOutputHandler(List<IGasTank> tanks) {
        return new IMekanismGasHandler() {
            @Override
            public void updateSnapshots(TransactionContext t) {
            }

            @NotNull
            @Override
            public List<IGasTank> getChemicalTanks(@Nullable Direction side) {
                return tanks;
            }

            @Override
            public void onContentsChanged() {
            }
        };
    }

    @Override
    protected ItemApiLookup<Storage<Gas>, ContainerItemContext> getItemLookup() {
        return Capabilities.GAS_HANDLER_ITEM;
    }

//    @Override
//    protected Predicate<Gas> cloneValidator(IGasHandler handler, int tank) {
//        return type -> handler.isValid(tank, new GasStack(type, 1));
//    }

    @Override
    protected Storage<Gas> getHandlerFromTile(TileEntityMekanism tile) {
        return tile.getGasManager().getContainers(null);
    }
}