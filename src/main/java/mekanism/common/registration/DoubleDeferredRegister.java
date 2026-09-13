package mekanism.common.registration;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class DoubleDeferredRegister<PRIMARY, SECONDARY> {

    private final Registry<PRIMARY> primaryRegister;
    private final Registry<SECONDARY> secondaryRegister;

    public DoubleDeferredRegister(Registry<PRIMARY> primaryRegistry, Registry<SECONDARY> secondaryRegistry) {
        this.primaryRegister = primaryRegistry;
        this.secondaryRegister = secondaryRegistry;
    }

//    protected DoubleDeferredRegister(String modid, ResourceKey<? extends Registry<PRIMARY>> primaryRegistryName,
//          ResourceKey<? extends Registry<SECONDARY>> secondaryRegistryName) {
//        this(DeferredRegister.create(primaryRegistryName, modid), DeferredRegister.create(secondaryRegistryName, modid));
//    }

    public <P extends PRIMARY, S extends SECONDARY, W extends DoubleWrappedRegistryObject<P, S>> W register(ResourceLocation name, Supplier<? extends P> primarySupplier,
                                                                                                            Supplier<? extends S> secondarySupplier, BiFunction<P, S, W> objectWrapper) {
        P primary = Registry.register(primaryRegister, name, primarySupplier.get());
        S secondary = Registry.register(secondaryRegister, name, secondarySupplier.get());
        return objectWrapper.apply(primary, secondary);
    }

    public <P extends PRIMARY, S extends SECONDARY, W extends DoubleWrappedRegistryObject<P, S>> W register(ResourceLocation rl, Supplier<? extends P> primarySupplier,
          Function<P, S> secondarySupplier, BiFunction<P, S, W> objectWrapper) {
        return registerAdvanced(rl, primarySupplier, secondarySupplier, objectWrapper);
    }

    public <P extends PRIMARY, S extends SECONDARY, W extends DoubleWrappedRegistryObject<P, S>> W registerAdvanced(ResourceLocation rl, Supplier<? extends P> primarySupplier,
          Function<P, S> secondarySupplier, BiFunction<P, S, W> objectWrapper) {
        P primaryObject = Registry.register(primaryRegister, rl, primarySupplier.get());
        return objectWrapper.apply(primaryObject, Registry.register(secondaryRegister, rl, secondarySupplier.apply(primaryObject)));
    }
}