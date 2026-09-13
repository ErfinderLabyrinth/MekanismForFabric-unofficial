package mekanism.additions.client.network;

import mekanism.additions.common.network.to_client.PacketSpawnBalloon;
import mekanism.client.network.ClientBasePacketHandler;

public class AdditionsClientPacketHandler extends ClientBasePacketHandler {
    @Override
    public void initialize() {
        registerServerToClient(PacketSpawnBalloon.TYPE);
    }
}
