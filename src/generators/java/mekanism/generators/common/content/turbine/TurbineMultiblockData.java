package mekanism.generators.common.content.turbine;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.FluidStack;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.MathUtils;
import mekanism.common.capabilities.energy.VariableCapacityEnergyContainer;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerChemicalTankWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.sync.*;
import mekanism.common.inventory.container.sync.chemical.SyncableGasStack;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.tags.MekanismTags;
import mekanism.common.tile.TileEntityChemicalTank.GasMode;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import mekanism.generators.common.tile.turbine.TileEntityTurbineVent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class TurbineMultiblockData extends MultiblockData {

    public static final float ROTATION_THRESHOLD = 0.001F;
    public static final Object2FloatMap<UUID> clientRotationMap = new Object2FloatOpenHashMap<>();

    @WrappingComputerMethod(wrapper = ComputerChemicalTankWrapper.class, methodNames = {"getSteam", "getSteamCapacity", "getSteamNeeded", "getSteamFilledPercentage"}, docPlaceholder = "steam tank")
    public IGasTank gasTank;
    public IExtendedFluidTank ventTank;
    public final List<IExtendedFluidTank> ventTanks;
    public IEnergyContainer energyContainer;
    @SyntheticComputerMethod(getter = "getDumpingMode")
    public GasMode dumpMode = GasMode.IDLE;
    private long energyCapacity = 0;

    @SyntheticComputerMethod(getter = "getBlades")
    public int blades;
    @SyntheticComputerMethod(getter = "getVents")
    public int vents;
    private List<VentData> ventData = Collections.emptyList();
    @SyntheticComputerMethod(getter = "getCoils")
    public int coils;
    @SyntheticComputerMethod(getter = "getCondensers")
    public int condensers;
    public int lowerVolume;

    public BlockPos complex;

    @SyntheticComputerMethod(getter = "getLastSteamInputRate")
    public long lastSteamInput;
    public long newSteamInput;

    @SyntheticComputerMethod(getter = "getFlowRate")
    public long clientFlow;

    public float clientRotation;
    public float prevSteamScale;

    public TurbineMultiblockData(TileEntityTurbineCasing tile) {
        super(tile);
        gasTanks.add(gasTank = new TurbineGasTank(this, createSaveAndComparator()));
        ventTank = VariableCapacityFluidTank.output(this, () -> isFormed() ? condensers * MekanismGeneratorsConfig.generators.condenserRate : FluidConstants.BUCKET,
              fluid -> MekanismTags.Fluids.WATER_LOOKUP.contains(fluid.getFluid()), this);
        ventTanks = Collections.singletonList(ventTank);
        energyContainer = VariableCapacityEnergyContainer.create(this::getEnergyCapacity, automationType -> isFormed(),
              automationType -> automationType == AutomationType.INTERNAL && isFormed(), this);
        energyContainers.add(energyContainer);
    }

    @Override
    protected void updateEjectors(Level world) {
        super.updateEjectors(world);
        for (VentData data : ventData) {
            TileEntityTurbineVent vent = WorldUtils.getTileEntity(TileEntityTurbineVent.class, world, data.location);
            if (vent != null) {
                //Ensure we don't use create a bunch of identical collections potentially using up a bunch of memory
                Set<Direction> sides = SIDE_REFERENCES.computeIfAbsent(data.side, Collections::singleton);
                vent.setEjectSides(sides);
            }
        }
    }

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);

        lastSteamInput = newSteamInput;
        newSteamInput = 0;
        long stored = gasTank.getStored();
        double flowRate = 0;

        long energyNeeded = energyContainer.getNeeded();
        if (stored > 0 && energyNeeded != 0) {
            double energyMultiplier = (MekanismConfig.COMMON.general.maxEnergyPerSteam / TurbineValidator.MAX_BLADES)
                  * Math.min(blades, coils * MekanismGeneratorsConfig.generators.turbineBladesPerCoil);
            if (energyMultiplier == 0) {
                clientFlow = 0;
            } else {
                double rate = lowerVolume * (getDispersers() * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow);
                rate = Math.min(rate, vents * MekanismGeneratorsConfig.generators.turbineVentGasFlow);
                double proportion = stored / (double) getSteamCapacity();
                double origRate = rate;
                rate = Math.min(Math.min(stored, rate), energyNeeded / energyMultiplier) * proportion;
                clientFlow = MathUtils.clampToLong(rate);
                if (clientFlow > 0) {
                    flowRate = rate / origRate;
                    try(Transaction t = Transaction.openOuter()) {
                        energyContainer.insert((long) (energyMultiplier * rate), t);
                        t.commit();
                    }
                    gasTank.shrinkStack(clientFlow);
                    ventTank.setStack(new FluidStack(FluidVariant.of(Fluids.WATER), Math.min(MathUtils.clampToInt(rate), condensers * MekanismGeneratorsConfig.generators.condenserRate)));
                }
            }
        } else {
            clientFlow = 0;
        }

        if (dumpMode != GasMode.IDLE && !gasTank.isEmpty()) {
            long amount = gasTank.getStored();
            if (dumpMode == GasMode.DUMPING) {
                gasTank.shrinkStack(getDumpingAmount(amount));
            } else {//DUMPING_EXCESS
                //Don't allow dumping more than the configured amount
                long targetLevel = MathUtils.clampToLong(gasTank.getCapacity() * MekanismConfig.COMMON.general.dumpExcessKeepRatio);
                if (targetLevel < amount) {
                    gasTank.shrinkStack(Math.min(amount - targetLevel, getDumpingAmount(amount)));
                }
            }
        }

        float newRotation = (float) flowRate;

        if (Math.abs(newRotation - clientRotation) > TurbineMultiblockData.ROTATION_THRESHOLD) {
            clientRotation = newRotation;
            needsPacket = true;
        }
        float scale = MekanismUtils.getScale(prevSteamScale, gasTank);
        if (scale != prevSteamScale) {
            needsPacket = true;
            prevSteamScale = scale;
        }
        return needsPacket;
    }

    private long getDumpingAmount(long stored) {
        return Math.min(stored, Math.max(stored / 50, lastSteamInput * 2));
    }

    public void updateVentData(List<VentData> vents) {
        this.ventData = vents;
        this.vents = this.ventData.size();
    }

    @Override
    public void readUpdateTag(CompoundTag tag) {
        super.readUpdateTag(tag);
        NBTUtils.setFloatIfPresent(tag, NBTConstants.SCALE, scale -> prevSteamScale = scale);
        NBTUtils.setIntIfPresent(tag, NBTConstants.VOLUME, this::setVolume);
        NBTUtils.setIntIfPresent(tag, NBTConstants.LOWER_VOLUME, value -> lowerVolume = value);
        NBTUtils.setGasStackIfPresent(tag, NBTConstants.GAS_STORED, value -> gasTank.setStack(value));
        NBTUtils.setBlockPosIfPresent(tag, NBTConstants.COMPLEX, value -> complex = value);
        NBTUtils.setFloatIfPresent(tag, NBTConstants.ROTATION, value -> clientRotation = value);
        clientRotationMap.put(inventoryID, clientRotation);
    }

    @Override
    public void writeUpdateTag(CompoundTag tag) {
        super.writeUpdateTag(tag);
        tag.putFloat(NBTConstants.SCALE, prevSteamScale);
        tag.putInt(NBTConstants.VOLUME, getVolume());
        tag.putInt(NBTConstants.LOWER_VOLUME, lowerVolume);
        tag.put(NBTConstants.GAS_STORED, gasTank.getStack().write(new CompoundTag()));
        tag.put(NBTConstants.COMPLEX, NbtUtils.writeBlockPos(complex));
        tag.putFloat(NBTConstants.ROTATION, clientRotation);
    }

    @ComputerMethod
    public int getDispersers() {
        return (length() - 2) * (width() - 2) - 1;
    }

    public long getSteamCapacity() {
        return lowerVolume * MekanismGeneratorsConfig.generators.turbineGasPerTank;
    }

    @NotNull
    public long getEnergyCapacity() {
        return energyCapacity;
    }

    @Override
    public void setVolume(int volume) {
        if (getVolume() != volume) {
            super.setVolume(volume);
            energyCapacity = MekanismGeneratorsConfig.generators.turbineEnergyCapacityPerVolume * volume;
        }
    }

    @Override
    protected int getMultiblockRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(gasTank.getStored(), gasTank.getCapacity());
    }

    @ComputerMethod
    public double getProductionRate() {
        double energyMultiplier = (MekanismConfig.COMMON.general.maxEnergyPerSteam / TurbineValidator.MAX_BLADES)
               * Math.min(blades, coils * MekanismGeneratorsConfig.generators.turbineBladesPerCoil);
        return energyMultiplier * clientFlow;
    }

    @ComputerMethod
    public double getMaxProduction() {
        double energyMultiplier = (MekanismConfig.COMMON.general.maxEnergyPerSteam / TurbineValidator.MAX_BLADES)
              * Math.min(blades, coils * MekanismGeneratorsConfig.generators.turbineBladesPerCoil);
        double rate = lowerVolume * (getDispersers() * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow);
        rate = Math.min(rate, vents * MekanismGeneratorsConfig.generators.turbineVentGasFlow);
        return energyMultiplier * rate;
    }

    @ComputerMethod
    public long getMaxFlowRate() {
        double rate = lowerVolume * (getDispersers() * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow);
        rate = Math.min(rate, vents * MekanismGeneratorsConfig.generators.turbineVentGasFlow);
        return MathUtils.clampToLong(rate);
    }

    @ComputerMethod
    public long getMaxWaterOutput() {
        return (long) condensers * MekanismGeneratorsConfig.generators.condenserRate;
    }

    @ComputerMethod(nameOverride = "setDumpingMode")
    public void setDumpMode(GasMode mode) {
        if (dumpMode != mode) {
            dumpMode = mode;
            markDirty();
        }
    }

    //Computer related methods
    @ComputerMethod
    void incrementDumpingMode() {
        setDumpMode(dumpMode.getNext());
    }

    @ComputerMethod
    void decrementDumpingMode() {
        setDumpMode(dumpMode.getPrevious());
    }
    //End computer related methods

    public record VentData(BlockPos location, Direction side) {
    }

    @Override
    public void addSyncables(Consumer<ISyncableData> acceptor, String tag) {
        acceptor.accept(SyncableGasStack.create(gasTank));
        acceptor.accept(SyncableFluidStack.create(ventTank));
        acceptor.accept(SyncableLong.create(energyContainer::getEnergy, energyContainer::setEnergy));
        acceptor.accept(SyncableEnum.create(ordinal -> GasMode.values()[ordinal], GasMode.IDLE, () -> dumpMode, newValue -> dumpMode = newValue));
        acceptor.accept(SyncableInt.create(() -> blades, newValue -> blades = newValue));
        acceptor.accept(SyncableInt.create(() -> vents, newValue -> vents = newValue));
        acceptor.accept(SyncableInt.create(() -> coils, newValue -> coils = newValue));
        acceptor.accept(SyncableInt.create(() -> condensers, newValue -> condensers = newValue));
        acceptor.accept(SyncableInt.create(() -> lowerVolume, newValue -> lowerVolume = newValue));
        acceptor.accept(SyncableLong.create(() -> lastSteamInput, newValue -> lastSteamInput = newValue));
        acceptor.accept(SyncableLong.create(() -> clientFlow, newValue -> clientFlow = newValue));
    }
}
