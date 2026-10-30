package mekanism.common.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.item.ItemStack;

public class SimpleSingleStackStorage extends SingleStackStorage {
    private ItemStack stack;

    public SimpleSingleStackStorage(ItemStack stack) {
        this.stack = stack;
    }

    public SimpleSingleStackStorage() {
        this(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public void setStack(ItemStack stack) {
        this.stack = stack;
    }
}
