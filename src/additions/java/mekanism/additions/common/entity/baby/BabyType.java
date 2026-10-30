package mekanism.additions.common.entity.baby;

import com.mojang.serialization.Codec;
import mekanism.additions.common.config.AdditionsConfig;
import mekanism.additions.common.config.MekanismAdditionsConfig;
import mekanism.additions.common.registries.AdditionsEntityTypes;
import mekanism.api.providers.IEntityTypeProvider;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public enum BabyType implements StringRepresentable {
    CREEPER(AdditionsEntityTypes.BABY_CREEPER, () -> EntityType.CREEPER),
    ENDERMAN(AdditionsEntityTypes.BABY_ENDERMAN, () -> EntityType.ENDERMAN),
    SKELETON(AdditionsEntityTypes.BABY_SKELETON, () -> EntityType.SKELETON),
    STRAY(AdditionsEntityTypes.BABY_STRAY, () -> EntityType.STRAY),
    WITHER_SKELETON(AdditionsEntityTypes.BABY_WITHER_SKELETON, () -> EntityType.WITHER_SKELETON);

    public static final Codec<BabyType> CODEC = StringRepresentable.fromEnum(BabyType::values);

    private final String serializedName;
    private IEntityTypeProvider entityTypeProvider;
    private IEntityTypeProvider parentTypeProvider;

    BabyType(IEntityTypeProvider entityTypeProvider, IEntityTypeProvider parentTypeProvider) {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.entityTypeProvider = entityTypeProvider;
        this.parentTypeProvider = parentTypeProvider;
    }

    public AdditionsConfig.SpawnConfig getConfig() {
        return MekanismAdditionsConfig.additions.getConfig(this);
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public MobSpawnSettings.SpawnerData getSpawner(MobSpawnSettings.SpawnerData parentEntry) {
        int weight = (int) Math.ceil(parentEntry.getWeight().asInt() * getConfig().weightPercentage);
        int minSize = (int) Math.ceil(parentEntry.minCount * getConfig().minSizePercentage);
        int maxSize = (int) Math.ceil(parentEntry.maxCount * getConfig().maxSizePercentage);
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

    public IEntityTypeProvider getEntityTypeProvider() {
        return entityTypeProvider;
    }

    public IEntityTypeProvider getParentTypeProvider() {
        return parentTypeProvider;
    }
}