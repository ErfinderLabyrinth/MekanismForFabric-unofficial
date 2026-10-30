package mekanism.common.network.to_client.container.property.list;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RegistryEntryListPropertyData<V> extends ListPropertyData<V> {

    private final Registry<V> registry;

    public RegistryEntryListPropertyData(short property, Registry<V> registry, @NotNull List<V> values) {
        super(property, ListType.REGISTRY_ENTRY, values);
        this.registry = registry;
    }

    static <V> RegistryEntryListPropertyData<V> read(short property, FriendlyByteBuf buffer) {
        //Based off of IForgeFriendlyByteBuf#readRegistryId but split into two parts so we only have to write it once for the entire list
        //TODO: If forge ever actually changes the registry name to being an id update this
        Registry<V> registry = (Registry<V>) BuiltInRegistries.REGISTRY.get(buffer.readResourceLocation());
        return new RegistryEntryListPropertyData<>(property, registry, buffer.readList(r -> {
            int id = r.readVarInt();
            return registry.byId(id);
        }));
    }

    @Override
    protected void writeList(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(registry.key().location());
        super.writeList(buffer);
    }

    @Override
    protected void writeListElement(FriendlyByteBuf buffer, V value) {
        buffer.writeVarInt(registry.getId(value));
    }
}