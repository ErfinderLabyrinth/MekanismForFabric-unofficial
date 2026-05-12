package mekanism.common.item.gear;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.render.RenderPropertiesProvider;
import mekanism.client.render.armor.ISpecialGear;
import mekanism.client.render.armor.ISpecialGearGetter;
import mekanism.common.Mekanism;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.attribute.AttributeCache;
import mekanism.common.lib.attribute.IAttributeRefresher;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ItemArmoredFreeRunners extends ItemFreeRunners implements IAttributeRefresher, ISpecialGearGetter {

    private static final ArmoredFreeRunnerMaterial ARMORED_FREE_RUNNER_MATERIAL = new ArmoredFreeRunnerMaterial();

    private final AttributeCache attributeCache;

    public ItemArmoredFreeRunners(Properties properties) {
        super(ARMORED_FREE_RUNNER_MATERIAL, properties);
        this.attributeCache = new AttributeCache(this, () -> MekanismConfig.gear.armoredFreeRunnerArmor, () -> MekanismConfig.gear.armoredFreeRunnerToughness,
                () -> MekanismConfig.gear.armoredFreeRunnerKnockbackResistance);
    }

    @Override
    public ISpecialGear getSpecialGear() {
        return RenderPropertiesProvider.armoredFreeRunners();
    }

    @Override
    public int getDefense() {
        return getMaterial().getDefenseForType(getType());
    }

    @Override
    public float getToughness() {
        return getMaterial().getToughness();
    }

    @NotNull
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull ItemStack stack, @NotNull EquipmentSlot slot) {
        return slot == getEquipmentSlot() ? attributeCache.get() : ImmutableMultimap.of();
    }

    @Override
    public void addToBuilder(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
        UUID modifier = ARMOR_MODIFIER_UUID_PER_TYPE.get(getType());
        builder.put(Attributes.ARMOR, new AttributeModifier(modifier, "Armor modifier", getDefense(), Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(modifier, "Armor toughness", getToughness(), Operation.ADDITION));
        builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(modifier, "Armor knockback resistance", getMaterial().getKnockbackResistance(),
              Operation.ADDITION));
    }

    @NothingNullByDefault
    private static class ArmoredFreeRunnerMaterial extends FreeRunnerMaterial {

        @Override
        public int getDefenseForType(ArmorItem.Type armorType) {
            return armorType == ArmorItem.Type.BOOTS ? MekanismConfig.gear.armoredFreeRunnerArmor : 0;
        }

        @Override
        public String getName() {
            return Mekanism.MODID + ":free_runners_armored";
        }

        @Override
        public float getToughness() {
            return MekanismConfig.gear.armoredFreeRunnerToughness;
        }

        @Override
        public float getKnockbackResistance() {
            return MekanismConfig.gear.armoredFreeRunnerKnockbackResistance;
        }
    }
}
