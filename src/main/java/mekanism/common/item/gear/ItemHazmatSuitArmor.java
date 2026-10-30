package mekanism.common.item.gear;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.radiation.capability.IRadiationShielding;
import mekanism.common.Mekanism;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;

public class ItemHazmatSuitArmor extends ArmorItem implements IRadiationShielding {

    private static final HazmatMaterial HAZMAT_MATERIAL = new HazmatMaterial();

    public ItemHazmatSuitArmor(ArmorItem.Type armorType, Properties properties) {
        super(HAZMAT_MATERIAL, armorType, properties.rarity(Rarity.UNCOMMON));
    }

    public static double getShieldingByArmor(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> 0.25;
            case CHESTPLATE -> 0.4;
            case LEGGINGS -> 0.2;
            case BOOTS -> 0.15;
        };
    }

//    @Override
//    public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
//        return super.getDefaultTooltipHideFlags(stack) | TooltipPart.MODIFIERS.getMask();
//    }

    @Override
    public double getRadiationShielding(ItemStack stack) {
        if(stack.getItem() instanceof ItemHazmatSuitArmor item) {
            return getShieldingByArmor(item.type);
        }
        return 0;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return material.getEnchantmentValue() > 0 && super.isEnchantable(stack);
    }

    @NothingNullByDefault
    protected static class HazmatMaterial extends BaseSpecialArmorMaterial {

        @Override
        public String getName() {
            return Mekanism.MODID + ":hazmat";
        }
    }
}
