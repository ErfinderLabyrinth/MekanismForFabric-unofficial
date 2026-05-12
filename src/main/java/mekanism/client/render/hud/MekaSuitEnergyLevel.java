package mekanism.client.render.hud;

import mekanism.api.math.FloatingLong;
import mekanism.client.gui.GuiUtils;
import mekanism.client.gui.element.bar.GuiBar;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

public class MekaSuitEnergyLevel {

    public static final MekaSuitEnergyLevel INSTANCE = new MekaSuitEnergyLevel();
    private static final ResourceLocation POWER_BAR = MekanismUtils.getResource(ResourceType.GUI_BAR, "horizontal_power_long.png");

    private MekaSuitEnergyLevel() {
    }

    public void render(Gui gui, GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight) {
        if (!gui.minecraft.options.hideGui && gui.minecraft.gameMode.canHurtPlayer() && gui.minecraft.getCameraEntity() instanceof Player) {
            FloatingLong capacity = FloatingLong.ZERO, stored = FloatingLong.ZERO;
            for (ItemStack stack : gui.minecraft.player.getArmorSlots()) {
                if (stack.getItem() instanceof ItemMekaSuitArmor) {
                    EnergyStorage storage = ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM);
                    if (storage != null) {
                        capacity = capacity.plusEqual(storage.getCapacity());
                        stored = stored.plusEqual(storage.getAmount());
                    }
                }
            }
            if (!capacity.isZero()) {
                int x = screenWidth / 2 - 91;
                int y = screenHeight + 2;
                int length = (int) Math.round(stored.divide(capacity).doubleValue() * 79);
                GuiUtils.renderExtendedTexture(guiGraphics, GuiBar.BAR, 2, 2, x, y, 81, 6);
                guiGraphics.blit(POWER_BAR, x + 1, y + 1, length, 4, 0, 0, length, 4, 79, 4);
            }
        }
    }
}