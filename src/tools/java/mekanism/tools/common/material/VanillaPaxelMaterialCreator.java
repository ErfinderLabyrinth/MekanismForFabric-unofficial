package mekanism.tools.common.material;

import mekanism.api.annotations.NothingNullByDefault;

@NothingNullByDefault
public class VanillaPaxelMaterialCreator implements IPaxelMaterial {
    public final float paxelDamage;
    public final float paxelAtkSpeed;
    private final float paxelEfficiency;
    private final int paxelEnchantability;
    private final int paxelMaxUses;

    public VanillaPaxelMaterialCreator(VanillaPaxelMaterial materialDefaults) {
        paxelDamage = materialDefaults.getPaxelDamage();
        paxelAtkSpeed = materialDefaults.getPaxelAtkSpeed();
        paxelEfficiency = materialDefaults.getPaxelEfficiency();
        paxelEnchantability = materialDefaults.getPaxelEnchantability();
        paxelMaxUses = materialDefaults.getPaxelMaxUses();
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
    public int getPaxelEnchantability() {
        return paxelEnchantability;
    }
}