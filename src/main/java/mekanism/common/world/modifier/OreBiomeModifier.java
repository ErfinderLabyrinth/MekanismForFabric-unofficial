package mekanism.common.world.modifier;

import mekanism.common.Mekanism;
import mekanism.common.resource.ore.OreType;
import mekanism.common.tags.MekanismTags;
import mekanism.common.util.EnumUtils;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class OreBiomeModifier {
    public static void register() {
        for (OreType type : EnumUtils.ORE_TYPES) {
            int features = type.getBaseConfigs().size();
            for (int vein = 0; vein < features; vein++) {
                OreType.OreVeinType oreVeinType = new OreType.OreVeinType(type, vein);
                ResourceLocation name = Mekanism.rl(oreVeinType.name());
                ResourceKey<PlacedFeature> key = placedFeature(name);
                BiomeModifications.addFeature(context -> context.hasTag(MekanismTags.Biomes.SPAWN_ORES), GenerationStep.Decoration.UNDERGROUND_ORES, key);
            }
        }
        BiomeModifications.addFeature(context -> context.hasTag(MekanismTags.Biomes.SPAWN_ORES), GenerationStep.Decoration.UNDERGROUND_ORES, placedFeature(Mekanism.rl("salt")));
    }

    protected static ResourceKey<PlacedFeature> placedFeature(ResourceLocation name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, name);
    }
}