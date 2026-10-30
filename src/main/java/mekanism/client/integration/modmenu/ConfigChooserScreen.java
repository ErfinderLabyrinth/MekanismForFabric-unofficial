package mekanism.client.integration.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigChooserScreen extends Screen {
    private final Screen parent;
    private final ConfigScreenFactory<?> common;
    private final ConfigScreenFactory<?> client;

    public ConfigChooserScreen(Screen parent, ConfigScreenFactory<?> common, ConfigScreenFactory<?> client) {
        super(Component.empty());
        this.parent = parent;
        this.common = common;
        this.client = client;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        addRenderableWidget(Button.builder(Component.literal("Common"), b -> open(common.create(this)))
                .bounds(centerX - 50, centerY - 10 - 24, 100, 20)
                .build()
        );

        addRenderableWidget(Button.builder(Component.literal("Client"), b -> open(client.create(this)))
                .bounds(centerX - 50, centerY - 10, 100, 20)
                .build()
        );

        addRenderableWidget(Button.builder(Component.translatable("gui.back"), b -> open(parent))
                .bounds(centerX - 50, centerY - 10 + 24, 100, 20)
                .build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, i, j, f);
    }

    public static void open(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }
}
