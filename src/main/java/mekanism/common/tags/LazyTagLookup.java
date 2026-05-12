package mekanism.common.tags;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalTags;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;

import java.util.function.Supplier;

public record LazyTagLookup<TYPE>(TagKey<TYPE> key, Supplier<HolderSet.Named<TYPE>> tagSupplier, Registry<TYPE> registry) {

    public static <TYPE> LazyTagLookup<TYPE> create(Registry<TYPE> registry, HolderSet.Named<TYPE> key) {
        return new LazyTagLookup<>(key.key(), () -> key, registry);
    }

    public static <TYPE> LazyTagLookup<TYPE> create(Registry<TYPE> registry, TagKey<TYPE> key) {
        return new LazyTagLookup<>(key, () -> registry.getTag(key).get(), registry);
    }

    public static <CHEMICAL extends Chemical<CHEMICAL>> LazyTagLookup<CHEMICAL> create(ChemicalTags<CHEMICAL> registry, TagKey<CHEMICAL> key) {
        return new LazyTagLookup<>(key, () -> registry.getTag(key).get(), registry.getRegistry());
    }

    public Supplier<HolderSet.Named<TYPE>> tagSupplier() {
        return tagSupplier;
    }

    public boolean contains(TYPE element) {
        return tagSupplier().get().contains(registry.wrapAsHolder(element));
    }

    public boolean isEmpty() {
        return tagSupplier().get().stream().findAny().isEmpty();
    }
}