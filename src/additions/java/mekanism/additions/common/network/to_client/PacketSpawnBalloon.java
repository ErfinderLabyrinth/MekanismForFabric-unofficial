package mekanism.additions.common.network.to_client;

import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.entity.EntityBalloon;
import mekanism.api.text.EnumColor;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.util.NetworkUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class PacketSpawnBalloon implements IMekanismPacket {
    public static final PacketType<PacketSpawnBalloon> TYPE = PacketType.create(MekanismAdditions.rl("spawn_balloon"), PacketSpawnBalloon::new);

    private final int id;
    private final Vec3 position;
    private final EnumColor color;
    private @Nullable BlockPos latched;
    private @Nullable Integer latchedEntity;

    public PacketSpawnBalloon(EntityBalloon balloon) {
        id = balloon.getId();
        position = balloon.position();
        color = balloon.getColor();
        latched = balloon.latched;
        if(balloon.latchedEntity != null) {
            latchedEntity = balloon.latchedEntity.getId();
        }
    }

    public PacketSpawnBalloon(FriendlyByteBuf data) {
        id = data.readVarInt();
        position = NetworkUtil.readVector3d(data);
        color = data.readEnum(EnumColor.class);
        byte type = data.readByte();
        if (type == 1) {
            latched = data.readBlockPos();
        } else if (type == 2) {
            latchedEntity = data.readVarInt();
        } else {
            latched = null;
        }
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        EntityBalloon balloon = EntityBalloon.create(player.level(), position.x, position.y, position.z, color);
        if(balloon == null) {
            return;
        }
        balloon.syncPacketPositionCodec(position.x, position.y, position.z);
        balloon.setId(id);
        balloon.latched = latched;
        if(latchedEntity != null) {
            Entity entity = player.level().getEntity(latchedEntity);
            if(entity instanceof LivingEntity livingEntity) {
                balloon.latchedEntity = livingEntity;
            }
        }
        if(player.level() instanceof ClientLevel clientLevel) {
            clientLevel.putNonPlayerEntity(id, balloon);
        }
        player.level().addFreshEntity(balloon);
    }

    @Override
    public void encode(FriendlyByteBuf data) {
        data.writeVarInt(id);
        NetworkUtil.writeVector3d(data, position);
        data.writeEnum(color);
        if (latched != null) {
            data.writeByte((byte) 1);
            data.writeBlockPos(latched);
        } else if (latchedEntity != null) {
            data.writeByte((byte) 2);
            data.writeVarInt(latchedEntity);
        } else {
            data.writeByte((byte) 0);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
