package mekanism.client.gui.element.gauge;

import mekanism.api.FluidStack;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.text.TextComponentUtil;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.FluidTextureType;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.holder.IHolder;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.network.to_server.PacketDropperUse.TankType;
import mekanism.common.util.text.TextUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class GuiFluidGauge extends GuiTankGauge<FluidStack, StorageView<FluidVariant>> {

    private Component label;

    public GuiFluidGauge(ITankInfoHandler<StorageView<FluidVariant>> handler, GaugeType type, IGuiWrapper gui, int x, int y, int sizeX, int sizeY) {
        super(type, gui, x, y, sizeX, sizeY, handler, TankType.FLUID_TANK);
        //Ensure it isn't null
        setDummyType(FluidStack.EMPTY);
    }

    public GuiFluidGauge(Supplier<IExtendedFluidTank> tankSupplier, Supplier<IHolder<IExtendedFluidTank>> holderSupplier, GaugeType type, IGuiWrapper gui, int x, int y) {
        this(tankSupplier, holderSupplier, type, gui, x, y, type.getGaugeOverlay().getWidth() + 2, type.getGaugeOverlay().getHeight() + 2);
    }

    public GuiFluidGauge(Supplier<IExtendedFluidTank> tankSupplier, Supplier<IHolder<IExtendedFluidTank>> holderSupplier, GaugeType type, IGuiWrapper gui, int x, int y, int sizeX, int sizeY) {
        this(new ITankInfoHandler<>() {
            @Nullable
            @Override
            public IExtendedFluidTank getTank() {
                return tankSupplier.get();
            }

            @Override
            @Deprecated(forRemoval = true)
            public int getTankIndex() {
                IExtendedFluidTank tank = getTank();
                IHolder<IExtendedFluidTank> holder = holderSupplier.get();
                if (holder == null) {
                    return -1;
                }
                return tank != null ? holder.indexOf(tank) : -1;
            }
        }, type, gui, x, y, sizeX, sizeY);
    }

    public GuiFluidGauge setLabel(Component label) {
        this.label = label;
        return this;
    }

    public static GuiFluidGauge getDummy(GaugeType type, IGuiWrapper gui, int x, int y) {
        GuiFluidGauge gauge = new GuiFluidGauge(null, type, gui, x, y, type.getGaugeOverlay().getWidth() + 2, type.getGaugeOverlay().getHeight() + 2);
        gauge.dummy = true;
        return gauge;
    }

    @Override
    public TransmissionType getTransmission() {
        return TransmissionType.FLUID;
    }

    @Override
    public int getScaledLevel() {
        if (dummy) {
            return height - 2;
        }
        StorageView<FluidVariant> view  = getTank();
        if (view == null || view.isResourceBlank() || view.getAmount() == 0 || view.getCapacity() == 0) {
            return 0;
        }
        if (view.getAmount() == Integer.MAX_VALUE) {
            return height - 2;
        }
        float scale = (float) view.getAmount() / (float) view.getCapacity();
        return Math.round(scale * (height - 2));
    }

    @Nullable
    @Override
    public TextureAtlasSprite getIcon() {
        if (dummy) {
            return MekanismRenderer.getFluidTexture(dummyType, FluidTextureType.STILL);
        }
        StorageView<FluidVariant> view = getTank();
        return view == null || view.isResourceBlank() || view.getAmount() == 0 ? null : MekanismRenderer.getFluidTexture(new FluidStack(view.getResource(), view.getAmount()), FluidTextureType.STILL);
    }

    @Override
    public Component getLabel() {
        return label;
    }

    @Override
    public List<Component> getTooltipText() {
        if (dummy) {
            return Collections.singletonList(TextComponentUtil.build(dummyType));
        }
        StorageView<FluidVariant> view = getTank();
        if (view == null || view.isResourceBlank() || view.getAmount() == 0) {
            return Collections.singletonList(MekanismLang.EMPTY.translate());
        }
        long amount = view.getAmount();
        FluidStack fluidStack = new FluidStack(view.getResource(), view.getAmount());
        if (amount == Integer.MAX_VALUE) {
            return Collections.singletonList(MekanismLang.GENERIC_STORED.translate(fluidStack, MekanismLang.INFINITE));
        }
        return Collections.singletonList(MekanismLang.GENERIC_STORED_MB.translate(fluidStack, TextUtils.format(amount / 81)));
    }

    @Override
    protected void applyRenderColor(GuiGraphics guiGraphics) {
        StorageView<FluidVariant> view = getTank();
        MekanismRenderer.color(guiGraphics, dummy || view == null ? dummyType : new FluidStack(view.getResource(), view.getAmount()));
    }

    @Override
    public Optional<?> getIngredient(double mouseX, double mouseY) {
        StorageView<FluidVariant> view = getTank();
        return view == null || view.isResourceBlank() || view.getAmount() == 0 ? Optional.empty() : Optional.of(new FluidStack(view.getResource(), view.getAmount()));
    }

    @Override
    public Rect2i getIngredientBounds(double mouseX, double mouseY) {
        return new Rect2i(getX() + 1, getY() + 1, width - 2, height - 2);
    }
}