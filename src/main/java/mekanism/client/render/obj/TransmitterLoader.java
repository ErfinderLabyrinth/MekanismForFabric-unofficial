package mekanism.client.render.obj;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.felnull.specialmodelloader.api.SpecialModelLoaderAPI;
import mekanism.api.JsonConstants;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.UnbakedModel;
import org.jetbrains.annotations.NotNull;

public class TransmitterLoader {

    public static final TransmitterLoader INSTANCE = new TransmitterLoader();

    private TransmitterLoader() {
    }

    @NotNull
    public TransmitterModel read(@NotNull JsonObject modelContents, @NotNull JsonDeserializationContext deserializationContext) throws JsonParseException {
        //Wrap the Obj loader to read our file
        UnbakedModel model = null;
        try {
            model = SpecialModelLoaderAPI.getInstance().getObjLoader().loadModel(Minecraft.getInstance().getResourceManager(), modelContents);
        } catch (ModelProviderException e) {
            throw new RuntimeException(e);
        }
        UnbakedModel glass = null;
        if (modelContents.has(JsonConstants.GLASS)) {
            try {
                glass = SpecialModelLoaderAPI.getInstance().getObjLoader().loadModel(Minecraft.getInstance().getResourceManager(), modelContents.getAsJsonObject(JsonConstants.GLASS));
            } catch (ModelProviderException e) {
                throw new RuntimeException(e);
            }
        }
        return new TransmitterModel(model, glass);
    }
}