package mekanism.common.registration.impl;

import mekanism.api.text.ILangEntry;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.ContainerProvider;
import mekanism.common.inventory.container.type.MekanismContainerType;
import mekanism.common.inventory.container.type.MekanismItemContainerType;
import mekanism.common.registration.WrappedRegistryObject;
import mekanism.common.util.RegistryUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ContainerTypeRegistryObject<CONTAINER extends AbstractContainerMenu> extends WrappedRegistryObject<MenuType<CONTAINER>> {

    public ContainerTypeRegistryObject(MenuType<CONTAINER> menuType) {
        super(menuType);
    }

    //Internal use only overwrite the registry object
    ContainerTypeRegistryObject<CONTAINER> setRegistryObject(MenuType<CONTAINER> menuType) {
        this.object = menuType;
        return this;
    }

    @Nullable
    public MenuProvider getProvider(ILangEntry name, Object object, Consumer<FriendlyByteBuf> sendAdditionalData) {
        return getProvider(name.translate(), object, sendAdditionalData);
    }

    @Nullable
    public MenuProvider getProvider(Component name, Object object, Consumer<FriendlyByteBuf> sendAdditionalData) {
        MenuConstructor provider = null;
        MenuType<CONTAINER> containerType = get();
        if (containerType instanceof MekanismContainerType<?, CONTAINER> mekanismContainerType) {
            provider = mekanismContainerType.create(object);
        }
        if (provider == null) {
            Mekanism.logger.info("Unable to create container for type: {}", RegistryUtils.getName(containerType));
        }
        return provider == null ? null : new ContainerProvider(name, provider, sendAdditionalData);
    }

    @Nullable
    public MenuProvider getProvider(ILangEntry name, InteractionHand hand, ItemStack stack, Consumer<FriendlyByteBuf> sendAdditionalData) {
        return getProvider(name.translate(), hand, stack, sendAdditionalData);
    }

    @Nullable
    public MenuProvider getProvider(Component name, InteractionHand hand, ItemStack stack, Consumer<FriendlyByteBuf> sendAdditionalData) {
        MenuConstructor provider = null;
        MenuType<CONTAINER> containerType = get();
        if (containerType instanceof MekanismItemContainerType<?, ?> mekanismItemContainerType) {
            provider = mekanismItemContainerType.create(hand, stack);
        }
        if (provider == null) {
            Mekanism.logger.info("Unable to create container for type: {}", RegistryUtils.getName(containerType));
        }
        return provider == null ? null : new ContainerProvider(name, provider, sendAdditionalData);
    }

    public void tryOpenGui(ServerPlayer player, InteractionHand hand, ItemStack stack) {
        MenuProvider provider = getProvider(stack.getHoverName(), hand, stack, buf -> {
                buf.writeEnum(hand);
                buf.writeItem(stack);
        });
        if (provider != null) {
            //Validate the provider isn't null, it shouldn't be but just in case
            player.openMenu(provider);
//            NetworkHooks.openScreen(player, provider, buf -> {
//                buf.writeEnum(hand);
//                buf.writeItem(stack);
//            });
        }
    }
}