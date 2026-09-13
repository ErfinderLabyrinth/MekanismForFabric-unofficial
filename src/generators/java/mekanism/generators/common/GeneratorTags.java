package mekanism.generators.common;

import mekanism.api.chemical.ChemicalTags;
import mekanism.api.chemical.gas.Gas;
import mekanism.common.Mekanism;
import mekanism.common.tags.LazyTagLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class GeneratorTags {

    /**
     * Call to force make sure this is all initialized
     */
    public static void init() {
        Fluids.init();
        Gases.init();
    }

    private GeneratorTags() {
    }

    public static class Blocks {
        public static final TagKey<Block> ENDERMAN_CANNOT_PLACE_ON = tag("enderman_cannot_place_on");

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, Mekanism.rl(name));
        }
    }

    public static class Fluids {

        private static void init() {
        }

        private Fluids() {
        }

        public static final TagKey<Fluid> BIOETHANOL = cTag("bioethanol");
        public static final LazyTagLookup<Fluid> BIOETHANOL_LOOKUP = LazyTagLookup.create(BuiltInRegistries.FLUID, BIOETHANOL);
        public static final TagKey<Fluid> DEUTERIUM = cTag("deuterium");
        public static final TagKey<Fluid> FUSION_FUEL = cTag("fusion_fuel");
        public static final TagKey<Fluid> TRITIUM = cTag("tritium");

        private static TagKey<Fluid> cTag(String name) {
            return TagKey.create(Registries.FLUID, new ResourceLocation("c", name));
        }
    }

    public static class Gases {

        private static void init() {
        }

        private Gases() {
        }

        public static final TagKey<Gas> DEUTERIUM = tag("deuterium");
        public static final LazyTagLookup<Gas> DEUTERIUM_LOOKUP = LazyTagLookup.create(ChemicalTags.GAS, DEUTERIUM);
        public static final TagKey<Gas> TRITIUM = tag("tritium");
        public static final LazyTagLookup<Gas> TRITIUM_LOOKUP = LazyTagLookup.create(ChemicalTags.GAS, TRITIUM);
        public static final TagKey<Gas> FUSION_FUEL = tag("fusion_fuel");
        public static final LazyTagLookup<Gas> FUSION_FUEL_LOOKUP = LazyTagLookup.create(ChemicalTags.GAS, FUSION_FUEL);

        private static TagKey<Gas> tag(String name) {
            return ChemicalTags.GAS.tag(Mekanism.rl(name));
        }
    }
}