package mekanism.client.network;

import mekanism.common.network.to_client.*;
import mekanism.common.network.to_client.container.PacketUpdateContainer;

public class ClientPacketHandler extends ClientBasePacketHandler {
    @Override
    public void initialize() {
        //Server to client messages
        registerServerToClient(PacketLaserHitBlock.TYPE);
        registerServerToClient(PacketLightningRender.TYPE);
        registerServerToClient(PacketPlayerData.TYPE);
        registerServerToClient(PacketPortalFX.TYPE);
        registerServerToClient(PacketQIOItemViewerGuiSync.TYPE);
        registerServerToClient(PacketRadiationData.TYPE);
        registerServerToClient(PacketResetPlayerClient.TYPE);
        registerServerToClient(PacketSecurityUpdate.TYPE);
        registerServerToClient(PacketShowModeChange.TYPE);
        registerServerToClient(PacketTransmitterUpdate.TYPE);
        registerServerToClient(PacketTransporterUpdate.TYPE);
        registerServerToClient(PacketUpdateContainer.TYPE);
        registerServerToClient(PacketUpdateTile.TYPE);
    }
}
