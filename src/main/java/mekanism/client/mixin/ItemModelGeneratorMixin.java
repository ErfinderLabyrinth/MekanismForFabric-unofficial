package mekanism.client.mixin;

import mekanism.client.mixinhelper.CustomGeometryHolder;
import mekanism.client.model.CustomGeometry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(ItemModelGenerator.class)
public class ItemModelGeneratorMixin {
    @Inject(method = "generateBlockModel", at = @At("TAIL"))
    public void disable3dGui(Function<Material, TextureAtlasSprite> function, BlockModel blockModel, CallbackInfoReturnable<BlockModel> cir) {
        CustomGeometry customGeometry = ((CustomGeometryHolder)cir.getReturnValue()).getCustomGeometry();
        if (customGeometry != null) {
            ((CustomGeometryHolder)cir.getReturnValue()).getCustomGeometry().setGui3d(false);
        }
    }
}
