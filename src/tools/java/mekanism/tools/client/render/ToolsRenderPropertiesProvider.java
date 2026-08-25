package mekanism.tools.client.render;

import mekanism.client.render.RenderPropertiesProvider.MekRenderProperties;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.tools.client.render.item.RenderMekanismShieldItem;
import mekanism.tools.common.item.ItemMekanismShield;
import mekanism.tools.common.registries.ToolsItems;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

//This class is used to prevent class loading issues on the server without having to use OnlyIn hacks
public class ToolsRenderPropertiesProvider {

    private ToolsRenderPropertiesProvider() {
    }

    public static MekRenderProperties shield() {
        return new MekRenderProperties(RenderMekanismShieldItem.RENDERER);
    }

    public static void register() {
        for(ItemRegistryObject<?> item : ToolsItems.ITEMS.getAllItems()) {
            if(item.get() instanceof ItemMekanismShield) {
                BuiltinItemRendererRegistry.INSTANCE.register(item.get(), RenderMekanismShieldItem.RENDERER::renderByItem);
            }
        }
    }
}