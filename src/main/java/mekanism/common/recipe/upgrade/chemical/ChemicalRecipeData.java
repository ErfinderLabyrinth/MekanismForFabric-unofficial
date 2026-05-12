package mekanism.common.recipe.upgrade.chemical;

import mekanism.api.DataHandlerUtils;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.*;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.inventory.SimpleSingleStackStorage;
import mekanism.common.recipe.upgrade.RecipeUpgradeData;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.base.TileEntityMekanism;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NothingNullByDefault
public abstract class ChemicalRecipeData<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>,
      HANDLER extends IChemicalHandler<CHEMICAL, STACK, TANK>> implements RecipeUpgradeData<ChemicalRecipeData<CHEMICAL, STACK, TANK, HANDLER>> {

    protected final List<TANK> tanks;

    protected ChemicalRecipeData(ListTag tanks) {
        int count = DataHandlerUtils.getMaxId(tanks, NBTConstants.TANK);
        this.tanks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            this.tanks.add(getTankBuilder().createDummy(Long.MAX_VALUE));
        }
        DataHandlerUtils.readContainers(this.tanks, tanks);
    }

    protected ChemicalRecipeData(List<TANK> tanks) {
        this.tanks = tanks;
    }

    @Nullable
    @Override
    public ChemicalRecipeData<CHEMICAL, STACK, TANK, HANDLER> merge(ChemicalRecipeData<CHEMICAL, STACK, TANK, HANDLER> other) {
        List<TANK> allTanks = new ArrayList<>(tanks);
        allTanks.addAll(other.tanks);
        return create(allTanks);
    }

    protected abstract ChemicalRecipeData<CHEMICAL, STACK, TANK, HANDLER> create(List<TANK> tanks);

    protected abstract SubstanceType getSubstanceType();

    protected abstract ChemicalTankBuilder<CHEMICAL, STACK, TANK> getTankBuilder();

    protected abstract HANDLER getOutputHandler(List<TANK> tanks);

    protected abstract ItemApiLookup<HANDLER, ContainerItemContext> getItemLookup();

//    protected abstract Predicate<@NotNull CHEMICAL> cloneValidator(HANDLER handler, int tank);

    protected abstract Storage<CHEMICAL> getHandlerFromTile(TileEntityMekanism tile);

    @Override
    public ItemStack applyToStack(ItemStack stack) {
        if (this.tanks.isEmpty()) {
            return stack;
        }
        Storage<CHEMICAL> handler;

        SimpleSingleStackStorage storage = new SimpleSingleStackStorage(stack);
        HANDLER itemHandler = ContainerItemContext.ofSingleSlot(storage).find(getItemLookup());

        TileEntityMekanism tile = null;
        if (itemHandler != null) {
            handler = itemHandler;
        } else if (stack.getItem() instanceof BlockItem blockItem) {
            tile = null;
            Block block = blockItem.getBlock();
            if (block instanceof IHasTileEntity<?> hasTileEntity) {
                BlockEntity tileEntity = hasTileEntity.createDummyBlockEntity();
                if (tileEntity instanceof TileEntityMekanism) {
                    tile = (TileEntityMekanism) tileEntity;
                }
            }
            if (tile == null || !tile.handles(getSubstanceType())) {
                //Something went wrong
                return null;
            }
            handler = getHandlerFromTile(tile);
        } else {
            return null;
        }
//        List<TANK> tanks = new ArrayList<>();
//        for (StorageView<CHEMICAL> storageView:handler) {
//            //TODO: Do we need to also clone the attribute validator
//            tanks.add(getTankBuilder().create(storageView.getCapacity(), cloneValidator(handler, tank), null));
//        }
//        //TODO: Improve the logic used so that it tries to batch similar types of chemicals together first
//        // and maybe make it try multiple slot combinations
//        HANDLER outputHandler = getOutputHandler(tanks);
        for (TANK tank : this.tanks) {
            if (!tank.isEmpty()) {
                try(Transaction t=Transaction.openOuter()) {
                    if (handler.insert(tank.getResource(), tank.getAmount(), t) != tank.getAmount()) {
                        //If we have a remainder something failed so bail
                        return null;
                    }
                    t.commit();
                }
                //hasData = true;
            }
        }

        if (tile != null) {
            ItemStack stack1 = storage.getStack();
            tile.saveToItem(stack1);
            return stack1;
        }

//        if (hasData) {
//            //We managed to transfer it all into valid slots, so save it to the stack
//            ItemDataUtils.writeContainers(stack, getSubstanceType().getContainerTag(), tanks);
//        }
        return storage.getStack();
    }
}