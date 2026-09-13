package mekanism.tools.client.integration.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import mekanism.client.integration.modmenu.ConfigChooserScreen;
import mekanism.tools.common.config.ToolsClientConfig;
import mekanism.tools.common.config.ToolsConfig;

public class ToolsModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigChooserScreen(
                parent,
                screen -> AutoConfig.getConfigScreen(ToolsConfig.class, screen).get(),
                screen -> AutoConfig.getConfigScreen(ToolsClientConfig.class, screen).get()
        );
    }
}
