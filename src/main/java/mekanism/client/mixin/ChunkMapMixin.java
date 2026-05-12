package mekanism.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mekanism.common.Mekanism;
import mekanism.common.lib.transmitter.TransmitterNetworkRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
    @Shadow
    @Final
    private ServerLevel level;

    @Inject(method = "updateChunkScheduling", at = @At("TAIL"))
    public void mekanism$onTicketLevelChange(long chunkPos, int newTicketLevel, ChunkHolder chunkHolder, int oldTicketLevel, CallbackInfoReturnable<ChunkHolder> cir) {
        if (oldTicketLevel != newTicketLevel) {
            TransmitterNetworkRegistry.getInstance().onTicketLevelChange(level, chunkPos, oldTicketLevel, newTicketLevel);
        }
    }

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;write(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)V"))
    public void mekanism$onChunkSave(ChunkAccess chunkAccess, CallbackInfoReturnable<Boolean> cir, @Local CompoundTag data) {
        Mekanism.worldTickHandler.chunkSave(level, chunkAccess, data);
    }
}
