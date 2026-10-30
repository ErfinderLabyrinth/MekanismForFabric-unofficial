package mekanism.common.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
//import net.minecraftforge.network.NetworkEvent;

public interface IMekanismPacket extends FabricPacket {


    void handle(Player player, PacketSender responseSender);

    void encode(FriendlyByteBuf buffer);

//    static <PACKET extends IMekanismPacket> void handle(PACKET message, Supplier<NetworkEvent.Context> ctx) {
//        if (message != null) {
//            //Message should never be null unless something went horribly wrong decoding.
//            // In which case we don't want to try enqueuing handling it, or set the packet as handled
//            NetworkEvent.Context context = ctx.get();
//            context.enqueueWork(() -> message.handle(context));
//            context.setPacketHandled(true);
//        }
//    }


    @Override
    default void write(FriendlyByteBuf buf) {
        encode(buf);
    }
}