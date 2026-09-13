package mekanism.common.network.to_server;

import mekanism.api.MekanismAPI;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.SelectedWindowData;
import mekanism.common.inventory.container.SelectedWindowData.WindowType;
import mekanism.common.network.IMekanismPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class PacketWindowSelect implements IMekanismPacket {
    public static final PacketType<PacketWindowSelect> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "window_select"), PacketWindowSelect::decode);

    @Nullable
    private final SelectedWindowData selectedWindow;

    public PacketWindowSelect(@Nullable SelectedWindowData selectedWindow) {
        this.selectedWindow = selectedWindow;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (player != null && player.containerMenu instanceof MekanismContainer container) {
            container.setSelectedWindow(player.getUUID(), selectedWindow);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        //We skip using a boolean to write if it is null or not and just write our byte before our enum so that we can
        // use -1 as a marker for invalid
        if (selectedWindow == null) {
            buffer.writeByte(-1);
        } else {
            buffer.writeByte(selectedWindow.extraData);
            buffer.writeEnum(selectedWindow.type);
        }
    }

    public static PacketWindowSelect decode(FriendlyByteBuf buffer) {
        byte extraData = buffer.readByte();
        if (extraData == -1) {
            return new PacketWindowSelect(null);
        }
        WindowType windowType = buffer.readEnum(WindowType.class);
        return new PacketWindowSelect(windowType == WindowType.UNSPECIFIED ? SelectedWindowData.UNSPECIFIED : new SelectedWindowData(windowType, extraData));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}