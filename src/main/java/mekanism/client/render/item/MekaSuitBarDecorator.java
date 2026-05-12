package mekanism.client.render.item;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import mekanism.api.FluidStack;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.gas.Gas;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.GenericTankSpec;
import mekanism.common.capabilities.chemical.item.ChemicalTankSpec;
import mekanism.common.capabilities.fluid.item.RateLimitMultiTankFluidHandler.FluidTankSpec;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class MekaSuitBarDecorator {

    public static final MekaSuitBarDecorator INSTANCE = new MekaSuitBarDecorator();

    private MekaSuitBarDecorator() {
    }

    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemMekaSuitArmor armor)) {
            return false;
        }
        yOffset += 12;

        if (tryRenderGas(guiGraphics, stack, xOffset, yOffset, armor.getGasTankSpecs())) {
            yOffset--;
        }
        //TODO: Other chemical types as they get added to different meka suit pieces

        List<FluidTankSpec> fluidTankSpecs = armor.getFluidTankSpecs();
        if (!fluidTankSpecs.isEmpty()) {
            ContainerItemContext context = ContainerItemContext.withConstant(stack);
            Storage<FluidVariant> fluidStorage = context.find(FluidStorage.ITEM);
            if (fluidStorage != null) {
                StorageView<FluidVariant> view = getDisplayTank(fluidTankSpecs, stack, fluidStorage);
                FluidStack fluidInTank = new FluidStack(view.getResource(), view.getAmount());
                if (fluidInTank != null) {
                    ChemicalFluidBarDecorator.renderBar(guiGraphics, xOffset, yOffset, fluidInTank.amount(), view.getCapacity(),
                          FluidUtils.getRGBDurabilityForDisplay(stack).orElse(0xFFFFFFFF));
                }
            }
        }
        return true;
    }

    private <CHEMICAL extends Chemical<CHEMICAL>> boolean tryRenderGas(GuiGraphics guiGraphics, ItemStack stack,
          int xOffset, int yOffset, List<ChemicalTankSpec<CHEMICAL>> chemicalTankSpecs) {
        if (!chemicalTankSpecs.isEmpty() && chemicalTankSpecs.stream().anyMatch(spec -> spec.supportsStack(stack))) {
            Storage<Gas> storage = ContainerItemContext.withConstant(stack).find(Capabilities.GAS_HANDLER_ITEM);
            if (storage != null) {
                int tank = getDisplayTank(chemicalTankSpecs, stack, Iterators.size(storage.iterator()));
                if (tank != -1) {
                    StorageView<Gas> chemicalInTank = Iterators.get(storage.iterator(), tank);
                    ChemicalFluidBarDecorator.renderBar(guiGraphics, xOffset, yOffset, chemicalInTank.getAmount(), chemicalInTank.getCapacity(),
                          chemicalInTank.getResource().getColorRepresentation());
                    return true;
                }
            }
        }
        return false;
    }

    private static <TYPE> int getDisplayTank(List<? extends GenericTankSpec<TYPE>> tankSpecs, ItemStack stack, int tanks) {
        if (tanks == 0) {
            return -1;
        } else if (tanks > 1 && tanks == tankSpecs.size() && Minecraft.getInstance().level != null) {
            IntList tankIndices = new IntArrayList(tanks);
            for (int i = 0; i < tanks; i++) {
                if (tankSpecs.get(i).supportsStack(stack)) {
                    tankIndices.add(i);
                }
            }
            if (tankIndices.isEmpty()) {
                return -1;
            } else if (tankIndices.size() == 1) {
                return tankIndices.getInt(0);
            }
            //Cycle through multiple tanks every second, to save some space if multiple tanks are present
            return tankIndices.getInt((int) (Minecraft.getInstance().level.getGameTime() / 20) % tankIndices.size());
        }
        for (int i = 0; i < tanks && i < tankSpecs.size(); i++) {
            if (tankSpecs.get(i).supportsStack(stack)) {
                return i;
            }
        }
        return -1;
    }

    private static <TYPE> StorageView<FluidVariant> getDisplayTank(List<? extends GenericTankSpec<TYPE>> tankSpecs, ItemStack stack, Storage<FluidVariant> storage) {
        List<StorageView<FluidVariant>> views = Lists.newArrayList(storage.iterator());
        int index = getDisplayTank(tankSpecs, stack, views.size());
        return views.get(index);
    }
}