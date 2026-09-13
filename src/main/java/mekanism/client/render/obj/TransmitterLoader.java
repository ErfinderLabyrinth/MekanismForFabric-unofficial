package mekanism.client.render.obj;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mekanism.api.JsonConstants;
import mekanism.client.model.obj.ObjModel;
import mekanism.client.model.obj.ObjParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TransmitterLoader {

    public static final TransmitterLoader INSTANCE = new TransmitterLoader();

    private TransmitterLoader() {
    }

    @NotNull
    public TransmitterModel read(@NotNull JsonObject modelContents, @NotNull JsonDeserializationContext deserializationContext) throws JsonParseException {
        //Wrap the Obj loader to read our file
        ObjModel model;
        try {
            ResourceLocation modelRl = new ResourceLocation(modelContents.get("model").getAsString());
            model = ObjParser.load(Minecraft.getInstance().getResourceManager().open(modelRl), modelRl.withPath(modelRl.getPath().substring(0, modelRl.getPath().lastIndexOf('/'))), false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ObjModel glass = null;
        if (modelContents.has(JsonConstants.GLASS)) {
            try {
                ResourceLocation glassRl = new ResourceLocation(modelContents.getAsJsonObject(JsonConstants.GLASS).get("model").getAsString());
                glass = ObjParser.load(Minecraft.getInstance().getResourceManager().open(glassRl), glassRl.withPath(glassRl.getPath().substring(0, glassRl.getPath().lastIndexOf('/'))), false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new TransmitterModel(model, glass);
    }
}