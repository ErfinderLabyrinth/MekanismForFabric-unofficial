package mekanism.common.tile.multiblock;

import mekanism.api.IContentsListener;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.content.sps.SPSMultiblockData;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.lib.multiblock.IMultiblockEjector;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.base.SubstanceType;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.text.BooleanStateDisplay.InputOutput;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TileEntitySPSPort extends TileEntitySPSCasing implements IMultiblockEjector {

    private MachineEnergyContainer<TileEntitySPSPort> energyContainer;
    private Set<Direction> outputDirections = Collections.emptySet();

    public TileEntitySPSPort(BlockPos pos, BlockState state) {
        super(MekanismBlocks.SPS_PORT, pos, state);
        delaySupplier = NO_DELAY;
    }

    @Override
    protected boolean onUpdateServer(SPSMultiblockData multiblock) {
        boolean needsPacket = super.onUpdateServer(multiblock);
        if (multiblock.isFormed()) {
            if (getActive()) {
                ChemicalUtil.emit(outputDirections, multiblock.outputTank, this.level, this.getBlockPos());
            }
            if (!energyContainer.isEmpty() && multiblock.canSupplyCoilEnergy(this)) {
                try(Transaction t=Transaction.openOuter()) {
                    multiblock.supplyCoilEnergy(this, energyContainer.extract(energyContainer.getEnergy(), t));
                    t.commit();
                }
            }
        }
        return needsPacket;
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(this::getDirection);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this, listener));
        return builder.build();
    }

    @NotNull
    @Override
    public IChemicalTankHolder<Gas, GasStack, IGasTank> getInitialGasTanks(IContentsListener listener) {
        //Note: We can just use a proxied holder as the input/output restrictions are done in the tanks themselves
        return new IChemicalTankHolder<Gas, GasStack, IGasTank>() {
            @Override
            public @NotNull Storage<Gas> getTanks(@Nullable Direction side) {
                return getMultiblock().getGasStorage(side);
            }

            @Override
            public List<IGasTank> getAll() {
                return getMultiblock().getGasTanks();
            }
        };
    }

    @Override
    public boolean persists(SubstanceType type) {
        if (type == SubstanceType.GAS) {
            return false;
        }
        return super.persists(type);
    }

    @Override
    public void setEjectSides(Set<Direction> sides) {
        outputDirections = sides;
    }

    @Override
    public InteractionResult onSneakRightClick(Player player) {
        if (!isRemote()) {
            boolean oldMode = getActive();
            setActive(!oldMode);
            player.displayClientMessage(MekanismLang.SPS_PORT_MODE.translateColored(EnumColor.GRAY, InputOutput.of(oldMode, true)), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public int getRedstoneLevel() {
        return getMultiblock().getCurrentRedstoneLevel();
    }

    //Methods relating to IComputerTile
    @ComputerMethod(methodDescription = "true -> output, false -> input.")//TODO change this to enum?
    boolean getMode() {
        return getActive();
    }

    @ComputerMethod(methodDescription = "true -> output, false -> input.")//TODO change this to enum?
    void setMode(boolean output) {
        setActive(output);
    }
    //End methods IComputerTile
}