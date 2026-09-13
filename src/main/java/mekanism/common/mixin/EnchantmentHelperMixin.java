package mekanism.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mekanism.common.mixinhelper.CustomEnchantmentsItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyReturnValue(method = "getItemEnchantmentLevel", at = @At("RETURN"))
    private static int addCustomEnchantmentLevel(int original, Enchantment enchantment, ItemStack itemStack) {
        if(itemStack.getItem() instanceof CustomEnchantmentsItem item) {
            return item.getEnchantmentLevel(itemStack, enchantment, original);
        } else {
            return original;
        }
    }

    @ModifyReturnValue(method = "getEnchantments", at = @At("RETURN"))
    private static Map<Enchantment, Integer> addCustomEnchantments(Map<Enchantment, Integer> original, ItemStack itemStack) {
        if(itemStack.getItem() instanceof CustomEnchantmentsItem item) {
            return item.getAllEnchantments(itemStack, original);
        } else {
            return original;
        }
    }
}
