package mekanism.common.network.to_client;

import mekanism.api.MekanismAPI;
import mekanism.common.Mekanism;
import mekanism.common.network.IMekanismPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class PacketResetPlayerClient implements IMekanismPacket {
    public static final PacketType<PacketResetPlayerClient> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "reset_player_client"), PacketResetPlayerClient::decode);

    private final UUID uuid;

    public PacketResetPlayerClient(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        Mekanism.playerState.clearPlayer(uuid, true, null);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
    }

    public static PacketResetPlayerClient decode(FriendlyByteBuf buffer) {
        return new PacketResetPlayerClient(buffer.readUUID());
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
