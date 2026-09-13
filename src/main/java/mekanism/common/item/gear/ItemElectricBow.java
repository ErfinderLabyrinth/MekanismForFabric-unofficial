package mekanism.common.item.gear;

import mekanism.api.NBTConstants;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.interfaces.IItemHUDProvider;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.registration.impl.CreativeTabDeferredRegister.ICustomCreativeTabContents;
import mekanism.common.storage.item.EnergyItemStorage;
import mekanism.common.storage.item.ItemStorageHandler;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.text.BooleanStateDisplay.OnOff;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public class ItemElectricBow extends BowItem implements IModeItem, IItemHUDProvider, ICustomCreativeTabContents, ItemStorageHandler {

    public ItemElectricBow(Properties properties) {
        super(properties.rarity(Rarity.RARE).stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        StorageUtils.addStoredEnergy(stack, tooltip, true);
        tooltip.add(MekanismLang.FIRE_MODE.translateColored(EnumColor.PINK, OnOff.of(getFireState(stack))));
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level world, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            //Vanilla diff - Get the energy container and validate we have enough energy, because if something went wrong, then we can exit early
            EnergyStorage energyContainer = null;
            long energyNeeded = 0;
            if (!player.isCreative()) {
                energyContainer = ContainerItemContext.forPlayerInteraction(player, player.getUsedItemHand()).find(EnergyStorage.ITEM);
                energyNeeded = getFireState(stack) ? MekanismConfig.COMMON.gear.electricBowEnergyUsageFire : MekanismConfig.COMMON.gear.electricBowEnergyUsage;
                try(Transaction t=Transaction.openOuter()) {
                    if (energyContainer == null || energyContainer.extract(energyNeeded, t) < energyNeeded) {
                        return;
                    }
                }
            }
            boolean infinity = player.isCreative() || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
            ItemStack ammo = player.getProjectile(stack);
            //int charge = ForgeEventFactory.onArrowLoose(stack, world, player, getUseDuration(stack) - timeLeft, !ammo.isEmpty() || infinity);
            int charge = getUseDuration(stack) - timeLeft;
            if (charge < 0) {
                return;
            }
            if (!ammo.isEmpty() || infinity) {
                float velocity = getPowerForTime(charge);
                if (velocity < 0.1) {
                    return;
                }
                if (ammo.isEmpty()) {
                    ammo = new ItemStack(Items.ARROW);
                }
                boolean noConsume = player.isCreative() ;//TODO || (ammo.getItem() instanceof ArrowItem arrow && arrow.isInfinite(ammo, stack, player));
                if (!world.isClientSide) {
                    ArrowItem arrowitem = (ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
                    AbstractArrow arrowEntity = arrowitem.createArrow(world, ammo, player);
                    //arrowEntity = customArrow(arrowEntity);
                    arrowEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 3 * velocity, 1);
                    if (velocity == 1) {
                        arrowEntity.setCritArrow(true);
                    }
                    int power = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                    if (power > 0) {
                        arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() + 0.5 * power + 0.5);
                    }
                    int punch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                    if (punch > 0) {
                        arrowEntity.setKnockback(punch);
                    }
                    if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                        arrowEntity.setSecondsOnFire(100);
                    }
                    //Vanilla diff - Instead of damaging the item we remove energy from it
                    if (energyContainer != null) {
                        try(Transaction t=Transaction.openOuter()) {
                            energyContainer.extract(energyNeeded, t);
                            t.commit();
                        }
                    }
                    if (noConsume || player.isCreative() && (ammo.getItem() == Items.SPECTRAL_ARROW || ammo.getItem() == Items.TIPPED_ARROW)) {
                        arrowEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }
                    world.addFreshEntity(arrowEntity);
                }
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1,
                      1.0F / (world.random.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);
                if (!noConsume && !player.isCreative()) {
                    ammo.shrink(1);
                    if (ammo.isEmpty()) {
                        player.getInventory().removeItem(ammo);
                    }
                }
                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

//    @Override
//    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
//        //Note: This stops application of it via enchanted books while in survival. We don't override isBookEnchantable as we don't care
//        // if someone enchants it in creative and would rather not stop players from enchanting with books that have flame and power on them
//        return enchantment != Enchantments.FLAMING_ARROWS && super.canApplyAtEnchantingTable(stack, enchantment);
//    }

//    @Override
//    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
//        if (stack.isEmpty()) {
//            return 0;
//        } else if (enchantment == Enchantments.FLAMING_ARROWS && getFireState(stack)) {
//            return Math.max(1, super.getEnchantmentLevel(stack, enchantment));
//        }
//        return super.getEnchantmentLevel(stack, enchantment);
//    }
//
//    @Override
//    public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack) {
//        Map<Enchantment, Integer> enchantments = super.getAllEnchantments(stack);
//        if (getFireState(stack)) {
//            enchantments.merge(Enchantments.FLAMING_ARROWS, 1, Math::max);
//        }
//        return enchantments;
//    }

    private void setFireState(ItemStack stack, boolean state) {
        ItemDataUtils.setBoolean(stack, NBTConstants.MODE, state);
    }

    private boolean getFireState(ItemStack stack) {
        return ItemDataUtils.getBoolean(stack, NBTConstants.MODE);
    }

    @Override
    public void addHUDStrings(List<Component> list, Player player, ItemStack stack, EquipmentSlot slotType) {
        list.add(MekanismLang.FIRE_MODE.translateColored(EnumColor.PINK, OnOff.of(getFireState(stack))));
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
        tabOutput.accept(StorageUtils.getFilledEnergyVariant(new ItemStack(this)));
    }

//    @Override
//    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
//        if (!MekanismConfig.gear.isLoaded()) {
//            //Only expose the capabilities if the required configs are loaded
//            return super.initCapabilities(stack, nbt);
//        }
//        //Note: We interact with this capability using "manual" as the automation type, to ensure we can properly bypass the energy limit for extracting
//        // Internal is used by the "null" side, which is what will get used for most items
//        return new ItemCapabilityWrapper(stack, RateLimitEnergyHandler.create(MekanismConfig.gear.electricBowChargeRate, MekanismConfig.gear.electricBowMaxEnergy,
//              BasicEnergyContainer.manualOnly, BasicEnergyContainer.alwaysTrue));
//    }

    @Override
    public EnergyStorage getEnergyStorage(ContainerItemContext context) {
        return new EnergyItemStorage(context, () -> RateLimitEnergyHandler.create(() -> MekanismConfig.COMMON.gear.electricBowChargeRate, () -> MekanismConfig.COMMON.gear.electricBowMaxEnergy,
                BasicEnergyContainer.manualOnly, BasicEnergyContainer.alwaysTrue));
    }

    @Override
    public void changeMode(@NotNull Player player, @NotNull ItemStack stack, int shift, DisplayChange displayChange) {
        if (Math.abs(shift) % 2 == 1) {
            //We are changing by an odd amount, so toggle the mode
            boolean newState = !getFireState(stack);
            setFireState(stack, newState);
            displayChange.sendMessage(player, () -> MekanismLang.FIRE_MODE.translate(OnOff.of(newState, true)));
        }
    }

    @NotNull
    @Override
    public Component getScrollTextComponent(@NotNull ItemStack stack) {
        return MekanismLang.FIRE_MODE.translateColored(EnumColor.PINK, OnOff.of(getFireState(stack), true));
    }

    @Override
    public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

//    @Override
//    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
//        //Ignore NBT for energized items causing re-equip animations
//        return slotChanged || oldStack.getItem() != newStack.getItem();
//    }

//    @Override
//    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
//        //Ignore NBT for energized items causing block break reset
//        return oldStack.getItem() != newStack.getItem();
//    }

    @Override
    public boolean allowContinuingBlockBreaking(Player player, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() == newStack.getItem();
    }
}