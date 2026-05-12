package mekanism.common.tile.machine;

import mekanism.api.IConfigCardAccess;
import mekanism.api.IContentsListener;
import mekanism.api.NBTConstants;
import mekanism.api.RelativeSide;
import mekanism.api.heat.HeatAPI.HeatTransfer;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.energy.ResistiveHeaterEnergyContainer;
import mekanism.common.capabilities.heat.BasicHeatCapacitor;
import mekanism.common.capabilities.heat.CachedAmbientTemperature;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.heat.HeatCapacitorHelper;
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.computer.ComputerException;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerHeatCapacitorWrapper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.ComputerMethod;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.integration.computer.computercraft.ComputerConstants;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableDouble;
import mekanism.common.inventory.container.sync.SyncableLong;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityResistiveHeater extends TileEntityMekanism implements IConfigCardAccess {

    private float soundScale = 1;
    private double lastEnvironmentLoss;
    private double lastTransferLoss;
    private long clientEnergyUsed = 0;

    private ResistiveHeaterEnergyContainer energyContainer;
    @WrappingComputerMethod(wrapper = ComputerHeatCapacitorWrapper.class, methodNames = "getTemperature", docPlaceholder = "heater")
    BasicHeatCapacitor heatCapacitor;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    public TileEntityResistiveHeater(BlockPos pos, BlockState state) {
        super(MekanismBlocks.RESISTIVE_HEATER, pos, state);
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(this::getDirection);
        builder.addContainer(energyContainer = ResistiveHeaterEnergyContainer.input(this, listener), RelativeSide.LEFT, RelativeSide.RIGHT);
        return builder.build();
    }

    @NotNull
    @Override
    protected IHeatCapacitorHolder getInitialHeatCapacitors(IContentsListener listener, CachedAmbientTemperature ambientTemperature) {
        HeatCapacitorHelper builder = HeatCapacitorHelper.forSide(this::getDirection);
        builder.addCapacitor(heatCapacitor = BasicHeatCapacitor.create(100, 5, 100, ambientTemperature, listener));
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 15, 35));
        return builder.build();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        long toUse = 0;
        if (MekanismUtils.canFunction(this)) {
            try(Transaction t=Transaction.openOuter()) {
                toUse = energyContainer.extract(energyContainer.getEnergyPerTick(), t);
                if (toUse != 0) {
                    heatCapacitor.handleHeat(toUse * MekanismConfig.general.resistiveHeaterEfficiency);
                    t.commit();
                }
            }
        }
        setActive(toUse != 0);
        clientEnergyUsed = toUse;
        HeatTransfer transfer = simulate();
        lastEnvironmentLoss = transfer.environmentTransfer();
        lastTransferLoss = transfer.adjacentTransfer();
        float newSoundScale = (float)toUse / 100;
        if (Math.abs(newSoundScale - soundScale) > 0.01) {
            soundScale = newSoundScale;
            sendUpdatePacket();
        }
    }

    @NotNull
    @ComputerMethod
    public long getEnergyUsed() {
        return clientEnergyUsed;
    }

    @ComputerMethod(nameOverride = "getTransferLoss")
    public double getLastTransferLoss() {
        return lastTransferLoss;
    }

    @ComputerMethod(nameOverride = "getEnvironmentalLoss")
    public double getLastEnvironmentLoss() {
        return lastEnvironmentLoss;
    }

    public void setEnergyUsageFromPacket(long floatingLong) {
        energyContainer.updateEnergyUsage(floatingLong);
        markForSave();
    }

    @Override
    public float getVolume() {
        return (float) Math.sqrt(soundScale);
    }

    public MachineEnergyContainer<TileEntityResistiveHeater> getEnergyContainer() {
        return energyContainer;
    }

    @Override
    public CompoundTag getConfigurationData(Player player) {
        CompoundTag data = super.getConfigurationData(player);
        data.putLong(NBTConstants.ENERGY_USAGE, energyContainer.getEnergyPerTick());
        return data;
    }

    @Override
    public void setConfigurationData(Player player, CompoundTag data) {
        super.setConfigurationData(player, data);
        NBTUtils.setLongIfPresent(data, NBTConstants.ENERGY_USAGE, energyContainer::updateEnergyUsage);
    }

    @Override
    public void addContainerTrackers(MekanismContainer container) {
        super.addContainerTrackers(container);
        container.track(SyncableDouble.create(this::getLastTransferLoss, value -> lastTransferLoss = value));
        container.track(SyncableDouble.create(this::getLastEnvironmentLoss, value -> lastEnvironmentLoss = value));
        container.track(SyncableLong.create(this::getEnergyUsed, value -> clientEnergyUsed = value));
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag updateTag = super.getReducedUpdateTag();
        updateTag.putFloat(NBTConstants.SOUND_SCALE, soundScale);
        return updateTag;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        NBTUtils.setFloatIfPresent(tag, NBTConstants.SOUND_SCALE, value -> soundScale = value);
    }

    //Methods relating to IComputerTile
    @ComputerMethod(methodDescription = ComputerConstants.DESCRIPTION_GET_ENERGY_USAGE)
    long getEnergyUsage() {
        return energyContainer.getEnergyPerTick();
    }

    @ComputerMethod(requiresPublicSecurity = true)
    void setEnergyUsage(long usage) throws ComputerException {
        validateSecurityIsPublic();
        setEnergyUsageFromPacket(usage);
    }
    //End methods IComputerTile
}
