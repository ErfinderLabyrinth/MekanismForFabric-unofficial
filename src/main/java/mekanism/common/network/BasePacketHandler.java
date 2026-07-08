package mekanism.common.network;

import mekanism.api.functions.TriConsumer;
import mekanism.common.Mekanism;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.math.Range3D;
import mekanism.common.lib.transmitter.DynamicBufferedNetwork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public abstract class BasePacketHandler {

    public abstract void initialize();

    protected <MSG extends IMekanismPacket> void registerClientToServer(PacketType<MSG> type) {
        ServerPlayNetworking.registerGlobalReceiver(type, (packet, player, responseSender) -> {if(packet != null) packet.handle(player, responseSender);});
    }

//    private <MSG extends IMekanismPacket> void registerMessage(PacketType<MSG> type, ServerPlayNetworking.PlayPacketHandler<MSG> handler, NetworkDirection networkDirection) {
//        if (networkDirection == NetworkDirection.CLIENT_TO_SERVER) {
//            ServerPlayNetworking.registerGlobalReceiver(type, handler);
//        }
//        //getChannel().registerMessage(index++, type, IMekanismPacket::encode, decoder, IMekanismPacket::handle, Optional.of(networkDirection));
//    }

//    private <MSG extends IMekanismPacket> void registerMessage(ResourceLocation rl, Function<FriendlyByteBuf, MSG> decoder, NetworkDirection networkDirection) {
//        if (networkDirection == NetworkDirection.CLIENT_TO_SERVER) {
//            ServerPlayNetworking.registerGlobalReceiver(rl, (server, player, handler, buf,  responseSender) -> {
//                MSG packet = decoder.apply(buf);
//                if (packet != null) {
//                    packet.handle(server, player, responseSender);
//                }
//            });
//        }
//        //getChannel().registerMessage(index++, type, IMekanismPacket::encode, decoder, IMekanismPacket::handle, Optional.of(networkDirection));
//    }

    /**
     * Send this message to the specified player.
     *
     * @param message - the message to send
     * @param player  - the player to send it to
     */
    public <MSG extends IMekanismPacket> void sendTo(MSG message, ServerPlayer player) {
        //Validate it is not a fake player, even though none of our code should call this with a fake player
        if (!(player instanceof FakePlayer)) {
            ServerPlayNetworking.send(player, message);
//            getChannel().send(PacketDistributor.PLAYER.with(() -> player), message);
        }
    }

    /**
     * Send this message to everyone connected to the server.
     *
     * @param message - message to send
     */
    public <MSG extends IMekanismPacket> void sendToAll(MSG message, MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendTo(message, player);
        }
    }

    /**
     * Send this message to everyone connected to the server if the server has loaded.
     *
     * @param message - message to send
     *
     * @apiNote This is useful for reload listeners
     */
//    public <MSG extends IMekanismPacket> void sendToAllIfLoaded(MSG message) {
//        if (ServerLifecycleHooks.getCurrentServer() != null) {
//            //If the server has loaded, send to all players
//            sendToAll(message);
//        }
//    }

    /**
     * Send this message to everyone within the supplied dimension.
     *
     * @param message   - the message to send
     * @param dimension - the dimension to target
     */
    public <MSG extends IMekanismPacket> void sendToDimension(MSG message, ResourceKey<Level> dimension, MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (server.getLevel(dimension) == player.level()) {
                sendTo(message, player);
            }
        }
        //getChannel().send(PacketDistributor.DIMENSION.with(() -> dimension), message);
    }

    public <MSG extends IMekanismPacket> void sendToAllTracking(MSG message, Entity entity) {
        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            sendTo(message, player);
        }
    }

    public <MSG extends IMekanismPacket> void sendToAllTrackingAndSelf(MSG message, Entity entity) {
        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            sendTo(message, player);
        }
        if (entity instanceof ServerPlayer player) {
            sendTo(message, player);
        }
    }

    public <MSG extends IMekanismPacket> void sendToAllTracking(MSG message, BlockEntity tile) {
        sendToAllTracking(message, tile.getLevel(), tile.getBlockPos());
    }

    public <MSG extends IMekanismPacket> void sendToAllTracking(MSG message, Level world, BlockPos pos) {
        if (world instanceof ServerLevel level) {
            //If we have a ServerWorld just directly figure out the ChunkPos to not require looking up the chunk
            // This provides a decent performance boost over using the packet distributor
            level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).forEach(p -> sendTo(message, p));
        }
    }

    public <MSG extends IMekanismPacket> void sendToReceivers(MSG message, DynamicBufferedNetwork<?, ?, ?, ?> network, MinecraftServer server) {
        //TODO: Figure out why we have a try catch and remove the need for it
        try {
            //TODO: Create a method in DynamicNetwork to get all players that are "tracking" the network
            // Also evaluate moving various network packet things over to using this at that point
            if (server != null) {
                Range3D range = network.getPacketRange();
                PlayerList playerList = server.getPlayerList();
                //Ignore height for partial Cubic chunks support as range comparison gets used ignoring player height normally anyway
                int radius = playerList.getViewDistance() * 16;
                for (ServerPlayer player : playerList.getPlayers()) {
                    if (range.dimension() == player.level().dimension()) {
                        BlockPos playerPosition = player.blockPosition();
                        int playerX = playerPosition.getX();
                        int playerZ = playerPosition.getZ();
                        //playerX/Z + radius is the max, so to stay in line with how it was before, it has an extra + 1 added to it
                        if (playerX + radius + 1.99999 > range.xMin() && range.xMax() + 0.99999 > playerX - radius &&
                            playerZ + radius + 1.99999 > range.zMin() && range.zMax() + 0.99999 > playerZ - radius) {
                            sendTo(message, player);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}