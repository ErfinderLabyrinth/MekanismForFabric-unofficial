package mekanism.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mekanism.client.network.ClientPacketHandler;
import mekanism.client.render.RenderTickHandler;
import mekanism.client.render.tileentity.RenderSPS;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.base.IModModule;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.lib.security.SecurityData;
import mekanism.common.lib.transmitter.TransmitterNetworkRegistry;
import mekanism.common.network.to_server.PacketKey;
import mekanism.common.recipe.MekanismRecipeType;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class MekanismClient implements ClientModInitializer {
    private static ClientPacketHandler clientPacketHandler = new ClientPacketHandler();

    @Override
    public void onInitializeClient() {
        ClientRegistration.init();
        clientPacketHandler.initialize();
    }

    public static final Map<UUID, SecurityData> clientSecurityMap = new Object2ObjectOpenHashMap<>();
    public static final Map<UUID, String> clientUUIDMap = new Object2ObjectOpenHashMap<>();

    public static long ticksPassed = 0;

    public static void updateKey(KeyMapping key, int type) {
        updateKey(key.isDown(), type);
    }

    public static void updateKey(boolean pressed, int type) {
        if (Minecraft.getInstance().player != null) {
            UUID playerUUID = Minecraft.getInstance().player.getUUID();
            boolean down = Minecraft.getInstance().screen == null && pressed;
            if (down != Mekanism.keyMap.has(playerUUID, type)) {
                clientPacketHandler.sendToServer(new PacketKey(type, down));
                Mekanism.keyMap.update(playerUUID, type, down);
            }
        }
    }

    public static ClientPacketHandler clientPacketHandler() {
        return clientPacketHandler;
    }

    public static void reset() {
        clientSecurityMap.clear();
        clientUUIDMap.clear();

        ClientTickHandler.portableTeleports.clear();
        ClientTickHandler.visionEnhancement = false;

        Mekanism.playerState.clear(true);
        Mekanism.activeVibrators.clear();
        RadiationManager.get().resetClient();
        SoundHandler.radiationSoundMap.clear();
        RenderSPS.clearBoltRenderers();
        TransmitterNetworkRegistry.getInstance().clearClientNetworks();
        RenderTickHandler.clearQueued();
        MekanismRecipeType.clearCache();

        for (IModModule module : Mekanism.modulesLoaded) {
            module.resetClient();
        }
    }

    public static void launchClient() {
        for (IModModule module : Mekanism.modulesLoaded) {
            module.launchClient();
        }
    }

    @Nullable
    public static Level tryGetClientWorld() {
        return Minecraft.getInstance().level;
    }

    @Nullable
    public static IntegratedServer tryGetSingleplayerServer() {
        return Minecraft.getInstance().getSingleplayerServer();
    }

    @Nullable
    public static Player tryGetClientPlayer() {
        return Minecraft.getInstance().player;
    }
}