package mekanism.client.network;

import mekanism.common.network.IMekanismPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketType;

public abstract class ClientBasePacketHandler {
    public abstract void initialize();

    protected <MSG extends IMekanismPacket> void registerServerToClient(PacketType<MSG> type) {
        ClientPlayNetworking.registerGlobalReceiver(type, (packet, player, responseSender) -> {if(packet != null) packet.handle(player, responseSender);});
    }

    /**
     * Send this message to the server.
     *
     * @param message - the message to send
     */
    public <MSG extends IMekanismPacket> void sendToServer(MSG message) {
        ClientPlayNetworking.send(message);
    }
}
