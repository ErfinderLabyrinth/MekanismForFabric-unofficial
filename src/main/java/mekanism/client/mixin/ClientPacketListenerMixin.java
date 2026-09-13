package mekanism.client.mixin;

import mekanism.client.ClientRegistration;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleAddOrRemoveRecipes", at = @At("TAIL"))
    public void updateRecipes(ClientboundRecipePacket clientboundRecipePacket, CallbackInfo ci) {
        ClientRegistration.TICK_HANDLER.recipesUpdated();
    }
}
