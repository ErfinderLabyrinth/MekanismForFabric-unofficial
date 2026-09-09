package mekanism.generators.common.content.fusion;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.heat.HeatAPI;
import mekanism.api.heat.HeatAPI.HeatTransfer;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.MathUtils;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.chemical.multiblock.MultiblockChemicalTankBuilder;
import mekanism.common.capabilities.energy.VariableCapacityEnergyContainer;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.capabilities.heat.ITileHeatHandler;
import mekanism.common.capabilities.heat.VariableHeatCapacitor;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.sync.*;
import mekanism.common.inventory.container.sync.chemical.SyncableGasStack;
import mekanism.common.lib.multiblock.IValveHandler.ValveData;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.registries.MekanismGases;
import mekanism.common.tags.MekanismTags;
import mekanism.common.util.HeatUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.GeneratorTags;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.item.ItemHohlraum;
import mekanism.generators.common.registries.GeneratorsGases;
import mekanism.generators.common.slot.ReactorInventorySlot;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorBlock;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorPort;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class FusionReactorMultiblockData extends MultiblockData {

    public static final String HEAT_TAB = "heat";
    public static final String FUEL_TAB = "fuel";
    public static final String STATS_TAB = "stats";

    public static final int MAX_INJECTION = 98;//this is the effective cap in the GUI, as text field is limited to 2 chars
    //Reaction characteristics
    private static final double burnTemperature = 100_000_000;
    private static final double burnRatio = 1;
    //Thermal characteristics
    private static final double plasmaHeatCapacity = 100;
    private static final double caseHeatCapacity = 1;
    private static final double inverseInsulation = 100_000;
    //Heat transfer metrics
    private static final double plasmaCaseConductivity = 0.2;

    private final Set<ITileHeatHandler> heatHandlers = new ObjectOpenHashSet<>();

    private boolean burning = false;

    public IEnergyContainer energyContainer;
    public IHeatCapacitor heatCapacitor;

    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = {"getWater", "getWaterCapacity", "getWaterNeeded", "getWaterFilledPercentage"}, docPlaceholder = "water tank")
    public IExtendedFluidTank waterTank;
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = {"getSteam", "getSteamCapacity", "getSteamNeeded", "getSteamFilledPercentage"}, docPlaceholder = "steam tank")
    public IGasTank steamTank;

    private double biomeAmbientTemp;
    private double lastPlasmaTemperature;
    private double lastCaseTemperature;
    @SyntheticComputerMethod(getter = "getEnvironmentalLoss")
    public double lastEnvironmentLoss;
    @SyntheticComputerMethod(getter = "getTransferLoss")
    public double lastTransferLoss;

    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = {"getDeuterium", "getDeuteriumCapacity", "getDeuteriumNeeded",
                                                                                        "getDeuteriumFilledPercentage"}, docPlaceholder = "deuterium tank")
    public IGasTank deuteriumTank;
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = {"getTritium", "getTritiumCapacity", "getTritiumNeeded",
                                                                                        "getTritiumFilledPercentage"}, docPlaceholder = "tritium tank")
    public IGasTank tritiumTank;
    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = {"getDTFuel", "getDTFuelCapacity", "getDTFuelNeeded", "getDTFuelFilledPercentage"}, docPlaceholder = "fuel tank")
    public IGasTank fuelTank;
    private int injectionRate = 2;
    private long lastBurned;

    public double plasmaTemperature;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getHohlraum", docPlaceholder = "Hohlraum slot")
    final ReactorInventorySlot reactorSlot;

    private boolean clientBurning;
    private double clientTemp;

    private long maxWater;
    private long maxSteam;

    private AABB deathZone;

    public FusionReactorMultiblockData(TileEntityFusionReactorBlock tile) {
        super(tile);
        //Default biome temp to the ambient temperature at the block we are at
        biomeAmbientTemp = HeatAPI.getAmbientTemp(tile.getLevel(), tile.getTilePos());
        lastPlasmaTemperature = biomeAmbientTemp;
        lastCaseTemperature = biomeAmbientTemp;
        plasmaTemperature = biomeAmbientTemp;
        gasTanks.add(deuteriumTank = MultiblockChemicalTankBuilder.GAS.input(this, () -> MekanismGeneratorsConfig.generators.fusionFuelCapacity,
              GeneratorTags.Gases.DEUTERIUM_LOOKUP::contains, this));
        gasTanks.add(tritiumTank = MultiblockChemicalTankBuilder.GAS.input(this, () -> MekanismGeneratorsConfig.generators.fusionFuelCapacity,
              GeneratorTags.Gases.TRITIUM_LOOKUP::contains, this));
        gasTanks.add(fuelTank = MultiblockChemicalTankBuilder.GAS.input(this, () -> MekanismGeneratorsConfig.generators.fusionFuelCapacity,
              GeneratorTags.Gases.FUSION_FUEL_LOOKUP::contains, createSaveAndComparator()));
        gasTanks.add(steamTank = MultiblockChemicalTankBuilder.GAS.output(this, this::getMaxSteam, gas -> gas == MekanismGases.STEAM.getChemical(), this));
        fluidTanks.add(waterTank = VariableCapacityFluidTank.input(this, this::getMaxWater, fluid -> MekanismTags.Fluids.WATER_LOOKUP.contains(fluid.getFluid()), this));
        energyContainers.add(energyContainer = VariableCapacityEnergyContainer.output(() -> MekanismGeneratorsConfig.generators.fusionEnergyCapacity, this));
        heatCapacitors.add(heatCapacitor = VariableHeatCapacitor.create(caseHeatCapacity, FusionReactorMultiblockData::getInverseConductionCoefficient,
              () -> inverseInsulation, () -> biomeAmbientTemp, this));
        inventorySlots.add(reactorSlot = ReactorInventorySlot.at(stack -> stack.getItem() instanceof ItemHohlraum, this, 80, 39));
    }

    @Override
    public void onCreated(Level world) {
        super.onCreated(world);
        for (ValveData data : valves) {
            BlockEntity tile = WorldUtils.getTileEntity(world, data.location);
            if (tile instanceof TileEntityFusionReactorPort port) {
                heatHandlers.add(port);
            }
        }
        biomeAmbientTemp = calculateAverageAmbientTemperature(world);
        deathZone = new AABB(getMinPos().offset(1, 1, 1), getMaxPos());
    }

    @Override
    public void readUpdateTag(CompoundTag tag) {
        super.readUpdateTag(tag);
        NBTUtils.setDoubleIfPresent(tag, NBTConstants.PLASMA_TEMP, this::setLastPlasmaTemp);
        NBTUtils.setBooleanIfPresent(tag, NBTConstants.BURNING, this::setBurning);
    }

    @Override
    public void writeUpdateTag(CompoundTag tag) {
        super.writeUpdateTag(tag);
        tag.putDouble(NBTConstants.PLASMA_TEMP, getLastPlasmaTemp());
        tag.putBoolean(NBTConstants.BURNING, isBurning());
    }

    public void addTemperatureFromEnergyInput(long energyAdded) {
        if (isBurning()) {
            setPlasmaTemp(getPlasmaTemp() + energyAdded / plasmaHeatCapacity);
        } else {
            setPlasmaTemp(getPlasmaTemp() + energyAdded / plasmaHeatCapacity * 10);
        }
    }

    private boolean hasHohlraum() {
        if (!reactorSlot.isEmpty()) {
            ItemStack hohlraum = reactorSlot.getStack();
            if (hohlraum.getItem() instanceof ItemHohlraum) {
                Storage<Gas> gasHandlerItem = Capabilities.GAS_HANDLER_ITEM.find(hohlraum, ContainerItemContext.withConstant(hohlraum));
                if (gasHandlerItem != null) {
                    Iterator<StorageView<Gas>> iterator = gasHandlerItem.iterator();
                    if (iterator.hasNext()) {
                        StorageView<Gas> gasView = iterator.next();
                        //Validate something didn't go terribly wrong, and we actually do have the tank we expect to have
                        return gasView.getAmount() == gasView.getCapacity();
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);
        long fuelBurned = 0;
        //Only thermal transfer happens unless we're hot enough to burn.
        if (getPlasmaTemp() >= burnTemperature) {
            //If we're not burning, yet we need a hohlraum to ignite
            if (!burning && hasHohlraum()) {
                vaporiseHohlraum();
            }

            //Only inject fuel if we're burning
            if (isBurning()) {
                injectFuel();
                fuelBurned = burnFuel();
                if (fuelBurned == 0) {
                    setBurning(false);
                }
            }
        } else {
            setBurning(false);
        }

        if (lastBurned != fuelBurned) {
            lastBurned = fuelBurned;
        }

        //Perform the heat transfer calculations
        transferHeat();
        updateHeatCapacitors(null);
        updateTemperatures();

        if (isBurning()) {
            kill(world);
        }

        if (isBurning() != clientBurning || Math.abs(getLastPlasmaTemp() - clientTemp) > 1_000_000) {
            clientBurning = isBurning();
            clientTemp = getLastPlasmaTemp();
            needsPacket = true;
        }
        return needsPacket;
    }

    public void updateTemperatures() {
        lastPlasmaTemperature = getPlasmaTemp();
        lastCaseTemperature = heatCapacitor.getTemperature();
    }

    private void kill(Level world) {
        if (world.getRandom().nextInt() % 20 != 0) {
            return;
        }
        List<Entity> entitiesToDie = getWorld().getEntitiesOfClass(Entity.class, deathZone);

        for (Entity entity : entitiesToDie) {
            entity.hurt(entity.damageSources().magic(), 50_000F);
        }
    }

    private void vaporiseHohlraum() {
        ItemStack hohlraum = reactorSlot.getStack();
        Storage<Gas> gasHandlerItem = Capabilities.GAS_HANDLER_ITEM.find(hohlraum, ContainerItemContext.withConstant(hohlraum));

        if (gasHandlerItem != null) {
            Iterator<StorageView<Gas>> gasIterator = gasHandlerItem.iterator();
            if (gasIterator.hasNext()) {
                StorageView<Gas> gasView = gasIterator.next();
                try(Transaction t=Transaction.openOuter()) {
                    fuelTank.insert(gasView.getResource(), gasView.getAmount(), t);
                    t.commit();
                }
                lastPlasmaTemperature = getPlasmaTemp();
                reactorSlot.setEmpty();
                setBurning(true);
            }
        }
    }

    private void injectFuel() {
        long amountNeeded = fuelTank.getNeeded();
        long amountAvailable = 2 * Math.min(deuteriumTank.getStored(), tritiumTank.getStored());
        long amountToInject = Math.min(amountNeeded, Math.min(amountAvailable, injectionRate));
        amountToInject -= amountToInject % 2;
        long injectingAmount = amountToInject / 2;
        MekanismUtils.logMismatchedStackSize(deuteriumTank.shrinkStack(injectingAmount), injectingAmount);
        MekanismUtils.logMismatchedStackSize(tritiumTank.shrinkStack(injectingAmount), injectingAmount);
        try(Transaction t=Transaction.openOuter()) {
            fuelTank.insert(GeneratorsGases.FUSION_FUEL.get(), amountToInject, t);
            t.commit();
        }
    }

    private long burnFuel() {
        long fuelBurned = MathUtils.clampToLong(Mth.clamp((lastPlasmaTemperature - burnTemperature) * burnRatio, 0, fuelTank.getStored()));
        MekanismUtils.logMismatchedStackSize(fuelTank.shrinkStack(fuelBurned), fuelBurned);
        setPlasmaTemp(getPlasmaTemp() + (MekanismGeneratorsConfig.generators.energyPerFusionFuel * fuelBurned) / plasmaHeatCapacity);
        return fuelBurned;
    }

    private void transferHeat() {
        //Transfer from plasma to casing
        double plasmaCaseHeat = plasmaCaseConductivity * (lastPlasmaTemperature - lastCaseTemperature);
        if (Math.abs(plasmaCaseHeat) > HeatAPI.EPSILON) {
            setPlasmaTemp(getPlasmaTemp() - plasmaCaseHeat / plasmaHeatCapacity);
            heatCapacitor.handleHeat(plasmaCaseHeat);
        }

        //Transfer from casing to water if necessary
        double caseWaterHeat = MekanismGeneratorsConfig.generators.fusionWaterHeatingRatio * (lastCaseTemperature - biomeAmbientTemp);
        if (Math.abs(caseWaterHeat) > HeatAPI.EPSILON) {
            long waterToVaporize = (long) (HeatUtils.getSteamEnergyEfficiency() * caseWaterHeat / HeatUtils.getWaterThermalEnthalpy());
            waterToVaporize = Math.min(waterToVaporize, Math.min(waterTank.getAmount(), MathUtils.clampToInt(steamTank.getNeeded())));
            if (waterToVaporize > 0) {
                MekanismUtils.logMismatchedStackSize(waterTank.shrinkStack(waterToVaporize), waterToVaporize);
                try(Transaction t=Transaction.openOuter()) {
                    steamTank.insert(MekanismGases.STEAM.get(), waterToVaporize, t);
                    t.commit();
                }
                caseWaterHeat = waterToVaporize * HeatUtils.getWaterThermalEnthalpy() / HeatUtils.getSteamEnergyEfficiency();
                heatCapacitor.handleHeat(-caseWaterHeat);
            }
        }

        HeatTransfer heatTransfer = simulate();
        lastEnvironmentLoss = heatTransfer.environmentTransfer();
        lastTransferLoss = heatTransfer.adjacentTransfer();

        //Passive energy generation
        double caseAirHeat = MekanismGeneratorsConfig.generators.fusionCasingThermalConductivity * (lastCaseTemperature - biomeAmbientTemp);
        if (Math.abs(caseAirHeat) > HeatAPI.EPSILON) {
            heatCapacitor.handleHeat(-caseAirHeat);
            try(Transaction t = Transaction.openOuter()) {
                energyContainer.insert((long)(caseAirHeat * MekanismGeneratorsConfig.generators.fusionThermocoupleEfficiency), t);
                t.commit();
            }
        }
    }

    @NotNull
    @Override
    public HeatTransfer simulate() {
        double environmentTransfer = 0;
        double adjacentTransfer = 0;
        for (ITileHeatHandler source : heatHandlers) {
            HeatTransfer heatTransfer = source.simulate();
            adjacentTransfer += heatTransfer.adjacentTransfer();
            environmentTransfer += heatTransfer.environmentTransfer();
        }
        return new HeatTransfer(adjacentTransfer, environmentTransfer);
    }

    public void setLastPlasmaTemp(double temp) {
        lastPlasmaTemperature = temp;
    }

    @ComputerMethod(nameOverride = "getPlasmaTemperature")
    public double getLastPlasmaTemp() {
        return lastPlasmaTemperature;
    }

    @ComputerMethod(nameOverride = "getCaseTemperature")
    public double getLastCaseTemp() {
        return lastCaseTemperature;
    }

    public double getPlasmaTemp() {
        return plasmaTemperature;
    }

    public void setPlasmaTemp(double temp) {
        if (plasmaTemperature != temp) {
            plasmaTemperature = temp;
            markDirty();
        }
    }

    @ComputerMethod
    public int getInjectionRate() {
        return injectionRate;
    }

    public void setInjectionRate(int rate) {
        if (injectionRate != rate) {
            injectionRate = rate;
            maxWater = injectionRate * MekanismGeneratorsConfig.generators.fusionWaterPerInjection;
            maxSteam = injectionRate * MekanismGeneratorsConfig.generators.fusionSteamPerInjection;
            if (getWorld() != null && !isRemote()) {
                if (!waterTank.isEmpty()) {
                    waterTank.setStackSize(Math.min(waterTank.getAmount(), waterTank.getCapacity()));
                }
                if (!steamTank.isEmpty()) {
                    steamTank.setStackSize(Math.min(steamTank.getStored(), steamTank.getCapacity()));
                }
            }
            markDirty();
        }
    }

    public long getMaxWater() {
        return maxWater;
    }

    public long getMaxSteam() {
        return maxSteam;
    }

    public boolean isBurning() {
        return burning;
    }

    public void setBurning(boolean burn) {
        if (burning != burn) {
            burning = burn;
            markDirty();
        }
    }

    public double getCaseTemp() {
        return heatCapacitor.getTemperature();
    }

    @Override
    protected int getMultiblockRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(fuelTank.getStored(), fuelTank.getCapacity());
    }

    @ComputerMethod(methodDescription = "true -> water cooled, false -> air cooled")
    public int getMinInjectionRate(boolean active) {
        double k = active ? MekanismGeneratorsConfig.generators.fusionWaterHeatingRatio : 0;
        double caseAirConductivity = MekanismGeneratorsConfig.generators.fusionCasingThermalConductivity;
        double aMin = burnTemperature * burnRatio * plasmaCaseConductivity * (k + caseAirConductivity) /
                      (MekanismGeneratorsConfig.generators.energyPerFusionFuel * burnRatio * (plasmaCaseConductivity + k + caseAirConductivity) -
                       plasmaCaseConductivity * (k + caseAirConductivity));
        return (int) (2 * Math.ceil(aMin / 2D));
    }

    @ComputerMethod(methodDescription = "true -> water cooled, false -> air cooled")
    public double getMaxPlasmaTemperature(boolean active) {
        double k = active ? MekanismGeneratorsConfig.generators.fusionWaterHeatingRatio : 0;
        double caseAirConductivity = MekanismGeneratorsConfig.generators.fusionCasingThermalConductivity;
        long injectionRate = Math.max(this.injectionRate, lastBurned);
        return injectionRate * MekanismGeneratorsConfig.generators.energyPerFusionFuel / plasmaCaseConductivity *
               (plasmaCaseConductivity + k + caseAirConductivity) / (k + caseAirConductivity);
    }

    @ComputerMethod(methodDescription = "true -> water cooled, false -> air cooled")
    public double getMaxCasingTemperature(boolean active) {
        double k = active ? MekanismGeneratorsConfig.generators.fusionWaterHeatingRatio : 0;
        long injectionRate = Math.max(this.injectionRate, lastBurned);
        return (MekanismGeneratorsConfig.generators.energyPerFusionFuel * injectionRate)
              / (k + MekanismGeneratorsConfig.generators.fusionCasingThermalConductivity);
    }

    @ComputerMethod(methodDescription = "true -> water cooled, false -> air cooled")
    public double getIgnitionTemperature(boolean active) {
        double k = active ? MekanismGeneratorsConfig.generators.fusionWaterHeatingRatio : 0;
        double caseAirConductivity = MekanismGeneratorsConfig.generators.fusionCasingThermalConductivity;
        double energyPerFusionFuel = MekanismGeneratorsConfig.generators.energyPerFusionFuel;
        return burnTemperature * energyPerFusionFuel * burnRatio * (plasmaCaseConductivity + k + caseAirConductivity) /
               (energyPerFusionFuel * burnRatio * (plasmaCaseConductivity + k + caseAirConductivity) - plasmaCaseConductivity * (k + caseAirConductivity));
    }

    public FloatingLong getPassiveGeneration(boolean active, boolean current) {
        double temperature = current ? getLastCaseTemp() : getMaxCasingTemperature(active);
        return FloatingLong.create(MekanismGeneratorsConfig.generators.fusionThermocoupleEfficiency *
                                   MekanismGeneratorsConfig.generators.fusionCasingThermalConductivity * temperature);
    }

    public long getSteamPerTick(boolean current) {
        double temperature = current ? getLastCaseTemp() : getMaxCasingTemperature(true);
        return MathUtils.clampToLong(HeatUtils.getSteamEnergyEfficiency() * MekanismGeneratorsConfig.generators.fusionWaterHeatingRatio * temperature / HeatUtils.getWaterThermalEnthalpy());
    }

    private static double getInverseConductionCoefficient() {
        return 1 / MekanismGeneratorsConfig.generators.fusionCasingThermalConductivity;
    }

    //Computer related methods
    @ComputerMethod(nameOverride = "setInjectionRate")
    void computerSetInjectionRate(int rate) throws ComputerException {
        if (rate < 0 || rate > MAX_INJECTION) {
            //Validate bounds even though we can clamp
            throw new ComputerException("Injection Rate '%d' is out of range must be an even number between 0 and %d. (Inclusive)", rate, MAX_INJECTION);
        } else if (rate % 2 != 0) {
            //Validate it is even
            throw new ComputerException("Injection Rate '%d' must be an even number between 0 and %d. (Inclusive)", rate, MAX_INJECTION);
        }
        setInjectionRate(rate);
    }

    @ComputerMethod
    FloatingLong getPassiveGeneration(boolean active) {
        return getPassiveGeneration(active, false);
    }

    @ComputerMethod
    FloatingLong getProductionRate() {
        return getPassiveGeneration(false, true);
    }
    //End computer related methods


    @Override
    public void addSyncables(Consumer<ISyncableData> acceptor, String tag) {
        acceptor.accept(SyncableBoolean.create(() -> burning, newValue -> burning = newValue));
        acceptor.accept(SyncableLong.create(() -> energyContainer.getEnergy(), newValue -> energyContainer.setEnergy(newValue)));
        if (HEAT_TAB.equals(tag)) {
            acceptor.accept(SyncableFluidStack.create(waterTank));
            acceptor.accept(SyncableGasStack.create(steamTank));
            acceptor.accept(SyncableDouble.create(() -> lastPlasmaTemperature, newValue -> lastPlasmaTemperature = newValue));
        }
        acceptor.accept(SyncableDouble.create(() -> lastCaseTemperature, newValue -> lastCaseTemperature = newValue));
        acceptor.accept(SyncableDouble.create(() -> lastEnvironmentLoss, newValue -> lastEnvironmentLoss = newValue));
        acceptor.accept(SyncableDouble.create(() -> lastTransferLoss, newValue -> lastTransferLoss = newValue));
        if (FUEL_TAB.equals(tag)) {
            acceptor.accept(SyncableGasStack.create(deuteriumTank));
            acceptor.accept(SyncableGasStack.create(tritiumTank));
            acceptor.accept(SyncableGasStack.create(fuelTank));
        }
        if (FUEL_TAB.equals(tag) || HEAT_TAB.equals(tag) || STATS_TAB.equals(tag)) {
            acceptor.accept(SyncableInt.create(this::getInjectionRate, this::setInjectionRate));
            acceptor.accept(SyncableLong.create(() -> lastBurned, newValue -> lastBurned = newValue));
        }
    }
}
