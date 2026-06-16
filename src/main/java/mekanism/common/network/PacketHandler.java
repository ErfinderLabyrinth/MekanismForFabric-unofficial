package mekanism.common.network;

import mekanism.common.network.to_client.*;
import mekanism.common.network.to_client.container.PacketUpdateContainer;
import mekanism.common.network.to_server.*;

public class PacketHandler extends BasePacketHandler {

//    private final SimpleChannel netHandler = createChannel(Mekanism.rl(Mekanism.MODID), Mekanism.instance.versionNumber);
//
//    @Override
//    protected SimpleChannel getChannel() {
//        return netHandler;
//    }

    @Override
    public void initialize() {
        //Client to server messages
        registerClientToServer(PacketAddTrusted.TYPE);
        registerClientToServer(PacketConfigurationUpdate.TYPE);
        registerClientToServer(PacketDropperUse.TYPE);
        registerClientToServer(PacketEditFilter.TYPE);
        registerClientToServer(PacketGearStateUpdate.TYPE);
        registerClientToServer(PacketGuiButtonPress.TYPE);
        registerClientToServer(PacketGuiInteract.TYPE);
        registerClientToServer(PacketGuiItemDataRequest.TYPE);
        registerClientToServer(PacketGuiSetEnergy.TYPE);
        registerClientToServer(PacketGuiSetFrequency.TYPE);
        registerClientToServer(PacketGuiSetFrequencyColor.TYPE);
        registerClientToServer(PacketKey.TYPE);
        registerClientToServer(PacketModeChange.TYPE);
        //registerClientToServer(PacketModeChangeCurios.class, PacketModeChangeCurios::decode);
        registerClientToServer(PacketNewFilter.TYPE);
        registerClientToServer(PacketOpenGui.TYPE);
        registerClientToServer(PacketPortableTeleporterTeleport.TYPE);
        registerClientToServer(PacketQIOClearCraftingWindow.TYPE);
        registerClientToServer(PacketQIOFillCraftingWindow.TYPE);
        registerClientToServer(PacketQIOItemViewerSlotInteract.TYPE);
        registerClientToServer(PacketRadialModeChange.TYPE);
        registerClientToServer(PacketRemoveModule.TYPE);
        registerClientToServer(PacketRobit.TYPE);
        registerClientToServer(PacketSecurityMode.TYPE);
        registerClientToServer(PacketUpdateModuleSettings.TYPE);
        registerClientToServer(PacketWindowSelect.TYPE);
    }
}