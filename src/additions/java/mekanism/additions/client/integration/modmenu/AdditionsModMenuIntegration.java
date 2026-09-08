package mekanism.additions.client.integration.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import mekanism.additions.common.config.AdditionsClientConfig;
import mekanism.additions.common.config.AdditionsConfig;
import mekanism.client.integration.modmenu.ConfigChooserScreen;

public class AdditionsModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigChooserScreen(
                parent,
                screen -> AutoConfig.getConfigScreen(AdditionsConfig.class, screen).get(),
                screen -> AutoConfig.getConfigScreen(AdditionsClientConfig.class, screen).get()
        );
    }
}
