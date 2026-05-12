package mekanism.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import mekanism.client.ClientTickHandler;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"))
    private void onScroll(long pWindowPointer, double pXOffset, double pYOffset, CallbackInfo info, @Local(ordinal = 2) LocalDoubleRef delta) {
        if(ClientTickHandler.onMouseScroll(delta.get())) {
            info.cancel();
        }
    }
}
