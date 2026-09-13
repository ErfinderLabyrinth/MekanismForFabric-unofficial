package mekanism.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class MekKeyHandler {

    private MekKeyHandler() {
    }

    public static boolean isKeyPressed(KeyMapping keyBinding) {
        if (keyBinding.isDown()) {
            return true;
        }
        return isKeyDown(keyBinding);
    }

    private static boolean isKeyDown(KeyMapping keyBinding) {
        InputConstants.Key key = keyBinding.key;
        int keyCode = key.getValue();
        if (keyCode != InputConstants.UNKNOWN.getValue()) {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            try {
                if (key.getType() == InputConstants.Type.KEYSYM) {
                    return InputConstants.isKeyDown(windowHandle, keyCode);
                } else if (key.getType() == InputConstants.Type.MOUSE) {
                    return GLFW.glfwGetMouseButton(windowHandle, keyCode) == GLFW.GLFW_PRESS;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    public static boolean isRadialPressed() {
        KeyMapping keyBinding = MekanismKeyHandler.handModeSwitchKey;
        if (keyBinding.isDown()) {
            return true;
        }
        return isKeyDown(keyBinding);
    }
}