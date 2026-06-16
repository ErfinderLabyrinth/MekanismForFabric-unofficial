package mekanism.common.item;

import mekanism.api.IAlloyInteraction;
import mekanism.api.tier.AlloyTier;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class ItemAlloy extends Item {

    private final AlloyTier tier;

    public ItemAlloy(AlloyTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && MekanismConfig.COMMON.general.transmitterAlloyUpgrade) {
            Level world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockEntity tile = WorldUtils.getTileEntity(world, pos);
            if (tile instanceof IAlloyInteraction iAlloyInteraction) {
                if (!world.isClientSide) {
                    iAlloyInteraction.onAlloyInteraction(player, context.getItemInHand(), tier);
                }
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    public AlloyTier getTier() {
        return tier;
    }
}