package mekanism.common.recipe.upgrade.chemical;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.IInfusionHandler.IMekanismInfusionHandler;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
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
public class InfusionRecipeData extends ChemicalRecipeData<InfuseType, InfusionStack, IInfusionTank, Storage<InfuseType>> {

    public InfusionRecipeData(ListTag tanks) {
        super(tanks);
    }

    private InfusionRecipeData(List<IInfusionTank> tanks) {
        super(tanks);
    }

    @Override
    protected InfusionRecipeData create(List<IInfusionTank> tanks) {
        return new InfusionRecipeData(tanks);
    }

    @Override
    protected SubstanceType getSubstanceType() {
        return SubstanceType.INFUSION;
    }

    @Override
    protected ChemicalTankBuilder<InfuseType, InfusionStack, IInfusionTank> getTankBuilder() {
        return ChemicalTankBuilder.INFUSION;
    }

    @Override
    protected IInfusionHandler getOutputHandler(List<IInfusionTank> tanks) {
        return new IMekanismInfusionHandler() {
            @Override
            public void updateSnapshots(TransactionContext t) {
            }

            @NotNull
            @Override
            public List<IInfusionTank> getChemicalTanks(@Nullable Direction side) {
                return tanks;
            }

            @Override
            public void onContentsChanged() {
            }
        };
    }

    @Override
    protected ItemApiLookup<Storage<InfuseType>, ContainerItemContext> getItemLookup() {
        return Capabilities.INFUSION_HANDLER_ITEM;
    }

//    @Override
//    protected Predicate<InfuseType> cloneValidator(IInfusionHandler handler, int tank) {
//        return type -> handler.isValid(tank, new InfusionStack(type, 1));
//    }

    @Override
    protected Storage<InfuseType> getHandlerFromTile(TileEntityMekanism tile) {
        return tile.getInfusionManager().getContainers(null);
    }
}