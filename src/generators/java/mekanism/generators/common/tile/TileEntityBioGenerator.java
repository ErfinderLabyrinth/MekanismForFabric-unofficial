package mekanism.generators.common.tile;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.NBTConstants;
import mekanism.api.RelativeSide;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.capabilities.holder.fluid.FluidTankHelper;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerFluidTankWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.tags.MekanismTags;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import mekanism.generators.common.GeneratorTags;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.registries.GeneratorsBlocks;
import mekanism.generators.common.registries.GeneratorsFluids;
import mekanism.generators.common.slot.FluidFuelInventorySlot;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityBioGenerator extends TileEntityGenerator {

    @WrappingComputerMethod(wrapper = ComputerFluidTankWrapper.class, methodNames = {"getBioFuel", "getBioFuelCapacity", "getBioFuelNeeded",
                                                                                     "getBioFuelFilledPercentage"}, docPlaceholder = "biofuel tank")
    public BasicFluidTank bioFuelTank;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getFuelItem", docPlaceholder = "fuel slot")
    FluidFuelInventorySlot fuelSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy item")
    EnergyInventorySlot energySlot;
    private float lastFluidScale;

    public TileEntityBioGenerator(BlockPos pos, BlockState state) {
        super(GeneratorsBlocks.BIO_GENERATOR, pos, state, () -> MekanismGeneratorsConfig.generators.bioGeneration);
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        FluidTankHelper builder = FluidTankHelper.forSide(this::getDirection);
        builder.addTank(bioFuelTank = VariableCapacityFluidTank.input(() -> MekanismGeneratorsConfig.generators.bioTankCapacity,
                    fluidStack -> GeneratorTags.Fluids.BIOETHANOL_LOOKUP.contains(fluidStack.getFluid()), listener), RelativeSide.LEFT, RelativeSide.RIGHT,
              RelativeSide.BACK, RelativeSide.TOP, RelativeSide.BOTTOM);
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);
        builder.addSlot(fuelSlot = FluidFuelInventorySlot.forFuel(bioFuelTank, stack -> stack.is(MekanismTags.Items.FUELS_BIO) ? 200 : 0,
                    GeneratorsFluids.BIOETHANOL::getFluidStack, listener, 17, 35), RelativeSide.FRONT, RelativeSide.LEFT, RelativeSide.BACK, RelativeSide.TOP,
              RelativeSide.BOTTOM);
        builder.addSlot(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35), RelativeSide.RIGHT);
        return builder.build();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.drainContainer();
        fuelSlot.fillOrBurn();
        if (MekanismUtils.canFunction(this) && !bioFuelTank.isEmpty()) {
            boolean isInserted = false;
            try(Transaction t = Transaction.openOuter()) {
                if(getEnergyContainer().insert(MekanismGeneratorsConfig.generators.bioGeneration, t) == MekanismGeneratorsConfig.generators.bioGeneration) {
                    t.commit();
                    isInserted = true;
                }
            }
            if (isInserted) {
                setActive(true);
                MekanismUtils.logMismatchedStackSize(bioFuelTank.shrinkStack(1), 1);
                float fluidScale = MekanismUtils.getScale(lastFluidScale, bioFuelTank);
                if (fluidScale != lastFluidScale) {
                    lastFluidScale = fluidScale;
                    sendUpdatePacket();
                }
            }
        } else {
            setActive(false);
        }
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag updateTag = super.getReducedUpdateTag();
        updateTag.put(NBTConstants.FLUID_STORED, bioFuelTank.serializeNBT());
        return updateTag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag) {
        super.handleUpdateTag(tag);
        NBTUtils.setCompoundIfPresent(tag, NBTConstants.FLUID_STORED, nbt -> bioFuelTank.deserializeNBT(nbt));
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(bioFuelTank.getAmount(), bioFuelTank.getCapacity());
    }

    @Override
    protected boolean makesComparatorDirty(@Nullable SubstanceType type) {
        return type == SubstanceType.FLUID;
    }

    //Methods relating to IComputerTile
    @Override
    long getProductionRate() {
        return getActive() ? MekanismGeneratorsConfig.generators.bioGeneration : 0;
    }
    //End methods IComputerTile
}