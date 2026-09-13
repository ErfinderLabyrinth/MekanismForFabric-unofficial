package mekanism.common.mixinhelper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface ElytraLayerAddon {
    boolean shouldRender(ItemStack instance);

    ResourceLocation getElytraTexture();
}
