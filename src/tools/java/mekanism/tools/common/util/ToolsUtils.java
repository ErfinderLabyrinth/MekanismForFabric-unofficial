package mekanism.tools.common.util;

import mekanism.tools.common.ToolsLang;
import mekanism.tools.common.config.MekanismToolsConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ToolsUtils {

    /**
     * Adds durability to the tooltip if enabled in the config
     *
     * @apiNote Only call on client
     */
    public static void addDurability(@NotNull List<Component> tooltip, @NotNull ItemStack stack) {
        if (MekanismToolsConfig.toolsClient.displayDurabilityTooltips) {
            tooltip.add(ToolsLang.HP.translate(stack.getMaxDamage() - stack.getDamageValue()));
        }
    }
}