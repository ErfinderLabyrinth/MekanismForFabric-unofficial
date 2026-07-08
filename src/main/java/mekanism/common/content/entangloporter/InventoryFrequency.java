package mekanism.common.content.entangloporter;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mekanism.api.Coord4D;
import mekanism.api.FluidStack;
import mekanism.api.IContentsListener;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.*;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.heat.HeatAPI;
import mekanism.api.heat.IHeatCapacitor;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.capabilities.heat.ITileHeatHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.network.distribution.ChemicalHandlerTarget;
import mekanism.common.content.network.distribution.EnergyAcceptorTarget;
import mekanism.common.content.network.distribution.FluidHandlerTarget;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.inventory.slot.EntangloporterInventorySlot;
import mekanism.common.lib.frequency.Frequency;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.TileEntityQuantumEntangloporter;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.util.*;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;
import java.util.function.BiConsumer;

public class InventoryFrequency extends Frequency implements /*IMekanismInventory, IMekanismFluidHandler, IMekanismStrictEnergyHandler, */ITileHeatHandler, /*IGasTracker,
      IInfusionTracker, IPigmentTracker, ISlurryTracker implements */IContentsListener {

    private final Map<Coord4D, TileEntityQuantumEntangloporter> activeQEs = new Object2ObjectOpenHashMap<>();
    private long lastEject = -1;

    private BasicFluidTank storedFluid;
    private IGasTank storedGas;
    private IInfusionTank storedInfusion;
    private IPigmentTank storedPigment;
    private ISlurryTank storedSlurry;
    private IInventorySlot storedItem;
    public IEnergyContainer storedEnergy;
    private BasicHeatCapacitor storedHeat;

    private List<IInventorySlot> inventorySlots;
    private List<IGasTank> gasTanks;
    private List<IInfusionTank> infusionTanks;
    private List<IPigmentTank> pigmentTanks;
    private List<ISlurryTank> slurryTanks;
    private List<IExtendedFluidTank> fluidTanks;
    private List<IEnergyContainer> energyContainers;
    private List<IHeatCapacitor> heatCapacitors;

    /**
     * @param uuid Should only be null if we have incomplete data that we are loading
     */
    public InventoryFrequency(String n, @Nullable UUID uuid) {
        super(FrequencyType.INVENTORY, n, uuid);
        presetVariables();
    }

    public InventoryFrequency() {
        super(FrequencyType.INVENTORY);
        presetVariables();
    }

    private void presetVariables() {
        fluidTanks = Collections.singletonList(storedFluid = BasicFluidTank.create(MekanismConfig.COMMON.general.entangloporterFluidBuffer, this));
        gasTanks = Collections.singletonList(storedGas = ChemicalTankBuilder.GAS.create(MekanismConfig.COMMON.general.entangloporterChemicalBuffer, this));
        infusionTanks = Collections.singletonList(storedInfusion = ChemicalTankBuilder.INFUSION.create(MekanismConfig.COMMON.general.entangloporterChemicalBuffer, this));
        pigmentTanks = Collections.singletonList(storedPigment = ChemicalTankBuilder.PIGMENT.create(MekanismConfig.COMMON.general.entangloporterChemicalBuffer, this));
        slurryTanks = Collections.singletonList(storedSlurry = ChemicalTankBuilder.SLURRY.create(MekanismConfig.COMMON.general.entangloporterChemicalBuffer, this));
        inventorySlots = Collections.singletonList(storedItem = EntangloporterInventorySlot.create(this));
        energyContainers = Collections.singletonList(storedEnergy = BasicEnergyContainer.create(MekanismConfig.COMMON.general.entangloporterEnergyBuffer, this));
        heatCapacitors = Collections.singletonList(storedHeat = BasicHeatCapacitor.create(HeatAPI.DEFAULT_HEAT_CAPACITY, HeatAPI.DEFAULT_INVERSE_CONDUCTION,
              1_000, null, this));
    }

    @Override
    public void write(CompoundTag nbtTags) {
        super.write(nbtTags);
        nbtTags.put(NBTConstants.ENERGY_STORED, storedEnergy.serializeNBT());
        nbtTags.put(NBTConstants.FLUID_STORED, storedFluid.serializeNBT());
        nbtTags.put(NBTConstants.GAS_STORED, storedGas.serializeNBT());
        nbtTags.put(NBTConstants.INFUSE_TYPE_STORED, storedInfusion.serializeNBT());
        nbtTags.put(NBTConstants.PIGMENT_STORED, storedPigment.serializeNBT());
        nbtTags.put(NBTConstants.SLURRY_STORED, storedSlurry.serializeNBT());
        nbtTags.put(NBTConstants.ITEM, storedItem.serializeNBT());
        nbtTags.put(NBTConstants.HEAT_STORED, storedHeat.serializeNBT());
    }

    @Override
    protected void read(CompoundTag nbtTags) {
        super.read(nbtTags);
        storedEnergy.deserializeNBT(nbtTags.getCompound(NBTConstants.ENERGY_STORED));
        storedFluid.deserializeNBT(nbtTags.getCompound(NBTConstants.FLUID_STORED));
        storedGas.deserializeNBT(nbtTags.getCompound(NBTConstants.GAS_STORED));
        storedInfusion.deserializeNBT(nbtTags.getCompound(NBTConstants.INFUSE_TYPE_STORED));
        storedPigment.deserializeNBT(nbtTags.getCompound(NBTConstants.PIGMENT_STORED));
        storedSlurry.deserializeNBT(nbtTags.getCompound(NBTConstants.SLURRY_STORED));
        storedItem.deserializeNBT(nbtTags.getCompound(NBTConstants.ITEM));
        storedHeat.deserializeNBT(nbtTags.getCompound(NBTConstants.HEAT_STORED));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        super.write(buffer);
        buffer.writeLong(storedEnergy.getEnergy());
        buffer.writeNbt(storedFluid.getFluid().variant().toNbt());
        buffer.writeLong(storedFluid.getFluid().amount());
        ChemicalUtils.writeChemicalStack(buffer, storedGas.getStack());
        ChemicalUtils.writeChemicalStack(buffer, storedInfusion.getStack());
        ChemicalUtils.writeChemicalStack(buffer, storedPigment.getStack());
        ChemicalUtils.writeChemicalStack(buffer, storedSlurry.getStack());
        buffer.writeNbt(storedItem.serializeNBT());
        buffer.writeDouble(storedHeat.getHeat());
    }

    @Override
    protected void read(FriendlyByteBuf dataStream) {
        super.read(dataStream);
        presetVariables();
        storedEnergy.setEnergy(dataStream.readLong());
        FluidVariant variant = FluidVariant.fromNbt(dataStream.readNbt());
        long amount = dataStream.readLong();
        storedFluid.setStack(new FluidStack(variant, amount));
        storedGas.setStack(ChemicalUtils.readGasStack(dataStream));
        storedInfusion.setStack(ChemicalUtils.readInfusionStack(dataStream));
        storedPigment.setStack(ChemicalUtils.readPigmentStack(dataStream));
        storedSlurry.setStack(ChemicalUtils.readSlurryStack(dataStream));
        storedItem.deserializeNBT(dataStream.readNbt());
        storedHeat.setHeat(dataStream.readDouble());
    }

    @NotNull
    //@Override
    public List<IInventorySlot> getInventorySlots(@Nullable Direction side) {
        return inventorySlots;
    }

    @NotNull
    //@Override
    public List<IGasTank> getGasTanks(@Nullable Direction side) {
        return gasTanks;
    }

    @NotNull
    //@Override
    public List<IInfusionTank> getInfusionTanks(@Nullable Direction side) {
        return infusionTanks;
    }

    @NotNull
    //@Override
    public List<IPigmentTank> getPigmentTanks(@Nullable Direction side) {
        return pigmentTanks;
    }

    @NotNull
    //@Override
    public List<ISlurryTank> getSlurryTanks(@Nullable Direction side) {
        return slurryTanks;
    }

    @NotNull
    //@Override
    public List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
        return fluidTanks;
    }

    @NotNull
    //@Override
    public List<IEnergyContainer> getEnergyContainers(@Nullable Direction side) {
        return energyContainers;
    }

    @NotNull
    //@Override
    public List<IHeatCapacitor> getHeatCapacitors(@Nullable Direction side) {
        return heatCapacitors;
    }

    @Override
    public void onContentsChanged() {
        dirty = true;
    }

    @Override
    public boolean update(BlockEntity tile) {
        boolean changedData = super.update(tile);
        if (tile instanceof TileEntityQuantumEntangloporter entangloporter) {
            //This should always be the case, but validate it and remove if it isn't
            activeQEs.put(entangloporter.getTileCoord(), entangloporter);
        } else {
            activeQEs.remove(new Coord4D(tile));
        }
        return changedData;
    }

    @Override
    public boolean onDeactivate(BlockEntity tile) {
        boolean changedData = super.onDeactivate(tile);
        activeQEs.remove(new Coord4D(tile));
        return changedData;
    }

    public void handleEject(long gameTime) {
        if (isValid() && !activeQEs.isEmpty() && lastEject != gameTime) {
            lastEject = gameTime;
            Map<TransmissionType, BiConsumer<BlockEntity, Direction>> typesToEject = new EnumMap<>(TransmissionType.class);
            //All but heat and item
            List<Runnable> transferHandlers = new ArrayList<>(EnumUtils.TRANSMISSION_TYPES.length - 2);
            int expected = 6 * activeQEs.size();
            addEnergyTransferHandler(typesToEject, transferHandlers, expected);
            addFluidTransferHandler(typesToEject, transferHandlers, expected);
            addChemicalTransferHandler(TransmissionType.GAS, storedGas, typesToEject, transferHandlers, expected);
            addChemicalTransferHandler(TransmissionType.INFUSION, storedInfusion, typesToEject, transferHandlers, expected);
            addChemicalTransferHandler(TransmissionType.PIGMENT, storedPigment, typesToEject, transferHandlers, expected);
            addChemicalTransferHandler(TransmissionType.SLURRY, storedSlurry, typesToEject, transferHandlers, expected);
            if (!typesToEject.isEmpty()) {
                //If we have at least one type to eject (we are not entirely empty)
                // then go through all the QEs and build up the target locations
                for (TileEntityQuantumEntangloporter qe : activeQEs.values()) {
                    if (!MekanismUtils.canFunction(qe)) {
                        //Skip trying to eject for this QE if it can't function
                        continue;
                    }
                    Map<Direction, BlockEntity> adjacentTiles = null;
                    for (Map.Entry<TransmissionType, BiConsumer<BlockEntity, Direction>> entry : typesToEject.entrySet()) {
                        TransmissionType transmissionType = entry.getKey();
                        ConfigInfo config = qe.getConfig().getConfig(transmissionType);
                        //Validate the ejector for the config allows ejecting this transmission type. In theory, we already check all
                        // of this except config#isEjecting before we get here, but we do so anyway for consistency
                        if (config != null && qe.getEjector().isEjecting(config, transmissionType)) {
                            Set<Direction> outputSides = config.getAllOutputtingSides();
                            if (!outputSides.isEmpty()) {
                                if (adjacentTiles == null) {
                                    //Lazy init the map of adjacent tiles
                                    adjacentTiles = new EnumMap<>(Direction.class);
                                }
                                for (Direction side : outputSides) {
                                    BlockEntity tile;
                                    if (adjacentTiles.containsKey(side)) {
                                        //Need to use contains because we allow for null values
                                        tile = adjacentTiles.get(side);
                                    } else {
                                        //Get tile and provide if not null and the block is loaded, prevents ghost chunk loading
                                        tile = WorldUtils.getTileEntity(qe.getLevel(), qe.getBlockPos().relative(side));
                                        adjacentTiles.put(side, tile);
                                    }
                                    if (tile != null) {
                                        entry.getValue().accept(tile, side);
                                    }
                                }
                            }
                        }
                    }
                }
                //Run all our transfer handlers that we have
                for (Runnable transferHandler : transferHandlers) {
                    transferHandler.run();
                }
            }
        }
    }

    private void addEnergyTransferHandler(Map<TransmissionType, BiConsumer<BlockEntity, Direction>> typesToEject, List<Runnable> transferHandlers, int expected) {
        long toSend;
        try(Transaction t = Transaction.openOuter()) {
            toSend = storedEnergy.extract(storedEnergy.getMaxEnergy(), t);
        }
        if (toSend != 0) {
            EnergyAcceptorTarget target = new EnergyAcceptorTarget(expected);
            typesToEject.put(TransmissionType.ENERGY, (tile, side) -> {
                EnergyStorage energyHandler = EnergyStorage.SIDED.find(tile.getLevel(), tile.getBlockPos(), side.getOpposite());
                if (energyHandler != null) {
                    target.addHandler(energyHandler);
                }
            });
            transferHandlers.add(() -> {
                if (target.getHandlerCount() > 0) {
                    try(Transaction t = Transaction.openOuter()) {
                        storedEnergy.extract(EmitUtils.sendToAcceptors(target, toSend), t);
                        t.commit();
                    }
                }
            });
        }
    }

    private void addFluidTransferHandler(Map<TransmissionType, BiConsumer<BlockEntity, Direction>> typesToEject, List<Runnable> transferHandlers, int expected) {
        FluidStack fluidToSend;
        try(Transaction t=Transaction.openOuter()) {
            fluidToSend = new FluidStack(storedFluid.getFluid(), storedFluid.extract(storedFluid.getResource(), storedFluid.getCapacity(), t));
        }
        if (fluidToSend.amount() != 0) {
            FluidHandlerTarget target = new FluidHandlerTarget(fluidToSend, expected);
            typesToEject.put(TransmissionType.FLUID, (tile, side) -> {
                Storage<FluidVariant> storage = FluidStorage.SIDED.find(tile.getLevel(), tile.getBlockPos(), side);
                if (FluidUtils.canFill(storage, fluidToSend)) {
                    target.addHandler(storage);
                };
            });
            transferHandlers.add(() -> {
                if (target.getHandlerCount() > 0) {
                    try(Transaction t=Transaction.openOuter()) {
                        storedFluid.extract(fluidToSend.variant(), EmitUtils.sendToAcceptors(target, fluidToSend.amount(), fluidToSend), t);
                        t.commit();
                    }
                }
            });
        }
    }

    private <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> void addChemicalTransferHandler(TransmissionType chemicalType,
          IChemicalTank<CHEMICAL, STACK> tank, Map<TransmissionType, BiConsumer<BlockEntity, Direction>> typesToEject, List<Runnable> transferHandlers, int expected) {
        long toSendAmount;
        try(Transaction t=Transaction.openOuter()) {
            toSendAmount = tank.extract(tank.getResource(), tank.getCapacity(), t);
        }
        STACK toSend = (STACK) tank.getResource().getStack(toSendAmount);
        if (!toSend.isEmpty()) {
            BlockApiLookup<IChemicalHandler<CHEMICAL, STACK, ?>, Direction> capability = ChemicalUtil.getBlockLookupForChemical(toSend);
            ChemicalHandlerTarget<CHEMICAL, STACK, IChemicalHandler<CHEMICAL, STACK, ?>> target = new ChemicalHandlerTarget<>(toSend, expected);
            typesToEject.put(chemicalType, (tile, side) -> {
                IChemicalHandler<CHEMICAL, STACK, ?> handler = capability.find(tile.getLevel(), tile.getBlockPos(), side.getOpposite());
                if(handler != null) {
                    if (ChemicalUtil.canInsert(handler, toSend)) {
                        target.addHandler(handler);
                    }
                }
            });
            transferHandlers.add(() -> {
                if (target.getHandlerCount() > 0) {
                    long amountExtract = EmitUtils.sendToAcceptors(target, toSend.getAmount(), toSend);
                    try(Transaction t=Transaction.openOuter()) {
                        tank.extract(tank.getResource(), amountExtract, t);
                        t.commit();
                    }
                }
            });
        }
    }
}