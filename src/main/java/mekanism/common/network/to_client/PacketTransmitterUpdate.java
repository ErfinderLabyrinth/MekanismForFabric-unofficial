package mekanism.common.network.to_client;

import mekanism.api.FluidStack;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.merged.BoxedChemical;
import mekanism.common.content.network.BoxedChemicalNetwork;
import mekanism.common.content.network.EnergyNetwork;
import mekanism.common.content.network.FluidNetwork;
import mekanism.common.lib.transmitter.DynamicBufferedNetwork;
import mekanism.common.lib.transmitter.DynamicNetwork;
import mekanism.common.lib.transmitter.TransmitterNetworkRegistry;
import mekanism.common.network.BasePacketHandler;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.util.NetworkUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Predicate;

public class PacketTransmitterUpdate implements IMekanismPacket {
    public static final PacketType<PacketTransmitterUpdate> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "transmitter_update"), PacketTransmitterUpdate::decode);

    private final TramsmittionType tramsmittionType;
    private final UUID networkID;
    private final float scale;
    @NotNull
    private BoxedChemical chemical = BoxedChemical.EMPTY;
    @NotNull
    private FluidStack fluidStack = FluidStack.EMPTY;

    public PacketTransmitterUpdate(EnergyNetwork network) {
        this(network, TramsmittionType.ENERGY);
    }

    public PacketTransmitterUpdate(BoxedChemicalNetwork network, @NotNull BoxedChemical chemical) {
        this(network, TramsmittionType.CHEMICAL);
        this.chemical = chemical;
    }

    public PacketTransmitterUpdate(FluidNetwork network, @NotNull FluidStack fluidStack) {
        this(network, TramsmittionType.FLUID);
        this.fluidStack = fluidStack;
    }

    private PacketTransmitterUpdate(DynamicBufferedNetwork<?, ?, ?, ?> network, TramsmittionType type) {
        this(type, network.getUUID(), network.currentScale);
    }

    private PacketTransmitterUpdate(TramsmittionType type, UUID networkID, float scale) {
        tramsmittionType = type;
        this.networkID = networkID;
        this.scale = scale;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        DynamicNetwork<?, ?, ?> clientNetwork = TransmitterNetworkRegistry.getInstance().getClientNetwork(networkID);
        if (clientNetwork != null && tramsmittionType.networkTypeMatches(clientNetwork)) {
            //Note: We set the information even if opaque transmitters is true in case the client turns the config setting off
            // so that they will have the proper information to then render
            if (tramsmittionType == TramsmittionType.CHEMICAL) {
                ((BoxedChemicalNetwork) clientNetwork).setLastChemical(chemical);
            } else if (tramsmittionType == TramsmittionType.FLUID) {
                ((FluidNetwork) clientNetwork).setLastFluid(fluidStack);
            }
            ((DynamicBufferedNetwork<?, ?, ?, ?>) clientNetwork).currentScale = scale;
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(tramsmittionType);
        buffer.writeUUID(networkID);
        buffer.writeFloat(scale);
        NetworkUtil.log("Sending '{}' update message for network with id {}", tramsmittionType, networkID);
        if (tramsmittionType == TramsmittionType.FLUID) {
            fluidStack.writeToBuffer(buffer);
        } else if (tramsmittionType == TramsmittionType.CHEMICAL) {
            chemical.write(buffer);
        }
    }

    public static PacketTransmitterUpdate decode(FriendlyByteBuf buffer) {
        PacketTransmitterUpdate packet = new PacketTransmitterUpdate(buffer.readEnum(TramsmittionType.class), buffer.readUUID(), buffer.readFloat());
        if (packet.tramsmittionType == TramsmittionType.FLUID) {
            packet.fluidStack = FluidStack.readFromBuffer(buffer);
        } else if (packet.tramsmittionType == TramsmittionType.CHEMICAL) {
            packet.chemical = BoxedChemical.read(buffer);
        }
        return packet;
    }

    public PacketType<PacketTransmitterUpdate> getType() {
        return TYPE;
    }

    public enum TramsmittionType {
        ENERGY(net -> net instanceof EnergyNetwork),
        FLUID(net -> net instanceof FluidNetwork),
        CHEMICAL(net -> net instanceof BoxedChemicalNetwork);

        private final Predicate<DynamicNetwork<?, ?, ?>> networkTypePredicate;

        TramsmittionType(Predicate<DynamicNetwork<?, ?, ?>> networkTypePredicate) {
            this.networkTypePredicate = networkTypePredicate;
        }

        private boolean networkTypeMatches(DynamicNetwork<?, ?, ?> network) {
            return networkTypePredicate.test(network);
        }
    }
}