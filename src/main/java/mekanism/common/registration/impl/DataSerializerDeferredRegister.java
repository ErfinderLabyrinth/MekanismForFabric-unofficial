package mekanism.common.registration.impl;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class DataSerializerDeferredRegister{

    public DataSerializerDeferredRegister(String modid) {
    }

    public <T extends Enum<T>> DataSerializerRegistryObject<T> registerEnum(String name, Class<T> enumClass) {
        return register(name, () -> EntityDataSerializer.simpleEnum(enumClass));
    }

    public <T> DataSerializerRegistryObject<T> registerSimple(String name, FriendlyByteBuf.Writer<T> writer, FriendlyByteBuf.Reader<T> reader) {
        return register(name, () -> EntityDataSerializer.simple(writer, reader));
    }

    public <T> DataSerializerRegistryObject<T> register(String name, FriendlyByteBuf.Writer<T> writer, FriendlyByteBuf.Reader<T> reader, UnaryOperator<T> copier) {
        return register(name, () -> new EntityDataSerializer<>() {
            @Override
            public void write(@NotNull FriendlyByteBuf buffer, @NotNull T value) {
                writer.accept(buffer, value);
            }

            @NotNull
            @Override
            public T read(@NotNull FriendlyByteBuf buffer) {
                return reader.apply(buffer);
            }

            @NotNull
            @Override
            public T copy(@NotNull T value) {
                return copier.apply(value);
            }
        });
    }

    public <T> DataSerializerRegistryObject<T> register(String name, Supplier<EntityDataSerializer<T>> sup) {
        EntityDataSerializer<T> eds = sup.get();
        EntityDataSerializers.registerSerializer(eds);
        return new DataSerializerRegistryObject<>(eds);
    }
}