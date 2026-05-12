package mekanism.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.common.config.MekanismConfig;
import mekanism.common.config.WorldConfig.OreVeinConfig;
import mekanism.common.resource.ore.OreType.OreVeinType;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;

import java.util.List;

public record ResizableOreFeatureConfig(List<TargetBlockState> targetStates, OreVeinType oreVeinType, int size,
                                        float discardChanceOnAirExposure) implements FeatureConfiguration {

    public static final Codec<ResizableOreFeatureConfig> CODEC = RecordCodecBuilder.create(builder -> builder.group(
          Codec.list(OreConfiguration.TargetBlockState.CODEC).fieldOf("targets").forGetter(config -> config.targetStates),
          OreVeinType.CODEC.fieldOf("oreVeinType").forGetter(config -> config.oreVeinType)
    ).apply(builder, (targetStates, oreVeinType) -> {
        OreVeinConfig veinConfig = MekanismConfig.world.getVeinConfig(oreVeinType);
        return new ResizableOreFeatureConfig(targetStates, oreVeinType, veinConfig.maxVeinSize(), veinConfig.discardChanceOnAirExposure());
    }));
}