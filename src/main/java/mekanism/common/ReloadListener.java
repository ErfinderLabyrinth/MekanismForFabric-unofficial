package mekanism.common;

import mekanism.common.base.TagCache;
import mekanism.common.recipe.MekanismRecipeType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.CloseableResourceManager;

public class ReloadListener implements ServerLifecycleEvents.EndDataPackReload {
    @Override
    public void endDataPackReload(MinecraftServer server, CloseableResourceManager resourceManager, boolean success) {
        CommonWorldTickHandler.flushTagAndRecipeCaches = true;
        MekanismRecipeType.clearCache();
        TagCache.resetTagCaches();
    }
}