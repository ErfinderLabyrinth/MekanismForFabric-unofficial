package mekanism.common.network.to_server;

import mekanism.api.MekanismAPI;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.util.SecurityUtils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PacketSecurityMode implements IMekanismPacket {
    public static final PacketType<PacketSecurityMode> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "secure_mode"), PacketSecurityMode::decode);

    private final InteractionHand currentHand;
    private final boolean increment;

    public PacketSecurityMode(InteractionHand hand, boolean increment) {
        this.currentHand = hand;
        this.increment = increment;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (player != null) {
            ItemStack stack = player.getItemInHand(currentHand);
            if (increment) {
                SecurityUtils.get().incrementSecurityMode(player, stack);
            } else {
                SecurityUtils.get().decrementSecurityMode(player, stack);
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(currentHand);
        buffer.writeBoolean(increment);
    }

    public static PacketSecurityMode decode(FriendlyByteBuf buffer) {
        return new PacketSecurityMode(buffer.readEnum(InteractionHand.class), buffer.readBoolean());
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}