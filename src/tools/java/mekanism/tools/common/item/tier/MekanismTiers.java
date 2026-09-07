package mekanism.tools.common.item.tier;

import mekanism.tools.common.config.MekanismToolsConfig;
import mekanism.tools.common.material.BaseMekanismMaterial;
import mekanism.tools.common.material.MaterialCreator;
import mekanism.tools.common.material.impl.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MekanismTiers extends BaseMekanismMaterial {
    public static final MekanismTiers BRONZE = new MekanismTiers(() -> MekanismToolsConfig.tools.bronze, new BronzeMaterialDefaults());
    public static final MekanismTiers LAPIS_LAZULI = new MekanismTiers(() -> MekanismToolsConfig.tools.lapisLazuli, new LapisLazuliMaterialDefaults());
    public static final MekanismTiers OSMIUM = new MekanismTiers(() -> MekanismToolsConfig.tools.osmium, new OsmiumMaterialDefaults());
    public static final MekanismTiers REFINED_GLOWSTONE = new MekanismTiers(() -> MekanismToolsConfig.tools.refinedGlowstone, new RefinedGlowstoneMaterialDefaults());
    public static final MekanismTiers REFINED_OBSIDIAN = new MekanismTiers(() -> MekanismToolsConfig.tools.refinedObsidian, new RefinedObsidianMaterialDefaults());
    public static final MekanismTiers STEEL = new MekanismTiers(() -> MekanismToolsConfig.tools.steel, new SteelMaterialDefaults());

    final Supplier<MaterialCreator> materialCreator;
    final BaseMekanismMaterial defaultMaterial;
    MekanismTiers(Supplier<MaterialCreator> materialCreator, BaseMekanismMaterial defaultMaterial) {
        this.materialCreator = materialCreator;
        this.defaultMaterial = defaultMaterial;
    }

    @Override
    public @Nullable TagKey<Block> getTag() {
        return defaultMaterial.getTag();
    }

    @Override
    public int getShieldDurability() {
        return materialCreator.get().getShieldDurability();
    }

    @Override
    public float getAxeDamage() {
        return materialCreator.get().getAxeDamage();
    }

    @Override
    public float getAxeAtkSpeed() {
        return materialCreator.get().getAxeAtkSpeed();
    }

    @Override
    public String getRegistryPrefix() {
        return defaultMaterial.getRegistryPrefix();
    }

    @Override
    public int getCommonEnchantability() {
        return materialCreator.get().getCommonEnchantability();
    }

    @Override
    public Ingredient getCommonRepairMaterial() {
        return defaultMaterial.getCommonRepairMaterial();
    }

    @Override
    public String getConfigCommentName() {
        return defaultMaterial.getConfigCommentName();
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return materialCreator.get().getDurabilityForType(type);
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return materialCreator.get().getDefenseForType(type);
    }

    @Override
    public SoundEvent getEquipSound() {
        return defaultMaterial.getEquipSound();
    }

    @Override
    public float getToughness() {
        return materialCreator.get().getToughness();
    }

    @Override
    public float getKnockbackResistance() {
        return materialCreator.get().getKnockbackResistance();
    }

    @Override
    public int getUses() {
        return materialCreator.get().getUses();
    }

    @Override
    public float getSpeed() {
        return materialCreator.get().getSpeed();
    }

    @Override
    public float getAttackDamageBonus() {
        return materialCreator.get().getAttackDamageBonus();
    }

    @Override
    public int getLevel() {
        return defaultMaterial.getLevel();
    }

    public int getHelmetArmor() {
        return materialCreator.get().helmetArmor;
    }

    public int getChestplateArmor() {
        return materialCreator.get().chestplateArmor;
    }

    public int getLeggingArmor() {
        return materialCreator.get().legginsArmor;
    }

    public int getBootArmor() {
        return materialCreator.get().bootArmor;
    }
}
