package mekanism.common.network.to_client;

import mekanism.api.MekanismAPI;
import mekanism.client.render.RenderTickHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.effect.BoltEffect;
import mekanism.common.lib.effect.BoltEffect.BoltRenderInfo;
import mekanism.common.lib.effect.BoltEffect.SpawnFunction;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.util.NetworkUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.function.BooleanSupplier;

public class PacketLightningRender implements IMekanismPacket {
    public static final PacketType<PacketLightningRender> TYPE = PacketType.create(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "lightning_render"), PacketLightningRender::decode);

    private final LightningPreset preset;
    private final Vec3 start;
    private final Vec3 end;
    private final int renderer;
    private final int segments;

    public PacketLightningRender(LightningPreset preset, int renderer, Vec3 start, Vec3 end, int segments) {
        this.preset = preset;
        this.renderer = renderer;
        this.start = start;
        this.end = end;
        this.segments = segments;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (preset.shouldAdd.getAsBoolean()) {
            RenderTickHandler.renderBolt(renderer, preset.boltCreator.create(start, end, segments));
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(preset);
        buffer.writeVarInt(renderer);
        NetworkUtil.writeVector3d(buffer, start);
        NetworkUtil.writeVector3d(buffer, end);
        buffer.writeVarInt(segments);
    }

    public static PacketLightningRender decode(FriendlyByteBuf buffer) {
        LightningPreset preset = buffer.readEnum(LightningPreset.class);
        int renderer = buffer.readVarInt();
        Vec3 start = NetworkUtil.readVector3d(buffer);
        Vec3 end = NetworkUtil.readVector3d(buffer);
        int segments = buffer.readVarInt();
        return new PacketLightningRender(preset, renderer, start, end, segments);
    }

    @FunctionalInterface
    public interface BoltCreator {

        BoltEffect create(Vec3 start, Vec3 end, int segments);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public enum LightningPreset {
        MAGNETIC_ATTRACTION(() -> MekanismConfig.CLIENT.client.renderMagneticAttractionParticles, (start, end, segments) ->
              new BoltEffect(BoltRenderInfo.ELECTRICITY, start, end, segments).size(0.04F).lifespan(8).spawn(SpawnFunction.noise(8, 4))),
        TOOL_AOE(() -> MekanismConfig.CLIENT.client.renderToolAOEParticles, (start, end, segments) ->
              new BoltEffect(BoltRenderInfo.ELECTRICITY, start, end, segments).size(0.015F).lifespan(12).spawn(SpawnFunction.NO_DELAY));

        private final BooleanSupplier shouldAdd;
        private final BoltCreator boltCreator;

        LightningPreset(BooleanSupplier shouldAdd, BoltCreator boltCreator) {
            this.shouldAdd = shouldAdd;
            this.boltCreator = boltCreator;
        }
    }
}