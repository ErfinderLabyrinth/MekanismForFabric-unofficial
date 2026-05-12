package mekanism.common.recipe.upgrade;

import mekanism.api.DataHandlerUtils;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.inventory.SimpleSingleStackStorage;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.base.TileEntityMekanism;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NothingNullByDefault
public class FluidRecipeData implements RecipeUpgradeData<FluidRecipeData> {

    private final List<IExtendedFluidTank> fluidTanks;

    FluidRecipeData(ListTag tanks) {
        int count = DataHandlerUtils.getMaxId(tanks, NBTConstants.TANK);
        fluidTanks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            fluidTanks.add(BasicFluidTank.create(Integer.MAX_VALUE, null));
        }
        DataHandlerUtils.readContainers(fluidTanks, tanks);
    }

    private FluidRecipeData(List<IExtendedFluidTank> fluidTanks) {
        this.fluidTanks = fluidTanks;
    }

    @Nullable
    @Override
    public FluidRecipeData merge(FluidRecipeData other) {
        List<IExtendedFluidTank> allTanks = new ArrayList<>(fluidTanks);
        allTanks.addAll(other.fluidTanks);
        return new FluidRecipeData(allTanks);
    }

    @Override
    public ItemStack applyToStack(ItemStack stack) {
        if (fluidTanks.isEmpty()) {
            return stack;
        }
        Storage<FluidVariant> handler;

        Item item = stack.getItem();
        //List<IExtendedFluidTank> fluidTanks = new ArrayList<>();
        SimpleSingleStackStorage storage = new SimpleSingleStackStorage(stack);
        Storage<FluidVariant> fluidHandler = ContainerItemContext.ofSingleSlot(storage).find(FluidStorage.ITEM);
        TileEntityMekanism tile = null;
        if (fluidHandler != null) {
            handler = fluidHandler;
        } else if (item instanceof BlockItem blockItem) {
            tile = getTileFromBlock(blockItem.getBlock());
            if (tile == null || !tile.handles(SubstanceType.FLUID)) {
                //Something went wrong
                return null;
            }
            handler = tile.getFluidTanks(null);
//            for (int i = 0; i < tile.getTanks(); i++) {
//                int tank = i;
//                fluidTanks.add(BasicFluidTank.create(tile.getTankCapacity(tank), fluid -> tile.isFluidValid(tank, fluid), null));
//            }
        } else {
            return null;
        }
        if (fluidTanks.isEmpty()) {
            //We don't actually have any tanks in the output
            return stack;
        }
        //TODO: Improve the logic used so that it tries to batch similar types of fluids together first
        // and maybe make it try multiple slot combinations
//        IMekanismFluidHandler outputHandler = new IMekanismFluidHandler() {
//            @NotNull
//            @Override
//            public List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
//                return fluidTanks;
//            }
//
//            @Override
//            public void onContentsChanged() {
//            }
//        };
//        boolean hasData = false;
        for (IExtendedFluidTank fluidTank : this.fluidTanks) {
            if (!fluidTank.isEmpty()) {
                try(Transaction t=Transaction.openOuter()) {
                    if (handler.insert(fluidTank.getResource(), fluidTank.getAmount(), t) != fluidTank.getAmount()) {
                        //If we have a remainder something failed so bail
                        return null;
                    }
                    t.commit();
                }
            }
        }

        if (tile != null) {
            ItemStack stack1 = storage.getStack();
            tile.saveToItem(stack1);
            return stack1;
        }

//        if (hasData) {
//            //We managed to transfer it all into valid slots, so save it to the stack
//            ItemDataUtils.writeContainers(stack, NBTConstants.FLUID_TANKS, fluidTanks);
//        }
        return storage.getStack();
    }
}