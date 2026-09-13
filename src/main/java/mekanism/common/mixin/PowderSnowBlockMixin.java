package mekanism.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mekanism.common.mixinhelper.WalkableOnPowderSnow;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {
    @WrapOperation(method = "canEntityWalkOnPowderSnow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    public static boolean checkCustomBoots(ItemStack instance, Item item, Operation<Boolean> original, @Local(argsOnly = true) Entity entity) {
        if (instance.getItem() instanceof WalkableOnPowderSnow walkableOnPowderSnow) {
            return walkableOnPowderSnow.canWalkOnPowderedSnow(instance, (LivingEntity) entity);
        }
        return original.call(instance, item);
    }
}
