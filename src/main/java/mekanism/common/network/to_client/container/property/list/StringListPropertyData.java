package mekanism.common.network.to_client.container.property.list;

import mekanism.common.network.BasePacketHandler;
import mekanism.common.util.NetworkUtil;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StringListPropertyData extends ListPropertyData<String> {

    public StringListPropertyData(short property, @NotNull List<String> values) {
        super(property, ListType.STRING, values);
    }

    static StringListPropertyData read(short property, ListPropertyReader<String> reader) {
        return new StringListPropertyData(property, reader.apply(NetworkUtil::readString));
    }

    @Override
    protected void writeListElement(FriendlyByteBuf buffer, String value) {
        buffer.writeUtf(value);
    }
}