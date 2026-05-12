package mekanism.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.common.config.MekanismConfig;
import mekanism.common.registries.MekanismIntProviderTypes;
import mekanism.common.resource.ore.OreType.OreVeinType;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.IntSupplier;

public class ConfigurableConstantInt extends IntProvider {

    public static final Codec<ConfigurableConstantInt> CODEC = RecordCodecBuilder.create(builder -> builder.group(
          OreVeinType.CODEC.optionalFieldOf("oreVeinType").forGetter(config -> Optional.ofNullable(config.oreVeinType))
    ).apply(builder, oreType -> {
        if (oreType.isPresent()) {
            OreVeinType type = oreType.get();
            return new ConfigurableConstantInt(type, () -> MekanismConfig.world.getVeinConfig(type).perChunk());
        }
        return new ConfigurableConstantInt(null, () -> MekanismConfig.world.salt.perChunk);
    }));

    @Nullable
    private final OreVeinType oreVeinType;
    private final IntSupplier value;

    public ConfigurableConstantInt(@Nullable OreVeinType oreVeinType, IntSupplier value) {
        this.oreVeinType = oreVeinType;
        this.value = value;
    }

    public int getValue() {
        //Needs to be getOrDefault so that when IntProvider's range codec validates things in CountPlacement,
        // even though how it gets that value doesn't matter for syncing. Our actual value here doesn't really
        // matter because we limit our config values at the ranges of CountPlacement
        return this.value.getAsInt();
    }

    @Override
    public int sample(@NotNull RandomSource random) {
        return getValue();
    }

    @Override
    public int getMinValue() {
        return getValue();
    }

    @Override
    public int getMaxValue() {
        return getValue();
    }

    @NotNull
    @Override
    public IntProviderType<?> getType() {
        return MekanismIntProviderTypes.CONFIGURABLE_CONSTANT.get();
    }

    @Override
    public String toString() {
        return Integer.toString(getValue());
    }
}