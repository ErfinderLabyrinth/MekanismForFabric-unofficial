package mekanism.common.mixinhelper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Map;

public interface CustomEnchantmentsItem {
    int getEnchantmentLevel(ItemStack stack, Enchantment enchantment, int originalLevel);

    Map<Enchantment, Integer> getAllEnchantments(ItemStack stack, Map<Enchantment, Integer> originalEnchantments);
}
