package mekanism.tools.common.item;

import mekanism.tools.common.IHasRepairType;
import mekanism.tools.common.item.tier.MekanismTiers;
import mekanism.tools.common.util.ToolsUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemMekanismSword extends SwordItem implements IHasRepairType {

    private final MekanismTiers material;

    public ItemMekanismSword(MekanismTiers material, Item.Properties properties) {
        super(material, (int) material.getSwordDamage(), material.getSwordAtkSpeed(), properties
                .durability(material.getUses())
        );
        this.material = material;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        ToolsUtils.addDurability(tooltip, stack);
    }

    @Override
    public float getDamage() {
        return material.getSwordDamage() + getTier().getAttackDamageBonus();
    }

    @NotNull
    @Override
    public Ingredient getRepairMaterial() {
        return getTier().getRepairIngredient();
    }

    @Override
    public boolean canBeDepleted() {
        return getTier().getUses() > 0;
    }
}