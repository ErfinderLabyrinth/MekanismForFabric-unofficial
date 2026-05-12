package mekanism.client.key;

import mekanism.client.ClientRegistrationUtil;
import mekanism.client.MekanismClient;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.base.KeySync;
import mekanism.common.config.MekanismConfig;
import mekanism.common.inventory.container.ModuleTweakerContainer;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.network.to_server.PacketModeChange;
import mekanism.common.network.to_server.PacketOpenGui;
import mekanism.common.network.to_server.PacketOpenGui.GuiType;
import mekanism.common.registries.MekanismSounds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

public class MekanismKeyHandler {

    public static final KeyMapping handModeSwitchKey = new MekKeyBindingBuilder().description(MekanismLang.KEY_HAND_MODE).keyCode(GLFW.GLFW_KEY_N)
          .onKeyDown((kb, isRepeat) -> {
              Player player = Minecraft.getInstance().player;
              if (player != null) {
                  if (IModeItem.isModeItem(player, EquipmentSlot.MAINHAND, false)) {
                      Mekanism.packetHandler().sendToServer(new PacketModeChange(EquipmentSlot.MAINHAND, player.isShiftKeyDown()));
                  } else if (!IModeItem.isModeItem(player, EquipmentSlot.MAINHAND) && IModeItem.isModeItem(player, EquipmentSlot.OFFHAND, false)) {
                      //Otherwise, try their offhand
                      Mekanism.packetHandler().sendToServer(new PacketModeChange(EquipmentSlot.OFFHAND, player.isShiftKeyDown()));
                  }
              }
          }).build();
    public static final KeyMapping headModeSwitchKey = new MekKeyBindingBuilder().description(MekanismLang.KEY_HEAD_MODE).keyCode(GLFW.GLFW_KEY_V)
          .onKeyDown((kb, isRepeat) -> handlePotentialModeItem(EquipmentSlot.HEAD)).build();
    public static final KeyMapping chestModeSwitchKey = new MekKeyBindingBuilder().description(MekanismLang.KEY_CHEST_MODE).keyCode(GLFW.GLFW_KEY_G)
          .onKeyDown((kb, isRepeat) -> handlePotentialModeItem(EquipmentSlot.CHEST)).build();
    public static final KeyMapping legsModeSwitchKey = new MekKeyBindingBuilder().description(MekanismLang.KEY_LEGS_MODE).keyCode(GLFW.GLFW_KEY_J)
          .onKeyDown((kb, isRepeat) -> handlePotentialModeItem(EquipmentSlot.LEGS)).build();
    public static final KeyMapping feetModeSwitchKey = new MekKeyBindingBuilder().description(MekanismLang.KEY_FEET_MODE).keyCode(GLFW.GLFW_KEY_B)
          .onKeyDown((kb, isRepeat) -> handlePotentialModeItem(EquipmentSlot.FEET)).build();
    public static final KeyMapping detailsKey = new MekKeyBindingBuilder().description(MekanismLang.KEY_DETAILS_MODE).keyCode(GLFW.GLFW_KEY_LEFT_SHIFT).build();
    public static final KeyMapping descriptionKey = new MekKeyBindingBuilder().description(MekanismLang.KEY_DESCRIPTION_MODE)
          .keyCode(GLFW.GLFW_KEY_N).build();
    public static final KeyMapping moduleTweakerKey = new MekKeyBindingBuilder().description(MekanismLang.KEY_MODULE_TWEAKER).keyCode(GLFW.GLFW_KEY_BACKSLASH)
          .onKeyDown((kb, isRepeat) -> {
              Player player = Minecraft.getInstance().player;
              if (player != null && ModuleTweakerContainer.hasTweakableItem(player)) {
                  Mekanism.packetHandler().sendToServer(new PacketOpenGui(GuiType.MODULE_TWEAKER));
              }
          }).build();
    public static final KeyMapping boostKey = new MekKeyBindingBuilder().description(MekanismLang.KEY_BOOST).keyCode(GLFW.GLFW_KEY_LEFT_CONTROL)
          .onKeyDown((kb, isRepeat) -> MekanismClient.updateKey(kb, KeySync.BOOST)).onKeyUp(kb -> MekanismClient.updateKey(kb, KeySync.BOOST)).build();
    public static final KeyMapping hudKey = new MekKeyBindingBuilder().description(MekanismLang.KEY_HUD).keyCode(GLFW.GLFW_KEY_H)
          .onKeyDown((kb, isRepeat) -> {
              MekanismConfig.client.enableHUD = !MekanismConfig.client.enableHUD;
              // save the updated config info
              //MekanismConfig.client.save();
          }).build();

    public static void registerKeybindings() {
        ClientRegistrationUtil.registerKeyBindings(handModeSwitchKey, headModeSwitchKey, chestModeSwitchKey, legsModeSwitchKey, feetModeSwitchKey,
              detailsKey, descriptionKey, moduleTweakerKey, boostKey, hudKey);
    }

    private static void handlePotentialModeItem(EquipmentSlot slot) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (IModeItem.isModeItem(player, slot)) {
                Mekanism.packetHandler().sendToServer(new PacketModeChange(slot, player.isShiftKeyDown()));
                SoundHandler.playSound(MekanismSounds.HYDRAULIC);
            } else if (Mekanism.hooks.CuriosLoaded) {
                //TODO support curios
//                CuriosIntegration.findFirstCurioAsResult(player, stack -> {
//                    if (stack.canEquip(slot, player) && IModeItem.isModeItem(stack, slot)) {
//                        return !(stack.getItem() instanceof IGasItem item) || item.hasGas(stack);
//                    }
//                    return false;
//                }).ifPresent(result -> {
//                    SlotContext slotContext = result.slotContext();
//                    Mekanism.packetHandler().sendToServer(new PacketModeChangeCurios(slotContext.identifier(), slotContext.index(), player.isShiftKeyDown()));
//                    SoundHandler.playSound(MekanismSounds.HYDRAULIC);
//                });
            }
        }
    }
}