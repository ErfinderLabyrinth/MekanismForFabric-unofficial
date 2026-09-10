package mekanism.tools.common.config;

import me.shedaniel.autoconfig.annotation.Config;
import mekanism.common.config.BaseMekanismConfig;

@Config(name = "mekanism/tools-client")
public class ToolsClientConfig extends BaseMekanismConfig {
    public final boolean displayDurabilityTooltips = true;

    public ToolsClientConfig() {
    }
}