package mekanism.client.mixin;

import mekanism.client.mixinhelper.CustomGeometryHolder;
import mekanism.client.model.CustomGeometry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(BlockModel.class)
public class BlockModelMixin implements CustomGeometryHolder {
    @Unique
    CustomGeometry customGeometry;

    public void setCustomGeometry(CustomGeometry customGeometry) {
        this.customGeometry = customGeometry;
    }

    @Override
    public CustomGeometry getCustomGeometry() {
        return customGeometry;
    }

    @Inject(method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("HEAD"), cancellable = true)
    public void customBake(ModelBaker modelBaker, BlockModel blockModel, Function<Material, TextureAtlasSprite> function, ModelState modelState, ResourceLocation resourceLocation, boolean bl, CallbackInfoReturnable<BakedModel> cir) {
        if (customGeometry != null) {
            cir.setReturnValue(customGeometry.bake(modelBaker, function, modelState, blockModel.getItemOverrides(modelBaker, blockModel), resourceLocation));
        }
    }
}
