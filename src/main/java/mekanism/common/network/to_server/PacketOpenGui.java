package mekanism.common.network.to_server;

import mekanism.api.MekanismAPI;
import mekanism.api.functions.ConstantPredicates;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.ContainerProvider;
import mekanism.common.inventory.container.ModuleTweakerContainer;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.registries.MekanismContainerTypes;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class PacketOpenGui implements IMekanismPacket {
    public static final PacketType<PacketOpenGui> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "open_gui"), PacketOpenGui::decode);

    private final GuiType type;

    public PacketOpenGui(GuiType type) {
        this.type = type;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (player != null && type.shouldOpenForPlayer.test(player)) {
            player.openMenu(type.containerSupplier.get());
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(type);
    }

    public static PacketOpenGui decode(FriendlyByteBuf buffer) {
        return new PacketOpenGui(buffer.readEnum(GuiType.class));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public enum GuiType {
        MODULE_TWEAKER(() -> new ContainerProvider(MekanismLang.MODULE_TWEAKER, (id, inv, player) -> new ModuleTweakerContainer(id, inv), buffer -> {}),
              ModuleTweakerContainer::hasTweakableItem);

        private final Supplier<MenuProvider> containerSupplier;
        private final Predicate<Player> shouldOpenForPlayer;

        GuiType(Supplier<MenuProvider> containerSupplier) {
            this(containerSupplier, ConstantPredicates.alwaysTrue());
        }

        GuiType(Supplier<MenuProvider> containerSupplier, Predicate<Player> shouldOpenForPlayer) {
            this.containerSupplier = containerSupplier;
            this.shouldOpenForPlayer = shouldOpenForPlayer;
        }
    }
}
