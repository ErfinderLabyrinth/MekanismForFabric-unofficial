package mekanism.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import mekanism.common.Mekanism;
import mekanism.common.mixinhelper.BypassSneakItem;
import mekanism.common.mixinhelper.FirstUsableItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

    @WrapOperation(method = "useItemOn", slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSecondaryUseActive()Z")), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private boolean modifySneakBypassUse(ItemStack instance, Operation<Boolean> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) ServerPlayer player, @Local BlockPos pos) {
        boolean empty = original.call(instance);
        if(!empty && instance.getItem() instanceof BypassSneakItem bypassSneakItem) {
            return bypassSneakItem.doesSneakBypassUse(instance, level, pos, player);
        }
        return empty;
    }

    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    public void onBlockClick(ServerPlayer serverPlayer, Level level, ItemStack itemStack, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        Pair<Boolean, Boolean> result = Mekanism.instance.COMMON_PLAYER_TRACKER.rightClickEvent(serverPlayer, interactionHand, level, blockHitResult.getBlockPos());
        if ((result.getSecond() == null || result.getSecond()) && (Object)itemStack instanceof FirstUsableItem fui) {
            UseOnContext useoncontext = new UseOnContext(serverPlayer, interactionHand, blockHitResult);
            InteractionResult result2 = fui.onItemUseFirst(itemStack, useoncontext);
            if (result2 != InteractionResult.PASS) cir.setReturnValue(result2);
        }
    }
}
