package mekanism.common.recipe.upgrade;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.inventory.BinMekanismInventory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class LockRecipeData implements RecipeUpgradeData<LockRecipeData> {

    private final ItemStack lock;

    LockRecipeData(BinMekanismInventory inventory) {
        this.lock = inventory.getBinSlot().getLockStack();
    }

    @Nullable
    @Override
    public LockRecipeData merge(LockRecipeData other) {
        return ItemEntity.areMergable(lock, other.lock) ? this : null;
    }

    @Override
    public ItemStack applyToStack(ItemStack stack) {
        BinMekanismInventory inventory = BinMekanismInventory.create(stack);
        if (inventory == null) {
            return null;
        }
        inventory.getBinSlot().setLockStack(this.lock);
        inventory.onContentsChanged();
        return stack;
    }
}