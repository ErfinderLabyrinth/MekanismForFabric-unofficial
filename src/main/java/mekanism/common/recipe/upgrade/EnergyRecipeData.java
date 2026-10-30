package mekanism.common.recipe.upgrade;

import mekanism.api.DataHandlerUtils;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IEnergyContainer;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.inventory.SimpleSingleStackStorage;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.tile.base.TileEntityMekanism;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;

@NothingNullByDefault
public class EnergyRecipeData implements RecipeUpgradeData<EnergyRecipeData> {

    private final List<IEnergyContainer> energyContainers;

    EnergyRecipeData(ListTag containers) {
        int count = DataHandlerUtils.getMaxId(containers, NBTConstants.CONTAINER);
        energyContainers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            energyContainers.add(BasicEnergyContainer.create(Long.MAX_VALUE, null));
        }
        DataHandlerUtils.readContainers(energyContainers, containers);
    }

    private EnergyRecipeData(List<IEnergyContainer> energyContainers) {
        this.energyContainers = energyContainers;
    }

    @Nullable
    @Override
    public EnergyRecipeData merge(EnergyRecipeData other) {
        List<IEnergyContainer> allContainers = new ArrayList<>(energyContainers);
        allContainers.addAll(other.energyContainers);
        return new EnergyRecipeData(allContainers);
    }

    @Override
    public ItemStack applyToStack(ItemStack stack) {
        if (energyContainers.isEmpty()) {
            return stack;
        }
        Item item = stack.getItem();
        SimpleSingleStackStorage storage = new SimpleSingleStackStorage(stack);
        EnergyStorage energyStorage = ContainerItemContext.ofSingleSlot(storage).find(EnergyStorage.ITEM);
//        List<IEnergyContainer> energyContainers = new ArrayList<>();
        TileEntityMekanism tile = null;
        if (energyStorage != null) {
            //energyContainers.add(BasicEnergyContainer.create(energyHandler.getMaxEnergy(container), null));
        } else if (item instanceof BlockItem blockItem) {
            tile = getTileFromBlock(blockItem.getBlock());
            if (tile == null || !tile.handles(SubstanceType.ENERGY)) {
                //Something went wrong
                return null;
            }
            energyStorage = tile.getEnergyManager().getContainer(null);
//            for (int container = 0; container < tile.getEnergyContainerCount(); container++) {
//                energyContainers.add(BasicEnergyContainer.create(tile.getMaxEnergy(container), null));
//            }
        } else {
            return null;
        }
        if (energyContainers.isEmpty()) {
            //We don't actually have any tanks in the output
            return storage.getStack();
        }
//        IMekanismStrictEnergyHandler outputHandler = new IMekanismStrictEnergyHandler() {
//            @NotNull
//            @Override
//            public List<IEnergyContainer> getEnergyContainer(@Nullable Direction side) {
//                return energyContainers;
//            }
//
//            @Override
//            public void onContentsChanged() {
//            }
//        };
        boolean hasData = false;
        for (IEnergyContainer energyContainer : this.energyContainers) {
            if (!energyContainer.isEmpty()) {
                hasData = true;
                try(Transaction t=Transaction.openOuter()) {
                    if (energyStorage.insert(energyContainer.getEnergy(), t) != energyContainer.getEnergy()) {
                        //If we have a remainder, stop trying to insert as our upgraded item's buffer is just full
                        t.commit();
                        break;
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
//            ItemDataUtils.writeContainers(stack, NBTConstants.ENERGY_CONTAINERS, energyContainers);
//        }
        return storage.getStack();
    }
}