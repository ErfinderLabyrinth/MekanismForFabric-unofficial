package mekanism.client.render.item;

import mekanism.api.FluidStack;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.math.MathUtils;
import mekanism.client.gui.GuiUtils;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.util.FluidUtils;
import mekanism.common.util.StorageUtils;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.Predicate;

public class ChemicalFluidBarDecorator{

//    private final Capability<? extends IChemicalHandler<?, ?>>[] chemicalCaps;
    private final boolean showFluid;
    private final Predicate<ItemStack> visibleFor;

    /**
     * @param showFluid    if the fluid capability should be checked for display, display above chemicalCaps if both are present
     * @param visibleFor   checks if bars should be rendered for the given itemstack
     * @param chemicalCaps the capabilities to be displayed in order, starting from the bottom
     */
    public ChemicalFluidBarDecorator(boolean showFluid, Predicate<ItemStack> visibleFor) {
        this.showFluid = showFluid;
//        this.chemicalCaps = chemicalCaps;
        this.visibleFor = visibleFor;
    }

    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (!visibleFor.test(stack)) {
            return false;
        }
        yOffset += 12;
        for (ItemApiLookup<? extends IChemicalHandler<? extends Chemical<?>, ? extends ChemicalStack<? extends Chemical<?>>, ?>, ContainerItemContext> chemicalCap : List.of(Capabilities.GAS_HANDLER_ITEM, Capabilities.INFUSION_HANDLER_ITEM, Capabilities.PIGMENT_HANDLER_ITEM, Capabilities.SLURRY_HANDLER_ITEM)) {
            IChemicalHandler<? extends Chemical<?>, ? extends ChemicalStack<? extends Chemical<?>>, ?> handler = ContainerItemContext.withConstant(stack).find(chemicalCap);
            if (handler != null) {
                StorageView<? extends Chemical<?>> tank = getDisplayTank(handler);
                if (tank != null) {
                    //ChemicalStack<?> chemicalInTank = chemicalHandler.getChemicalInTank(tank);
                    renderBar(guiGraphics, xOffset, yOffset, tank.getAmount(), tank.getCapacity(), tank.getResource().getColorRepresentation());
                    yOffset--;
                }
            }
        }

        if (showFluid) {
            Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
            if (storage != null) {
                StorageView<FluidVariant> tank = getDisplayTank(storage);
                if (tank != null) {
                    FluidStack fluidInTank = new FluidStack(tank.getResource(), tank.getAmount());
                    renderBar(guiGraphics, xOffset, yOffset, fluidInTank.amount(), tank.getCapacity(), FluidUtils.getRGBDurabilityForDisplay(stack).orElse(0xFFFFFFFF));
                }
            }
        }
        return true;
    }

    protected static void renderBar(GuiGraphics guiGraphics, int stackXPos, int yPos, long amount, long capacity, int color) {
        int pixelWidth = convertWidth(StorageUtils.getRatio(amount, capacity));
        GuiUtils.fill(guiGraphics, RenderType.guiOverlay(), stackXPos + 2 + pixelWidth, yPos, 13 - pixelWidth, 1, 0xFF000000);
        GuiUtils.fill(guiGraphics, RenderType.guiOverlay(), stackXPos + 2, yPos, pixelWidth, 1, color | 0xFF000000);
    }

    private static int convertWidth(double width) {
        return MathUtils.clampToInt(Math.round(13.0F * width));
    }

    private int getDisplayTank(int tanks) {
        if (tanks == 0) {
            return -1;
        } else if (tanks > 1 && Minecraft.getInstance().level != null) {
            //Cycle through multiple tanks every second, to save some space if multiple tanks are present
            return (int) (Minecraft.getInstance().level.getGameTime() / 20) % tanks;
        }
        return 0;
    }

    private <T> StorageView<T> getDisplayTank(Storage<T> storage) {
        List<StorageView<T>> views = Lists.newArrayList(storage.iterator());
        if (views.size() == 0) {
            return null;
        } else if (Minecraft.getInstance().level != null) {
            //Cycle through multiple tanks every second, to save some space if multiple tanks are present
            return views.get((int) ((Minecraft.getInstance().level.getGameTime() / 20) % views.size()));
        }
        return views.get(0);
    }
}