package mekanism.common;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import mekanism.api.NBTConstants;
import mekanism.api.security.ISecurityUtils;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.qio.IQIOCraftingWindowHolder;
import mekanism.common.content.qio.QIOGlobalItemLookup;
import mekanism.common.inventory.container.item.PortableQIODashboardContainer;
import mekanism.common.lib.frequency.FrequencyManager;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.util.WorldUtils;
import mekanism.common.world.GenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.Predicate;

public class CommonWorldTickHandler {

    private static final long maximumDeltaTimeNanoSecs = 16_000_000; // 16 milliseconds

    //TODO: I believe this may be fine as is with just the load and save methods being synchronized
    // but there is a chance this is not the case in which case we should adjust how this is done
    private Map<ResourceLocation, Object2IntMap<ChunkPos>> chunkVersions;
    private Map<ResourceLocation, Queue<ChunkPos>> chunkRegenMap;
    public static boolean flushTagAndRecipeCaches;
    public static boolean monitoringCardboardBox;
    @Nullable
    public static Predicate<ItemStack> fallbackItemCollector;

    public void addRegenChunk(ResourceKey<Level> dimension, ChunkPos chunkCoord) {
        if (chunkRegenMap == null) {
            chunkRegenMap = new Object2ObjectArrayMap<>();
        }
        ResourceLocation dimensionName = dimension.location();
        if (!chunkRegenMap.containsKey(dimensionName)) {
            LinkedList<ChunkPos> list = new LinkedList<>();
            list.add(chunkCoord);
            chunkRegenMap.put(dimensionName, list);
        } else {
            Queue<ChunkPos> regenPositions = chunkRegenMap.get(dimensionName);
            if (!regenPositions.contains(chunkCoord)) {
                regenPositions.add(chunkCoord);
            }
        }
    }

    public void resetChunkData() {
        chunkRegenMap = null;
        chunkVersions = null;
    }

    public boolean onEntitySpawn(Entity entity) {
        //If we are in the middle of breaking a block using a cardboard box, cancel any items
        // that are dropped, we do this at highest priority to ensure we cancel it the same tick
        // before forge replaces items with custom item entities with a tick delay
        // We also cancel any experience orbs from spawning as things like the furnace will store
        // how much xp they have but also try to drop it on replace
        if (monitoringCardboardBox) {
            if (entity instanceof ItemEntity || entity instanceof ExperienceOrb) {
                entity.discard();
                return true;
            }
        } else if (fallbackItemCollector != null && entity instanceof ItemEntity itemEntity && fallbackItemCollector.test(itemEntity.getItem())) {
            //If we have a fallback item collector active and the entity that is being added is an item,
            // try to let our fallback collector handle the item and keep track of it instead of actually adding it to the world
            entity.discard();
            return true;
        }
        return false;
    }

    public boolean onBlockBreak(Level level, Player player, BlockPos blockPos, BlockState state, @Nullable BlockEntity blockEntity) {
        //Skip empty block, shouldn't be a null state but the BreakEvent still handles that as the empty block,
        // so we need to skip handling it that way, AND skip and blocks that can never have a block entity
        if (state != null && !state.isAir() && state.hasBlockEntity()) {
            //If the block might have a block entity, look it up from the world and see if the player has access to destroy it
            if (!ISecurityUtils.INSTANCE.canAccess(player, blockEntity)) {
                //If they don't because it is something that is locked, then cancel the event
                return false;
            }
        }
        return true;
    }

    public synchronized void chunkSave(Level level, ChunkAccess chunk, CompoundTag data) {
        if (!level.isClientSide()) {
            int chunkVersion = MekanismConfig.world.userGenVersion;
            if (chunkVersions != null) {
                chunkVersion = chunkVersions.getOrDefault(level.dimension().location(), Object2IntMaps.emptyMap())
                      .getOrDefault(chunk.getPos(), chunkVersion);
            }
            data.putInt(NBTConstants.WORLD_GEN_VERSION, chunkVersion);
        }
    }

    public synchronized void onChunkDataLoad(Level level, ProtoChunk chunk, CompoundTag data) {
        if (!level.isClientSide()) {
            int version = data.getInt(NBTConstants.WORLD_GEN_VERSION);
            //When a chunk is loaded, if it has an older version than the latest one
            if (version < MekanismConfig.world.userGenVersion) {
                //Track what version it has so that when we save it, if we haven't gotten a chance to update
                // the chunk yet, then we are able to properly save that we still will need to update it
                if (chunkVersions == null) {
                    chunkVersions = new Object2ObjectArrayMap<>();
                }
                ChunkPos chunkCoord = chunk.getPos();
                ResourceKey<Level> dimension = level.dimension();
                chunkVersions.computeIfAbsent(dimension.location(), dim -> new Object2IntOpenHashMap<>())
                      .put(chunkCoord, version);
                if (MekanismConfig.world.enableRegeneration) {
                    //If retrogen is enabled, then we also need to mark the chunk as needing retrogen
                    addRegenChunk(dimension, chunkCoord);
                }
            }
        }
    }

    public void chunkUnloadEvent(ServerLevel level, LevelChunk chunk) {
        if (!level.isClientSide() && chunkVersions != null) {
            //When a chunk unloads, free up the memory tracking what version it has
            chunkVersions.getOrDefault(level.dimension().location(), Object2IntMaps.emptyMap())
                  .removeInt(chunk.getPos());
        }
    }

    public void worldUnloadEvent(MinecraftServer server, ServerLevel world) {
        if (!world.isClientSide() && world instanceof Level && chunkVersions != null) {
            //When a world unloads, free up memory tracking the versions of the chunks in it
            chunkVersions.remove(world.dimension().location());
        }
    }

    public void worldLoadEvent(MinecraftServer server, ServerLevel world) {
        FrequencyManager.load(server);
        MultiblockManager.createOrLoadAll(server);
        QIOGlobalItemLookup.INSTANCE.createOrLoad(server);
        RadiationManager.get().createOrLoad(server);
    }

    public void serverTick(MinecraftServer server) {
        FrequencyManager.tick(server);
        RadiationManager.get().tickServer(server);
    }

    public void tickEnd(ServerLevel world) {
        if (!world.isClientSide) {
            RadiationManager.get().tickServerWorld(world);
            if (flushTagAndRecipeCaches) {
                //Loop all open containers and if it is a portable qio dashboard force refresh the window's recipes
                for (ServerPlayer player : world.players()) {
                    if (player.containerMenu instanceof PortableQIODashboardContainer qioDashboard) {
                        for (byte index = 0; index < IQIOCraftingWindowHolder.MAX_CRAFTING_WINDOWS; index++) {
                            qioDashboard.getCraftingWindow(index).invalidateRecipe();
                        }
                    }
                }
                flushTagAndRecipeCaches = false;
            }

            if (chunkRegenMap == null || !MekanismConfig.world.enableRegeneration) {
                return;
            }
            ResourceLocation dimensionName = world.dimension().location();
            //Credit to E. Beef
            if (chunkRegenMap.containsKey(dimensionName)) {
                Queue<ChunkPos> chunksToGen = chunkRegenMap.get(dimensionName);
                //Chunk versions may be null if retrogen is forced by command
                Object2IntMap<ChunkPos> dimensionChunkVersions = chunkVersions == null ? Object2IntMaps.emptyMap() : chunkVersions.getOrDefault(dimensionName, Object2IntMaps.emptyMap());
                long startTime = System.nanoTime();
                while (System.nanoTime() - startTime < maximumDeltaTimeNanoSecs && !chunksToGen.isEmpty()) {
                    ChunkPos nextChunk = chunksToGen.poll();
                    if (nextChunk == null) {
                        break;
                    }
                    //Ensure the chunk actually exists and is still loaded before trying to retrogen it
                    if (WorldUtils.isChunkLoaded(world, nextChunk)) {
                        if (GenHandler.generate(world, nextChunk)) {
                            Mekanism.logger.info("Regenerating ores and salt at chunk {}", nextChunk);
                        }
                        //Regardless of whether we were able to generate anything in the chunk, now that we have
                        // handled it, update the chunk version. We do this by removing tracking the chunk's
                        // version so that we can just default it to the latest version when saved and free up the
                        // memory as early as possible
                        if (chunkVersions != null) {
                            dimensionChunkVersions.removeInt(nextChunk);
                        }
                    }
                }
                if (chunksToGen.isEmpty()) {
                    chunkRegenMap.remove(dimensionName);
                }
            }
        }
    }
}