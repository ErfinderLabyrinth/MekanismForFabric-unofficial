package mekanism.common.tile.component.config.slot;

import mekanism.api.heat.IHeatCapacitor;

import java.util.List;

public class HeatSlotInfo extends BaseSlotInfo {

    private final List<IHeatCapacitor> capacitors;

    protected HeatSlotInfo(boolean canInput, boolean canOutput, IHeatCapacitor... capacitors) {
        this(canInput, canOutput, List.of(capacitors));
    }

    public HeatSlotInfo(boolean canInput, boolean canOutput, List<IHeatCapacitor> capacitors) {
        super(canInput, canOutput);
        this.capacitors = capacitors;
    }

    public List<IHeatCapacitor> getHeatCapacitors() {
        return capacitors;
    }
}