package mekanism.common.mixinhelper;

import net.minecraft.data.HashCache;

import java.util.Map;

public interface HashCacheExtension {
    Map<String, HashCache.ProviderCache> mekanism$originalCache();
}
