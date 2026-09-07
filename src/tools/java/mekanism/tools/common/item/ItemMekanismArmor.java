package mekanism.tools.common.item;

import mekanism.tools.common.IHasRepairType;
import mekanism.tools.common.item.tier.MekanismTiers;
import mekanism.tools.common.material.MaterialCreator;
import mekanism.tools.common.util.ToolsUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemMekanismArmor extends ArmorItem implements IHasRepairType {

    private final MaterialCreator material;

    public ItemMekanismArmor(MekanismTiers material, ArmorItem.Type armorType, Item.Properties properties) {
        super(material, armorType, properties.durability(material.getDurabilityForType(armorType)));
        this.material = material;
        int armorConfig = switch (armorType) {
            case BOOTS -> material.getBootArmor();
            case LEGGINGS -> material.getLeggingArmor();
            case CHESTPLATE -> material.getChestplateArmor();
            case HELMET -> material.getHelmetArmor();
        };
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        ToolsUtils.addDurability(tooltip, stack);
    }

    @NotNull
    @Override
    public Ingredient getRepairMaterial() {
        return getMaterial().getRepairIngredient();
    }

    @Override
    public int getDefense() {
        return getMaterial().getDefenseForType(getType());
    }

    @Override
    public float getToughness() {
        return getMaterial().getToughness();
    }

    public float getKnockbackResistance() {
        return getMaterial().getKnockbackResistance();
    }

    @Override
    public boolean canBeDepleted() {
        return material.getDurabilityForType(getType()) > 0;
    }
}