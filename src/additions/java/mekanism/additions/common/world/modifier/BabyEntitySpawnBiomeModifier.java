package mekanism.additions.common.world.modifier;

import java.util.List;

import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.config.AdditionsConfig;
import mekanism.additions.common.config.MekanismAdditionsConfig;
import mekanism.additions.common.entity.baby.BabyType;
import mekanism.common.Mekanism;
import mekanism.common.util.RegistryUtils;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class BabyEntitySpawnBiomeModifier {
    public static void modify(BabyType babyType, BiomeSelectionContext biomeSelectionContext, BiomeModificationContext biomeModificationContext) {
        AdditionsConfig.SpawnConfig spawnConfig = MekanismAdditionsConfig.additions.getConfig(babyType);
        if (spawnConfig.shouldSpawn) {
            //Note: We need to run after addition in case we ran after any mods added their skeletons,
            // but we run before after everything to make it easier for another mod to remove us
            ResourceLocation biomeName = biomeSelectionContext.getBiomeKey().location();
            if (!spawnConfig.biomeBlackList.contains(biomeName)) {
                EntityType<?> parent = spawnConfig.parentTypeProvider.getEntityType();
                BiomeModificationContext.SpawnSettingsContext mobSpawnSettings = biomeModificationContext.getSpawnSettings();
                MobSpawnSettings previousSettings = biomeSelectionContext.getBiome().getMobSettings();
                List<MobSpawnSettings.SpawnerData> monsterSpawns = previousSettings.getMobs(MobCategory.MONSTER).unwrap();
                for (MobSpawnSettings.SpawnerData spawner : spawnConfig.getSpawnersToAdd(monsterSpawns)) {
                    mobSpawnSettings.addSpawn(MobCategory.MONSTER, spawner);
                    MobSpawnSettings.MobSpawnCost parentCost = previousSettings.getMobSpawnCost(parent);
                    if (parentCost == null) {
                        Mekanism.logger.debug("Adding spawn rate for '{}' in biome '{}', with weight: {}, minSize: {}, maxSize: {}",
                              RegistryUtils.getName(spawner.type), biomeName, spawner.getWeight(), spawner.minCount, spawner.maxCount);
                    } else {
                        double spawnCostPerEntity = parentCost.charge() * spawnConfig.spawnCostPerEntityPercentage;
                        double maxSpawnCost = parentCost.energyBudget() * spawnConfig.maxSpawnCostPercentage;
                        mobSpawnSettings.setSpawnCost(spawner.type, spawnCostPerEntity, maxSpawnCost);
                        Mekanism.logger.debug("Adding spawn rate for '{}' in biome '{}', with weight: {}, minSize: {}, maxSize: {}, spawnCostPerEntity: {}, maxSpawnCost: {}",
                              RegistryUtils.getName(spawner.type), biomeName, spawner.getWeight(), spawner.minCount, spawner.maxCount, spawnCostPerEntity, maxSpawnCost);
                    }
                }
            }
        }
    }

    public static void register() {
        BiomeModifications.create(MekanismAdditions.rl("spawn_babies"))
                .add(ModificationPhase.POST_PROCESSING, context -> true, (biomeSelectionContext, biomeModificationContext) -> {
                    for(BabyType babyType : BabyType.values()) {
                        modify(babyType, biomeSelectionContext, biomeModificationContext);
                    }
                });

    }
}