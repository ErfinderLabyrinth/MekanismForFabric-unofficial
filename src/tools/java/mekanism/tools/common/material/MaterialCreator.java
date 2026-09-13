package mekanism.tools.common.material;

import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.world.item.ArmorItem;

@NothingNullByDefault
public class MaterialCreator {
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
    public final int legginsArmor;
    public final int chestplateArmor;
    public final int helmetArmor;

    public MaterialCreator(BaseMekanismMaterial materialDefaults) {
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
        legginsArmor = materialDefaults.getDefenseForType(ArmorItem.Type.LEGGINGS);
        chestplateArmor = materialDefaults.getDefenseForType(ArmorItem.Type.CHESTPLATE);
        helmetArmor = materialDefaults.getDefenseForType(ArmorItem.Type.HELMET);
    }

    public int getShieldDurability() {
        return shieldDurability;
    }

    public float getSwordDamage() {
        return swordDamage;
    }

    public float getSwordAtkSpeed() {
        return swordAtkSpeed;
    }

    public float getShovelDamage() {
        return shovelDamage;
    }

    public float getShovelAtkSpeed() {
        return shovelAtkSpeed;
    }

    public float getAxeDamage() {
        return axeDamage;
    }

    public float getAxeAtkSpeed() {
        return axeAtkSpeed;
    }

    public float getPickaxeDamage() {
        return pickaxeDamage;
    }

    public float getPickaxeAtkSpeed() {
        return pickaxeAtkSpeed;
    }

    public float getHoeDamage() {
        return hoeDamage;
    }

    public float getHoeAtkSpeed() {
        return hoeAtkSpeed;
    }

    public int getPaxelMaxUses() {
        return paxelMaxUses;
    }

    public float getPaxelEfficiency() {
        return paxelEfficiency;
    }

    public float getPaxelDamage() {
        return paxelDamage;
    }

    public float getPaxelAtkSpeed() {
        return paxelAtkSpeed;
    }

    public int getUses() {
        return toolMaxUses;
    }

    public float getSpeed() {
        return efficiency;
    }

    public float getAttackDamageBonus() {
        return attackDamage;
    }

    public int getDurabilityForType(ArmorItem.Type armorType) {
        return switch (armorType) {
            case BOOTS -> bootDurability;
            case LEGGINGS -> leggingDurability;
            case CHESTPLATE -> chestplateDurability;
            case HELMET -> helmetDurability;
        };
    }

    public int getDefenseForType(ArmorItem.Type armorType) {
        return switch (armorType) {
            case BOOTS -> bootArmor;
            case LEGGINGS -> legginsArmor;
            case CHESTPLATE -> chestplateArmor;
            case HELMET -> helmetArmor;
        };
    }

    public int getCommonEnchantability() {
        return enchantability;
    }

    public float getToughness() {
        return toughness;
    }

    public int getPaxelEnchantability() {
        return paxelEnchantability;
    }

    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}