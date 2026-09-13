package mekanism.common.network.to_client;

import mekanism.api.MekanismAPI;
import mekanism.common.network.IMekanismPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class PacketLaserHitBlock implements IMekanismPacket {
    public static final PacketType<PacketLaserHitBlock> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "laser_hit_block"), PacketLaserHitBlock::decode);

    private final BlockHitResult result;

    public PacketLaserHitBlock(BlockHitResult result) {
        this.result = result;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (Minecraft.getInstance().level != null) {
            Minecraft.getInstance().particleEngine.crack(result.getBlockPos(), result.getDirection());
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockHitResult(result);
    }

    public static PacketLaserHitBlock decode(FriendlyByteBuf buffer) {
        return new PacketLaserHitBlock(buffer.readBlockHitResult());
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}