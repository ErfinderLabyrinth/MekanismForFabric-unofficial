package mekanism.tools.common.item;

import mekanism.tools.common.IHasRepairType;
import mekanism.tools.common.ToolsTags;
import mekanism.tools.common.item.tier.MekanismTiers;
import mekanism.tools.common.material.IPaxelMaterial;
import mekanism.tools.common.material.VanillaPaxelMaterialCreator;
import mekanism.tools.common.util.ToolsUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ItemMekanismPaxel extends AxeItem implements IHasRepairType {
    private final IPaxelMaterial material;

    public ItemMekanismPaxel(MekanismTiers material, Item.Properties properties) {
        super(material, material.getPaxelDamage(), material.getPaxelAtkSpeed(), properties.durability(material.getPaxelMaxUses()));
        this.material = material;
    }

    public ItemMekanismPaxel(VanillaPaxelMaterialCreator material, Tier tier, Item.Properties properties) {
        super(tier, material.getPaxelDamage(), material.getPaxelAtkSpeed(), properties.durability(material.getPaxelMaxUses()));
        this.material = material;
        //Don't add the material's damage as a listener as the vanilla component is not configurable
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        ToolsUtils.addDurability(tooltip, stack);
    }

    @Override
    public float getAttackDamage() {
        return material.getPaxelDamage() + getTier().getAttackDamageBonus();
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        return state.is(ToolsTags.Blocks.MINEABLE_WITH_PAXEL) ? material.getPaxelEfficiency() : 1;
    }

    /**
     * {@inheritDoc}
     *
     * Merged version of {@link AxeItem#useOn(UseOnContext)} and {@link net.minecraft.world.item.ShovelItem#useOn(UseOnContext)}
     */
    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Attempt to use the paxel as an axe
        InteractionResult axeResult = super.useOn(context);
        if (axeResult != InteractionResult.PASS) {
            return axeResult;
        }

        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState blockstate = world.getBlockState(blockpos);
        BlockState resultToSet = null;
        //We cannot strip the item that was right-clicked, so attempt to use the paxel as a shovel
        if (context.getClickedFace() == Direction.DOWN) {
            return InteractionResult.PASS;
        }
        BlockState foundResult = ShovelItem.FLATTENABLES.get(blockstate.getBlock());
        if (foundResult != null && world.isEmptyBlock(blockpos.above())) {
            //We can flatten the item as a shovel
            world.playSound(player, blockpos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            resultToSet = foundResult;
        } else if (blockstate.getBlock() instanceof CampfireBlock && blockstate.getValue(CampfireBlock.LIT)) {
            //We can use the paxel as a shovel to extinguish a campfire
            if (!world.isClientSide) {
                world.levelEvent(null, LevelEvent.SOUND_EXTINGUISH_FIRE, blockpos, 0);
            }
            CampfireBlock.dowse(player, world, blockpos, blockstate);
            resultToSet = blockstate.setValue(CampfireBlock.LIT, false);
        }
        if (resultToSet == null) {
            return InteractionResult.PASS;
        }
        if (!world.isClientSide) {
            ItemStack stack = context.getItemInHand();
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, blockpos, stack);
            }
            world.setBlock(blockpos, resultToSet, Block.UPDATE_ALL_IMMEDIATE);
            if (player != null) {
                stack.hurtAndBreak(1, player, onBroken -> onBroken.broadcastBreakEvent(context.getHand()));
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    public int getEnchantmentValue() {
        return material.getPaxelEnchantability();
    }

    @NotNull
    @Override
    public Ingredient getRepairMaterial() {
        return getTier().getRepairIngredient();
    }

    @Override
    public boolean canBeDepleted() {
        return material.getPaxelMaxUses() > 0;
    }

    // Need to override both method as DiggerItem performs two different behaviors
    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        return state.is(ToolsTags.Blocks.MINEABLE_WITH_PAXEL) && super.isCorrectToolForDrops(state);
    }
}