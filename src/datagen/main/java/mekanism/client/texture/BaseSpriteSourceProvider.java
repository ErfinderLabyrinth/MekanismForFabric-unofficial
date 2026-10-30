package mekanism.client.texture;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.common.registration.impl.FluidDeferredRegister;
import mekanism.common.registration.impl.FluidRegistryObject;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public abstract class BaseSpriteSourceProvider implements DataProvider {
    public static final ResourceLocation BLOCKS_ATLAS = new ResourceLocation("blocks");

    private final Map<ResourceLocation, List<SpriteSource>> atlases = new HashMap<>();
    protected final PackOutput output;
    protected final String modid;
    private final Set<ResourceLocation> trackedSingles = new HashSet<>();

    protected BaseSpriteSourceProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    @Override
    public String getName() {
        return "SpriteSource generator of " + modid;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        addSources();

        Path resourcePack = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK);
        for(Map.Entry<ResourceLocation, List<SpriteSource>> atlas : atlases.entrySet()) {
            Path path = resourcePack.resolve(atlas.getKey().getNamespace() + "/atlases/" + atlas.getKey().getPath() + ".json");
            DataResult<JsonElement> output = SpriteSources.FILE_CODEC.encodeStart(JsonOps.INSTANCE, atlas.getValue());
            futures.add(DataProvider.saveStable(cachedOutput, output.getOrThrow(false, error -> {}), path));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{}));
    }

    protected abstract void addSources();

    protected void addFiles(List<SpriteSource> atlas, ResourceLocation... resourceLocations) {
        for (ResourceLocation rl : resourceLocations) {
            //Only add this source if we haven't already added it as a direct single file source
            if (trackedSingles.add(rl)) {
                atlas.add(new SingleFile(rl, Optional.empty()));
            }
        }
    }

    //TODO - 1.20: Re-evaluate doing this
    protected void addChemicalSprites(List<SpriteSource> atlas) {
        addChemicalSprites(atlas, MekanismAPI.gasRegistry());
        addChemicalSprites(atlas, MekanismAPI.infuseTypeRegistry());
        addChemicalSprites(atlas, MekanismAPI.pigmentRegistry());
        addChemicalSprites(atlas, MekanismAPI.slurryRegistry());
    }

    private <CHEMICAL extends Chemical<CHEMICAL>> void addChemicalSprites(List<SpriteSource> atlas, Registry<CHEMICAL> chemicalRegistry) {
        for (Chemical<?> chemical : chemicalRegistry) {
            //TODO - 1.20: Evaluate this
            if (chemical.getRegistryName().getNamespace().equals(modid)) {
                addFiles(atlas, chemical.getIcon());
            }
        }
    }

    protected void addFluids(List<SpriteSource> atlas, FluidDeferredRegister register) {
        for (FluidRegistryObject<?, ?, ?, ?> fluidRO : register.getAllFluids()) {
            FluidDeferredRegister.FluidTypeRenderProperties properties  = fluidRO.getRenderProperties();
            addFiles(atlas, properties.stillTexture, properties.flowingTexture, properties.overlayTexture);
        }
    }

    protected void addDirectory(List<SpriteSource> atlas, String directory, String spritePrefix) {
        atlas.add(new DirectoryLister(directory, spritePrefix));
    }

    protected final List<SpriteSource> atlas(ResourceLocation atlas)
    {
        return atlases.computeIfAbsent(atlas, $ -> new ArrayList<>());
    }
}