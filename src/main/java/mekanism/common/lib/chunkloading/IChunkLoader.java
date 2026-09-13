package mekanism.common.lib.chunkloading;

import mekanism.common.tile.component.TileComponentChunkLoader;
import net.minecraft.world.level.ChunkPos;

import java.util.Set;

public interface IChunkLoader {

    TileComponentChunkLoader<?> getChunkLoader();

    Set<ChunkPos> getChunkSet();
}