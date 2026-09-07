package mekanism.tools.common.item;

import mekanism.common.mixinhelper.PiglinNeutralizingArmor;
import mekanism.tools.common.item.tier.MekanismTiers;
import mekanism.tools.common.material.MaterialCreator;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemRefinedGlowstoneArmor extends ItemMekanismArmor implements PiglinNeutralizingArmor {

    public ItemRefinedGlowstoneArmor(MekanismTiers material, ArmorItem.Type armorType, Properties properties) {
        super(material, armorType, properties);
    }

    @Override
    public boolean makesPiglinsNeutral(@NotNull ItemStack stack, @NotNull LivingEntity wearer) {
        return true;
    }
}