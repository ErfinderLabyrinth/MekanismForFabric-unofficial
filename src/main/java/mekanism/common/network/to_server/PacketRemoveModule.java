package mekanism.common.network.to_server;

import mekanism.api.MekanismAPI;
import mekanism.api.gear.ModuleData;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.tile.TileEntityModificationStation;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PacketRemoveModule implements IMekanismPacket {
    public static final PacketType<PacketRemoveModule> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "remove_module"), PacketRemoveModule::decode);

    private final BlockPos pos;
    private final ModuleData<?> moduleType;

    public PacketRemoveModule(BlockPos pos, ModuleData<?> type) {
        this.pos = pos;
        moduleType = type;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (player != null) {
            TileEntityModificationStation tile = WorldUtils.getTileEntity(TileEntityModificationStation.class, player.level(), pos);
            if (tile != null) {
                tile.removeModule(player, moduleType);
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeResourceLocation(MekanismAPI.moduleRegistry().getKey(moduleType));
    }

    public static PacketRemoveModule decode(FriendlyByteBuf buffer) {
        return new PacketRemoveModule(buffer.readBlockPos(), MekanismAPI.moduleRegistry().get(buffer.readResourceLocation()));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
