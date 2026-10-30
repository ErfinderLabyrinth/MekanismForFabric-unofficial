package mekanism.common.tile.component.config.slot;

import mekanism.api.fluid.IExtendedFluidTank;

import java.util.List;

public class FluidSlotInfo extends BaseSlotInfo {

    private final List<IExtendedFluidTank> tanks;

    public FluidSlotInfo(boolean canInput, boolean canOutput, IExtendedFluidTank... tanks) {
        this(canInput, canOutput, List.of(tanks));
    }

    public FluidSlotInfo(boolean canInput, boolean canOutput, List<IExtendedFluidTank> tanks) {
        super(canInput, canOutput);
        this.tanks = tanks;
    }

    public List<IExtendedFluidTank> getTanks() {
        return tanks;
    }
}