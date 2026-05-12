package mekanism.common.network.to_server;

import mekanism.api.MekanismAPI;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.item.interfaces.IModeItem.DisplayChange;
import mekanism.common.network.IMekanismPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PacketModeChange implements IMekanismPacket {
    public static final PacketType<PacketModeChange> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "mode_change"), PacketModeChange::decode);

    private final boolean displayChangeMessage;
    private final EquipmentSlot slot;
    private final int shift;

    public PacketModeChange(EquipmentSlot slot, boolean holdingShift) {
        this(slot, holdingShift ? -1 : 1, true);
    }

    public PacketModeChange(EquipmentSlot slot, int shift) {
        this(slot, shift, false);
    }

    private PacketModeChange(EquipmentSlot slot, int shift, boolean displayChangeMessage) {
        this.slot = slot;
        this.shift = shift;
        this.displayChangeMessage = displayChangeMessage;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (player != null) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof IModeItem modeItem) {
                DisplayChange displayChange;
                if (displayChangeMessage) {
                    displayChange = slot == EquipmentSlot.MAINHAND ? DisplayChange.MAIN_HAND : DisplayChange.OTHER;
                } else {
                    displayChange = DisplayChange.NONE;
                }
                modeItem.changeMode(player, stack, shift, displayChange);
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(slot);
        buffer.writeVarInt(shift);
        buffer.writeBoolean(displayChangeMessage);
    }

    public static PacketModeChange decode(FriendlyByteBuf buffer) {
        return new PacketModeChange(buffer.readEnum(EquipmentSlot.class), buffer.readVarInt(), buffer.readBoolean());
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}