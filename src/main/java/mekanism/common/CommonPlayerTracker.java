package mekanism.common;

import com.mojang.datafixers.util.Pair;
import mekanism.api.radiation.capability.IRadiationEntity;
import mekanism.api.text.EnumColor;
import mekanism.common.advancements.MekanismCriteriaTriggers;
import mekanism.common.block.BlockBounding;
import mekanism.common.block.BlockCardboardBox;
import mekanism.common.block.BlockMekanism;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.lib.radiation.capability.DefaultRadiationEntity;
import mekanism.common.network.to_client.PacketPlayerData;
import mekanism.common.network.to_client.PacketRadiationData;
import mekanism.common.network.to_client.PacketResetPlayerClient;
import mekanism.common.network.to_client.PacketSecurityUpdate;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tags.MekanismTags.Items;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class CommonPlayerTracker {

    private static final Component ALPHA_WARNING = MekanismLang.LOG_FORMAT.translateColored(EnumColor.RED, MekanismLang.MEKANISM, EnumColor.GRAY,
          MekanismLang.ALPHA_WARNING.translate(EnumColor.INDIGO, ChatFormatting.UNDERLINE, new ClickEvent(Action.OPEN_URL,
                "https://github.com/mekanism/Mekanism#alpha-status"), MekanismLang.ALPHA_WARNING_HERE));

    public CommonPlayerTracker() {
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerLoginEvent);
        ServerPlayConnectionEvents.DISCONNECT.register(this::onPlayerLogoutEvent);
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(this::onPlayerDimChangedEvent);
        EntityTrackingEvents.START_TRACKING.register(this::onPlayerStartTrackingEvent);
        ServerPlayerEvents.AFTER_RESPAWN.register(this::respawnEvent);
    }

    public void onPlayerLoginEvent(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        Player player = handler.getPlayer();
        if (!player.level().isClientSide) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            Mekanism.packetHandler().sendTo(new PacketSecurityUpdate(server), serverPlayer);
            serverPlayer.sendSystemMessage(ALPHA_WARNING);
            MekanismCriteriaTriggers.LOGGED_IN.trigger(serverPlayer);
        }
    }

    public void onPlayerLogoutEvent(ServerGamePacketListenerImpl handler, MinecraftServer server) {
        Player player = handler.getPlayer();
        Mekanism.playerState.clearPlayer(player.getUUID(), false, server);
        Mekanism.playerState.clearPlayerServerSideOnly(player.getUUID());
    }

    public void onPlayerDimChangedEvent(ServerPlayer player, ServerLevel origin, ServerLevel destination) {
        Mekanism.playerState.clearPlayer(player.getUUID(), false, player.getServer());
        Mekanism.playerState.reapplyServerSideOnly(player);
        IRadiationEntity radiation = player.getAttachedOrCreate(DefaultRadiationEntity.ATTACHMENT_TYPE);
        if(radiation != null) {
            Mekanism.packetHandler().sendTo(PacketRadiationData.createPlayer(radiation.getRadiation()), player);
        }
        RadiationManager.get().updateClientRadiation(player);
    }

    public void onPlayerStartTrackingEvent(Entity trackedEntity, ServerPlayer player) {
        if (trackedEntity instanceof Player trackedPlayer) {
            Mekanism.packetHandler().sendTo(new PacketPlayerData(trackedPlayer.getUUID()), player);
        }
    }

    public void respawnEvent(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean fromEnd) {
        DefaultRadiationEntity c = oldPlayer.getAttachedOrCreate(DefaultRadiationEntity.ATTACHMENT_TYPE);
        if (!fromEnd) {
            //If the player is returning from the end don't reset radiation
            c.set(RadiationManager.BASELINE);
        }
        Mekanism.packetHandler().sendTo(PacketRadiationData.createPlayer(c.getRadiation()), oldPlayer);
        RadiationManager.get().updateClientRadiation(oldPlayer);
        Mekanism.packetHandler().sendToAll(new PacketResetPlayerClient(newPlayer.getUUID()), newPlayer.getServer());
    }

    /**
     * If the player is sneaking and the dest block is a cardboard box, ensure onBlockActivated is called, and that the item use is not.
     */
    public Pair<Boolean, Boolean> rightClickEvent(ServerPlayer player, InteractionHand hand, Level level, BlockPos pos) {
        boolean useBlock, useItem;
        ItemStack itemInHand = player.getItemInHand(hand);
        if (itemInHand.is(Items.CONFIGURATORS) && !itemInHand.is(MekanismItems.CONFIGURATOR.asItem())) {
            //it's a wrench, see if it's our block. Not the configurator, as it handles bypass correctly
            Block block = level.getBlockState(pos).getBlock();
            if (block instanceof BlockMekanism || block instanceof BlockBounding) {
                return Pair.of(true, null);//force it to use the item on the block
//                event.setUseBlock(Event.Result.ALLOW);
            }
        } else if (player.isShiftKeyDown() && level.getBlockState(pos).getBlock() instanceof BlockCardboardBox) {
//            event.setUseBlock(Event.Result.ALLOW);
//            event.setUseItem(Event.Result.DENY);
            return Pair.of(true, false);
        }
        return Pair.of(null, null);
    }
}