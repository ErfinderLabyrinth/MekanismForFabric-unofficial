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

    public void resetFallbacks() {
        wood.fallback = new WoodPaxelMaterialDefaults();
        stone.fallback = new StonePaxelMaterialDefaults();
        iron.fallback = new IronPaxelMaterialDefaults();
        diamond.fallback = new DiamondPaxelMaterialDefaults();
        gold.fallback = new GoldPaxelMaterialDefaults();
        netherite.fallback = new NetheritePaxelMaterialDefaults();

        bronze.fallBack = new BronzeMaterialDefaults();
        lapisLazuli.fallBack = new LapisLazuliMaterialDefaults();
        osmium.fallBack = new OsmiumMaterialDefaults();
        refinedGlowstone.fallBack = new RefinedGlowstoneMaterialDefaults();
        refinedObsidian.fallBack = new RefinedObsidianMaterialDefaults();
        steel.fallBack = new SteelMaterialDefaults();
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

    /*public static class VanillaPaxelMaterialConfig {
        public float paxelDamage;
        public float paxelAtkSpeed;
        public float paxelEfficiency;
        public int paxelEnchantability;
        public int paxelMaxUses;

        public VanillaPaxelMaterialConfig(VanillaPaxelMaterial materialDefaults) {
            paxelDamage = materialDefaults.getPaxelDamage();
            paxelAtkSpeed = materialDefaults.getPaxelAtkSpeed();
            paxelEfficiency = materialDefaults.getPaxelEfficiency();
            paxelEnchantability = materialDefaults.getPaxelEnchantability();
            paxelMaxUses = materialDefaults.getPaxelMaxUses();
        }

        public VanillaPaxelMaterialCreator toMaterialCreator(VanillaPaxelMaterial paxelMaterial) {
            return new VanillaPaxelMaterialCreator(paxelMaterial, this);
        }
    }*/

    /*public static class MaterialConfig {
        public int shieldDurability;
        public float swordDamage;
        public float swordAtkSpeed;
        public float shovelDamage;
        public float shovelAtkSpeed;
        public float axeDamage;
        public float axeAtkSpeed;
        public float pickaxeDamage;
        public float pickaxeAtkSpeed;
        public float hoeDamage;
        public float hoeAtkSpeed;
        public float paxelDamage;
        public float paxelAtkSpeed;
        public float paxelEfficiency;
        public int paxelEnchantability;
        public int paxelMaxUses;
        public int toolMaxUses;
        public float efficiency;
        public float attackDamage;
        public int enchantability;
        public float toughness;
        public float knockbackResistance;
        public int bootDurability;
        public int leggingDurability;
        public int chestplateDurability;
        public int helmetDurability;
        public int bootArmor;
        public int leggingArmor;
        public int chestplateArmor;
        public int helmetArmor;

        public MaterialConfig(BaseMekanismMaterial materialDefaults) {
            attackDamage = materialDefaults.getAttackDamageBonus();
            shieldDurability = materialDefaults.getShieldDurability();
            swordDamage = materialDefaults.getSwordDamage();
            swordAtkSpeed = materialDefaults.getSwordAtkSpeed();
            shovelDamage = materialDefaults.getShovelDamage();
            shovelAtkSpeed = materialDefaults.getShovelAtkSpeed();
            axeDamage = materialDefaults.getAxeDamage();
            axeAtkSpeed = materialDefaults.getAxeAtkSpeed();
            pickaxeDamage = materialDefaults.getPickaxeDamage();
            pickaxeAtkSpeed = materialDefaults.getPickaxeAtkSpeed();
            hoeDamage = materialDefaults.getHoeDamage();
            hoeAtkSpeed = materialDefaults.getHoeAtkSpeed();
            toolMaxUses = materialDefaults.getUses();
            efficiency = materialDefaults.getSpeed();
            paxelDamage = materialDefaults.getPaxelDamage();
            paxelAtkSpeed = materialDefaults.getPaxelAtkSpeed();
            paxelEfficiency = materialDefaults.getPaxelEfficiency();
            paxelEnchantability = materialDefaults.getPaxelEnchantability();
            paxelMaxUses = materialDefaults.getPaxelMaxUses();
            enchantability = materialDefaults.getCommonEnchantability();
            toughness = materialDefaults.getToughness();
            knockbackResistance = materialDefaults.getKnockbackResistance();
            bootDurability = materialDefaults.getDurabilityForType(ArmorItem.Type.BOOTS);
            leggingDurability = materialDefaults.getDurabilityForType(ArmorItem.Type.LEGGINGS);
            chestplateDurability = materialDefaults.getDurabilityForType(ArmorItem.Type.CHESTPLATE);
            helmetDurability = materialDefaults.getDurabilityForType(ArmorItem.Type.HELMET);
            bootArmor = materialDefaults.getDefenseForType(ArmorItem.Type.BOOTS);
            leggingArmor = materialDefaults.getDefenseForType(ArmorItem.Type.LEGGINGS);
            chestplateArmor = materialDefaults.getDefenseForType(ArmorItem.Type.CHESTPLATE);
            helmetArmor = materialDefaults.getDefenseForType(ArmorItem.Type.HELMET);
        }

        public MaterialCreator toMaterialCreator(BaseMekanismMaterial mathe) {
            return new VanillaPaxelMaterialCreator(paxelMaterial, this);
        }
    }*/
}