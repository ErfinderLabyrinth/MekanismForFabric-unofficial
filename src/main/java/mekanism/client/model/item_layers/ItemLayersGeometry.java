package mekanism.client.model.item_layers;

import mekanism.client.model.CustomGeometry;

import java.util.*;

public class ItemLayersGeometry extends CustomGeometry {
    List<Integer> fullLightLayers;
    public ItemLayersGeometry(List<Integer> fullLightLayers) {
        this.fullLightLayers = fullLightLayers;
    }

    public List<Integer> getFullLightLayers() {
        return fullLightLayers;
    }
}
