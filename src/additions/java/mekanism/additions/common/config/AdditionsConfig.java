package mekanism.additions.common.config;

import mekanism.additions.common.entity.baby.BabyType;
import mekanism.common.config.BaseMekanismConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AdditionsConfig extends BaseMekanismConfig {
    public final int obsidianTNTDelay = 100;
    public final float obsidianTNTBlastRadius = 12;
    public final boolean voiceServerEnabled = false;
    public final int voicePort = 36_123;
    private final Map<BabyType, SpawnConfig> spawnConfigs = new EnumMap<>(BabyType.class);

    AdditionsConfig() {
        for (BabyType type : BabyType.values()) {
            addBabyTypeConfig(type);
        }
    }

    private void addBabyTypeConfig(BabyType type) {
        spawnConfigs.put(type, new SpawnConfig());
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

        private SpawnConfig() {}
    }
}