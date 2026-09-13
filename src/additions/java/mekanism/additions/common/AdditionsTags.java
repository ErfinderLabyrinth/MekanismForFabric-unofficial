package mekanism.additions.common;

import mekanism.common.tags.TagUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class AdditionsTags {

    /**
     * Call to force make sure this is all initialized
     */
    public static void init() {
        Items.init();
        Blocks.init();
        DamageTypes.init();
        Entities.init();
    }

    private AdditionsTags() {
    }

    public static class Items {

        private static void init() {
        }

        private Items() {
        }

        public static final TagKey<Item> BALLOONS = tag("balloons");

        public static final TagKey<Item> FENCES_PLASTIC = cTag("plastic_fences");
        public static final TagKey<Item> FENCE_GATES_PLASTIC = cTag("plastic_fence_gates");
        public static final TagKey<Item> STAIRS_PLASTIC = cTag("plastic_stairs");
        public static final TagKey<Item> SLABS_PLASTIC = cTag("plastic_slabs");
        public static final TagKey<Item> STAIRS_PLASTIC_GLOW = cTag("glowing_plastic_stairs");
        public static final TagKey<Item> SLABS_PLASTIC_GLOW = cTag("glowing_plastic_slabs");
        public static final TagKey<Item> STAIRS_PLASTIC_TRANSPARENT = cTag("transparent_plastic_stairs");
        public static final TagKey<Item> SLABS_PLASTIC_TRANSPARENT = cTag("transparent_plastic_slabs");

        public static final TagKey<Item> GLOW_PANELS = tag("glow_panels");

        public static final TagKey<Item> PLASTIC_BLOCKS = tag("plastic_blocks");
        public static final TagKey<Item> PLASTIC_BLOCKS_GLOW = tag("plastic_blocks/glow");
        public static final TagKey<Item> PLASTIC_BLOCKS_PLASTIC = tag("plastic_blocks/plastic");
        public static final TagKey<Item> PLASTIC_BLOCKS_REINFORCED = tag("plastic_blocks/reinforced");
        public static final TagKey<Item> PLASTIC_BLOCKS_ROAD = tag("plastic_blocks/road");
        public static final TagKey<Item> PLASTIC_BLOCKS_SLICK = tag("plastic_blocks/slick");
        public static final TagKey<Item> PLASTIC_BLOCKS_TRANSPARENT = tag("plastic_blocks/transparent");

        private static TagKey<Item> cTag(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation("c", name));
        }

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, MekanismAdditions.rl(name));
        }
    }

    public static class Blocks {

        private static void init() {
        }

        private Blocks() {
        }

        public static final TagKey<Block> FENCES_PLASTIC = cTag("plastic_fences");
        public static final TagKey<Block> FENCE_GATES_PLASTIC = cTag("plastic_fence_gates");
        public static final TagKey<Block> STAIRS_PLASTIC = cTag("plastic_stairs");
        public static final TagKey<Block> SLABS_PLASTIC = cTag("plastic_slabs");
        public static final TagKey<Block> STAIRS_PLASTIC_GLOW = cTag("glowing_plastic_stairs");
        public static final TagKey<Block> SLABS_PLASTIC_GLOW = cTag("glowing_plastic_slabs");
        public static final TagKey<Block> STAIRS_PLASTIC_TRANSPARENT = cTag("transparent_plastic_stairs");
        public static final TagKey<Block> SLABS_PLASTIC_TRANSPARENT = cTag("transparent_plastic_slabs");

        public static final TagKey<Block> GLOW_PANELS = tag("glow_panels");

        public static final TagKey<Block> PLASTIC_BLOCKS = tag("plastic_blocks");
        public static final TagKey<Block> PLASTIC_BLOCKS_GLOW = tag("plastic_blocks/glow");
        public static final TagKey<Block> PLASTIC_BLOCKS_PLASTIC = tag("plastic_blocks/plastic");
        public static final TagKey<Block> PLASTIC_BLOCKS_REINFORCED = tag("plastic_blocks/reinforced");
        public static final TagKey<Block> PLASTIC_BLOCKS_ROAD = tag("plastic_blocks/road");
        public static final TagKey<Block> PLASTIC_BLOCKS_SLICK = tag("plastic_blocks/slick");
        public static final TagKey<Block> PLASTIC_BLOCKS_TRANSPARENT = tag("plastic_blocks/transparent");

        private static TagKey<Block> cTag(String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation("c", name));
        }

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, MekanismAdditions.rl(name));
        }
    }

    public static class DamageTypes {

        private static void init() {
        }

        private DamageTypes() {
        }

        public static final TagKey<DamageType> BALLOON_INVULNERABLE = tag("balloon_invulnerable");

        private static TagKey<DamageType> tag(String name) {
            return TagUtils.createKey(Registries.DAMAGE_TYPE, MekanismAdditions.rl(name));
        }
    }

    public static class Entities {

        private static void init() {
        }

        private Entities() {
        }

        public static final TagKey<EntityType<?>> CREEPERS = cTag("creepers");
        public static final TagKey<EntityType<?>> ENDERMEN = cTag("endermen");

        private static TagKey<EntityType<?>> cTag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("c", name));
        }
    }
}