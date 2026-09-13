package mekanism.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mekanism.common.mixinhelper.ElytraFlyable;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Shadow
    @Final
    public ClientPacketListener connection;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"), cancellable = true)
    public void checkForMekaSuit(CallbackInfo ci, @Local(ordinal = 0) ItemStack item) {
        if (item.getItem() instanceof ElytraFlyable elytraFlyable && elytraFlyable.canElytraFly(item, (LivingEntity) (Object) this)) {
            connection.send(new ServerboundPlayerCommandPacket((Entity) (Object)this, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        }
    }
}
