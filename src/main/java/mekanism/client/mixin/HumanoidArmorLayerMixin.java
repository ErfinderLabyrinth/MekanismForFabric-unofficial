package mekanism.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import mekanism.common.mixinhelper.CustomArmorTexture;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
    @Shadow
    @Final
    private static Map<String, ResourceLocation> ARMOR_LOCATION_CACHE;

    @WrapMethod(method = "getArmorLocation")
    public ResourceLocation replaceArmorLocation(ArmorItem armorItem, boolean bl, String string, Operation<ResourceLocation> original) {
        if (armorItem instanceof CustomArmorTexture customArmorTexture) {
            String customTexture = customArmorTexture.getArmorTexture();
            return ARMOR_LOCATION_CACHE.computeIfAbsent(customTexture, ResourceLocation::new);
        }
        return original.call(armorItem, bl, string);
    }
}
