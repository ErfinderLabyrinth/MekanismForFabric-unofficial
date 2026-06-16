package mekanism.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import mekanism.common.Mekanism;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.mixinhelper.LivingEntityExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements LivingEntityExtension {
    @Unique
    float swimSpeedModifier;

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V"))
    private float applySwimSpeedModifier(float swimSpeed) {
        return swimSpeed * swimSpeedModifier;
    }

    @ModifyArg(method = "jumpInLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"), index = 1)
    private double applySwimSpeedModifierUp(double upSpeed) {
        return upSpeed * swimSpeedModifier;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void addMekanismData(CompoundTag tag, CallbackInfo info) {
        tag.putFloat("mekanism$swimSpeedModifier", swimSpeedModifier);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readMekanismData(CompoundTag tag, CallbackInfo info) {
        swimSpeedModifier = tag.getFloat("mekanism$swimSpeedModifier");
    }

    @Override
    public float mekanism$getSwimSpeedModifier() {
        return swimSpeedModifier;
    }

    @Override
    public void mekanism$setSwimSpeedModifier(float swimSpeedModifier) {
        this.swimSpeedModifier = swimSpeedModifier;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onLivingEntityTick(CallbackInfo ci) {
        RadiationManager.get().onLivingTick((LivingEntity) (Object)this);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void onLivingEntityHurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        if (Mekanism.commonPlayerTickHandler.onEntityAttacked((LivingEntity) (Object)this, damageSource, f)) {
            cir.cancel();
        }
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    public void onLivingEntityActuallyHurt(DamageSource damageSource, float f, CallbackInfo ci, @Local(argsOnly = true) LocalFloatRef amount) {
        if (Mekanism.commonPlayerTickHandler.onLivingHurt((LivingEntity) (Object)this, damageSource, f, amount::set)) {
            ci.cancel();
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    public void onLivingEntityJump(CallbackInfo ci) {
        Mekanism.commonPlayerTickHandler.onLivingJump((LivingEntity) (Object)this);
    }

    @ModifyExpressionValue(method = "onEquipItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameTags(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean suppressEquipmentSound(boolean original) {
        return original || SUPPRESS_SOUND.get();
    }


//    public boolean checkForCustomElytra(boolean bl) {
//
//        return bl;
//    }
}
