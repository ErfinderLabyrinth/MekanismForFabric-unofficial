package mekanism.generators.common.network.to_server;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import mekanism.api.MekanismAPI;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.registries.GeneratorsContainerTypes;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorCasing;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorController;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.function.TriFunction;

/**
 * Used for informing the server that a click happened in a GUI and the gui window needs to change
 */
public class PacketGeneratorsGuiButtonPress implements IMekanismPacket {
    public static final PacketType<PacketGeneratorsGuiButtonPress> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "generators_gui_button_press"), PacketGeneratorsGuiButtonPress::decode);

    private final ClickedGeneratorsTileButton tileButton;
    private final int extra;
    private final BlockPos tilePosition;

    public PacketGeneratorsGuiButtonPress(ClickedGeneratorsTileButton buttonClicked, BlockPos tilePosition) {
        this(buttonClicked, tilePosition, 0);
    }

    public PacketGeneratorsGuiButtonPress(ClickedGeneratorsTileButton buttonClicked, BlockPos tilePosition, int extra) {
        this.tileButton = buttonClicked;
        this.tilePosition = tilePosition;
        this.extra = extra;
    }

    @Override
    public void handle(Player p, PacketSender sender) {
        if (p instanceof ServerPlayer player) {//If we are on the server (the only time we should be receiving this packet), let forge handle switching the Gui
            TileEntityMekanism tile = WorldUtils.getTileEntity(TileEntityMekanism.class, player.level(), tilePosition);
            if (tile != null) {
                MenuProvider provider = tileButton.getProvider(tile, extra, buf -> {
                    buf.writeBlockPos(tilePosition);
                    buf.writeVarInt(extra);
                });
                if (provider != null) {
                    //Ensure valid data
                    player.openMenu(provider);
                }
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(tileButton);
        buffer.writeBlockPos(tilePosition);
        buffer.writeVarInt(extra);
    }

    public static PacketGeneratorsGuiButtonPress decode(FriendlyByteBuf buffer) {
        return new PacketGeneratorsGuiButtonPress(buffer.readEnum(ClickedGeneratorsTileButton.class), buffer.readBlockPos(), buffer.readVarInt());
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public enum ClickedGeneratorsTileButton {
        TAB_MAIN((tile, extra, sendAdditionalData) -> {
            if (tile instanceof TileEntityTurbineCasing) {
                return GeneratorsContainerTypes.INDUSTRIAL_TURBINE.getProvider(GeneratorsLang.TURBINE, tile, sendAdditionalData);
            } else if (tile instanceof TileEntityFissionReactorCasing) {
                return GeneratorsContainerTypes.FISSION_REACTOR.getProvider(GeneratorsLang.FISSION_REACTOR, tile, sendAdditionalData);
            }
            return null;
        }),
        TAB_HEAT((tile, extra, sendAdditionalData) -> GeneratorsContainerTypes.FUSION_REACTOR_HEAT.getProvider(GeneratorsLang.FUSION_REACTOR, tile, sendAdditionalData)),
        TAB_FUEL((tile, extra, sendAdditionalData) -> GeneratorsContainerTypes.FUSION_REACTOR_FUEL.getProvider(GeneratorsLang.FUSION_REACTOR, tile, sendAdditionalData)),
        TAB_STATS((tile, extra, sendAdditionalData) -> {
            if (tile instanceof TileEntityTurbineCasing) {
                return GeneratorsContainerTypes.TURBINE_STATS.getProvider(GeneratorsLang.TURBINE_STATS, tile, sendAdditionalData);
            } else if (tile instanceof TileEntityFusionReactorController) {
                return GeneratorsContainerTypes.FUSION_REACTOR_STATS.getProvider(GeneratorsLang.FUSION_REACTOR, tile, sendAdditionalData);
            } else if (tile instanceof TileEntityFissionReactorCasing) {
                return GeneratorsContainerTypes.FISSION_REACTOR_STATS.getProvider(GeneratorsLang.FISSION_REACTOR_STATS, tile, sendAdditionalData);
            }
            return null;
        });

        private final TriFunction<TileEntityMekanism, Integer, Consumer<FriendlyByteBuf>, MenuProvider> providerFromTile;

        ClickedGeneratorsTileButton(TriFunction<TileEntityMekanism, Integer, Consumer<FriendlyByteBuf>, MenuProvider> providerFromTile) {
            this.providerFromTile = providerFromTile;
        }

        public MenuProvider getProvider(TileEntityMekanism tile, int extra, Consumer<FriendlyByteBuf> sendAdditionalData) {
            return providerFromTile.apply(tile, extra, sendAdditionalData);
        }
    }
}