package mekanism.tools.common.material;

import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.world.item.Tiers;

@NothingNullByDefault
public class VanillaPaxelMaterialCreator implements IPaxelMaterial {

    public transient VanillaPaxelMaterial fallback;

    public final float paxelDamage;
    public final float paxelAtkSpeed;
    private final float paxelEfficiency;
    private final int paxelEnchantability;
    private final int paxelMaxUses;

    public VanillaPaxelMaterialCreator(VanillaPaxelMaterial materialDefaults) {
        this.fallback = materialDefaults;
        paxelDamage = materialDefaults.getPaxelDamage();
        paxelAtkSpeed = materialDefaults.getPaxelAtkSpeed();
        paxelEfficiency = materialDefaults.getPaxelEfficiency();
        paxelEnchantability = materialDefaults.getPaxelEnchantability();
        paxelMaxUses = materialDefaults.getPaxelMaxUses();
    }

    public Tiers getVanillaTier() {
        return fallback.getVanillaTier();
    }

    public String getRegistryPrefix() {
        return fallback.getRegistryPrefix();
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

    @Override
    public String getConfigCommentName() {
        return fallback.getConfigCommentName();
    }
}