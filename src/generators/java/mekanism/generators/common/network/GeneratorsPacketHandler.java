package mekanism.generators.common.network;

import mekanism.common.network.BasePacketHandler;
import mekanism.generators.common.MekanismGenerators;
import mekanism.generators.common.network.to_server.PacketGeneratorsGuiButtonPress;
import mekanism.generators.common.network.to_server.PacketGeneratorsGuiInteract;

public class GeneratorsPacketHandler extends BasePacketHandler {
    @Override
    public void initialize() {
        //Client to server messages
        registerClientToServer(PacketGeneratorsGuiButtonPress.TYPE);
        registerClientToServer(PacketGeneratorsGuiInteract.TYPE);
        //Server to client messages
    }
}