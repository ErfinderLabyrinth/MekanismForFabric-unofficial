package mekanism.common.registration;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;
import java.util.function.Supplier;

public class WrappedDeferredRegister<T> {

    protected final Registry<T> internal;

    protected WrappedDeferredRegister(Registry<T> internal) {
        this.internal = internal;
    }


    /**
     * @apiNote For use with vanilla or custom registries
     */
    protected WrappedDeferredRegister(ResourceKey<? extends Registry<T>> registryName) {
        this(new MappedRegistry<>(registryName, Lifecycle.stable()));
    }

    protected <I extends T, W extends WrappedRegistryObject<I>> W register(ResourceLocation id, Supplier<I> sup, Function<I, W> objectWrapper) {
        return objectWrapper.apply(Registry.register(internal, id, sup.get()));
    }

    public void register(RegistryAttribute... attributes) {
        FabricRegistryBuilder.from((WritableRegistry<? extends Object>) internal).attribute(RegistryAttribute.MODDED).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    }

    public void register() {
        register(RegistryAttribute.MODDED, RegistryAttribute.SYNCED);
    }

    /**
     * Only call this from mekanism and for custom registries
     */

    /**
     * Only call this from mekanism and for custom chemical registries
     */
    public Supplier<Registry<T>> createAndRegisterChemical() {
        return createAndRegister(/*builder -> builder.hasTags().setDefaultKey(Mekanism.rl("empty"))*/);
    }

    /**
     * Only call this from mekanism and for custom registries
     */
    public Supplier<Registry<T>> createAndRegister() {
        Supplier<Registry<T>> registry = () -> internal;
        register();
        return registry;
    }

    public Registry<T> getRegistry() {
        return internal;
    }
}