package mekanism.additions.common.world.modifier;

import com.google.common.collect.ImmutableMap;
import mekanism.additions.common.config.AdditionsConfig;
import mekanism.additions.common.config.MekanismAdditionsConfig;
import mekanism.additions.common.entity.baby.BabyType;
import mekanism.common.Mekanism;
import mekanism.common.util.RegistryUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;

import java.util.ArrayList;
import java.util.List;

public class BabyEntitySpawnStructureModifier {
    public static Structure.StructureSettings modifyStructure(Structure structure, Structure.StructureSettings settings) {
        if(settings.spawnOverrides().containsKey(MobCategory.MONSTER)) {
            StructureSpawnOverride override = settings.spawnOverrides().get(MobCategory.MONSTER);
            if(!override.spawns().isEmpty()) {
                List<MobSpawnSettings.SpawnerData> spawnerData = new ArrayList<>(override.spawns().unwrap());
                for(BabyType babyType : BabyType.values()) {
                    modify(babyType, structure, spawnerData);
                }

                StructureSpawnOverride newOverride = new StructureSpawnOverride(override.boundingBox(), WeightedRandomList.create(spawnerData));
                ImmutableMap<MobCategory, StructureSpawnOverride> map = ImmutableMap.<MobCategory, StructureSpawnOverride>builder()
                        .putAll(settings.spawnOverrides())
                        .put(MobCategory.MONSTER, newOverride)
                        .buildKeepingLast();

                return new Structure.StructureSettings(settings.biomes(), map, settings.step(), settings.terrainAdaptation());
            }
        }
        return settings;
    }

    public static void modify(BabyType babyType, Structure structure, List<MobSpawnSettings.SpawnerData> spawnerData) {
        AdditionsConfig.SpawnConfig spawnConfig = MekanismAdditionsConfig.additions.getConfig(babyType);
        if (spawnConfig.shouldSpawn) {
            //Fail quick if there are no overrides for this structure, or it is blacklisted
            ResourceLocation structureName = BuiltInRegistries.STRUCTURE_TYPE.getKey(structure.type());
            if (!spawnConfig.structureBlackList.contains(structureName)) {
                for (MobSpawnSettings.SpawnerData spawner : babyType.getSpawnersToAdd(spawnerData)) {
                    spawnerData.add(spawner);
                    Mekanism.logger.debug("Adding spawn rate for '{}' in structure '{}', with weight: {}, minSize: {}, maxSize: {}",
                          RegistryUtils.getName(spawner.type), structureName, spawner.getWeight(), spawner.minCount, spawner.maxCount);
                }
            }
        }
    }
}