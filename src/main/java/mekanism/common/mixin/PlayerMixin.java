package mekanism.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mekanism.common.Mekanism;
import mekanism.common.mixinhelper.ElytraFlyable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow
    public abstract void startFallFlying();

    @Inject(method = "getDestroySpeed", at = @At("TAIL"), cancellable = true)
    public void onLivingEntityActuallyHurt(BlockState blockState, CallbackInfoReturnable<Float> cir) {
        float breakSpeed = Mekanism.commonPlayerTickHandler.getBreakSpeed((Player) (Object)this, cir.getReturnValueF(), blockState, Optional.empty());
        cir.setReturnValue(breakSpeed);
    }

    @Inject(method = "tryToStartFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"), cancellable = true)
    public void checkForMekaSuit(CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) ItemStack item) {
        if (item.getItem() instanceof ElytraFlyable elytraFlyable && elytraFlyable.canElytraFly(item, (LivingEntity) (Object) this)) {
            startFallFlying();
            cir.setReturnValue(elytraFlyable.canElytraFly(item, (LivingEntity) (Object) this));
        }
    }
}
