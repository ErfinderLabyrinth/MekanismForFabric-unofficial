package mekanism.common.item.gear;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.client.render.RenderPropertiesProvider;
import mekanism.client.render.armor.ISpecialGear;
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

public class ItemArmoredJetpack extends ItemJetpack implements IAttributeRefresher {

    private static final ArmoredJetpackMaterial ARMORED_JETPACK_MATERIAL = new ArmoredJetpackMaterial();

    private final AttributeCache attributeCache;

    public ItemArmoredJetpack(Properties properties) {
        super(ARMORED_JETPACK_MATERIAL, properties);
        this.attributeCache = new AttributeCache(this, () -> MekanismConfig.COMMON.gear.armoredJetpackArmor, () -> MekanismConfig.COMMON.gear.armoredJetpackToughness,
                () -> MekanismConfig.COMMON.gear.armoredJetpackKnockbackResistance);
    }

    @Override
    public ISpecialGear getSpecialGear() {
        return RenderPropertiesProvider.armoredJetpack();
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
    private static class ArmoredJetpackMaterial extends JetpackMaterial {

        @Override
        public int getDefenseForType(ArmorItem.Type armorType) {
            return armorType == ArmorItem.Type.CHESTPLATE ? MekanismConfig.COMMON.gear.armoredJetpackArmor : 0;
        }

        @Override
        public String getName() {
            return Mekanism.MODID + ":jetpack_armored";
        }

        @Override
        public float getToughness() {
            return MekanismConfig.COMMON.gear.armoredJetpackToughness;
        }

        @Override
        public float getKnockbackResistance() {
            return MekanismConfig.COMMON.gear.armoredJetpackKnockbackResistance;
        }
    }
}