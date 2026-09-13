package mekanism.client.model.composite;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockModel;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CompositeModelLoader {
    public static final CompositeModelLoader INSTANCE = new CompositeModelLoader();

    @NotNull
    public CompositeGeometry read(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext ctx) {
        JsonObject childrenJson = jsonObject.getAsJsonObject("children");
        Map<String, BlockModel> children = childrenJson.entrySet().stream()
                .map(child -> new AbstractMap.SimpleEntry<>(child.getKey(), (BlockModel) ctx.deserialize(child.getValue(), BlockModel.class)))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        return new CompositeGeometry(children);
    }
}
