package mekanism.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import mekanism.common.mixinhelper.PiglinNeutralizingArmor;
import mekanism.common.registries.MekanismItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
    @ModifyReturnValue(method = "isBarterCurrency", at = @At("RETURN"))
    private static boolean addRefinedGlowstoneIngot(boolean original, ItemStack itemStack) {
        if (itemStack.is(MekanismItems.REFINED_GLOWSTONE_INGOT.get())) {
            return true;
        }
        return original;
    }

    @Inject(method = "isWearingGold", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"), cancellable = true)
    private static void addMekaSuit(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir, @Local ItemStack stack) {
        if (stack.getItem() instanceof PiglinNeutralizingArmor armor && armor.makesPiglinsNeutral(stack, livingEntity)) {
            cir.setReturnValue(true);
        }
    }
}
