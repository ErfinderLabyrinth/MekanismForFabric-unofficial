package mekanism.common.network.to_server;

import mekanism.api.MekanismAPI;
import mekanism.common.Mekanism;
import mekanism.common.network.IMekanismPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PacketKey implements IMekanismPacket {
    public static final PacketType<PacketKey> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "key"), PacketKey::decode);

    private final int key;
    private final boolean add;

    public PacketKey(int key, boolean add) {
        this.key = key;
        this.add = add;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (player != null) {
            if (add) {
                Mekanism.keyMap.add(player.getUUID(), key);
            } else {
                Mekanism.keyMap.remove(player.getUUID(), key);
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(key);
        buffer.writeBoolean(add);
    }

    public static PacketKey decode(FriendlyByteBuf buffer) {
        return new PacketKey(buffer.readVarInt(), buffer.readBoolean());
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}