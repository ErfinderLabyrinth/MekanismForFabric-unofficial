package mekanism.common.item.block;

import mekanism.api.NBTConstants;
import mekanism.api.RelativeSide;
import mekanism.api.text.EnumColor;
import mekanism.client.render.RenderPropertiesProvider;
import mekanism.common.MekanismLang;
import mekanism.common.block.BlockEnergyCube;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.interfaces.IItemSustainedInventory;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registration.impl.CreativeTabDeferredRegister.ICustomCreativeTabContents;
import mekanism.common.storage.item.EnergyItemStorage;
import mekanism.common.tier.EnergyCubeTier;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.text.EnergyDisplay;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public class ItemBlockEnergyCube extends ItemBlockTooltip<BlockEnergyCube> implements IItemSustainedInventory, ICustomCreativeTabContents {

    public ItemBlockEnergyCube(BlockEnergyCube block) {
        super(block);
    }

    @NotNull
    @Override
    public EnergyCubeTier getTier() {
        return Attribute.getTier(getBlock(), EnergyCubeTier.class);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        StorageUtils.addStoredEnergy(stack, tooltip, true);
        tooltip.add(MekanismLang.CAPACITY.translateColored(EnumColor.INDIGO, EnumColor.GRAY, EnergyDisplay.of(getTier().getMaxEnergy())));
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    protected void addTypeDetails(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        //Don't call super so that we can exclude the stored energy from being shown as we show it in hover text
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return StorageUtils.getEnergyBarWidth(stack);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return MekanismConfig.CLIENT.client.energyColor;
    }

    @Override
    public void addItems(CreativeModeTab.Output tabOutput) {
        EnergyCubeTier tier = getTier();
        if (tier == EnergyCubeTier.CREATIVE) {
            //Add the empty and charged variants
            tabOutput.accept(withEnergyCubeSideConfig(DataType.INPUT));
            tabOutput.accept(StorageUtils.getFilledEnergyVariant(withEnergyCubeSideConfig(DataType.OUTPUT)));
        } else {
            tabOutput.accept(StorageUtils.getFilledEnergyVariant(new ItemStack(this)));
        }
    }

    @Override
    public boolean addDefault() {
        return getTier() != EnergyCubeTier.CREATIVE;
    }

    private ItemStack withEnergyCubeSideConfig(DataType dataType) {
        CompoundTag sideConfig = new CompoundTag();
        for (RelativeSide side : EnumUtils.SIDES) {
            NBTUtils.writeEnum(sideConfig, NBTConstants.SIDE + side.ordinal(), dataType);
        }
        CompoundTag configNBT = new CompoundTag();
        configNBT.put(NBTConstants.CONFIG + TransmissionType.ENERGY.ordinal(), sideConfig);
        ItemStack stack = new ItemStack(this);
        ItemDataUtils.setCompound(stack, NBTConstants.COMPONENT_CONFIG, configNBT);
        return stack;
    }

//    @Override
//    protected void gatherCapabilities(List<ItemCapability> capabilities, ItemStack stack, CompoundTag nbt) {
//        super.gatherCapabilities(capabilities, stack, nbt);
//        ItemCapability capability = RateLimitEnergyHandler.create(getTier());
//        int index = IntStream.range(0, capabilities.size()).filter(i -> capabilities.get(i) instanceof ItemStackEnergyHandler).findFirst().orElse(-1);
//        if (index != -1) {
//            //This is likely always the path that will be taken
//            capabilities.set(index, capability);
//        } else {
//            capabilities.add(capability);
//        }
//    }

    @Override
    public EnergyStorage getEnergyStorage(ContainerItemContext context) {
        return new EnergyItemStorage(context, () -> RateLimitEnergyHandler.create(getTier()));
    }
}