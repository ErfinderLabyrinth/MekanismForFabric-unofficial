package mekanism.additions.common.config;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import mekanism.additions.common.entity.baby.BabyType;
import mekanism.additions.common.registries.AdditionsEntityTypes;
import mekanism.api.providers.IEntityTypeProvider;
import mekanism.common.config.BaseMekanismConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class AdditionsConfig extends BaseMekanismConfig {
    public final int obsidianTNTDelay = 100;
    public final float obsidianTNTBlastRadius = 12;
    public final boolean voiceServerEnabled = false;
    public final int voicePort = 36_123;
    private final Map<BabyType, SpawnConfig> spawnConfigs = new EnumMap<>(BabyType.class);

    AdditionsConfig() {
        addBabyTypeConfig(BabyType.CREEPER, AdditionsEntityTypes.BABY_CREEPER, () -> EntityType.CREEPER);
        addBabyTypeConfig(BabyType.ENDERMAN, AdditionsEntityTypes.BABY_ENDERMAN, () -> EntityType.ENDERMAN);
        addBabyTypeConfig(BabyType.SKELETON, AdditionsEntityTypes.BABY_SKELETON, () -> EntityType.SKELETON);
        addBabyTypeConfig(BabyType.STRAY, AdditionsEntityTypes.BABY_STRAY, () -> EntityType.STRAY);
        addBabyTypeConfig(BabyType.WITHER_SKELETON, AdditionsEntityTypes.BABY_WITHER_SKELETON, () -> EntityType.WITHER_SKELETON);
    }

    private void addBabyTypeConfig(BabyType type, IEntityTypeProvider entityTypeProvider, IEntityTypeProvider parentTypeProvider) {
        spawnConfigs.put(type, new SpawnConfig(entityTypeProvider, parentTypeProvider));
    }

    @Override
    public String getFileName() {
        return "additions";
    }

    public SpawnConfig getConfig(BabyType babyType) {
        return spawnConfigs.get(babyType);
    }

    public static class SpawnConfig {

        public final boolean shouldSpawn = true;
        public final double weightPercentage = 0.5;
        public final double minSizePercentage = 0.5;
        public final double maxSizePercentage = 0.5;
        public final double spawnCostPerEntityPercentage = 1D;
        public final double maxSpawnCostPercentage = 1D;
        public final List<ResourceLocation> biomeBlackList = List.of();
        public final List<ResourceLocation> structureBlackList = List.of();
        public transient IEntityTypeProvider entityTypeProvider;
        public transient IEntityTypeProvider parentTypeProvider;

        private SpawnConfig() {}

        private SpawnConfig(IEntityTypeProvider entityTypeProvider, IEntityTypeProvider parentTypeProvider) {
        }

        public MobSpawnSettings.SpawnerData getSpawner(MobSpawnSettings.SpawnerData parentEntry) {
            int weight = (int) Math.ceil(parentEntry.getWeight().asInt() * weightPercentage);
            int minSize = (int) Math.ceil(parentEntry.minCount * minSizePercentage);
            int maxSize = (int) Math.ceil(parentEntry.maxCount * maxSizePercentage);
            return new MobSpawnSettings.SpawnerData(entityTypeProvider.getEntityType(), weight, minSize, Math.max(minSize, maxSize));
        }

        public List<MobSpawnSettings.SpawnerData> getSpawnersToAdd(List<MobSpawnSettings.SpawnerData> monsterSpawns) {
            EntityType<?> parent = parentTypeProvider.getEntityType();
            //If the adult mob can spawn let the baby mob spawn as well
            //Note: We adjust the mob's spawning based on the adult's spawn rates
            return monsterSpawns.stream()
                  .filter(monsterSpawn -> monsterSpawn.type == parent)
                  .map(this::getSpawner)
                  .toList();
        }
    }
}