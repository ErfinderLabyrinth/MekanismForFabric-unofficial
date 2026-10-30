package mekanism.common.recipe.upgrade.chemical;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.IPigmentHandler.IMekanismPigmentHandler;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
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
public class PigmentRecipeData extends ChemicalRecipeData<Pigment, PigmentStack, IPigmentTank, Storage<Pigment>> {

    public PigmentRecipeData(ListTag tanks) {
        super(tanks);
    }

    private PigmentRecipeData(List<IPigmentTank> tanks) {
        super(tanks);
    }

    @Override
    protected PigmentRecipeData create(List<IPigmentTank> tanks) {
        return new PigmentRecipeData(tanks);
    }

    @Override
    protected SubstanceType getSubstanceType() {
        return SubstanceType.PIGMENT;
    }

    @Override
    protected ChemicalTankBuilder<Pigment, PigmentStack, IPigmentTank> getTankBuilder() {
        return ChemicalTankBuilder.PIGMENT;
    }

    @Override
    protected IPigmentHandler getOutputHandler(List<IPigmentTank> tanks) {
        return new IMekanismPigmentHandler() {
            @Override
            public void updateSnapshots(TransactionContext t) {
            }

            @NotNull
            @Override
            public List<IPigmentTank> getChemicalTanks(@Nullable Direction side) {
                return tanks;
            }

            @Override
            public void onContentsChanged() {
            }
        };
    }

    @Override
    protected ItemApiLookup<Storage<Pigment>, ContainerItemContext> getItemLookup() {
        return Capabilities.PIGMENT_HANDLER_ITEM;
    }

//    @Override
//    protected Predicate<Pigment> cloneValidator(IPigmentHandler handler, int tank) {
//        return type -> handler.isValid(tank, new PigmentStack(type, 1));
//    }

    @Override
    protected Storage<Pigment> getHandlerFromTile(TileEntityMekanism tile) {
        return tile.getPigmentManager().getContainers(null);
    }
}