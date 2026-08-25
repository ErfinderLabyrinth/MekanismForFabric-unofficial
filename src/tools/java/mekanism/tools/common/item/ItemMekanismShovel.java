package mekanism.tools.common.item;

import mekanism.tools.common.IHasRepairType;
import mekanism.tools.common.material.MaterialCreator;
import mekanism.tools.common.util.ToolsUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemMekanismShovel extends ShovelItem implements IHasRepairType {

    private final MaterialCreator material;

    public ItemMekanismShovel(MaterialCreator material, Item.Properties properties) {
        super(material, material.getShovelDamage(), material.getShovelAtkSpeed(), properties.durability(material.getUses()));
        this.material = material;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        ToolsUtils.addDurability(tooltip, stack);
    }

    @Override
    public float getAttackDamage() {
        return material.getShovelDamage() + getTier().getAttackDamageBonus();
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        return super.getDestroySpeed(stack, state) == 1 ? 1 : getTier().getSpeed();
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