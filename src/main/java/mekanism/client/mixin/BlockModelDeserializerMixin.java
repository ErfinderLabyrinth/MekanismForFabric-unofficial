package mekanism.client.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mekanism.api.MekanismAPI;
import mekanism.client.model.CustomGeometry;
import mekanism.client.model.energycube.EnergyCubeModelLoader;
import mekanism.client.model.robit.RobitModel;
import mekanism.client.render.obj.TransmitterLoader;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.Locale;

@Mixin(BlockModel.Deserializer.class)
public class BlockModelDeserializerMixin {
    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;", at = @At("RETURN"), cancellable = true)
    public void addLoaderSupport(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<BlockModel> cir) {
        CustomGeometry customGeometry = deserializeGeometry(jsonDeserializationContext, jsonElement.getAsJsonObject());
    }

    @Unique
    private static CustomGeometry deserializeGeometry(JsonDeserializationContext deserializationContext, JsonObject object) throws JsonParseException
    {
        if (!object.has("loader"))
            return null;

        var name = new ResourceLocation(GsonHelper.getAsString(object, "loader"));
        if (name.equals(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "robit"))) {
            return RobitModel.Loader.INSTANCE.read(object, deserializationContext);
        }
        if (name.equals(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "energy_cube"))) {
            return EnergyCubeModelLoader.INSTANCE.read(object, deserializationContext);
        }
        if (name.equals(new ResourceLocation(MekanismAPI.MEKANISM_MODID, "transmitter"))) {
            return TransmitterLoader.INSTANCE.read(object, deserializationContext);
        }
        throw new JsonParseException(String.format(Locale.ENGLISH, "Model loader '%s' not found.", name));

        //return loader.read(object, deserializationContext);
    }
}
