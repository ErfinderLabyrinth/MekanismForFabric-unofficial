package mekanism.common.network.to_client.container;

import mekanism.api.MekanismAPI;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.network.to_client.container.property.PropertyData;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class PacketUpdateContainer implements IMekanismPacket {
    public static final PacketType<PacketUpdateContainer> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "update_container"), PacketUpdateContainer::decode);

    //Note: windowId gets transferred over the network as an unsigned byte
    private final short windowId;
    private final List<PropertyData> data;

    public PacketUpdateContainer(short windowId, List<PropertyData> data) {
        this.windowId = windowId;
        this.data = data;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        //Ensure that the container is one of ours and that the window id is the same as we expect it to be
        if (player != null && player.containerMenu instanceof MekanismContainer container && container.containerId == windowId) {
            //If so then handle the packet
            data.forEach(data -> data.handleWindowProperty(container));
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeByte(windowId);
        buffer.writeCollection(data, (buf, data) -> data.writeToPacket(buf));
    }

    public static PacketUpdateContainer decode(FriendlyByteBuf buffer) {
        short windowId = buffer.readUnsignedByte();
        int size = buffer.readVarInt();
        List<PropertyData> data = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            PropertyData propertyData = PropertyData.fromBuffer(buffer);
            if (propertyData != null) {
                data.add(propertyData);
            }
        }
        return new PacketUpdateContainer(windowId, data);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}