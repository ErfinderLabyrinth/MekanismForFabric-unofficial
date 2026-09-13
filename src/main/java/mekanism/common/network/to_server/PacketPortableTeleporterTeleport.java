package mekanism.common.network.to_server;

import mekanism.api.Coord4D;
import mekanism.api.MekanismAPI;
import mekanism.common.Mekanism;
import mekanism.common.content.teleporter.TeleporterFrequency;
import mekanism.common.item.ItemPortableTeleporter;
import mekanism.common.lib.frequency.Frequency.FrequencyIdentity;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.network.to_client.PacketPortalFX;
import mekanism.common.tile.TileEntityTeleporter;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import team.reborn.energy.api.EnergyStorage;

public class PacketPortableTeleporterTeleport implements IMekanismPacket {
    public static final PacketType<PacketPortableTeleporterTeleport> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "portable_teleporter_teleport"), PacketPortableTeleporterTeleport::decode);

    private final FrequencyIdentity identity;
    private final InteractionHand currentHand;

    public PacketPortableTeleporterTeleport(InteractionHand hand, FrequencyIdentity identity) {
        currentHand = hand;
        this.identity = identity;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        ItemStack stack = player.getItemInHand(currentHand);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemPortableTeleporter) {
            //Note: We make use of the player's own UUID, given they shouldn't be allowed to teleport to a private frequency of another player
            TeleporterFrequency found = FrequencyType.TELEPORTER.getFrequency(identity, player.getUUID(), player.getServer());
            if (found == null) {
                return;
            }
            Coord4D coords = found.getClosestCoords(new Coord4D(player));
            if (coords != null) {
                Level teleWorld = serverPlayer.getServer().getLevel(coords.dimension);
                TileEntityTeleporter teleporter = WorldUtils.getTileEntity(TileEntityTeleporter.class, teleWorld, coords.getPos());
                if (teleporter != null) {
                    if (!player.isCreative()) {
                        long energyCost = TileEntityTeleporter.calculateEnergyCost(player, teleWorld, coords);
                        EnergyStorage energyStorage = ContainerItemContext.forPlayerInteraction(player, currentHand).find(EnergyStorage.ITEM);
                        try(Transaction t=Transaction.openOuter()) {
                            if (energyStorage == null || energyStorage.extract(energyCost, t) < energyCost) {
                                return;
                            }
                            t.commit();
                        }
                    }
                    //TODO: Figure out what this try catch is meant to be catching as I don't see much of a reason for it to exist
                    try {
                        teleporter.didTeleport.add(player.getUUID());
                        teleporter.teleDelay = 5;
                        serverPlayer.connection.aboveGroundTickCount = 0;
                        player.closeContainer();
                        Mekanism.packetHandler().sendToAllTracking(new PacketPortalFX(player.blockPosition()), player.level(), coords.getPos());
                        if (player.isPassenger()) {
                            player.stopRiding();
                        }
                        double oldX = player.getX();
                        double oldY = player.getY();
                        double oldZ = player.getZ();
                        Level oldWorld = player.level();
                        BlockPos teleporterTargetPos = teleporter.getTeleporterTargetPos();
                        TileEntityTeleporter.teleportEntityTo(player, teleWorld, teleporterTargetPos);
                        TileEntityTeleporter.alignPlayer(serverPlayer, teleporterTargetPos, teleporter);
                        if (player.level() != oldWorld || player.distanceToSqr(oldX, oldY, oldZ) >= 25) {
                            //If the player teleported over 5 blocks, play the sound at both the destination and the source
                            oldWorld.playSound(null, oldX, oldY, oldZ, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                        }
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                        teleporter.sendTeleportParticles();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(currentHand);
        FrequencyType.TELEPORTER.getIdentitySerializer().write(buffer, identity);
    }

    public static PacketPortableTeleporterTeleport decode(FriendlyByteBuf buffer) {
        return new PacketPortableTeleporterTeleport(buffer.readEnum(InteractionHand.class), FrequencyType.TELEPORTER.getIdentitySerializer().read(buffer));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}