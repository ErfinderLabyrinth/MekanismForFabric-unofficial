package mekanism.common.network.to_client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mekanism.api.MekanismAPI;
import mekanism.client.MekanismClient;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.security.SecurityData;
import mekanism.common.lib.security.SecurityFrequency;
import mekanism.common.network.BasePacketHandler;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.util.MekanismUtils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PacketSecurityUpdate implements IMekanismPacket {
    public static final PacketType<PacketSecurityUpdate> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "security_update"), PacketSecurityUpdate::decode);

    private final boolean isUpdate;
    //Sync
    @Nullable
    private MinecraftServer server;
    private SecurityData securityData;
    private String playerUsername;
    private UUID playerUUID;
    //Batch
    private Map<UUID, SecurityData> securityMap = new Object2ObjectOpenHashMap<>();
    private Map<UUID, String> uuidMap = new Object2ObjectOpenHashMap<>();

    public PacketSecurityUpdate(SecurityFrequency frequency) {
        this(frequency.getOwner());
        securityData = new SecurityData(frequency);
    }

    public PacketSecurityUpdate(UUID uuid) {
        this(true);
        playerUUID = uuid;
        playerUsername = MekanismUtils.getLastKnownUsername(uuid, null);
    }

    @Deprecated(forRemoval = true)
    public PacketSecurityUpdate(MinecraftServer server) {
        this(false);
        this.server = server;
    }

    private PacketSecurityUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (isUpdate) {
            MekanismClient.clientUUIDMap.put(playerUUID, playerUsername);
            if (securityData != null) {
                MekanismClient.clientSecurityMap.put(playerUUID, securityData);
            }
        } else {
            MekanismClient.clientSecurityMap.clear();
            MekanismClient.clientSecurityMap.putAll(securityMap);
            MekanismClient.clientUUIDMap.putAll(uuidMap);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(isUpdate);
        if (isUpdate) {
            buffer.writeUUID(playerUUID);
            buffer.writeUtf(playerUsername);
            BasePacketHandler.writeOptional(buffer, securityData, (buf, data) -> data.write(buf));
        } else {
            List<SecurityFrequency> frequencies = new ArrayList<>(FrequencyType.SECURITY.getManager(null, null).getFrequencies());
            //In theory no owner should be null but handle the case anyway just in case
            frequencies.removeIf(frequency -> frequency.getOwner() == null);
            buffer.writeCollection(frequencies, (buf, frequency) -> {
                UUID owner = frequency.getOwner();
                //We remove all null cases above
                buf.writeUUID(owner);
                new SecurityData(frequency).write(buf);
                buf.writeUtf(MekanismUtils.getLastKnownUsername(owner, server));
            });
        }
    }

    public static PacketSecurityUpdate decode(FriendlyByteBuf buffer) {
        PacketSecurityUpdate packet = new PacketSecurityUpdate(buffer.readBoolean());
        if (packet.isUpdate) {
            packet.playerUUID = buffer.readUUID();
            packet.playerUsername = BasePacketHandler.readString(buffer);
            packet.securityData = BasePacketHandler.readOptional(buffer, SecurityData::read);
        } else {
            int frequencySize = buffer.readVarInt();
            packet.securityMap = new Object2ObjectOpenHashMap<>(frequencySize);
            packet.uuidMap = new Object2ObjectOpenHashMap<>(frequencySize);
            for (int i = 0; i < frequencySize; i++) {
                UUID uuid = buffer.readUUID();
                packet.securityMap.put(uuid, SecurityData.read(buffer));
                packet.uuidMap.put(uuid, BasePacketHandler.readString(buffer));
            }
        }
        return packet;
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}