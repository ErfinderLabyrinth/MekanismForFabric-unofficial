package mekanism.common.network.to_server;

import mekanism.api.MekanismAPI;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.inventory.container.QIOItemViewerContainer;
import mekanism.common.network.IMekanismPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PacketGuiItemDataRequest implements IMekanismPacket {
    public static final PacketType<PacketGuiItemDataRequest> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "gui_item_data_request"), PacketGuiItemDataRequest::decode);

    private final Type type;

    private PacketGuiItemDataRequest(Type type) {
        this.type = type;
    }

    public static PacketGuiItemDataRequest qioItemViewer() {
        return new PacketGuiItemDataRequest(Type.QIO_ITEM_VIEWER);
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (type == Type.QIO_ITEM_VIEWER) {
                if (player.containerMenu instanceof QIOItemViewerContainer container) {
                    QIOFrequency freq = container.getFrequency();
                    if (!player.level().isClientSide() && freq != null) {
                        freq.openItemViewer(serverPlayer);
                    }
                }
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(type);
    }

    public static PacketGuiItemDataRequest decode(FriendlyByteBuf buffer) {
        return new PacketGuiItemDataRequest(buffer.readEnum(Type.class));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    private enum Type {
        QIO_ITEM_VIEWER
    }
}
