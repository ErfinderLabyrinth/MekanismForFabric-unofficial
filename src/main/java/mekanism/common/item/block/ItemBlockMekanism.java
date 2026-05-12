package mekanism.common.item.block;

import mekanism.api.AutomationType;
import mekanism.api.NBTConstants;
import mekanism.api.Upgrade;
import mekanism.api.security.IItemOwnerObjectGetter;
import mekanism.api.security.IOwnerObject;
import mekanism.api.text.TextComponentUtil;
import mekanism.api.tier.ITier;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeEnergy;
import mekanism.common.block.attribute.AttributeUpgradeSupport;
import mekanism.common.block.attribute.Attributes.AttributeSecurity;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.capabilities.security.item.ItemStackSecurityObject;
import mekanism.common.config.MekanismConfig;
import mekanism.common.storage.item.EnergyItemStorage;
import mekanism.common.storage.item.ItemStorageHandler;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.LongSupplier;
import java.util.function.Predicate;

public class ItemBlockMekanism<BLOCK extends Block> extends BlockItem implements IItemOwnerObjectGetter, ItemStorageHandler {

    @NotNull
    private final BLOCK block;

    public ItemBlockMekanism(@NotNull BLOCK block, Item.Properties properties) {
        super(block, properties);
        this.block = block;
    }

    @NotNull
    @Override
    public BLOCK getBlock() {
        return block;
    }

    public ITier getTier() {
        return null;
    }

    public TextColor getTextColor(ItemStack stack) {
        ITier tier = getTier();
        return tier == null ? null : tier.getBaseTier().getColor();
    }

    @NotNull
    @Override
    public Component getName(@NotNull ItemStack stack) {
        TextColor color = getTextColor(stack);
        if (color == null) {
            return super.getName(stack);
        }
        return TextComponentUtil.build(color, super.getName(stack));
    }

//    @Override
//    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
//        if (exposesEnergyCap(oldStack) && exposesEnergyCap(newStack)) {
//            //Ignore NBT for energized items causing re-equip animations
//            return slotChanged || oldStack.getItem() != newStack.getItem();
//        }
//        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
//    }

    @Override
    public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        if (exposesEnergyCap(oldStack) && exposesEnergyCap(newStack)) {
            //Ignore NBT for energized items causing re-equip animations
            return oldStack.getItem() != newStack.getItem();
        }
        return super.allowNbtUpdateAnimation(player, hand, oldStack, newStack);
    }

//    @Override
//    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
//        if (exposesEnergyCap(oldStack) && exposesEnergyCap(newStack)) {
//            //Ignore NBT for energized items causing block break reset
//            return oldStack.getItem() != newStack.getItem();
//        }
//        return super.shouldCauseBlockBreakReset(oldStack, newStack);
//    }

    @Override
    public boolean allowContinuingBlockBreaking(Player player, ItemStack oldStack, ItemStack newStack) {
        if (exposesEnergyCap(oldStack) && exposesEnergyCap(newStack)) {
            //Ignore NBT for energized items causing block break reset
            return oldStack.getItem() == newStack.getItem();
        }
        return super.allowContinuingBlockBreaking(player, oldStack, newStack);
    }

//    protected void gatherCapabilities(List<ItemCapability> capabilities, ItemStack stack, CompoundTag nbt) {
//        if (exposesEnergyCap(stack)) {
//            AttributeEnergy attributeEnergy = Attribute.get(block, AttributeEnergy.class);
//            FloatingLongSupplier maxEnergy;
//            if (Attribute.matches(block, AttributeUpgradeSupport.class, attribute -> attribute.supportedUpgrades().contains(Upgrade.ENERGY))) {
//                //If our block supports energy upgrades, make a more dynamically updating cache for our item's max energy
//                maxEnergy = new UpgradeBasedFloatingLongCache(stack, attributeEnergy::getStorage);
//            } else {
//                //Otherwise, just return that the max is what the base max is
//                maxEnergy = attributeEnergy::getStorage;
//            }
//            capabilities.add(RateLimitEnergyHandler.create(maxEnergy, BasicEnergyContainer.manualOnly, getEnergyCapInsertPredicate()));
//        }
//    }

    @Override
    public EnergyStorage getEnergyStorage(ContainerItemContext context) {
        if (exposesEnergyCap(context.getItemVariant().toStack((int)context.getAmount()))) {
            AttributeEnergy attributeEnergy = Attribute.get(block, AttributeEnergy.class);
            LongSupplier maxEnergy;
            if (Attribute.matches(block, AttributeUpgradeSupport.class, attribute -> attribute.supportedUpgrades().contains(Upgrade.ENERGY))) {
                //If our block supports energy upgrades, make a more dynamically updating cache for our item's max energy
                maxEnergy = new UpgradeBasedFloatingLongCache(context.getItemVariant().toStack((int)context.getAmount()), attributeEnergy::getStorage);
            } else {
                //Otherwise, just return that the max is what the base max is
                maxEnergy = attributeEnergy::getStorage;
            }
            return new EnergyItemStorage(context, () -> RateLimitEnergyHandler.create(maxEnergy, BasicEnergyContainer.manualOnly, getEnergyCapInsertPredicate()));
        }
        return null;
    }

    protected Predicate<@NotNull AutomationType> getEnergyCapInsertPredicate() {
        return BasicEnergyContainer.alwaysTrue;
    }

    protected boolean exposesEnergyCap(ItemStack stack) {
        //Only expose it if the block can't stack
        return Attribute.has(block, AttributeEnergy.class) && !stack.isStackable();
    }

    protected boolean areCapabilityConfigsLoaded(ItemStack stack) {
        if (exposesEnergyCap(stack)) {
            return MekanismConfig.storage.isLoaded() && MekanismConfig.usage.isLoaded();
        }
        return true;
    }

    @Override
    public @Nullable IOwnerObject getOwnerObject(ItemStack stack) {
        if (Attribute.has(block, AttributeSecurity.class)) {
            return new ItemStackSecurityObject(stack);
        }
        return null;
    }

    private static class UpgradeBasedFloatingLongCache implements LongSupplier {

        private final ItemStack stack;
        //TODO: Eventually fix this, ideally we want this to update the overall cached value if this changes because of the config
        // for how much energy a machine can store changes
        private final LongSupplier baseStorage;
        @Nullable
        private CompoundTag lastNBT;
        private long value;

        private UpgradeBasedFloatingLongCache(ItemStack stack, LongSupplier baseStorage) {
            this.stack = stack;
            if (ItemDataUtils.hasData(stack, NBTConstants.COMPONENT_UPGRADE, Tag.TAG_COMPOUND)) {
                this.lastNBT = ItemDataUtils.getCompound(stack, NBTConstants.COMPONENT_UPGRADE).copy();
            } else {
                this.lastNBT = null;
            }
            this.baseStorage = baseStorage;
            this.value = MekanismUtils.getMaxEnergy(this.stack, this.baseStorage.getAsLong());
        }

        @NotNull
        @Override
        public long getAsLong() {
            if (ItemDataUtils.hasData(stack, NBTConstants.COMPONENT_UPGRADE, Tag.TAG_COMPOUND)) {
                CompoundTag upgrades = ItemDataUtils.getCompound(stack, NBTConstants.COMPONENT_UPGRADE);
                if (lastNBT == null || !lastNBT.equals(upgrades)) {
                    lastNBT = upgrades.copy();
                    value = MekanismUtils.getMaxEnergy(stack, baseStorage.getAsLong());
                }
            } else if (lastNBT != null) {
                lastNBT = null;
                value = MekanismUtils.getMaxEnergy(stack, baseStorage.getAsLong());
            }
            return value;
        }
    }
}