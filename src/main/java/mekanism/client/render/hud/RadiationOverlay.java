package mekanism.client.render.hud;

import mekanism.api.radiation.IRadiationManager;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.lib.radiation.RadiationManager.RadiationScale;
import mekanism.common.lib.radiation.capability.DefaultRadiationEntity;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public class RadiationOverlay {

    public static final RadiationOverlay INSTANCE = new RadiationOverlay();

    private double prevRadiation = 0;
    private long lastTick;

    private RadiationOverlay() {
    }

    public void resetRadiation() {
        prevRadiation = 0;
    }

    public void render(Gui gui, GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight) {
        Player player = gui.minecraft.player;
        if (player != null && IRadiationManager.INSTANCE.isRadiationEnabled() && MekanismUtils.isPlayingMode(player)) {
            DefaultRadiationEntity c = player.getAttachedOrCreate(DefaultRadiationEntity.ATTACHMENT_TYPE);
            double radiation = c.getRadiation();
            double severity = RadiationScale.getScaledDoseSeverity(radiation) * 0.8;
            //Only update the previous radiation level at most once a tick
            if (lastTick != player.level().getGameTime()) {
                lastTick = player.level().getGameTime();
                if (prevRadiation < severity) {
                    prevRadiation = Math.min(severity, prevRadiation + 0.01);
                }
                if (prevRadiation > severity) {
                    prevRadiation = Math.max(severity, prevRadiation - 0.01);
                }
            }
            if (severity > RadiationManager.BASELINE) {
                int effect = (int) (prevRadiation * 255);
                int color = (0x701E1E << 8) + effect;
                MekanismRenderer.renderColorOverlay(guiGraphics, 0, 0, color);
            }
        }
    }
}