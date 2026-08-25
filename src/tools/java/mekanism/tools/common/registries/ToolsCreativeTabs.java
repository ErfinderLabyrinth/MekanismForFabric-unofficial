package mekanism.tools.common.registries;

import mekanism.api.providers.IItemProvider;
import mekanism.common.registration.impl.CreativeTabDeferredRegister;
import mekanism.common.registration.impl.CreativeTabRegistryObject;
import mekanism.tools.common.MekanismTools;
import mekanism.tools.common.ToolsLang;
import mekanism.tools.common.item.ItemMekanismArmor;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;

public class ToolsCreativeTabs {

    public static final CreativeTabDeferredRegister CREATIVE_TABS = new CreativeTabDeferredRegister(MekanismTools.MODID);

    public static final CreativeTabRegistryObject TOOLS = CREATIVE_TABS.registerMain(ToolsLang.MEKANISM_TOOLS, ToolsItems.DIAMOND_PAXEL, builder ->
          builder.displayItems((displayParameters, output) -> CreativeTabDeferredRegister.addToDisplay(ToolsItems.ITEMS, output))
    );

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            for (IItemProvider item : ToolsItems.ITEMS.getAllItems()) {
                if (item.asItem() instanceof DiggerItem) {
                    CreativeTabDeferredRegister.addToDisplay(entries, item);
                }
            }
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            for (IItemProvider itemProvider : ToolsItems.ITEMS.getAllItems()) {
                Item item = itemProvider.asItem();
                if (item instanceof ItemMekanismArmor || item instanceof SwordItem || item instanceof ShieldItem) {
                    CreativeTabDeferredRegister.addToDisplay(entries, item);
                }
            }
        });
    }
}