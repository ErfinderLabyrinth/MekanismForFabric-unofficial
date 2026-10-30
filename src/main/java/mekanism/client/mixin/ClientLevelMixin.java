package mekanism.client.mixin;

import mekanism.common.Mekanism;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstruction(CallbackInfo ci) {
        Mekanism.playerState.init((ClientLevel) (Object) this);
    }
}
