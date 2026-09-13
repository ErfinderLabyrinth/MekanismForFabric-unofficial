package mekanism.common.registration.impl;

import mekanism.api.providers.IBlockProvider;
import mekanism.api.providers.IItemProvider;
import mekanism.api.text.ILangEntry;
import mekanism.common.block.BlockBounding;
import mekanism.common.registration.WrappedDeferredRegister;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class CreativeTabDeferredRegister extends WrappedDeferredRegister<CreativeModeTab> {

    private final String modid;

    public CreativeTabDeferredRegister(String modid) {
        super(BuiltInRegistries.CREATIVE_MODE_TAB);
        this.modid = modid;
    }

    /**
     * @apiNote We manually require the title and icon to be passed so that we ensure all tabs have one.
     */
    public CreativeTabRegistryObject registerMain(ILangEntry title, IItemProvider icon, Consumer<CreativeModeTab.Builder> additionBuild) {
        return register(new ResourceLocation(modid, modid), title, icon, additionBuild);
    }

    /**
     * @apiNote We manually require the title and icon to be passed so that we ensure all tabs have one.
     */
    public CreativeTabRegistryObject register(ResourceLocation name, ILangEntry title, IItemProvider icon, Consumer<CreativeModeTab.Builder> additionBuild) {
        return register(name, () -> {
            CreativeModeTab.Builder builder = FabricItemGroup.builder()
                    .title(title.translate())
                    .icon(icon::getItemStack);
            additionBuild.accept(builder);
            return builder.build();
        }, CreativeTabRegistryObject::new);
    }

    public static void addToDisplay(CreativeModeTab.Output output, ItemLike... items) {
        for (ItemLike item : items) {
            addToDisplay(output, item);
        }
    }

    public static void addToDisplay(CreativeModeTab.Output output, ItemLike itemLike) {
        if (itemLike.asItem() instanceof ICustomCreativeTabContents contents) {
            if (contents.addDefault()) {
                output.accept(itemLike);
            }
            contents.addItems(output);
        } else {
            output.accept(itemLike);
        }
    }

    public static void addToDisplay(ItemDeferredRegister register, CreativeModeTab.Output output) {
        for (IItemProvider itemProvider : register.getAllItems()) {
            addToDisplay(output, itemProvider);
        }
    }

    public static void addToDisplay(BlockDeferredRegister register, CreativeModeTab.Output output) {
        for (IBlockProvider itemProvider : register.getAllBlocks()) {
            //Don't add bounding blocks to the creative tab
            if (!(itemProvider.getBlock() instanceof BlockBounding)) {
                addToDisplay(output, itemProvider);
            }
        }
    }

    public static void addToDisplay(FluidDeferredRegister register, CreativeModeTab.Output output) {
        for (FluidRegistryObject<?, ?, ?, ?> fluidRO : register.getAllFluids()) {
            addToDisplay(output, fluidRO.getBucket());
        }
    }

    public interface ICustomCreativeTabContents {

        void addItems(CreativeModeTab.Output tabOutput);

        default boolean addDefault() {
            return true;
        }
    }

//    public static class MekanismCreativeTab extends CreativeModeTab {
//
//        protected MekanismCreativeTab(CreativeModeTab.Builder builder) {
//            super(builder);
//        }
//
//        @Override //TODO need mixin
//        public int getLabelColor() {
//            return SpecialColors.TEXT_TITLE.argb();
//        }
//    }
}