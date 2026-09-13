package mekanism.common.network.to_client;

import mekanism.api.MekanismAPI;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.lib.radiation.RadiationManager.LevelAndMaxMagnitude;
import mekanism.common.lib.radiation.capability.DefaultRadiationEntity;
import mekanism.common.network.IMekanismPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class PacketRadiationData implements IMekanismPacket {
    public static final PacketType<PacketRadiationData> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "radiation_data"), PacketRadiationData::decode);

    private final RadiationPacketType type;
    private final double radiation;
    private final double maxMagnitude;

    private PacketRadiationData(RadiationPacketType type, double radiation, double maxMagnitude) {
        this.type = type;
        this.radiation = radiation;
        this.maxMagnitude = maxMagnitude;
    }

    public static PacketRadiationData createEnvironmental(LevelAndMaxMagnitude levelAndMaxMagnitude) {
        return new PacketRadiationData(RadiationPacketType.ENVIRONMENTAL, levelAndMaxMagnitude.level(), levelAndMaxMagnitude.maxMagnitude());
    }

    public static PacketRadiationData createPlayer(double radiation) {
        return new PacketRadiationData(RadiationPacketType.PLAYER, radiation, 0);
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (type == RadiationPacketType.ENVIRONMENTAL) {
            RadiationManager.get().setClientEnvironmentalRadiation(radiation, maxMagnitude);
        } else if (type == RadiationPacketType.PLAYER) {
            if (player != null) {
                Optional.ofNullable(player.getAttached(DefaultRadiationEntity.ATTACHMENT_TYPE)).ifPresent(c -> c.set(radiation));
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(type);
        buffer.writeDouble(radiation);
        if (type.tracksMaxMagnitude) {
            buffer.writeDouble(maxMagnitude);
        }
    }

    public static PacketRadiationData decode(FriendlyByteBuf buffer) {
        RadiationPacketType type = buffer.readEnum(RadiationPacketType.class);
        return new PacketRadiationData(type, buffer.readDouble(), type.tracksMaxMagnitude ? buffer.readDouble() : 0);
    }

    @Override
    public PacketType<PacketRadiationData> getType() {
        return TYPE;
    }

    public enum RadiationPacketType {
        ENVIRONMENTAL(true),
        PLAYER(false);

        private final boolean tracksMaxMagnitude;

        RadiationPacketType(boolean tracksMaxMagnitude) {
            this.tracksMaxMagnitude = tracksMaxMagnitude;
        }
    }
}
