package mekanism.common.registration;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class WrappedDatapackDeferredRegister<T> extends WrappedDeferredRegister<Codec<? extends T>> {

    protected final ResourceKey<Registry<T>> datapackRegistryName;
    private final String modid;

    protected WrappedDatapackDeferredRegister(String modid, ResourceKey<? extends Registry<Codec<? extends T>>> serializerRegistryName,
          ResourceKey<Registry<T>> datapackRegistryName) {
        super(serializerRegistryName);
        this.modid = modid;
        this.datapackRegistryName = datapackRegistryName;
    }

    /**
     * Only call this from mekanism and for custom datapack registries
     */
    public Codec<T> createAndRegisterDatapack(Function<? super T, Codec<? extends T>> baseCodec) {
        return createAndRegisterDatapack(baseCodec, null);
    }

    /**
     * Only call this from mekanism and for custom datapack registries
     */
    public Codec<T> createAndRegisterDatapack(Function<? super T, Codec<? extends T>> baseCodec, @Nullable Codec<T> networkCodec) {
        //Create the register for the serializers and mark they don't need to be persisted or sync'd
        register();



        Registry<Codec<? extends T>> registry = internal;
        Codec<T> directCodec = ExtraCodecs.lazyInitializedCodec(() -> registry.byNameCodec())
              .dispatch(baseCodec, Function.identity());
        //Create a new datapack registry using the direct codec that is created based on the serializer's codec
        if(networkCodec != null) {
            DynamicRegistries.registerSynced(datapackRegistryName, directCodec, networkCodec);
        } else {
            DynamicRegistries.registerSynced(datapackRegistryName, directCodec);
        }
        return directCodec;
    }

    public ResourceKey<T> dataKey(String name) {
        return ResourceKey.create(datapackRegistryName, new ResourceLocation(modid, name));
    }
}