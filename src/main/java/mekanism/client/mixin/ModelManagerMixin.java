package mekanism.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mekanism.client.mixinhelper.ModelManagerModelBakeryGetter;
import mekanism.client.model.ModelBakingCompletedEvent;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelManager.class)
public class ModelManagerMixin implements ModelManagerModelBakeryGetter {
    @Shadow
    private Map<ResourceLocation, BakedModel> bakedRegistry;

    @Unique ModelBakery bakery;

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;getModelGroups()Lit/unimi/dsi/fastutil/objects/Object2IntMap;", shift = At.Shift.AFTER))
    public void finish(ModelManager.ReloadState reloadState, ProfilerFiller profilerFiller, CallbackInfo ci, @Local ModelBakery modelBakery) {
        ModelBakingCompletedEvent.EVENT.invoker().onModelBakingCompleted((ModelManager)(Object)this, modelBakery, bakedRegistry);
        bakery = modelBakery;
    }

    @Override
    public ModelBakery getModelBakery() {
        return bakery;
    }
}
