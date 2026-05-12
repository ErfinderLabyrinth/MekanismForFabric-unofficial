package mekanism.common.network.to_client;

import mekanism.api.MekanismAPI;
import mekanism.common.Mekanism;
import mekanism.common.network.IMekanismPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class PacketPlayerData implements IMekanismPacket {
    public static final PacketType<PacketPlayerData> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "player_data"), PacketPlayerData::decode);

    private final UUID uuid;
    private final boolean activeFlamethrower;
    private final boolean activeJetpack;
    private final boolean activeScubaMask;
    private final boolean activeModulator;

    public PacketPlayerData(UUID uuid) {
        this.uuid = uuid;
        this.activeFlamethrower = Mekanism.playerState.isFlamethrowerOn(uuid);
        this.activeJetpack = Mekanism.playerState.isJetpackOn(uuid);
        this.activeScubaMask = Mekanism.playerState.isScubaMaskOn(uuid);
        this.activeModulator = Mekanism.playerState.isGravitationalModulationOn(uuid);
    }

    private PacketPlayerData(UUID uuid, boolean activeFlamethrower, boolean activeJetpack, boolean activeGasMask, boolean activeModulator) {
        this.uuid = uuid;
        this.activeFlamethrower = activeFlamethrower;
        this.activeJetpack = activeJetpack;
        this.activeScubaMask = activeGasMask;
        this.activeModulator = activeModulator;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        Mekanism.playerState.setFlamethrowerState(uuid, activeFlamethrower, false);
        Mekanism.playerState.setJetpackState(uuid, activeJetpack, false);
        Mekanism.playerState.setScubaMaskState(uuid, activeScubaMask, false);
        Mekanism.playerState.setGravitationalModulationState(uuid, activeModulator, false);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeBoolean(activeFlamethrower);
        buffer.writeBoolean(activeJetpack);
        buffer.writeBoolean(activeScubaMask);
        buffer.writeBoolean(activeModulator);
    }

    public static PacketPlayerData decode(FriendlyByteBuf buffer) {
        return new PacketPlayerData(buffer.readUUID(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}