package mekanism.tools.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.tools.common.item.ItemRefinedGlowstoneArmor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {

    @ModifyVariable(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;setPartVisibility(Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/world/entity/EquipmentSlot;)V"), argsOnly = true)
    private int modifyLight(int light, PoseStack poseStack, MultiBufferSource multiBufferSource, LivingEntity livingEntity, EquipmentSlot equipmentSlot, int i, HumanoidModel<?> humanoidModel) {
        if(livingEntity.getItemBySlot(equipmentSlot).getItem() instanceof ItemRefinedGlowstoneArmor) {
            return LightTexture.FULL_BRIGHT;
        }
        return light;
    }
}
