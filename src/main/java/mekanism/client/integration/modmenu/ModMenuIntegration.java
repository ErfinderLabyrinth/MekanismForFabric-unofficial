package mekanism.client.integration.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import mekanism.common.config.MekanismConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigChooserScreen(
                parent,
                screen -> AutoConfig.getConfigScreen(MekanismConfig.Common.class, screen).get(),
                screen -> AutoConfig.getConfigScreen(MekanismConfig.Client.class, screen).get()
        );
    }
}
