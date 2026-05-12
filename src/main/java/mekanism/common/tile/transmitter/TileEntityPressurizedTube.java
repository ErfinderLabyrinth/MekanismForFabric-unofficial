package mekanism.common.tile.transmitter;

import mekanism.api.NBTConstants;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.gas.attribute.GasAttributes.Radiation;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.math.MathUtils;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.radiation.IRadiationManager;
import mekanism.api.tier.BaseTier;
import mekanism.common.block.states.BlockStateHelper;
import mekanism.common.block.states.TransmitterType;
import mekanism.common.capabilities.DynamicHandler.InteractPredicate;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.GasHandlerManager;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.InfusionHandlerManager;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.PigmentHandlerManager;
import mekanism.common.capabilities.resolver.manager.ChemicalHandlerManager.SlurryHandlerManager;
import mekanism.common.content.network.BoxedChemicalNetwork;
import mekanism.common.content.network.transmitter.BoxedPressurizedTube;
import mekanism.common.integration.computer.IComputerTile;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.interfaces.ITileRadioactive;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;

public class TileEntityPressurizedTube extends TileEntityTransmitter implements IComputerTile, ITileRadioactive{

//    private static final Collection<Capability<?>> CAPABILITIES = Set.of(
//          Capabilities.GAS_HANDLER,
//          Capabilities.INFUSION_HANDLER,
//          Capabilities.PIGMENT_HANDLER,
//          Capabilities.SLURRY_HANDLER
//    );

    private final GasHandlerManager gasHandlerManager;
    private final InfusionHandlerManager infusionHandlerManager;
    private final PigmentHandlerManager pigmentHandlerManager;
    private final SlurryHandlerManager slurryHandlerManager;

    public TileEntityPressurizedTube(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
        InteractPredicate canExtract = getExtractPredicate();
        InteractPredicate canInsert = getInsertPredicate();
        gasHandlerManager = new GasHandlerManager(getHolder(BoxedPressurizedTube::getGasStorage));
        infusionHandlerManager = new InfusionHandlerManager(getHolder(BoxedPressurizedTube::getInfusionStorage));
        pigmentHandlerManager = new PigmentHandlerManager(getHolder(BoxedPressurizedTube::getPigmentStorage));
        slurryHandlerManager = new SlurryHandlerManager(getHolder(BoxedPressurizedTube::getSlurryStorage));
//        ComputerCapabilityHelper.addComputerCapabilities(this, this::addCapabilityResolver);
    }

    @Override
    protected BoxedPressurizedTube createTransmitter(IBlockProvider blockProvider) {
        return new BoxedPressurizedTube(blockProvider, this);
    }

    @Override
    public BoxedPressurizedTube getTransmitter() {
        return (BoxedPressurizedTube) super.getTransmitter();
    }

    @Override
    protected void onUpdateServer() {
        getTransmitter().pullFromAcceptors();
        super.onUpdateServer();
    }

    @Override
    public TransmitterType getTransmitterType() {
        return TransmitterType.PRESSURIZED_TUBE;
    }

    @NotNull
    @Override
    protected BlockState upgradeResult(@NotNull BlockState current, @NotNull BaseTier tier) {
        return BlockStateHelper.copyStateData(current, switch (tier) {
            case BASIC -> MekanismBlocks.BASIC_PRESSURIZED_TUBE;
            case ADVANCED -> MekanismBlocks.ADVANCED_PRESSURIZED_TUBE;
            case ELITE -> MekanismBlocks.ELITE_PRESSURIZED_TUBE;
            case ULTIMATE -> MekanismBlocks.ULTIMATE_PRESSURIZED_TUBE;
            default -> null;
        });
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        //Note: We add the stored information to the initial update tagSupplier and not to the one we sync on side changes which uses getReducedUpdateTag
        CompoundTag updateTag = super.getUpdateTag();
        if (getTransmitter().hasTransmitterNetwork()) {
            BoxedChemicalNetwork network = getTransmitter().getTransmitterNetwork();
            updateTag.put(NBTConstants.BOXED_CHEMICAL, network.lastChemical.write(new CompoundTag()));
            updateTag.putFloat(NBTConstants.SCALE, network.currentScale);
        }
        return updateTag;
    }

    private <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, TANK extends IChemicalTank<CHEMICAL, STACK>>
    IChemicalTankHolder<CHEMICAL, STACK, TANK> getHolder(BiFunction<BoxedPressurizedTube, Direction, Storage<CHEMICAL>> tankFunction) {
        return new IChemicalTankHolder<CHEMICAL, STACK, TANK>() {
            @Override
            public @NotNull Storage<CHEMICAL> getTanks(@Nullable Direction direction) {
                BoxedPressurizedTube tube = getTransmitter();
                if (direction != null && (tube.getConnectionTypeRaw(direction) == ConnectionType.NONE) || tube.isRedstoneActivated()) {
                    //If we actually have a side, and our connection type on that side is none, or we are currently activated by redstone,
                    // then return that we have no tanks
                    return Storage.empty();
                }
                return tankFunction.apply(tube, direction);
            }

            @Override
            public List<TANK> getAll() {
                return List.of();
            }
        };
    }

    @Override
    public float getRadiationScale() {
        if (IRadiationManager.INSTANCE.isRadiationEnabled()) {
            BoxedPressurizedTube tube = getTransmitter();
            if (isRemote()) {
                if (tube.hasTransmitterNetwork()) {
                    BoxedChemicalNetwork network = tube.getTransmitterNetwork();
                    if (!network.lastChemical.isEmpty() && !network.isTankEmpty() && network.lastChemical.getChemical().has(Radiation.class)) {
                        //Note: This may act as full when the network isn't actually full if there is radioactive stuff
                        // going through it, but it shouldn't matter too much
                        return network.currentScale;
                    }
                }
            } else {
                IGasTank gasTank = tube.getGasTank();
                if (!gasTank.isEmpty() && gasTank.getStack().has(Radiation.class)) {
                    return gasTank.getStored() / (float) gasTank.getCapacity();
                }
            }
        }
        return 0;
    }

    @Override
    public int getRadiationParticleCount() {
        return MathUtils.clampToInt(3 * getRadiationScale());
    }

    private Storage<Gas> getGasTanks(@Nullable Direction side) {
        return gasHandlerManager.getContainers(side);
    }

    private Storage<InfuseType> getInfusionTanks(@Nullable Direction side) {
        return infusionHandlerManager.getContainers(side);
    }

    private Storage<Pigment> getPigmentTanks(@Nullable Direction side) {
        return pigmentHandlerManager.getContainers(side);
    }

    private Storage<Slurry> getSlurryTanks(@Nullable Direction side) {
        return slurryHandlerManager.getContainers(side);
    }

    @Override
    public void sideChanged(@NotNull Direction side, @NotNull ConnectionType old, @NotNull ConnectionType type) {
        super.sideChanged(side, old, type);
        if (type == ConnectionType.NONE) {
//            invalidateCapabilities(CAPABILITIES, side);
            //Notify the neighbor on that side our state changed and we no longer have a capability
            WorldUtils.notifyNeighborOfChange(level, side, worldPosition);
        } else if (old == ConnectionType.NONE) {
            //Notify the neighbor on that side our state changed, and we now do have a capability
            WorldUtils.notifyNeighborOfChange(level, side, worldPosition);
        }
    }

    @Override
    public void redstoneChanged(boolean powered) {
        super.redstoneChanged(powered);
        if (powered) {
            //The transmitter now is powered by redstone and previously was not
            //Note: While at first glance the below invalidation may seem over aggressive, it is not actually that aggressive as
            // if a cap has not been initialized yet on a side then invalidating it will just NO-OP
//            invalidateCapabilities(CAPABILITIES, EnumUtils.DIRECTIONS);
        }
        //Note: We do not have to invalidate any caps if we are going from powered to unpowered as all the caps would already be "empty"
    }

    //Methods relating to IComputerTile
    @Override
    public String getComputerName() {
        return getTransmitter().getTier().getBaseTier().getLowerName() + "PressurizedTube";
    }

    @ComputerMethod
    ChemicalStack<?> getBuffer() {
        return getTransmitter().getBufferWithFallback().getChemicalStack();
    }

    @ComputerMethod
    long getCapacity() {
        BoxedPressurizedTube tube = getTransmitter();
        return tube.hasTransmitterNetwork() ? tube.getTransmitterNetwork().getCapacity() : tube.getCapacity();
    }

    @ComputerMethod
    long getNeeded() {
        return getCapacity() - getBuffer().getAmount();
    }

    @ComputerMethod
    double getFilledPercentage() {
        return getBuffer().getAmount() / (double) getCapacity();
    }

    //End methods IComputerTile
}