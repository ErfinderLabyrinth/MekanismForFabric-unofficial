package mekanism.common.recipe.upgrade.chemical;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.ISlurryHandler.IMekanismSlurryHandler;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
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
public class SlurryRecipeData extends ChemicalRecipeData<Slurry, SlurryStack, ISlurryTank, Storage<Slurry>> {

    public SlurryRecipeData(ListTag tanks) {
        super(tanks);
    }

    private SlurryRecipeData(List<ISlurryTank> tanks) {
        super(tanks);
    }

    @Override
    protected SlurryRecipeData create(List<ISlurryTank> tanks) {
        return new SlurryRecipeData(tanks);
    }

    @Override
    protected SubstanceType getSubstanceType() {
        return SubstanceType.SLURRY;
    }

    @Override
    protected ChemicalTankBuilder<Slurry, SlurryStack, ISlurryTank> getTankBuilder() {
        return ChemicalTankBuilder.SLURRY;
    }

    @Override
    protected ISlurryHandler getOutputHandler(List<ISlurryTank> tanks) {
        return new IMekanismSlurryHandler() {
            @Override
            public void updateSnapshots(TransactionContext t) {
            }

            @NotNull
            @Override
            public List<ISlurryTank> getChemicalTanks(@Nullable Direction side) {
                return tanks;
            }

            @Override
            public void onContentsChanged() {
            }
        };
    }

    @Override
    protected ItemApiLookup<Storage<Slurry>, ContainerItemContext> getItemLookup() {
        return Capabilities.SLURRY_HANDLER_ITEM;
    }

//    @Override
//    protected Predicate<Slurry> cloneValidator(ISlurryHandler handler, int tank) {
//        return type -> handler.isValid(tank, new SlurryStack(type, 1));
//    }

    @Override
    protected Storage<Slurry> getHandlerFromTile(TileEntityMekanism tile) {
        return tile.getSlurryManager().getContainers(null);
    }
}