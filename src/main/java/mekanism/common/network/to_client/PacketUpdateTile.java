package mekanism.common.network.to_client;

import mekanism.api.MekanismAPI;
import mekanism.common.Mekanism;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.tile.base.TileEntityUpdateable;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PacketUpdateTile implements IMekanismPacket {
    public static final PacketType<PacketUpdateTile> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "update_tile"), PacketUpdateTile::decode);

    private final CompoundTag updateTag;
    private final BlockPos pos;

    public PacketUpdateTile(TileEntityUpdateable tile) {
        this(tile.getBlockPos(), tile.getReducedUpdateTag());
    }

    private PacketUpdateTile(BlockPos pos, CompoundTag updateTag) {
        this.pos = pos;
        this.updateTag = updateTag;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        ClientLevel world = Minecraft.getInstance().level;
        //Only handle the update packet if the block is currently loaded
        if (WorldUtils.isBlockLoaded(world, pos)) {
            TileEntityUpdateable tile = WorldUtils.getTileEntity(TileEntityUpdateable.class, world, pos, true);
            if (tile == null) {
                Mekanism.logger.warn("Update tile packet received for position: {} in world: {}, but no valid tile was found.", pos,
                      world.dimension().location());
            } else {
                tile.handleUpdateTag(updateTag);
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeNbt(updateTag);
    }

    public static PacketUpdateTile decode(FriendlyByteBuf buffer) {
        return new PacketUpdateTile(buffer.readBlockPos(), buffer.readNbt());
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}