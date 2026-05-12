package mekanism.common.network.to_client;

import mekanism.api.MekanismAPI;
import mekanism.client.render.hud.MekanismStatusOverlay;
import mekanism.common.network.IMekanismPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PacketShowModeChange implements IMekanismPacket {
    public static final PacketType<PacketShowModeChange> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "show_mode_change"), PacketShowModeChange::decode);

    public static final PacketShowModeChange INSTANCE = new PacketShowModeChange();

    private PacketShowModeChange() {
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        //TODO - 1.20: Test on server
        MekanismStatusOverlay.INSTANCE.setTimer();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
    }

    public static PacketShowModeChange decode(FriendlyByteBuf buffer) {
        return INSTANCE;
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
