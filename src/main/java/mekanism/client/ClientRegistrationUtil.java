package mekanism.client;

import mekanism.api.providers.IBlockProvider;
import mekanism.api.providers.IItemProvider;
import mekanism.api.text.EnumColor;
import mekanism.client.gui.machine.GuiAdvancedElectricMachine;
import mekanism.client.gui.machine.GuiElectricMachine;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.block.interfaces.IColoredBlock;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.item.interfaces.IColoredItem;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.registration.impl.FluidDeferredRegister;
import mekanism.common.registration.impl.FluidRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.tile.prefab.TileEntityAdvancedElectricMachine;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class ClientRegistrationUtil {

    private static final BlockColor COLORED_BLOCK_COLOR = (state, world, pos, tintIndex) -> {
        Block block = state.getBlock();
        if (block instanceof IColoredBlock coloredBlock) {
            return MekanismRenderer.getColorARGB(coloredBlock.getColor(), 1);
        }
        return -1;
    };
    private static final ItemColor COLORED_BLOCK_ITEM_COLOR = (stack, tintIndex) -> {
        Item item = stack.getItem();
        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof IColoredBlock coloredBlock) {
                return MekanismRenderer.getColorARGB(coloredBlock.getColor(), 1);
            }
        }
        return -1;
    };
    private static final ItemColor COLORED_ITEM_COLOR = (stack, tintIndex) -> {
        Item item = stack.getItem();
        if (tintIndex == 1 && item instanceof IColoredItem coloredItem) {
            EnumColor color = coloredItem.getColor(stack);
            if (color != null) {
                return MekanismRenderer.getColorARGB(color, 1);
            }
            return 0xFF555555;
        }
        return -1;
    };
    private static final ItemColor BUCKET_ITEM_COLOR = (ItemStack itemStack, int tintIndex) -> {
        if (tintIndex < 1) {
            return -1;
        }
        if (itemStack.getItem() instanceof BucketItem bucket) {
            Fluid fluid = bucket.content;
            for (FluidRegistryObject<?, ?, ?, ?> registeredFluid : MekanismFluids.FLUIDS.getAllFluids()) {
                if (registeredFluid.getFluid() == fluid || registeredFluid.getFlowingFluid() == fluid) {
                    return registeredFluid.getRenderProperties().getColor();
                }
            }
        }
        return 0xFFFFFFFF;
    };

    private ClientRegistrationUtil() {
    }

    @SafeVarargs
    public static <T extends BlockEntity> void bindTileEntityRenderer(BlockEntityRendererProvider<T> rendererProvider,
          TileEntityTypeRegistryObject<? extends T>... tileEntityTypeROs) {
        if (tileEntityTypeROs.length == 0) {
            throw new IllegalArgumentException("No renderers provided.");
        } else if (tileEntityTypeROs.length == 1) {
            BlockEntityRenderers.register(tileEntityTypeROs[0].get(), rendererProvider);
        } else {
            BlockEntityRendererProvider<T> provider = new BlockEntityRendererProvider<>() {
                @Nullable
                private WeakReference<Context> cachedContext;
                @Nullable
                private WeakReference<BlockEntityRenderer<T>> cachedRenderer;

                @NotNull
                @Override
                public BlockEntityRenderer<T> create(@NotNull Context context) {
                    //If there is a cached context and renderer make use of it, otherwise create one and cache it
                    // this allows us to reduce the number of renderer classes we create
                    BlockEntityRenderer<T> renderer = cachedRenderer == null ? null : cachedRenderer.get();
                    if (cachedContext == null || cachedContext.get() != context || renderer == null) {
                        renderer = rendererProvider.create(context);
                        cachedContext = new WeakReference<>(context);
                        cachedRenderer = new WeakReference<>(renderer);
                    }
                    return renderer;
                }
            };
            for (TileEntityTypeRegistryObject<? extends T> tileTypeRO : tileEntityTypeROs) {
                BlockEntityRenderers.register(tileTypeRO.get(), provider);
            }
        }
    }

    public static <C extends AbstractContainerMenu, U extends Screen & MenuAccess<C>> void registerScreen(ContainerTypeRegistryObject<C> type, ScreenConstructor<C, U> factory) {
        MenuScreens.register(type.get(), factory);
    }

    //Helper method to register GuiElectricMachine due to generics not being able to be resolved through registerScreen
    @SuppressWarnings("Convert2Lambda")
    public static <TILE extends TileEntityElectricMachine, C extends MekanismTileContainer<TILE>> void registerElectricScreen(ContainerTypeRegistryObject<C> type) {
        registerScreen(type, new ScreenConstructor<C, GuiElectricMachine<TILE, C>>() {
            @NotNull
            @Override
            public GuiElectricMachine<TILE, C> create(@NotNull C container, @NotNull Inventory inv, @NotNull Component title) {
                return new GuiElectricMachine<>(container, inv, title);
            }
        });
    }

    //Helper method to register GuiAdvancedElectricMachine due to generics not being able to be resolved through registerScreen
    @SuppressWarnings("Convert2Lambda")
    public static <TILE extends TileEntityAdvancedElectricMachine, C extends MekanismTileContainer<TILE>> void registerAdvancedElectricScreen(ContainerTypeRegistryObject<C> type) {
        registerScreen(type, new ScreenConstructor<C, GuiAdvancedElectricMachine<TILE, C>>() {
            @NotNull
            @Override
            public GuiAdvancedElectricMachine<TILE, C> create(@NotNull C container, @NotNull Inventory inv, @NotNull Component title) {
                return new GuiAdvancedElectricMachine<>(container, inv, title);
            }
        });
    }

    public static void registerKeyBindings(KeyMapping... keys) {
        for (KeyMapping key : keys) {
            KeyBindingHelper.registerKeyBinding(key);
        }
    }

    public static void setPropertyOverride(IItemProvider itemProvider, ResourceLocation override, ClampedItemPropertyFunction propertyGetter) {
        ItemProperties.register(itemProvider.asItem(), override, propertyGetter);
    }

    public static void registerItemColorHandler(ItemColor itemColor, IItemProvider... items) {
        for (IItemProvider itemProvider : items) {
            ColorProviderRegistry.ITEM.register(itemColor, itemProvider.asItem());
        }
    }

    public static void registerBlockColorHandler(BlockColor blockColor, IBlockProvider... blocks) {
        for (IBlockProvider blockProvider : blocks) {
            ColorProviderRegistry.BLOCK.register(blockColor, blockProvider.getBlock());
        }
    }

    public static void registerBucketColorHandler(FluidDeferredRegister register) {
        for (FluidRegistryObject<?, ?, ?, ?> fluidRO : register.getAllFluids()) {
            ColorProviderRegistry.ITEM.register(BUCKET_ITEM_COLOR, fluidRO.getBucket());
        }
    }

    public static void registerIColoredBlockHandler(boolean block, IBlockProvider... blocks) {
        if (block) {
            registerBlockColorHandler(COLORED_BLOCK_COLOR, blocks);
        } else {
            registerItemColorHandler(COLORED_BLOCK_ITEM_COLOR, blocks);
        }
    }

    public static void registerIColoredItemHandler(IItemProvider... items) {
        registerItemColorHandler(COLORED_ITEM_COLOR, items);
    }

    public static void setRenderLayer(RenderType type, FluidRegistryObject<?, ?, ?, ?>... fluidROs) {
        for (FluidRegistryObject<?, ?, ?, ?> fluidRO : fluidROs) {
            BlockRenderLayerMap.INSTANCE.putFluids(type, fluidRO.getStillFluid(), fluidRO.getFlowingFluid());
        }
    }

    public static void registerRenderHandler(FluidRegistryObject<?, ?, ?, ?>... fluidROs) {}
}