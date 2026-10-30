package mekanism.client.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mekanism.client.mixinhelper.BlockElementExtension;
import net.minecraft.client.renderer.block.model.BlockElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.reflect.Type;

@Mixin(targets = "net/minecraft/client/renderer/block/model/BlockElement$Deserializer")
public class BlockElementDeserializerMixin {
    @ModifyReturnValue(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockElement;", at = @At("RETURN"))
    private BlockElement deserializeMekanismData(BlockElement original, JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        BlockElementExtension.loadExtensionData(jsonElement, (BlockElementExtension) original);
        return original;
    }
}
