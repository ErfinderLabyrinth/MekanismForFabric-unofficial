package mekanism.common.inventory.container;

import mekanism.api.text.ILangEntry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ContainerProvider implements ExtendedScreenHandlerFactory {

    private final Component displayName;
    private final MenuConstructor provider;
    private final Consumer<FriendlyByteBuf> sendAdditionalData;

    public ContainerProvider(ILangEntry translationHelper, MenuConstructor provider, Consumer<FriendlyByteBuf> sendAdditionalData) {
        this(translationHelper.translate(), provider, sendAdditionalData);
    }

    public ContainerProvider(Component displayName, MenuConstructor provider, Consumer<FriendlyByteBuf> sendAdditionalData) {
        this.displayName = displayName;
        this.provider = provider;
        this.sendAdditionalData = sendAdditionalData;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inv, @NotNull Player player) {
        return provider.createMenu(i, inv, player);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return displayName;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        sendAdditionalData.accept(buf);
    }
}