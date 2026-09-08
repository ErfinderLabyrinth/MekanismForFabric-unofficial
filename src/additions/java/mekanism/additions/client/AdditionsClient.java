package mekanism.additions.client;

import io.netty.channel.local.LocalAddress;
import mekanism.additions.client.model.MekanismAdditionsModelLoadingPlugin;
import mekanism.additions.client.voice.VoiceClient;
import mekanism.additions.common.config.MekanismAdditionsConfig;
import mekanism.common.Mekanism;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class AdditionsClient implements ClientModInitializer {

    private AdditionsClient() {
    }

    private static VoiceClient voiceClient;

    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(new MekanismAdditionsModelLoadingPlugin());
        MekanismAdditionsConfig.registerClientConfigs();
        AdditionsClientRegistration.init();

    }

    public static void reset() {
        if (voiceClient != null) {
            voiceClient.disconnect();
            voiceClient = null;
        }
    }

    public static void launch() {
        if (MekanismAdditionsConfig.additions.voiceServerEnabled) {
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            SocketAddress address = connection == null ? null : connection.getConnection().getRemoteAddress();
            //local connection
            if (address instanceof LocalAddress) {
                voiceClient = new VoiceClient("127.0.0.1");
                AdditionsClient.voiceClient.start();
                //remote connection
            } else if (address instanceof InetSocketAddress socketAddress) {
                voiceClient = new VoiceClient(socketAddress.getHostString());
                AdditionsClient.voiceClient.start();
            } else {
                Mekanism.logger.error("Unknown connection address detected, voice client will not launch.");
            }
        }
    }
}