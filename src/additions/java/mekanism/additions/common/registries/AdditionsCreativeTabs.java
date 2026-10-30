package mekanism.additions.common.registries;

import mekanism.additions.common.AdditionsLang;
import mekanism.additions.common.MekanismAdditions;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.text.EnumColor;
import mekanism.common.registration.impl.CreativeTabDeferredRegister;
import mekanism.common.registration.impl.CreativeTabRegistryObject;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import java.util.Map;

public class AdditionsCreativeTabs {

    public static final CreativeTabDeferredRegister CREATIVE_TABS = new CreativeTabDeferredRegister(MekanismAdditions.MODID);

    public static final CreativeTabRegistryObject ADDITIONS = CREATIVE_TABS.registerMain(AdditionsLang.MEKANISM_ADDITIONS,
          AdditionsItems.BALLOONS.get(EnumColor.BRIGHT_GREEN), builder ->
                builder.displayItems((displayParameters, output) -> {
                          CreativeTabDeferredRegister.addToDisplay(AdditionsItems.ITEMS, output);
                          CreativeTabDeferredRegister.addToDisplay(AdditionsBlocks.BLOCKS, output);
                      })
    );

    private static void addToExistingTabs(CreativeModeTab tab, FabricItemGroupEntries entries) {
        ResourceKey<CreativeModeTab> tabKey = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).get();
        if (tabKey == CreativeModeTabs.COLORED_BLOCKS) {
            addToDisplay(entries, AdditionsBlocks.GLOW_PANELS, AdditionsBlocks.PLASTIC_BLOCKS, AdditionsBlocks.SLICK_PLASTIC_BLOCKS, AdditionsBlocks.PLASTIC_GLOW_BLOCKS,
                  AdditionsBlocks.REINFORCED_PLASTIC_BLOCKS, AdditionsBlocks.PLASTIC_ROADS, AdditionsBlocks.TRANSPARENT_PLASTIC_BLOCKS, AdditionsBlocks.PLASTIC_STAIRS,
                  AdditionsBlocks.PLASTIC_SLABS, AdditionsBlocks.PLASTIC_FENCES, AdditionsBlocks.PLASTIC_FENCE_GATES, AdditionsBlocks.PLASTIC_GLOW_STAIRS,
                  AdditionsBlocks.PLASTIC_GLOW_SLABS, AdditionsBlocks.TRANSPARENT_PLASTIC_STAIRS, AdditionsBlocks.TRANSPARENT_PLASTIC_SLABS);
        } else if (tabKey == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            addToDisplay(entries, AdditionsBlocks.GLOW_PANELS, AdditionsBlocks.PLASTIC_GLOW_BLOCKS, AdditionsBlocks.PLASTIC_GLOW_STAIRS, AdditionsBlocks.PLASTIC_GLOW_SLABS);
        } else if (tabKey == CreativeModeTabs.REDSTONE_BLOCKS) {
            CreativeTabDeferredRegister.addToDisplay(entries, AdditionsBlocks.OBSIDIAN_TNT);
        } else if (tabKey == CreativeModeTabs.COMBAT) {
            CreativeTabDeferredRegister.addToDisplay(entries, AdditionsBlocks.OBSIDIAN_TNT);
        } else if (tabKey == CreativeModeTabs.SPAWN_EGGS) {
            CreativeTabDeferredRegister.addToDisplay(entries, AdditionsItems.BABY_CREEPER_SPAWN_EGG, AdditionsItems.BABY_ENDERMAN_SPAWN_EGG,
                  AdditionsItems.BABY_SKELETON_SPAWN_EGG, AdditionsItems.BABY_STRAY_SPAWN_EGG, AdditionsItems.BABY_WITHER_SKELETON_SPAWN_EGG);
        }
    }

    @SafeVarargs
    private static void addToDisplay(CreativeModeTab.Output output, Map<EnumColor, ? extends IBlockProvider>... blocks) {
        for (Map<EnumColor, ? extends IBlockProvider> blockMap : blocks) {
            for (IBlockProvider block : blockMap.values()) {
                CreativeTabDeferredRegister.addToDisplay(output, block);
            }
        }
    }

    public static void register() {
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register(AdditionsCreativeTabs::addToExistingTabs);
    }
}