package mekanism.client.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mekanism.client.mixinhelper.BlockElementExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.lang.reflect.Type;

@Mixin(targets = "net/minecraft/client/renderer/block/model/BlockElementFace$Deserializer")
public class BlockElementFaceDeserializerMixin {
    @ModifyReturnValue(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Ljava/lang/Object;", at = @At("RETURN"))
    private @Coerce Object deserializeMekanismData(@Coerce Object original, JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        BlockElementExtension.loadExtensionData(jsonElement, (BlockElementExtension) original);
        return original;
    }
}
