package mekanism.common.mixinhelper;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public interface FirstUsableItem {
    InteractionResult onItemUseFirst(ItemStack item, UseOnContext useoncontext);
}
