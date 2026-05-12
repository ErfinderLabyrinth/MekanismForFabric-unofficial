package mekanism.common.network.to_client;

import mekanism.api.MekanismAPI;
import mekanism.common.network.IMekanismPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PacketPortalFX implements IMekanismPacket {
    public static final PacketType<PacketPortalFX> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "portal_fx"), PacketPortalFX::decode);

    private final BlockPos pos;
    private final Direction direction;

    public PacketPortalFX(BlockPos pos) {
        this(pos, Direction.UP);
    }

    public PacketPortalFX(BlockPos pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            BlockPos secondPos = pos.relative(direction);
            for (int i = 0; i < 50; i++) {
                world.addParticle(ParticleTypes.PORTAL, pos.getX() + world.random.nextFloat(), pos.getY() + world.random.nextFloat(),
                      pos.getZ() + world.random.nextFloat(), 0.0F, 0.0F, 0.0F);
                world.addParticle(ParticleTypes.PORTAL, secondPos.getX() + world.random.nextFloat(), secondPos.getY() + world.random.nextFloat(),
                      secondPos.getZ() + world.random.nextFloat(), 0.0F, 0.0F, 0.0F);
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeEnum(direction);
    }

    public static PacketPortalFX decode(FriendlyByteBuf buffer) {
        return new PacketPortalFX(buffer.readBlockPos(), buffer.readEnum(Direction.class));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}