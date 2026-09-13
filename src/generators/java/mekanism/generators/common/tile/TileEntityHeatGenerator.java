package mekanism.generators.common.tile;

import java.util.Arrays;
import java.util.Optional;

import com.google.common.math.DoubleMath;
import com.google.common.math.LongMath;
import mekanism.api.*;
import mekanism.api.heat.HeatAPI.HeatTransfer;
import mekanism.api.heat.IHeatHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.holder.fluid.FluidTankHelper;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.heat.HeatCapacitorHelper;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.config.listener.ConfigBasedCachedFLSupplier;
import mekanism.common.config.listener.ConfigBasedCachedSupplier;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerHeatCapacitorWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.container.sync.SyncableFloatingLong;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.tags.MekanismTags;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.registries.GeneratorsBlocks;
import mekanism.generators.common.slot.FluidFuelInventorySlot;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityHeatGenerator extends TileEntityGenerator {

    private static final double THERMAL_EFFICIENCY = 0.5;
    //Default configs this is 510 compared to the previous 500
    private static final ConfigBasedCachedSupplier<Long> MAX_PRODUCTION = new ConfigBasedCachedSupplier<>(() -> {
        double passiveMax = MekanismGeneratorsConfig.generators.heatGenerationLava * (EnumUtils.DIRECTIONS.length + 1);
        passiveMax += MekanismGeneratorsConfig.generators.heatGenerationNether;
        return (long) (passiveMax + MekanismGeneratorsConfig.generators.heatGeneration);
    });

    /**
     * The FluidTank for this generator.
     */
    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = {"getLava", "getLavaCapacity", "getLavaNeeded", "getLavaFilledPercentage"}, docPlaceholder = "lava tank")
    public BasicFluidTank lavaTank;
    private long producingEnergy = 0;
    private double lastTransferLoss;
    private double lastEnvironmentLoss;

    @WrappingComputerMethod(wrapper = ComputerHeatCapacitorWrapper.class, methodNames = "getTemperature", docPlaceholder = "generator")
    BasicHeatCapacitor heatCapacitor;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getFuelItem", docPlaceholder = "fuel item slot")
    FluidFuelInventorySlot fuelSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item slot")
    EnergyInventorySlot energySlot;

    public TileEntityHeatGenerator(BlockPos pos, BlockState state) {
        super(GeneratorsBlocks.HEAT_GENERATOR, pos, state, MAX_PRODUCTION::get);
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        FluidTankHelper builder = FluidTankHelper.forSide(this::getDirection);
        builder.addTank(lavaTank = VariableCapacityFluidTank.input(() -> MekanismGeneratorsConfig.generators.heatTankCapacity,
                    fluidStack -> MekanismTags.Fluids.LAVA_LOOKUP.contains(fluidStack.getFluid()), listener), RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.BACK,
              RelativeSide.TOP, RelativeSide.BOTTOM);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);
        //Divide the burn time by 20 as that is the ratio of how much a bucket of lava would burn for
        // Eventually we may want to grab the 20 dynamically in case some mod is changing the burn time of a lava bucket
        builder.addSlot(fuelSlot = FluidFuelInventorySlot.forFuel(lavaTank, stack -> FuelRegistry.INSTANCE.get(stack.getItem()) / 20, size -> new FluidStack(FluidVariant.of(Fluids.LAVA), size),
              listener, 17, 35), RelativeSide.FRONT, RelativeSide.LEFT, RelativeSide.BACK, RelativeSide.TOP, RelativeSide.BOTTOM);
        builder.addSlot(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35), RelativeSide.RIGHT);
        return builder.build();
    }

    @NotNull
    @Override
    protected IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener, CachedAmbientTemperature ambientTemperature) {
        HeatCapacitorHelper builder = HeatCapacitorHelper.forSide(this::getDirection);
        builder.addCapacitor(heatCapacitor = BasicHeatCapacitor.create(10, 5, 100, ambientTemperature, listener));
        return builder.build();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.drainContainer();
        fuelSlot.fillOrBurn();
        long prev = getEnergyContainer().getEnergy();
        heatCapacitor.handleHeat(getBoost());
        if (MekanismUtils.canFunction(this) && getEnergyContainer().getNeeded() != 0) {
            int fluidRate = MekanismGeneratorsConfig.generators.heatGenerationFluidRate;
            boolean extracted = false;
            try(Transaction t = Transaction.openOuter()) {
                long amountExtracted = lavaTank.extract(lavaTank.getResource(), fluidRate, t);
                if (amountExtracted == fluidRate) {
                    t.commit();
                    extracted = true;
                }
            }
            if (extracted) {
                setActive(true);
                heatCapacitor.handleHeat(MekanismGeneratorsConfig.generators.heatGeneration);
            } else {
                setActive(false);
            }
        } else {
            setActive(false);
        }
        HeatTransfer loss = simulate();
        lastTransferLoss = loss.adjacentTransfer();
        lastEnvironmentLoss = loss.environmentTransfer();
        producingEnergy = getEnergyContainer().getEnergy() - prev;
    }

    private double getBoost() {
        if (level == null) {
            return 0;
        }
        double boost;
        double passiveLavaAmount = MekanismGeneratorsConfig.generators.heatGenerationLava;
        if (passiveLavaAmount == 0) {
            //If neighboring lava blocks produce no energy, don't bother checking the sides for them
            boost = 0;
        } else {
            //Otherwise, calculate boost to apply from lava
            long lavaSides = Arrays.stream(EnumUtils.DIRECTIONS).filter(side -> {
                //Only check and add loaded neighbors to the which sides have lava on them
                Optional<FluidState> fluidState = WorldUtils.getFluidState(level, worldPosition.relative(side));
                return fluidState.isPresent() && fluidState.get().is(FluidTags.LAVA);
            }).count();
            if (getBlockState().getFluidState().is(FluidTags.LAVA)) {
                //If the heat generator is lava-logged then add it as another side that is adjacent to lava for the heat calculations
                lavaSides++;
            }
            boost = passiveLavaAmount * lavaSides;
        }
        if (level.dimensionType().ultraWarm()) {
            boost = boost + MekanismGeneratorsConfig.generators.heatGenerationNether;
        }
        if (boost == Double.POSITIVE_INFINITY) {
            boost = Double.MAX_VALUE;
        }
        return boost;
    }

    @Override
    public double getInverseInsulation(int capacitor, @Nullable Direction side) {
        return side == Direction.DOWN ? 0 : super.getInverseInsulation(capacitor, side);
    }

    @NotNull
    @Override
    public HeatTransfer simulate() {
        double ambientTemp = ambientTemperature.getAsDouble();
        double temp = getTotalTemperature();
        // 1 - Qc / Qh
        double carnotEfficiency = 1 - Math.min(ambientTemp, temp) / Math.max(ambientTemp, temp);
        double heatLost = THERMAL_EFFICIENCY * (temp - ambientTemp);
        heatCapacitor.handleHeat(-heatLost);
        long energyFromHeat = (long) (Math.abs(heatLost) * carnotEfficiency);
        try(Transaction t = Transaction.openOuter()) {
            getEnergyContainer().insert(Math.min(energyFromHeat, MAX_PRODUCTION.get()), t);
        }
        return super.simulate();
    }

    @Nullable
    @Override
    public IHeatHandler getAdjacent(@NotNull Direction side) {
        if (side == Direction.DOWN) {
            return Capabilities.HEAT_HANDLER_BLOCK.find(getLevel(), worldPosition.below(), side.getOpposite());
        }
        return null;
    }

    @Override
    public long getProductionRate() {
        return producingEnergy;
    }

    @ComputerMethod(nameOverride = "getTransferLoss")
    public double getLastTransferLoss() {
        return lastTransferLoss;
    }

    @ComputerMethod(nameOverride = "getEnvironmentalLoss")
    public double getLastEnvironmentLoss() {
        return lastEnvironmentLoss;
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(lavaTank.getAmount(), lavaTank.getCapacity());
    }

    @Override
    protected boolean makesComparatorDirty(@Nullable SubstanceType type) {
        return type == SubstanceType.FLUID;
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableLong.create(this::getProductionRate, value -> producingEnergy = value));
        container.track(SyncableDouble.create(this::getLastTransferLoss, value -> lastTransferLoss = value));
        container.track(SyncableDouble.create(this::getLastEnvironmentLoss, value -> lastEnvironmentLoss = value));
    }
}
