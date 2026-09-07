package mekanism.tools.common.config;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import mekanism.common.config.BaseMekanismConfig;
import mekanism.tools.common.material.MaterialCreator;
import mekanism.tools.common.material.VanillaPaxelMaterialCreator;
import mekanism.tools.common.material.impl.BronzeMaterialDefaults;
import mekanism.tools.common.material.impl.LapisLazuliMaterialDefaults;
import mekanism.tools.common.material.impl.OsmiumMaterialDefaults;
import mekanism.tools.common.material.impl.RefinedGlowstoneMaterialDefaults;
import mekanism.tools.common.material.impl.RefinedObsidianMaterialDefaults;
import mekanism.tools.common.material.impl.SteelMaterialDefaults;
import mekanism.tools.common.material.impl.vanilla.DiamondPaxelMaterialDefaults;
import mekanism.tools.common.material.impl.vanilla.GoldPaxelMaterialDefaults;
import mekanism.tools.common.material.impl.vanilla.IronPaxelMaterialDefaults;
import mekanism.tools.common.material.impl.vanilla.NetheritePaxelMaterialDefaults;
import mekanism.tools.common.material.impl.vanilla.StonePaxelMaterialDefaults;
import mekanism.tools.common.material.impl.vanilla.WoodPaxelMaterialDefaults;

@Config(name = "mekanism/tools")
public class ToolsConfig extends BaseMekanismConfig {
    public final float armorSpawnChance = 0.1F;
    public final float weaponSpawnChance = 0.01F;
    public final float weaponSpawnChanceHard = 0.05F;
    @CollapsibleObject public final ArmorSpawnChanceConfig bronzeSpawnRate;
    @CollapsibleObject public final ArmorSpawnChanceConfig lapisLazuliSpawnRate;
    @CollapsibleObject public final ArmorSpawnChanceConfig osmiumSpawnRate;
    @CollapsibleObject public final ArmorSpawnChanceConfig refinedGlowstoneSpawnRate;
    @CollapsibleObject public final ArmorSpawnChanceConfig refinedObsidianSpawnRate;
    @CollapsibleObject public final ArmorSpawnChanceConfig steelSpawnRate;
    @CollapsibleObject public final VanillaPaxelMaterialCreator wood;
    @CollapsibleObject public final VanillaPaxelMaterialCreator stone;
    @CollapsibleObject public final VanillaPaxelMaterialCreator iron;
    @CollapsibleObject public final VanillaPaxelMaterialCreator diamond;
    @CollapsibleObject public final VanillaPaxelMaterialCreator gold;
    @CollapsibleObject public final VanillaPaxelMaterialCreator netherite;
    @CollapsibleObject public final MaterialCreator bronze;
    @CollapsibleObject public final MaterialCreator lapisLazuli;
    @CollapsibleObject public final MaterialCreator osmium;
    @CollapsibleObject public final MaterialCreator refinedGlowstone;
    @CollapsibleObject public final MaterialCreator refinedObsidian;
    @CollapsibleObject public final MaterialCreator steel;

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

    @Override
    public String getFileName() {
        return "tools";
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