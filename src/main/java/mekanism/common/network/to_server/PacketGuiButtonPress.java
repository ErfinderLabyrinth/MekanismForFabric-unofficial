package mekanism.common.network.to_server;

import mekanism.api.MekanismAPI;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeGui;
import mekanism.common.item.interfaces.IGuiItem;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.registries.MekanismContainerTypes;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.multiblock.TileEntityBoilerCasing;
import mekanism.common.tile.multiblock.TileEntityInductionCasing;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Used for informing the server that a click happened in a GUI and the gui window needs to change
 */
public class PacketGuiButtonPress implements IMekanismPacket {
    public static final PacketType<PacketGuiButtonPress> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "gui_button_press"), PacketGuiButtonPress::decode);

    private final Type type;
    private ClickedItemButton itemButton;
    private ClickedTileButton tileButton;
    private ClickedEntityButton entityButton;
    private InteractionHand hand;
    private int entityID;
    private int extra;
    private BlockPos tilePosition;

    public PacketGuiButtonPress(ClickedTileButton buttonClicked, BlockEntity tile) {
        this(buttonClicked, tile.getBlockPos());
    }

    public PacketGuiButtonPress(ClickedTileButton buttonClicked, BlockEntity tile, int extra) {
        this(buttonClicked, tile.getBlockPos(), extra);
    }

    public PacketGuiButtonPress(ClickedTileButton buttonClicked, BlockPos tilePosition) {
        this(buttonClicked, tilePosition, 0);
    }

    public PacketGuiButtonPress(ClickedItemButton buttonClicked, InteractionHand hand) {
        type = Type.ITEM;
        this.itemButton = buttonClicked;
        this.hand = hand;
    }

    public PacketGuiButtonPress(ClickedTileButton buttonClicked, BlockPos tilePosition, int extra) {
        type = Type.TILE;
        this.tileButton = buttonClicked;
        this.tilePosition = tilePosition;
        this.extra = extra;
    }

    public PacketGuiButtonPress(ClickedEntityButton buttonClicked, Entity entity) {
        this(buttonClicked, entity.getId());
    }

    public PacketGuiButtonPress(ClickedEntityButton buttonClicked, int entityID) {
        type = Type.ENTITY;
        this.entityButton = buttonClicked;
        this.entityID = entityID;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (player == null) {
            return;
        }
        if (type == Type.ENTITY) {
            Entity entity = player.level().getEntity(entityID);
            if (entity != null) {
                MenuProvider provider = entityButton.getProvider(entity, buf -> {
                    buf.writeVarInt(entityID);
                });
                if (provider != null) {
                    //Ensure valid data
                    player.openMenu(provider);
                }
            }
        } else if (type == Type.TILE) {
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
        } else if (type == Type.ITEM) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof IGuiItem) {
                MenuProvider provider = itemButton.getProvider(stack, hand, buf -> {
                    buf.writeEnum(hand);
                    buf.writeItem(stack);
                });
                if (provider != null) {
                    player.openMenu(provider);
                }
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(type);
        switch (type) {
            case ENTITY -> {
                buffer.writeEnum(entityButton);
                buffer.writeVarInt(entityID);
            }
            case TILE -> {
                buffer.writeEnum(tileButton);
                buffer.writeBlockPos(tilePosition);
                buffer.writeVarInt(extra);
            }
            case ITEM -> {
                buffer.writeEnum(itemButton);
                buffer.writeEnum(hand);
            }
        }
    }

    public static PacketGuiButtonPress decode(FriendlyByteBuf buffer) {
        return switch (buffer.readEnum(Type.class)) {
            case ENTITY -> new PacketGuiButtonPress(buffer.readEnum(ClickedEntityButton.class), buffer.readVarInt());
            case TILE -> new PacketGuiButtonPress(buffer.readEnum(ClickedTileButton.class), buffer.readBlockPos(), buffer.readVarInt());
            case ITEM -> new PacketGuiButtonPress(buffer.readEnum(ClickedItemButton.class), buffer.readEnum(InteractionHand.class));
        };
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public enum ClickedItemButton {
        BACK_BUTTON((stack, hand, sendAdditionalData) -> {
            if (stack.getItem() instanceof IGuiItem guiItem) {
                return guiItem.getContainerType().getProvider(stack.getHoverName(), hand, stack, sendAdditionalData);
            }
            return null;
        }),
        QIO_FREQUENCY_SELECT((stack, hand, sendAdditionalData) -> MekanismContainerTypes.QIO_FREQUENCY_SELECT_ITEM.getProvider(MekanismLang.QIO_FREQUENCY_SELECT, hand, stack, sendAdditionalData));

        private final TriFunction<ItemStack, InteractionHand, Consumer<FriendlyByteBuf>, MenuProvider> providerFromItem;

        ClickedItemButton(TriFunction<ItemStack, InteractionHand, Consumer<FriendlyByteBuf>, MenuProvider> providerFromItem) {
            this.providerFromItem = providerFromItem;
        }

        public MenuProvider getProvider(ItemStack stack, InteractionHand hand, Consumer<FriendlyByteBuf> sendAdditionalData) {
            return providerFromItem.apply(stack, hand, sendAdditionalData);
        }
    }

    public enum ClickedTileButton {
        BACK_BUTTON((tile, extra, sendAdditionalData) -> {
            //Special handling to basically reset to the tiles default gui container
            AttributeGui attributeGui = Attribute.get(tile.getBlockType(), AttributeGui.class);
            if (attributeGui != null) {
                return attributeGui.getProvider(tile, sendAdditionalData);
            }
            return null;
        }),
        QIO_FREQUENCY_SELECT((tile, extra, sendAdditionalData) -> MekanismContainerTypes.QIO_FREQUENCY_SELECT_TILE.getProvider(MekanismLang.QIO_FREQUENCY_SELECT, tile, sendAdditionalData)),
        DIGITAL_MINER_CONFIG((tile, extra, sendAdditionalData) -> MekanismContainerTypes.DIGITAL_MINER_CONFIG.getProvider(MekanismLang.MINER_CONFIG, tile, sendAdditionalData)),

        TAB_MAIN((tile, extra, sendAdditionalData) -> {
            if (tile instanceof TileEntityInductionCasing) {
                return MekanismContainerTypes.INDUCTION_MATRIX.getProvider(MekanismLang.MATRIX, tile, sendAdditionalData);
            } else if (tile instanceof TileEntityBoilerCasing) {
                return MekanismContainerTypes.THERMOELECTRIC_BOILER.getProvider(MekanismLang.BOILER, tile, sendAdditionalData);
            }
            return null;
        }),
        TAB_STATS((tile, extra, sendAdditionalData) -> {
            if (tile instanceof TileEntityInductionCasing) {
                return MekanismContainerTypes.MATRIX_STATS.getProvider(MekanismLang.MATRIX_STATS, tile, sendAdditionalData);
            } else if (tile instanceof TileEntityBoilerCasing) {
                return MekanismContainerTypes.BOILER_STATS.getProvider(MekanismLang.BOILER_STATS, tile, sendAdditionalData);
            }
            return null;
        });

        private final TriFunction<TileEntityMekanism, Integer, Consumer<FriendlyByteBuf>, MenuProvider> providerFromTile;

        ClickedTileButton(TriFunction<TileEntityMekanism, Integer, Consumer<FriendlyByteBuf>, MenuProvider> providerFromTile) {
            this.providerFromTile = providerFromTile;
        }

        public MenuProvider getProvider(TileEntityMekanism tile, int extra, Consumer<FriendlyByteBuf> sendAdditionalData) {
            return providerFromTile.apply(tile, extra, sendAdditionalData);
        }
    }

    public enum ClickedEntityButton {
        //Entities
        ROBIT_CRAFTING((entity, sendAdditionalData) -> MekanismContainerTypes.CRAFTING_ROBIT.getProvider(MekanismLang.ROBIT_CRAFTING, entity, sendAdditionalData)),
        ROBIT_INVENTORY((entity, sendAdditionalData) -> MekanismContainerTypes.INVENTORY_ROBIT.getProvider(MekanismLang.ROBIT_INVENTORY, entity, sendAdditionalData)),
        ROBIT_MAIN((entity, sendAdditionalData) -> MekanismContainerTypes.MAIN_ROBIT.getProvider(MekanismLang.ROBIT, entity, sendAdditionalData)),
        ROBIT_REPAIR((entity, sendAdditionalData) -> MekanismContainerTypes.REPAIR_ROBIT.getProvider(MekanismLang.ROBIT_REPAIR, entity, sendAdditionalData)),
        ROBIT_SMELTING((entity, sendAdditionalData) -> MekanismContainerTypes.SMELTING_ROBIT.getProvider(MekanismLang.ROBIT_SMELTING, entity, sendAdditionalData));

        private final BiFunction<Entity, Consumer<FriendlyByteBuf>, MenuProvider> providerFromEntity;

        ClickedEntityButton(BiFunction<Entity, Consumer<FriendlyByteBuf>, MenuProvider> providerFromEntity) {
            this.providerFromEntity = providerFromEntity;
        }

        public MenuProvider getProvider(Entity entity, Consumer<FriendlyByteBuf> sendAdditionalData) {
            return providerFromEntity.apply(entity, sendAdditionalData);
        }
    }

    public enum Type {
        TILE,
        ITEM,
        ENTITY;
    }
}