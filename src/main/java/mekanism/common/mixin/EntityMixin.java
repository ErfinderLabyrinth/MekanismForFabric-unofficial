package mekanism.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mekanism.common.mixinhelper.EntityExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements EntityExtension {
    @Unique
    private float stepHeightModifier;

    @ModifyReturnValue(method = "maxUpStep", at = @At("RETURN"))
    private float applyStepHeight(float stepHeight) {
        return stepHeight + stepHeightModifier;
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void save(CompoundTag tag, CallbackInfoReturnable<CompoundTag> info) {
        tag.putFloat("mekanism$stepHeightModifier", stepHeightModifier);
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void load(CompoundTag tag, CallbackInfo info) {
        stepHeightModifier = tag.getFloat("mekanism$stepHeightModifier");
    }

    @Override
    public float mekanism$getStepHeightModifier() {
        return stepHeightModifier;
    }

    @Override
    public void mekanism$setStepHeightModifier(float stepHeightModifier) {
        this.stepHeightModifier = stepHeightModifier;
    }
}
