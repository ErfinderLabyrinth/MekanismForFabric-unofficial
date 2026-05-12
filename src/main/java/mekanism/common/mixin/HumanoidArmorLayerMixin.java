package mekanism.common.mixin;

import mekanism.common.mixinhelper.CustomArmorTexture;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
    @Shadow
    @Final
    private static Map<String, ResourceLocation> ARMOR_LOCATION_CACHE;

    @Inject(method = "getArmorLocation", at = @At("HEAD"), cancellable = true)
    public void replaceArmorLocation(ArmorItem armorItem, boolean bl, String string, CallbackInfoReturnable<ResourceLocation> cir) {
        if (armorItem instanceof CustomArmorTexture customArmorTexture) {
            String customTexture = customArmorTexture.getArmorTexture();
            cir.setReturnValue(ARMOR_LOCATION_CACHE.computeIfAbsent(customTexture, ResourceLocation::new));
        }
    }
}
