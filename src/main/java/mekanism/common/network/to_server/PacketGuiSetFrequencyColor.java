package mekanism.common.network.to_server;

import mekanism.api.MekanismAPI;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.Frequency.FrequencyIdentity;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.frequency.IColorableFrequency;
import mekanism.common.network.IMekanismPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PacketGuiSetFrequencyColor<FREQ extends Frequency & IColorableFrequency> implements IMekanismPacket {
    public static final PacketType<PacketGuiSetFrequencyColor> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "gui_set_frequency_color"), PacketGuiSetFrequencyColor::decode);

    private final FrequencyType<FREQ> frequencyType;
    private final FrequencyIdentity identity;
    private final boolean next;

    private PacketGuiSetFrequencyColor(FrequencyType<FREQ> frequencyType, FrequencyIdentity identity, boolean next) {
        this.frequencyType = frequencyType;
        this.identity = identity;
        this.next = next;
    }

    public static <FREQ extends Frequency & IColorableFrequency> PacketGuiSetFrequencyColor<FREQ> create(FREQ freq, boolean next) {
        return new PacketGuiSetFrequencyColor<>((FrequencyType<FREQ>) freq.getType(), freq.getIdentity(), next);
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (player != null) {
            FREQ freq = frequencyType.getFrequency(identity, player.getUUID(), player.getServer());
            if (freq != null && freq.ownerMatches(player.getUUID())) {
                freq.setColor(next ? freq.getColor().getNext() : freq.getColor().getPrevious());
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        frequencyType.write(buffer);
        frequencyType.getIdentitySerializer().write(buffer, identity);
        buffer.writeBoolean(next);
    }

    public static <FREQ extends Frequency & IColorableFrequency> PacketGuiSetFrequencyColor<FREQ> decode(FriendlyByteBuf buffer) {
        FrequencyType<FREQ> frequencyType = FrequencyType.load(buffer);
        FrequencyIdentity identity = frequencyType.getIdentitySerializer().read(buffer);
        return new PacketGuiSetFrequencyColor<>(frequencyType, identity, buffer.readBoolean());
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}