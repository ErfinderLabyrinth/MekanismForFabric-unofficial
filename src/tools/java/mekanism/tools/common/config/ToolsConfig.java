package mekanism.tools.common.config;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import mekanism.common.config.BaseMekanismConfig;
import mekanism.tools.common.material.MaterialCreator;
import mekanism.tools.common.material.VanillaPaxelMaterialCreator;
import mekanism.tools.common.material.impl.*;
import mekanism.tools.common.material.impl.vanilla.*;

@Config(name = "mekanism/tools")
public class ToolsConfig extends BaseMekanismConfig {
    public float armorSpawnChance = 0.1F;
    public float weaponSpawnChance = 0.01F;
    public float weaponSpawnChanceHard = 0.05F;
    @CollapsibleObject public ArmorSpawnChanceConfig bronzeSpawnRate;
    @CollapsibleObject public ArmorSpawnChanceConfig lapisLazuliSpawnRate;
    @CollapsibleObject public ArmorSpawnChanceConfig osmiumSpawnRate;
    @CollapsibleObject public ArmorSpawnChanceConfig refinedGlowstoneSpawnRate;
    @CollapsibleObject public ArmorSpawnChanceConfig refinedObsidianSpawnRate;
    @CollapsibleObject public ArmorSpawnChanceConfig steelSpawnRate;
    @CollapsibleObject public VanillaPaxelMaterialCreator wood;
    @CollapsibleObject public VanillaPaxelMaterialCreator stone;
    @CollapsibleObject public VanillaPaxelMaterialCreator iron;
    @CollapsibleObject public VanillaPaxelMaterialCreator diamond;
    @CollapsibleObject public VanillaPaxelMaterialCreator gold;
    @CollapsibleObject public VanillaPaxelMaterialCreator netherite;
    @CollapsibleObject public MaterialCreator bronze;
    @CollapsibleObject public MaterialCreator lapisLazuli;
    @CollapsibleObject public MaterialCreator osmium;
    @CollapsibleObject public MaterialCreator refinedGlowstone;
    @CollapsibleObject public MaterialCreator refinedObsidian;
    @CollapsibleObject public MaterialCreator steel;

    ToolsConfig() {
        bronzeSpawnRate = new ArmorSpawnChanceConfig();
        lapisLazuliSpawnRate = new ArmorSpawnChanceConfig();
        osmiumSpawnRate = new ArmorSpawnChanceConfig();
        refinedGlowstoneSpawnRate = new ArmorSpawnChanceConfig();
        refinedObsidianSpawnRate = new ArmorSpawnChanceConfig();
        steelSpawnRate = new ArmorSpawnChanceConfig();

        wood = new VanillaPaxelMaterialCreator(new WoodPaxelMaterialDefaults());
        stone = new VanillaPaxelMaterialCreator(new StonePaxelMaterialDefaults());
        iron = new VanillaPaxelMaterialCreator(new IronPaxelMaterialDefaults());
        diamond = new VanillaPaxelMaterialCreator(new DiamondPaxelMaterialDefaults());
        gold = new VanillaPaxelMaterialCreator(new GoldPaxelMaterialDefaults());
        netherite = new VanillaPaxelMaterialCreator(new NetheritePaxelMaterialDefaults());

        bronze = new MaterialCreator(new BronzeMaterialDefaults());
        lapisLazuli = new MaterialCreator(new LapisLazuliMaterialDefaults());
        osmium = new MaterialCreator(new OsmiumMaterialDefaults());
        refinedGlowstone = new MaterialCreator(new RefinedGlowstoneMaterialDefaults());
        refinedObsidian = new MaterialCreator(new RefinedObsidianMaterialDefaults());
        steel = new MaterialCreator(new SteelMaterialDefaults());
    }

    public static class ArmorSpawnChanceConfig {

        public final boolean canSpawnWeapon = true;
        public final float swordWeight;
        public final float helmetChance;
        public final float chestplateChance;
        public final float leggingsChance;
        public final float bootsChance;

        public final float multiplePieceChance = 0.25F;
        public final float multiplePieceChanceHard = 0.1F;

        public final float weaponEnchantmentChance;
        public final float armorEnchantmentChance;

        private ArmorSpawnChanceConfig() {
            this(0.33F, 1, 1, 1, 1, 0.25F, 0.5F);
        }

        private ArmorSpawnChanceConfig(float swordChance, float helmetChance,
                                       float chestplateChance, float leggingsChance, float bootsChance, float weaponEnchantmentChance, float armorEnchantmentChance) {
            this.swordWeight = swordChance;
            this.helmetChance = helmetChance;
            this.chestplateChance = chestplateChance;
            this.leggingsChance = leggingsChance;
            this.bootsChance = bootsChance;

            this.weaponEnchantmentChance = weaponEnchantmentChance;
            this.armorEnchantmentChance = armorEnchantmentChance;
        }
    }
}