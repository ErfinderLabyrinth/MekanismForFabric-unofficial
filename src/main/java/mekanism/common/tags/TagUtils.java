package mekanism.common.tags;

import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagUtils {

    public static <TYPE> Optional<HolderSet.Named<TYPE>> tag(Registry<TYPE> registry, TagKey<TYPE> key) {
        return registry.getTag(key);
    }

    public static <TYPE> HolderSet.Named<TYPE> createKey(Registry<TYPE> registry, ResourceLocation tag) {
        return registry.getOrCreateTag(createKey((ResourceKey<Registry<TYPE>>) registry.key(), tag));
    }

    public static <TYPE> TagKey<TYPE> createKey(ResourceKey<Registry<TYPE>> registryKey, ResourceLocation tag) {
        return TagKey.create(registryKey, tag);
    }

    public static <TYPE> Stream<TagKey<TYPE>> tagEntries(Registry<TYPE> registry, TYPE element) {
        return registry.wrapAsHolder(element).tags();
    }

    public static <TYPE> Stream<HolderSet.Named<TYPE>> tags(Registry<TYPE> registry, TYPE element) {
        return tagEntries(registry, element).map(key -> registry.getTag(key).get());
    }

//    public static <TYPE> Set<TagKey<TYPE>> tags(ITagManager<TYPE> tagManager, TYPE element) {
//        return tagsStream(tagManager, element).collect(Collectors.toSet());
//    }

//    public static <TYPE> Stream<TagKey<TYPE>> tagsStream(Registry<TYPE> registry, TYPE element) {
//        return tagsStream(manager(registry), element);
//    }

//    public static <TYPE> Stream<TagKey<TYPE>> tagsStream(ITagManager<TYPE> tagManager, TYPE element) {
//        return tagManager.getReverseTag(element)
//              .map(IReverseTag::getTagKeys)
//              .orElse(Stream.empty());
//    }

    public static <TYPE> Stream<ResourceLocation> tagNames(Registry<TYPE> registry, TYPE element) {
        return tagEntries(registry, element).map(TagKey::location);
    }

//    public static <TYPE> Set<ResourceLocation> tagNames(ITagManager<TYPE> tagManager, TYPE element) {
//        return tagNames(tagsStream(tagManager, element));
//    }

    public static Set<ResourceLocation> tagNames(Stream<? extends TagKey<?>> stream) {
        return stream.map(TagKey::location)
              .collect(Collectors.toUnmodifiableSet());
    }
}