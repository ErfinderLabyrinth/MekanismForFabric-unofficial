package mekanism.common.util;

import mekanism.api.FluidStack;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.*;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.math.MathUtils;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.inventory.SimpleSingleStackStorage;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.common.util.text.TextUtils;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class StorageUtils {

    private StorageUtils() {
    }

    public static void addStoredEnergy(@NotNull ItemStack stack, @NotNull List<Component> tooltip, boolean showMissingCap) {
        addStoredEnergy(stack, tooltip, showMissingCap, MekanismLang.STORED_ENERGY);
    }

    public static void addStoredEnergy(@NotNull ItemStack stack, @NotNull List<Component> tooltip, boolean showMissingCap, ILangEntry langEntry) {
        EnergyStorage energyHandlerItem = ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM);
        if (energyHandlerItem != null) {
            tooltip.add(langEntry.translateColored(EnumColor.BRIGHT_GREEN, EnumColor.GRAY,
                    EnergyDisplay.of(energyHandlerItem.getAmount(), energyHandlerItem.getCapacity())));
        } else if (showMissingCap) {
            tooltip.add(langEntry.translateColored(EnumColor.BRIGHT_GREEN, EnumColor.GRAY, EnergyDisplay.ZERO));
        }
    }

    public static void addStoredGas(@NotNull ItemStack stack, @NotNull List<Component> tooltip, boolean showMissingCap, boolean showAttributes) {
        addStoredGas(stack, tooltip, showMissingCap, showAttributes, MekanismLang.NO_GAS);
    }

    public static void addStoredGas(@NotNull ItemStack stack, @NotNull List<Component> tooltip, boolean showMissingCap, boolean showAttributes,
          ILangEntry emptyLangEntry) {
        addStoredChemical(stack, tooltip, showMissingCap, showAttributes, emptyLangEntry, stored -> {
            if (stored.isEmpty()) {
                return emptyLangEntry.translateColored(EnumColor.GRAY);
            }
            return MekanismLang.STORED.translateColored(EnumColor.ORANGE, EnumColor.ORANGE, stored, EnumColor.GRAY,
                  MekanismLang.GENERIC_MB.translate(TextUtils.format(stored.getAmount())));
        }, Capabilities.GAS_HANDLER_ITEM);
    }

    public static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, HANDLER extends Storage<CHEMICAL>>
    void addStoredChemical(@NotNull ItemStack stack, @NotNull List<Component> tooltip, boolean showMissingCap, boolean showAttributes, ILangEntry emptyLangEntry,
          Function<STACK, Component> storedFunction, ItemApiLookup<HANDLER, ContainerItemContext> capability) {
        HANDLER handler = ContainerItemContext.withConstant(stack).find(capability);
        if (handler != null) {
            for (StorageView<CHEMICAL> view:handler) {
                STACK chemicalInTank = (STACK) view.getResource().getStack(view.getAmount());
                tooltip.add(storedFunction.apply(chemicalInTank));
                if (showAttributes) {
                    ChemicalUtil.addAttributeTooltips(tooltip, chemicalInTank.getType());
                }
            }
        } else if (showMissingCap) {
            tooltip.add(emptyLangEntry.translate());
        }
    }

    public static void addStoredFluid(@NotNull ItemStack stack, @NotNull List<Component> tooltip, boolean showMissingCap) {
        addStoredFluid(stack, tooltip, showMissingCap, MekanismLang.NO_FLUID_TOOLTIP);
    }

    public static void addStoredFluid(@NotNull ItemStack stack, @NotNull List<Component> tooltip, boolean showMissingCap, ILangEntry emptyLangEntry) {
        addStoredFluid(stack, tooltip, showMissingCap, emptyLangEntry, stored -> {
            if (stored.isEmpty()) {
                return emptyLangEntry.translateColored(EnumColor.GRAY);
            }
            return MekanismLang.STORED.translateColored(EnumColor.ORANGE, EnumColor.ORANGE, stored, EnumColor.GRAY,
                  MekanismLang.GENERIC_MB.translate(TextUtils.format(stored.amount() / 81)));
        });
    }

    public static void addStoredFluid(@NotNull ItemStack stack, @NotNull List<Component> tooltip, boolean showMissingCap, ILangEntry emptyLangEntry,
          Function<FluidStack, Component> storedFunction) {
        ContainerItemContext context = ContainerItemContext.withConstant(stack);
        Storage<FluidVariant> storage = context.find(FluidStorage.ITEM);
        if (storage != null) {
            for (StorageView<FluidVariant> view : storage) {
                tooltip.add(storedFunction.apply(new FluidStack(view.getResource(), view.getAmount())));
            }
        } else if (showMissingCap) {
            tooltip.add(emptyLangEntry.translate());
        }
    }

    /**
     * @implNote Assumes there is only one "tank"
     */
    public static void addStoredSubstance(@NotNull ItemStack stack, @NotNull List<Component> tooltip, boolean isCreative) {
        FluidStack fluidStack = StorageUtils.getStoredFluidFromNBT(stack);
        GasStack gasStack = StorageUtils.getStoredGasFromNBT(stack);
        InfusionStack infusionStack = StorageUtils.getStoredInfusionFromNBT(stack);
        PigmentStack pigmentStack = StorageUtils.getStoredPigmentFromNBT(stack);
        SlurryStack slurryStack = StorageUtils.getStoredSlurryFromNBT(stack);
        if (fluidStack.isEmpty() && gasStack.isEmpty() && infusionStack.isEmpty() && pigmentStack.isEmpty() && slurryStack.isEmpty()) {
            tooltip.add(MekanismLang.EMPTY.translate());
            return;
        }
        ILangEntry type;
        Object contents;
        long amount;
        if (!fluidStack.isEmpty()) {
            contents = fluidStack;
            amount = fluidStack.amount();
            type = MekanismLang.LIQUID;
        } else {
            ChemicalStack<?> chemicalStack;
            if (!gasStack.isEmpty()) {
                chemicalStack = gasStack;
                type = MekanismLang.GAS;
            } else if (!infusionStack.isEmpty()) {
                chemicalStack = infusionStack;
                type = MekanismLang.INFUSE_TYPE;
            } else if (!pigmentStack.isEmpty()) {
                chemicalStack = pigmentStack;
                type = MekanismLang.PIGMENT;
            } else if (!slurryStack.isEmpty()) {
                chemicalStack = slurryStack;
                type = MekanismLang.SLURRY;
            } else {
                throw new IllegalStateException("Unknown chemical");
            }
            contents = chemicalStack;
            amount = chemicalStack.getAmount();
        }
        if (isCreative) {
            tooltip.add(type.translateColored(EnumColor.YELLOW, EnumColor.ORANGE, MekanismLang.GENERIC_STORED.translate(contents, EnumColor.GRAY, MekanismLang.INFINITE)));
        } else {
            tooltip.add(type.translateColored(EnumColor.YELLOW, EnumColor.ORANGE, MekanismLang.GENERIC_STORED_MB.translate(contents, EnumColor.GRAY, TextUtils.format(amount))));
        }
    }

    /**
     * Gets the fluid if one is stored from an item's tank going off the basis there is a single tank. This is for cases when we may not actually have a fluid handler
     * attached to our item, but it may have stored data in its tank from when it was a block
     */
    @NotNull
    public static FluidStack getStoredFluidFromNBT(ItemStack stack) {
        Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
        if (storage != null) {
            Iterator<StorageView<FluidVariant>> iterator = storage.iterator();
            if (iterator.hasNext()) {
                StorageView<FluidVariant> view = iterator.next();
                return new FluidStack(view.getResource(), view.getAmount());
            }
        }
        return FluidStack.EMPTY;
    }

    /**
     * Gets the gas if one is stored from an item's tank going off the basis there is a single tank. This is for cases when we may not actually have a gas handler
     * attached to our item, but it may have stored data in its tank from when it was a block
     */
    @NotNull
    public static GasStack getStoredGasFromNBT(ItemStack stack) {
        return getStoredChemicalFromNBT(stack, ChemicalTankBuilder.GAS.createDummy(Long.MAX_VALUE), NBTConstants.GAS_TANKS);
    }

    /**
     * Gets the infuse type if one is stored from an item's tank going off the basis there is a single tank. This is for cases when we may not actually have an infusion
     * handler attached to our item, but it may have stored data in its tank from when it was a block
     */
    @NotNull
    public static InfusionStack getStoredInfusionFromNBT(ItemStack stack) {
        return getStoredChemicalFromNBT(stack, ChemicalTankBuilder.INFUSION.createDummy(Long.MAX_VALUE), NBTConstants.INFUSION_TANKS);
    }

    /**
     * Gets the pigment if one is stored from an item's tank going off the basis there is a single tank. This is for cases when we may not actually have a pigment handler
     * attached to our item, but it may have stored data in its tank from when it was a block
     */
    @NotNull
    public static PigmentStack getStoredPigmentFromNBT(ItemStack stack) {
        return getStoredChemicalFromNBT(stack, ChemicalTankBuilder.PIGMENT.createDummy(Long.MAX_VALUE), NBTConstants.PIGMENT_TANKS);
    }

    /**
     * Gets the slurry if one is stored from an item's tank going off the basis there is a single tank. This is for cases when we may not actually have a slurry handler
     * attached to our item, but it may have stored data in its tank from when it was a block
     */
    @NotNull
    public static SlurryStack getStoredSlurryFromNBT(ItemStack stack) {
        return getStoredChemicalFromNBT(stack, ChemicalTankBuilder.SLURRY.createDummy(Long.MAX_VALUE), NBTConstants.SLURRY_TANKS);
    }

    @NotNull
    private static <STACK extends ChemicalStack<?>> STACK getStoredChemicalFromNBT(ItemStack stack, IChemicalTank<?, STACK> tank, String tag) {
        ItemDataUtils.readContainers(stack, tag, Collections.singletonList(tank));
        return tank.getStack();
    }

    /**
     * Gets the energy if one is stored from an item's container going off the basis there is a single energy container. This is for cases when we may not actually have
     * an energy handler attached to our item, but it may have stored data in its container from when it was a block
     */
    public static long getStoredEnergyFromNBT(ItemStack stack) {
        EnergyStorage storage = ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM);
        if (storage != null) {
            return storage.getAmount();
        }
        return 0;
    }

    public static ItemStack getFilledEnergyVariant(ItemStack toFill) {
        //Manually handle this as capabilities are not necessarily loaded yet (at least not on the first call to this, which is made via fillItemGroup)
        SimpleSingleStackStorage itemStorage = new SimpleSingleStackStorage(toFill);
        EnergyStorage energyStorage = ContainerItemContext.ofSingleSlot(itemStorage).find(EnergyStorage.ITEM);
        if (energyStorage != null) {
            try(Transaction t=Transaction.openOuter()) {
                energyStorage.insert(Long.MAX_VALUE, t);
                t.commit();
            }
        }
        return itemStorage.getStack();
    }

    @Nullable
    @Deprecated(forRemoval = true)
    public static EnergyStorage getEnergyStorage(ItemStack stack) {
        if (!stack.isEmpty()) {
            EnergyStorage energyStorage = ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM);
            if (energyStorage != null) {
                return energyStorage;
            }
        }
        return null;
    }

    public static double getEnergyRatio(ItemStack stack) {
        EnergyStorage container = getEnergyStorage(stack);
        double ratio = 0.0D;
        if (container != null) {
            ratio = (double) container.getAmount() / container.getCapacity();
        }
        return ratio;
    }

    public static Component getEnergyPercent(ItemStack stack, boolean colorText) {
        return getStoragePercent(getEnergyRatio(stack), colorText);
    }

    public static Component getStoragePercent(double ratio, boolean colorText) {
        Component text = TextUtils.getPercent(ratio);
        if (!colorText) {
            return text;
        }
        EnumColor color;
        if (ratio < 0.01F) {
            color = EnumColor.DARK_RED;
        } else if (ratio < 0.1F) {
            color = EnumColor.RED;
        } else if (ratio < 0.25F) {
            color = EnumColor.ORANGE;
        } else if (ratio < 0.5F) {
            color = EnumColor.YELLOW;
        } else {
            color = EnumColor.BRIGHT_GREEN;
        }
        return TextComponentUtil.build(color, text);
    }

    public static int getBarWidth(ItemStack stack) {
        return MathUtils.clampToInt(Math.round(13.0F - 13.0F * getDurabilityForDisplay(stack)));
    }

    private static double getDurabilityForDisplay(ItemStack stack) {
        double bestRatio = 0;
        bestRatio = calculateRatio(stack, bestRatio, Capabilities.GAS_HANDLER_ITEM);
        bestRatio = calculateRatio(stack, bestRatio, Capabilities.INFUSION_HANDLER_ITEM);
        bestRatio = calculateRatio(stack, bestRatio, Capabilities.PIGMENT_HANDLER_ITEM);
        bestRatio = calculateRatio(stack, bestRatio, Capabilities.SLURRY_HANDLER_ITEM);
        ContainerItemContext context = ContainerItemContext.withConstant(stack);
        Storage<FluidVariant> storage = context.find(FluidStorage.ITEM);
        if (storage != null) {
            for (StorageView<FluidVariant> view : storage) {
                bestRatio = Math.max(bestRatio, getRatio(view.getAmount(), view.getCapacity()));
            }
        }
        return 1 - bestRatio;
    }

    public static int getEnergyBarWidth(ItemStack stack) {
        return MathUtils.clampToInt(Math.round(13.0F - 13.0F * getEnergyDurabilityForDisplay(stack)));
    }

    private static double getEnergyDurabilityForDisplay(ItemStack stack) {
        double bestRatio = 0;
        EnergyStorage energyHandlerItem = ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM);
        if (energyHandlerItem != null) {
            bestRatio = Math.max(bestRatio, (double) energyHandlerItem.getAmount() / energyHandlerItem.getCapacity());
        }
        return 1 - bestRatio;
    }

    private static double calculateRatio(ItemStack stack, double bestRatio, ItemApiLookup<? extends Storage<?>, ContainerItemContext> capability) {
        Storage<?> handler = ContainerItemContext.withConstant(stack).find(capability);
        if (handler != null) {
            for (StorageView<?> view:handler) {
                bestRatio = Math.max(bestRatio, getRatio(view.getAmount(), view.getCapacity()));
            }
        }
        return bestRatio;
    }

    public static double getRatio(long amount, long capacity) {
        return capacity == 0 ? 1 : amount / (double) capacity;
    }

    public static void mergeFluidTanks(List<IExtendedFluidTank> tanks, List<IExtendedFluidTank> toAdd, List<FluidStack> rejects) {
        validateSizeMatches(tanks, toAdd, "tank");
        for (int i = 0; i < toAdd.size(); i++) {
            IExtendedFluidTank mergeTank = toAdd.get(i);
            if (!mergeTank.isEmpty()) {
                IExtendedFluidTank tank = tanks.get(i);
                FluidStack mergeStack = mergeTank.getFluid();
                if (tank.isEmpty()) {
                    long capacity = tank.getCapacity();
                    if (mergeStack.amount() <= capacity) {
                        tank.setStack(mergeStack);
                    } else {
                        tank.setStack(new FluidStack(mergeStack, capacity));
                        long remaining = mergeStack.amount() - capacity;
                        if (remaining > 0) {
                            rejects.add(new FluidStack(mergeStack, remaining));
                        }
                    }
                } else if (tank.isFluidEqual(mergeStack)) {
                    long amount = tank.growStack(mergeStack.amount());
                    long remaining = mergeStack.amount() - amount;
                    if (remaining > 0) {
                        rejects.add(new FluidStack(mergeStack, remaining));
                    }
                } else {
                    rejects.add(mergeStack);
                }
            }
        }
    }

    public static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>> void mergeTanks(
          List<TANK> tanks, List<TANK> toAdd, List<STACK> rejects) {
        validateSizeMatches(tanks, toAdd, "tank");
        for (int i = 0; i < toAdd.size(); i++) {
            TANK mergeTank = toAdd.get(i);
            if (!mergeTank.isEmpty()) {
                TANK tank = tanks.get(i);
                STACK mergeStack = mergeTank.getStack();
                if (tank.isEmpty()) {
                    long capacity = tank.getCapacity();
                    if (mergeStack.getAmount() <= capacity) {
                        tank.setStack(mergeStack);
                    } else {
                        tank.setStack(ChemicalUtil.copyWithAmount(mergeStack, capacity));
                        long remaining = mergeStack.getAmount() - capacity;
                        if (remaining > 0) {
                            rejects.add(ChemicalUtil.copyWithAmount(mergeStack, remaining));
                        }
                    }
                } else if (tank.isTypeEqual(mergeStack)) {
                    long amount = tank.growStack(mergeStack.getAmount());
                    long remaining = mergeStack.getAmount() - amount;
                    if (remaining > 0) {
                        rejects.add(ChemicalUtil.copyWithAmount(mergeStack, remaining));
                    }
                } else {
                    rejects.add(mergeStack);
                }
            }
        }
    }

    public static void mergeEnergyContainers(List<IEnergyContainer> containers, List<IEnergyContainer> toAdd) {
        validateSizeMatches(containers, toAdd, "energy container");
        for (int i = 0; i < toAdd.size(); i++) {
            IEnergyContainer container = containers.get(i);
            IEnergyContainer mergeContainer = toAdd.get(i);
            container.setEnergy(container.getEnergy() + mergeContainer.getEnergy());
        }
    }

    public static void mergeHeatCapacitors(List<IHeatCapacitor> capacitors, List<IHeatCapacitor> toAdd) {
        validateSizeMatches(capacitors, toAdd, "heat capacitor");
        for (int i = 0; i < toAdd.size(); i++) {
            IHeatCapacitor capacitor = capacitors.get(i);
            IHeatCapacitor mergeCapacitor = toAdd.get(i);
            capacitor.setHeat(capacitor.getHeat() + mergeCapacitor.getHeat());
            if (capacitor instanceof BasicHeatCapacitor heatCapacitor) {
                heatCapacitor.setHeatCapacity(capacitor.getHeatCapacity() + mergeCapacitor.getHeatCapacity(), false);
            }
        }
    }

    public static <T> void validateSizeMatches(List<T> base, List<T> toAdd, String type) {
        if (base.size() != toAdd.size()) {
            throw new IllegalArgumentException("Mismatched " + type + " count, orig: " + base.size() + ", toAdd: " + toAdd.size());
        }
    }
}