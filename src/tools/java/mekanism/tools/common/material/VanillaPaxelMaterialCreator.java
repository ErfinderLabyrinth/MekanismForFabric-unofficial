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
        //Note: Damage predicate to allow for tools to go negative to the value of the base tier so that a tool
        // can effectively have zero damage for things like the hoe
        paxelDamage = materialDefaults.getPaxelDamage();
        paxelAtkSpeed = materialDefaults.getPaxelAtkSpeed();
        paxelEfficiency = materialDefaults.getPaxelEfficiency();
        paxelEnchantability = materialDefaults.getPaxelEnchantability();
        paxelMaxUses = materialDefaults.getPaxelMaxUses();
    }

    /*public VanillaPaxelMaterialCreator(VanillaPaxelMaterial materialDefaults, ToolsConfig.VanillaPaxelMaterialConfig config) {
        this.fallback = materialDefaults;
        //Note: Damage predicate to allow for tools to go negative to the value of the base tier so that a tool
        // can effectively have zero damage for things like the hoe
        paxelDamage = config.paxelDamage;
        paxelAtkSpeed = config.paxelAtkSpeed;
        paxelEfficiency = config.paxelEfficiency;
        paxelEnchantability = config.paxelEnchantability;
        paxelMaxUses = config.paxelMaxUses;
    }*/

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