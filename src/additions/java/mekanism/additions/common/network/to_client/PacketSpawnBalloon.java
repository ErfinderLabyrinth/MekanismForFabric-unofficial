package mekanism.additions.common.network.to_client;

import mekanism.additions.common.MekanismAdditions;
import mekanism.common.network.IMekanismPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class PacketSpawnBalloon implements IMekanismPacket {
    public static final PacketType<PacketSpawnBalloon> TYPE = PacketType.create(MekanismAdditions.rl("spawn_balloon"), PacketSpawnBalloon::new);

    public PacketSpawnBalloon(FriendlyByteBuf buffer) {

    }

    @Override
    public void handle(Player player, PacketSender responseSender) {

    }

    @Override
    public void encode(FriendlyByteBuf buffer) {

    }

    @Override
    public PacketType<?> getType() {
        return null;
    }
}
