package mekanism.client.gui.element.gauge;

import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.client.gui.IGuiWrapper;
import mekanism.common.capabilities.holder.IHolder;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.network.to_server.PacketDropperUse.TankType;

import java.util.function.Supplier;

public class GuiInfusionGauge extends GuiChemicalGauge<InfuseType, InfusionStack, IInfusionTank> {

    public GuiInfusionGauge(ITankInfoHandler<IInfusionTank> handler, GaugeType type, IGuiWrapper gui, int x, int y, int sizeX, int sizeY) {
        super(handler, type, gui, x, y, sizeX, sizeY, TankType.INFUSION_TANK);
    }

    public GuiInfusionGauge(Supplier<IInfusionTank> tankSupplier, Supplier<IHolder<IInfusionTank>> holderSupplier, GaugeType type, IGuiWrapper gui, int x, int y) {
        super(tankSupplier, holderSupplier, type, gui, x, y, TankType.INFUSION_TANK);
    }

    public GuiInfusionGauge(Supplier<IInfusionTank> tankSupplier, Supplier<IHolder<IInfusionTank>> holderSupplier, GaugeType type, IGuiWrapper gui, int x, int y, int sizeX, int sizeY) {
        super(tankSupplier, holderSupplier, type, gui, x, y, sizeX, sizeY, TankType.INFUSION_TANK);
    }

    public static GuiInfusionGauge getDummy(GaugeType type, IGuiWrapper gui, int x, int y) {
        GuiInfusionGauge gauge = new GuiInfusionGauge(null, type, gui, x, y, type.getGaugeOverlay().getWidth() + 2, type.getGaugeOverlay().getHeight() + 2);
        gauge.dummy = true;
        return gauge;
    }

    @Override
    public TransmissionType getTransmission() {
        return TransmissionType.INFUSION;
    }
}