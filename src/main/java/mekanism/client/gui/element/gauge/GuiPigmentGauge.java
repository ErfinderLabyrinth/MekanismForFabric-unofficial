package mekanism.client.gui.element.gauge;

import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.client.gui.IGuiWrapper;
import mekanism.common.capabilities.holder.IHolder;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.network.to_server.PacketDropperUse.TankType;

import java.util.function.Supplier;

public class GuiPigmentGauge extends GuiChemicalGauge<Pigment, PigmentStack, IPigmentTank> {

    public GuiPigmentGauge(ITankInfoHandler<IPigmentTank> handler, GaugeType type, IGuiWrapper gui, int x, int y, int sizeX, int sizeY) {
        super(handler, type, gui, x, y, sizeX, sizeY, TankType.PIGMENT_TANK);
    }

    public GuiPigmentGauge(Supplier<IPigmentTank> tankSupplier, Supplier<IHolder<IPigmentTank>> holderSupplier, GaugeType type, IGuiWrapper gui, int x, int y) {
        super(tankSupplier, holderSupplier, type, gui, x, y, TankType.PIGMENT_TANK);
    }

    public GuiPigmentGauge(Supplier<IPigmentTank> tankSupplier, Supplier<IHolder<IPigmentTank>> holderSupplier, GaugeType type, IGuiWrapper gui, int x, int y, int sizeX, int sizeY) {
        super(tankSupplier, holderSupplier, type, gui, x, y, sizeX, sizeY, TankType.PIGMENT_TANK);
    }

    public static GuiPigmentGauge getDummy(GaugeType type, IGuiWrapper gui, int x, int y) {
        GuiPigmentGauge gauge = new GuiPigmentGauge(null, type, gui, x, y, type.getGaugeOverlay().getWidth() + 2, type.getGaugeOverlay().getHeight() + 2);
        gauge.dummy = true;
        return gauge;
    }

    @Override
    public TransmissionType getTransmission() {
        return TransmissionType.PIGMENT;
    }
}