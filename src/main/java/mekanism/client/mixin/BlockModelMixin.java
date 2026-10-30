package mekanism.client.mixin;

import mekanism.client.mixinhelper.CustomGeometryHolder;
import mekanism.client.mixinhelper.RenderTypeHolder;
import mekanism.client.model.CustomGeometry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(BlockModel.class)
public class BlockModelMixin implements CustomGeometryHolder, RenderTypeHolder {
    @Shadow
    @Nullable
    public BlockModel parent;
    @Unique
    CustomGeometry customGeometry;
    @Unique
    String renderType;

    public void setCustomGeometry(CustomGeometry customGeometry) {
        this.customGeometry = customGeometry;
    }

    @Override
    public CustomGeometry getCustomGeometry() {
        return customGeometry;
    }

    @Inject(method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("RETURN"), cancellable = true)
    public void customBake(ModelBaker modelBaker, BlockModel blockModel, Function<Material, TextureAtlasSprite> function, ModelState modelState, ResourceLocation resourceLocation, boolean bl, CallbackInfoReturnable<BakedModel> cir) {
        BlockModel currentBlockModel = (BlockModel) (Object)this;
        while (currentBlockModel != null) {
            CustomGeometry geometry = ((CustomGeometryHolder)currentBlockModel).getCustomGeometry();
            if (geometry != null) {
                cir.setReturnValue(geometry.bake((BlockModel)(Object)this, null, modelBaker, function, modelState, blockModel.getItemOverrides(modelBaker, blockModel), resourceLocation, cir.getReturnValue()));
                return;
            }
            currentBlockModel = currentBlockModel.parent;
        }
    }

    @Inject(method = "resolveParents", at = @At("HEAD"))
    public void resolveGeometryParents(Function<ResourceLocation, UnbakedModel> function, CallbackInfo ci) {
        if (customGeometry != null) {
            customGeometry.resolveParents(function);
        }
    }

    @Override
    public void mekanism$setRenderType(String renderType) {
        this.renderType = renderType;
    }

    @Override
    public String mekanism$getRenderType() {
        return renderType;
    }
}
