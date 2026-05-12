package mekanism.client.model;

import mekanism.client.ClientRegistration;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

public class MekanismModelLoadingPlugin implements ModelLoadingPlugin {

    @Override
    public void onInitializeModelLoader(Context context) {
        MekanismModelCache.INSTANCE.setup(context);

        context.modifyModelAfterBake().register(ClientRegistration::onModelBake);
    }
}