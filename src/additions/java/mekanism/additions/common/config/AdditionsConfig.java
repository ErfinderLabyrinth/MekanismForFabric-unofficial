package mekanism.additions.common.config;

import me.shedaniel.autoconfig.annotation.Config;
import mekanism.additions.common.entity.baby.BabyType;
import mekanism.common.config.BaseMekanismConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Config(name = "mekanism/additions")
public class AdditionsConfig extends BaseMekanismConfig {
    public int obsidianTNTDelay = 100;
    public float obsidianTNTBlastRadius = 12;
    public boolean voiceServerEnabled = false;
    public int voicePort = 36_123;
    private Map<BabyType, SpawnConfig> spawnConfigs = new EnumMap<>(BabyType.class);

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