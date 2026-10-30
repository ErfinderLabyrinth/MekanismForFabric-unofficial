package mekanism.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import mekanism.common.Mekanism;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.ShaderInstance;

import java.io.IOException;
import java.util.function.Supplier;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.client.event.RegisterShadersEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;

//@Mod.EventBusSubscriber(modid = Mekanism.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MekanismShaders {

    static final ShaderTracker MEKASUIT = new ShaderTracker();
    //Merge of position_color_tex and rendertype_lightning
    static final ShaderTracker SPS = new ShaderTracker();
    //Copy of position_color_tex with support for fog
    static final ShaderTracker FLAME = new ShaderTracker();


    public static void registerShaders(CoreShaderRegistrationCallback.RegistrationContext context) throws IOException {
        context.register(Mekanism.rl("rendertype_flame"), DefaultVertexFormat.POSITION_COLOR_TEX, shader -> FLAME.setInstance(shader));
        context.register(Mekanism.rl("rendertype_mekasuit"), DefaultVertexFormat.NEW_ENTITY, shader -> MEKASUIT.setInstance(shader));
        context.register(Mekanism.rl("rendertype_sps"), DefaultVertexFormat.POSITION_COLOR_TEX, shader -> SPS.setInstance(shader));
    }

    static class ShaderTracker implements Supplier<ShaderInstance> {

        private ShaderInstance instance;
        final ShaderStateShard shard = new ShaderStateShard(this);

        private ShaderTracker() {
        }

        private void setInstance(ShaderInstance instance) {
            this.instance = instance;
        }

        @Override
        public ShaderInstance get() {
            return instance;
        }
    }
}