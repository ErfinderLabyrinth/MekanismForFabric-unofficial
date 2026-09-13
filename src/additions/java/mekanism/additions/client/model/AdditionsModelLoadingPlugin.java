package mekanism.additions.client.model;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

public class AdditionsModelLoadingPlugin implements ModelLoadingPlugin {
    @Override
    public void onInitializeModelLoader(Context context) {
        AdditionsModelCache.INSTANCE.setup(context);
    }
}
