package mekanism.api.security;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface IItemOwnerObjectGetter {
    @Nullable
    IOwnerObject getOwnerObject(ItemStack stack);
}
