package mekanism.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mekanism.client.mixinhelper.BlockElementExtension;
import mekanism.client.mixinhelper.CustomGeometryHolder;
import mekanism.client.model.CustomGeometry;
import mekanism.client.model.item_layers.ItemLayersGeometry;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
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

    @WrapOperation(method = "generateBlockModel", at = @At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"))
    public boolean setFullLight(List<BlockElement> instance, Collection<BlockElement> es, Operation<Boolean> original, @Local(argsOnly = true) BlockModel blockModel, @Local int i) {
        CustomGeometry customGeometry = ((CustomGeometryHolder)blockModel).getCustomGeometry();
        if (customGeometry instanceof ItemLayersGeometry itemLayersGeometry && itemLayersGeometry.getFullLightLayers().contains(i)) {
            for (BlockElement e : es) {
                ((BlockElementExtension)e).mekanism$setLight(15, 15);
            }
        }
        return original.call(instance, es);
    }
}
