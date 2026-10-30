package mekanism.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mekanism.common.mixinhelper.DataGeneratorHashCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataGenerator.class)
public class DataGeneratorMixin {
    @ModifyExpressionValue(method = "run", at = @At(value = "NEW", target = "(Ljava/nio/file/Path;Ljava/util/Collection;Lnet/minecraft/WorldVersion;)Lnet/minecraft/data/HashCache;"))
    private static HashCache captureGlobalCache(HashCache cache) {
        DataGeneratorHashCache.globalCache = cache;
        return cache;
    }
}
