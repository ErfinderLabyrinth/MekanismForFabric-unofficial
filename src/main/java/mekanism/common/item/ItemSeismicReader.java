package mekanism.common.item;

import mekanism.api.text.EnumColor;
import mekanism.client.key.MekKeyHandler;
import mekanism.client.key.MekanismKeyHandler;
import mekanism.common.MekanismLang;
import mekanism.common.advancements.MekanismCriteriaTriggers;
import mekanism.common.config.MekanismConfig;
import mekanism.common.registries.MekanismContainerTypes;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public class ItemSeismicReader extends ItemEnergized {

    public ItemSeismicReader(Properties properties) {
        super(() -> MekanismConfig.COMMON.gear.seismicReaderChargeRate, () -> MekanismConfig.COMMON.gear.seismicReaderMaxEnergy, properties.rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (MekKeyHandler.isKeyPressed(MekanismKeyHandler.descriptionKey)) {
            tooltip.add(MekanismLang.DESCRIPTION_SEISMIC_READER.translate());
        } else if (MekKeyHandler.isKeyPressed(MekanismKeyHandler.detailsKey)) {
            super.appendHoverText(stack, world, tooltip, flag);
        } else {
            tooltip.add(MekanismLang.HOLD_FOR_DETAILS.translateColored(EnumColor.GRAY, EnumColor.INDIGO, MekanismKeyHandler.detailsKey.getTranslatedKeyMessage()));
            tooltip.add(MekanismLang.HOLD_FOR_DESCRIPTION.translateColored(EnumColor.GRAY, EnumColor.AQUA, MekanismKeyHandler.descriptionKey.getTranslatedKeyMessage()));
        }
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        if (!WorldUtils.isChunkVibrated(new ChunkPos(player.blockPosition()), player.level())) {
            player.sendSystemMessage(MekanismUtils.logFormat(EnumColor.RED, MekanismLang.NO_VIBRATIONS));
        } else {
            if (!player.isCreative()) {
                EnergyStorage energyContainer = ContainerItemContext.forPlayerInteraction(player, hand).find(EnergyStorage.ITEM);
                long energyUsage = MekanismConfig.COMMON.gear.seismicReaderEnergyUsage;
                try(Transaction t=Transaction.openOuter()) {
                    if (energyContainer == null || energyContainer.extract(energyUsage, t) < energyUsage) {
                        player.sendSystemMessage(MekanismUtils.logFormat(EnumColor.RED, MekanismLang.NEEDS_ENERGY));
                        return InteractionResultHolder.consume(stack);
                    }
                    t.commit();
                }
            }
            ServerPlayer serverPlayer = (ServerPlayer) player;
            MekanismCriteriaTriggers.VIEW_VIBRATIONS.trigger(serverPlayer);
            MekanismContainerTypes.SEISMIC_READER.tryOpenGui(serverPlayer, hand, stack);
        }
        return InteractionResultHolder.consume(stack);
    }
}