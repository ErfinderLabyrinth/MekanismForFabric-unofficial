package mekanism.common.mixinhelper;

import net.minecraft.world.item.ItemStack;

public interface ElytraLayerAddon {
    boolean shouldRender(ItemStack instance);
}
