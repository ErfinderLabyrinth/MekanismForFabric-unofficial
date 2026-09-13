package mekanism.tools.client;

import mekanism.api.providers.IItemProvider;
import mekanism.client.ClientRegistrationUtil;
import mekanism.tools.client.render.ToolsRenderPropertiesProvider;
import mekanism.tools.client.render.item.RenderMekanismShieldItem;
import mekanism.tools.common.MekanismTools;
import mekanism.tools.common.config.MekanismToolsConfig;
import mekanism.tools.common.registries.ToolsItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class ToolsClientRegistration implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        addShieldPropertyOverrides(MekanismTools.rl("blocking"),
              (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F,
              ToolsItems.BRONZE_SHIELD, ToolsItems.LAPIS_LAZULI_SHIELD, ToolsItems.OSMIUM_SHIELD, ToolsItems.REFINED_GLOWSTONE_SHIELD,
              ToolsItems.REFINED_OBSIDIAN_SHIELD, ToolsItems.STEEL_SHIELD);

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(RenderMekanismShieldItem.RENDERER);
        registerItemRenderers();
        MekanismToolsConfig.registerClientConfigs();
    }

    private static void addShieldPropertyOverrides(ResourceLocation override, ClampedItemPropertyFunction propertyGetter, IItemProvider... shields) {
        for (IItemProvider shield : shields) {
            ClientRegistrationUtil.setPropertyOverride(shield, override, propertyGetter);
        }
    }

    public static void registerItemRenderers() {
        ClientRegistrationUtil.registerISTER(RenderMekanismShieldItem.RENDERER,
                ToolsItems.BRONZE_SHIELD, ToolsItems.LAPIS_LAZULI_SHIELD, ToolsItems.OSMIUM_SHIELD, ToolsItems.REFINED_GLOWSTONE_SHIELD,
                ToolsItems.REFINED_OBSIDIAN_SHIELD, ToolsItems.STEEL_SHIELD);
    }
}