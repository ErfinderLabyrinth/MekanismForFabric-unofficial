package mekanism.common.network.to_server;

import mekanism.api.MekanismAPI;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.laser.TileEntityLaserAmplifier;
import mekanism.common.tile.machine.TileEntityResistiveHeater;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public class PacketGuiSetEnergy implements IMekanismPacket {
    public static final PacketType<PacketGuiSetEnergy> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "gui_set_energy"), PacketGuiSetEnergy::decode);

    private final GuiEnergyValue interaction;
    private final BlockPos tilePosition;
    private final long value;

    public PacketGuiSetEnergy(GuiEnergyValue interaction, BlockPos tilePosition, long value) {
        this.interaction = interaction;
        this.tilePosition = tilePosition;
        this.value = value;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (player != null) {
            TileEntityMekanism tile = WorldUtils.getTileEntity(TileEntityMekanism.class, player.level(), tilePosition);
            if (tile != null) {
                interaction.consume(tile, value);
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(interaction);
        buffer.writeBlockPos(tilePosition);
        buffer.writeLong(value);
    }

    public static PacketGuiSetEnergy decode(FriendlyByteBuf buffer) {
        return new PacketGuiSetEnergy(buffer.readEnum(GuiEnergyValue.class), buffer.readBlockPos(), buffer.readLong());
    }

    public enum GuiEnergyValue {
        MIN_THRESHOLD((tile, value) -> {
            if (tile instanceof TileEntityLaserAmplifier amplifier) {
                amplifier.setMinThresholdFromPacket(value);
            }
        }),
        MAX_THRESHOLD((tile, value) -> {
            if (tile instanceof TileEntityLaserAmplifier amplifier) {
                amplifier.setMaxThresholdFromPacket(value);
            }
        }),
        ENERGY_USAGE((tile, value) -> {
            if (tile instanceof TileEntityResistiveHeater heater) {
                heater.setEnergyUsageFromPacket(value);
            }
        });

        private final BiConsumer<TileEntityMekanism, Long> consumerForTile;

        GuiEnergyValue(BiConsumer<TileEntityMekanism, Long> consumerForTile) {
            this.consumerForTile = consumerForTile;
        }

        public void consume(TileEntityMekanism tile, long value) {
            consumerForTile.accept(tile, value);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}