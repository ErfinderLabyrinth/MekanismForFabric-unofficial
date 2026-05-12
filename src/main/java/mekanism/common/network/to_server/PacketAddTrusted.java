package mekanism.common.network.to_server;

import mekanism.api.MekanismAPI;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.tile.TileEntitySecurityDesk;
import mekanism.common.util.WorldUtils;
import mekanism.common.util.text.InputValidator;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PacketAddTrusted implements IMekanismPacket {
    public static final PacketType<PacketAddTrusted> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "add_trust"), PacketAddTrusted::decode);

    //Constant to make it more clear what is going on and make it easier to change in case Mojang ever ups the max name length
    public static final int MAX_NAME_LENGTH = 16;

    private final BlockPos tilePosition;
    private final String name;

    public PacketAddTrusted(BlockPos tilePosition, String name) {
        this.tilePosition = tilePosition;
        this.name = name;
    }

    public static boolean validateNameLength(int length) {
        return length >= 3 && length <= MAX_NAME_LENGTH;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (!name.isEmpty() && InputValidator.test(name, InputValidator.USERNAME)) {
            if (player != null) {
                TileEntitySecurityDesk tile = WorldUtils.getTileEntity(TileEntitySecurityDesk.class, player.level(), tilePosition);
                if (tile != null) {
                    tile.addTrusted(name);
                }
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(tilePosition);
        buffer.writeUtf(name, MAX_NAME_LENGTH);
    }

    public static PacketAddTrusted decode(FriendlyByteBuf buffer) {
        return new PacketAddTrusted(buffer.readBlockPos(), buffer.readUtf(MAX_NAME_LENGTH));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}