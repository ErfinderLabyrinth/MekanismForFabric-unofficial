package mekanism.client.gui.element.gauge;

import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.client.gui.IGuiWrapper;
import mekanism.common.capabilities.holder.IHolder;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.network.to_server.PacketDropperUse.TankType;

import java.util.function.Supplier;

public class GuiSlurryGauge extends GuiChemicalGauge<Slurry, SlurryStack, ISlurryTank> {

    public GuiSlurryGauge(ITankInfoHandler<ISlurryTank> handler, GaugeType type, IGuiWrapper gui, int x, int y, int sizeX, int sizeY) {
        super(handler, type, gui, x, y, sizeX, sizeY, TankType.SLURRY_TANK);
    }

    public GuiSlurryGauge(Supplier<ISlurryTank> tankSupplier, Supplier<IHolder<ISlurryTank>> holderSupplier, GaugeType type, IGuiWrapper gui, int x, int y) {
        super(tankSupplier, holderSupplier, type, gui, x, y, TankType.SLURRY_TANK);
    }

    public GuiSlurryGauge(Supplier<ISlurryTank> tankSupplier, Supplier<IHolder<ISlurryTank>> holderSupplier, GaugeType type, IGuiWrapper gui, int x, int y, int sizeX, int sizeY) {
        super(tankSupplier, holderSupplier, type, gui, x, y, sizeX, sizeY, TankType.SLURRY_TANK);
    }

    public static GuiSlurryGauge getDummy(GaugeType type, IGuiWrapper gui, int x, int y) {
        GuiSlurryGauge gauge = new GuiSlurryGauge(null, type, gui, x, y, type.getGaugeOverlay().getWidth() + 2, type.getGaugeOverlay().getHeight() + 2);
        gauge.dummy = true;
        return gauge;
    }

    @Override
    public TransmissionType getTransmission() {
        return TransmissionType.SLURRY;
    }
}