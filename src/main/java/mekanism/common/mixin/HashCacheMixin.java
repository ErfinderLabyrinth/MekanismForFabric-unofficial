package mekanism.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mekanism.common.mixinhelper.HashCacheExtension;
import net.minecraft.WorldVersion;
import net.minecraft.data.HashCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

@Mixin(HashCache.class)
public class HashCacheMixin implements HashCacheExtension {
    @Unique
    private Map<String, HashCache.ProviderCache> originalCache;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initOriginalCache(Path path, Collection collection, WorldVersion worldVersion, CallbackInfo ci, @Local Map<String, HashCache.ProviderCache> map) {
        originalCache = Map.copyOf(map);
    }

    @Override
    public Map<String, HashCache.ProviderCache> mekanism$originalCache() {
        return originalCache;
    }
}
