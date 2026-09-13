package mekanism.common.mixin;

import mekanism.common.Mekanism;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
    @Inject(method = "read", at = @At(value = "RETURN"))
    private static void mekanism$onChunkLoad(ServerLevel serverLevel, PoiManager poiManager, ChunkPos chunkPos, CompoundTag data, CallbackInfoReturnable<ProtoChunk> cir) {
        Mekanism.worldTickHandler.onChunkDataLoad(serverLevel, cir.getReturnValue(), data);
    }
}
