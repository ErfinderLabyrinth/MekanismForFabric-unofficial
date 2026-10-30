package mekanism.common.tile.interfaces;

import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.gas.attribute.GasAttributes.Radiation;
import mekanism.api.math.MathUtils;
import mekanism.api.radiation.IRadiationManager;

import java.util.List;

public interface ITileRadioactive {

    static float calculateRadiationScale(List<IGasTank> tanks) {
        if (IRadiationManager.INSTANCE.isRadiationEnabled() && !tanks.isEmpty()) {
            float summedScale = 0;
            for (IGasTank tank : tanks) {
                if (!tank.isEmpty() && tank.getStack().has(Radiation.class)) {
                    //TODO: Eventually we may want to debate doing this based on the radioactivity
                    // but for now this will work well
                    summedScale += tank.getStored() / (float) tank.getCapacity();
                }
            }
            return summedScale / tanks.size();
        }
        return 0;
    }

    float getRadiationScale();

    default int getRadiationParticleCount() {
        return MathUtils.clampToInt(10 * getRadiationScale());
    }
}