package mekanism.common.network.to_client.container.property;

import mekanism.common.inventory.container.MekanismContainer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public class RegistryEntryPropertyData<V> extends PropertyData {

    private final Registry<V> registry;
    private final V value;

    public RegistryEntryPropertyData(short property, Registry<V> registry, V value) {
        super(PropertyType.REGISTRY_ENTRY, property);
        this.registry = registry;
        this.value = value;
    }

    public static <V> RegistryEntryPropertyData<V> readRegistryEntry(short property, FriendlyByteBuf buffer) {
        //Copy of IForgeFriendlyByteBuf#readRegistryId but captures the registry
        //TODO: If forge ever actually changes the registry name to being an id update this
        Registry<V> registry = (Registry<V>) BuiltInRegistries.REGISTRY.get(buffer.readResourceLocation());
        return new RegistryEntryPropertyData<>(property, registry, registry.byId(buffer.readVarInt()));
    }

    @Override
    public void handleWindowProperty(MekanismContainer container) {
        container.handleWindowProperty(getProperty(), value);
    }

    @Override
    public void writeToPacket(FriendlyByteBuf buffer) {
        super.writeToPacket(buffer);
        buffer.writeVarInt(registry.getId(value));
    }
}