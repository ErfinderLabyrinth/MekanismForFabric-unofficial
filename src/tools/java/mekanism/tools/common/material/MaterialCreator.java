package mekanism.tools.common.material;

import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class MaterialCreator extends BaseMekanismMaterial {
    public transient BaseMekanismMaterial fallBack;

    private final int shieldDurability;
    public final float swordDamage;
    public final float swordAtkSpeed;
    public final float shovelDamage;
    public final float shovelAtkSpeed;
    public final float axeDamage;
    public final float axeAtkSpeed;
    public final float pickaxeDamage;
    public final float pickaxeAtkSpeed;
    public final float hoeDamage;
    public final float hoeAtkSpeed;
    public final float paxelDamage;
    public final float paxelAtkSpeed;
    private final float paxelEfficiency;
    private final int paxelEnchantability;
    private final int paxelMaxUses;
    private final int toolMaxUses;
    private final float efficiency;
    public final float attackDamage;
    private final int enchantability;
    public final float toughness;
    public final float knockbackResistance;
    private final int bootDurability;
    private final int leggingDurability;
    private final int chestplateDurability;
    private final int helmetDurability;
    public final int bootArmor;
    public final int leggingArmor;
    public final int chestplateArmor;
    public final int helmetArmor;

    public MaterialCreator(BaseMekanismMaterial materialDefaults) {
        fallBack = materialDefaults;
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

    @Override
    public int getShieldDurability() {
        return shieldDurability;
    }

    @Override
    public float getSwordDamage() {
        return swordDamage;
    }

    @Override
    public float getSwordAtkSpeed() {
        return swordAtkSpeed;
    }

    @Override
    public float getShovelDamage() {
        return shovelDamage;
    }

    @Override
    public float getShovelAtkSpeed() {
        return shovelAtkSpeed;
    }

    @Override
    public float getAxeDamage() {
        return axeDamage;
    }

    @Override
    public float getAxeAtkSpeed() {
        return axeAtkSpeed;
    }

    @Override
    public float getPickaxeDamage() {
        return pickaxeDamage;
    }

    @Override
    public float getPickaxeAtkSpeed() {
        return pickaxeAtkSpeed;
    }

    @Override
    public float getHoeDamage() {
        return hoeDamage;
    }

    @Override
    public float getHoeAtkSpeed() {
        return hoeAtkSpeed;
    }

    @Override
    public int getPaxelMaxUses() {
        return paxelMaxUses;
    }

    @Override
    public float getPaxelEfficiency() {
        return paxelEfficiency;
    }

    @Override
    public float getPaxelDamage() {
        return paxelDamage;
    }

    @Override
    public float getPaxelAtkSpeed() {
        return paxelAtkSpeed;
    }

    @Override
    public int getUses() {
        return toolMaxUses;
    }

    @Override
    public float getSpeed() {
        return efficiency;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamage;
    }

    @Override
    @Deprecated
    public int getLevel() {
        return fallBack.getLevel();
    }

    @Nullable
    @Override
    public TagKey<Block> getTag() {
        return fallBack.getTag();
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type armorType) {
        return switch (armorType) {
            case BOOTS -> bootDurability;
            case LEGGINGS -> leggingDurability;
            case CHESTPLATE -> chestplateDurability;
            case HELMET -> helmetDurability;
        };
    }

    @Override
    public int getDefenseForType(ArmorItem.Type armorType) {
        return switch (armorType) {
            case BOOTS -> bootArmor;
            case LEGGINGS -> leggingArmor;
            case CHESTPLATE -> chestplateArmor;
            case HELMET -> helmetArmor;
        };
    }

    @Override
    public int getCommonEnchantability() {
        return enchantability;
    }

    @Override
    public boolean burnsInFire() {
        return fallBack.burnsInFire();
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public SoundEvent getEquipSound() {
        return fallBack.getEquipSound();
    }

    @Override
    public Ingredient getCommonRepairMaterial() {
        return fallBack.getCommonRepairMaterial();
    }

    @Override
    public String getConfigCommentName() {
        return fallBack.getConfigCommentName();
    }

    /**
     * Only used on the client in vanilla
     */
    @Override
    public String getName() {
        return fallBack.getName();
    }

    @Override
    public String getRegistryPrefix() {
        return fallBack.getRegistryPrefix();
    }

    @Override
    public int getPaxelEnchantability() {
        return paxelEnchantability;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}