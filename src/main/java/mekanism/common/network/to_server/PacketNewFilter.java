package mekanism.common.network.to_server;

import mekanism.api.MekanismAPI;
import mekanism.common.content.filter.BaseFilter;
import mekanism.common.content.filter.IFilter;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.tile.interfaces.ITileFilterHolder;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PacketNewFilter implements IMekanismPacket {
    public static final PacketType<PacketNewFilter> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "new_filter"), PacketNewFilter::decode);

    private final BlockPos pos;
    private final IFilter<?> filter;

    public PacketNewFilter(BlockPos pos, IFilter<?> filter) {
        this.pos = pos;
        this.filter = filter;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (player != null) {
            BlockEntity blockEntity = WorldUtils.getTileEntity(player.level(), pos);
            if (blockEntity instanceof ITileFilterHolder<?> filterHolder) {
                filterHolder.getFilterManager().tryAddFilter(filter, true);
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        filter.write(buffer);
    }

    public static PacketNewFilter decode(FriendlyByteBuf buffer) {
        return new PacketNewFilter(buffer.readBlockPos(), BaseFilter.readFromPacket(buffer));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}