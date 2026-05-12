package mekanism.common.item;

import mekanism.common.config.MekanismConfig;
import mekanism.common.util.MekanismUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public class ItemNutritionalPasteBucket extends BucketItem {

    public ItemNutritionalPasteBucket(Fluid fluid, Properties builder) {
        super(fluid, builder);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 32;
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (MekanismUtils.isPlayingMode(player)) {
            int needed = Math.min(20 - player.getFoodData().getFoodLevel(), (int) FluidConstants.BUCKET / MekanismConfig.general.nutritionalPasteMBPerFood);
            if (needed > 0) {
                return ItemUtils.startUsingInstantly(level, player, hand);
            }
        }
        return super.use(level, player, hand);
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (entity instanceof Player player && MekanismUtils.isPlayingMode(player)) {
            int needed = Math.min(20 - player.getFoodData().getFoodLevel(), (int) FluidConstants.BUCKET / MekanismConfig.general.nutritionalPasteMBPerFood);
            if (needed > 0) {
                if (entity instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
                    serverPlayer.awardStat(Stats.ITEM_USED.get(this));
                }
                if (!level.isClientSide) {
                    player.getFoodData().eat(needed, MekanismConfig.general.nutritionalPasteSaturation);
                }
                stack.shrink(1);
                return stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack;
            }
        }
        return super.finishUsingItem(stack, level, entity);
    }
}
