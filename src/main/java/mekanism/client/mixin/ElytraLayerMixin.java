package mekanism.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mekanism.common.mixinhelper.ElytraLayerAddon;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ElytraLayer.class)
public class ElytraLayerMixin {
    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    public boolean addOtherElytren(ItemStack instance, Item item, Operation<Boolean> original) {
        if (this instanceof ElytraLayerAddon addon) {
            return addon.shouldRender(instance);
        }
        return original.call(instance, item);
    }
}
