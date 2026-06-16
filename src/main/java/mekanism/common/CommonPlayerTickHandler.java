package mekanism.common;

import mekanism.api.chemical.gas.GasStack;
import mekanism.api.functions.FloatSupplier;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import mekanism.common.base.KeySync;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IBlastingItem;
import mekanism.common.content.gear.mekasuit.ModuleGravitationalModulatingUnit;
import mekanism.common.content.gear.mekasuit.ModuleHydraulicPropulsionUnit;
import mekanism.common.content.gear.mekasuit.ModuleHydrostaticRepulsorUnit;
import mekanism.common.content.gear.mekasuit.ModuleLocomotiveBoostingUnit;
import mekanism.common.entity.EntityFlame;
import mekanism.common.inventory.SimpleSingleStackStorage;
import mekanism.common.item.gear.*;
import mekanism.common.item.interfaces.IJetpackItem;
import mekanism.common.item.interfaces.IJetpackItem.JetpackMode;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.registries.MekanismGameEvents;
import mekanism.common.registries.MekanismModules;
import mekanism.common.tags.MekanismTags;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.MekanismUtils;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalFluidTags;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

public class CommonPlayerTickHandler {

    public static boolean isOnGroundOrSleeping(Player player) {
        return player.onGround() || player.isSleeping();
    }

    public static boolean isScubaMaskOn(Player player, ItemStack tank) {
        ItemStack mask = player.getItemBySlot(EquipmentSlot.HEAD);
        return !tank.isEmpty() && !mask.isEmpty() && tank.getItem() instanceof ItemScubaTank scubaTank &&
               mask.getItem() instanceof ItemScubaMask && ChemicalUtil.hasGas(tank) && scubaTank.getFlowing(tank);
    }

    private static boolean isFlamethrowerOn(Player player, ItemStack currentItem) {
        return Mekanism.playerState.isFlamethrowerOn(player) && !currentItem.isEmpty() && currentItem.getItem() instanceof ItemFlamethrower;
    }

    public static float getStepBoost(Player player) {
        ItemStack stack = player.getItemBySlot(EquipmentSlot.FEET);
        if (!stack.isEmpty() && !player.isShiftKeyDown()) {
            if (stack.getItem() instanceof ItemFreeRunners freeRunners && freeRunners.getMode(stack).providesStepBoost()) {
                return 0.5F;
            }
            IModule<ModuleHydraulicPropulsionUnit> module = IModuleHelper.INSTANCE.load(stack, MekanismModules.HYDRAULIC_PROPULSION_UNIT);
            if (module != null && module.isEnabled()) {
                return module.getCustomInstance().getStepHeight();
            }
        }
        return 0;
    }

    public static float getSwimBoost(Player player) {
        ItemStack stack = player.getItemBySlot(EquipmentSlot.LEGS);
        if (!stack.isEmpty()) {
            IModule<ModuleHydrostaticRepulsorUnit> module = IModuleHelper.INSTANCE.load(stack, MekanismModules.HYDROSTATIC_REPULSOR_UNIT);
            if (module != null && module.isEnabled() && module.getCustomInstance().isSwimBoost(module, player)) {
                return 1F;
            }
        }
        return 0;
    }

    public void tickEnd(Player player) {
        Mekanism.playerState.updateStepAssist(player);
        Mekanism.playerState.updateSwimBoost(player);
        if (player instanceof ServerPlayer serverPlayer) {
            RadiationManager.get().tickServer(serverPlayer);
        }

        ItemStack currentItem = player.getInventory().getSelected();
        if (isFlamethrowerOn(player, currentItem)) {
            EntityFlame flame = EntityFlame.create(player);
            if (flame != null) {
                if (flame.isAlive()) {
                    //If the flame is alive (and didn't just instantly hit a block while trying to spawn add it to the world)
                    player.level().addFreshEntity(flame);
                }
                if (MekanismUtils.isPlayingMode(player)) {
                    ((ItemFlamethrower) currentItem.getItem()).useGas(ContainerItemContext.ofPlayerHand(player, InteractionHand.MAIN_HAND), 1);
                }
            }
        }

        ContainerItemContext jetpackContext = IJetpackItem.getActiveJetpack(player);
        if(!jetpackContext.getItemVariant().isBlank()) {
            ItemStack jetpack = jetpackContext.getItemVariant().toStack((int)jetpackContext.getAmount());
            if (!jetpack.isEmpty()) {
                ContainerItemContext primaryJetpackContext = IJetpackItem.getPrimaryJetpack(player);
                ItemStack primaryJetpack = primaryJetpackContext.getItemVariant().toStack((int)primaryJetpackContext.getAmount());
                if (!primaryJetpack.isEmpty()) {
                    JetpackMode primaryMode = ((IJetpackItem) primaryJetpack.getItem()).getJetpackMode(primaryJetpack);
                    JetpackMode mode = IJetpackItem.getPlayerJetpackMode(player, primaryMode, () -> Mekanism.keyMap.has(player.getUUID(), KeySync.ASCEND));
                    if (mode != JetpackMode.DISABLED) {
                        if (IJetpackItem.handleJetpackMotion(player, mode, () -> Mekanism.keyMap.has(player.getUUID(), KeySync.ASCEND))) {
                            player.resetFallDistance();
                            if (player instanceof ServerPlayer serverPlayer) {
                                serverPlayer.connection.aboveGroundTickCount = 0;
                            }
                        }
                        ((IJetpackItem) jetpack.getItem()).useJetpackFuel(jetpackContext);
                        if (player.level().getGameTime() % 10 == 0) {
                            player.gameEvent(MekanismGameEvents.JETPACK_BURN.get());
                        }
                    }
                }
            }
        }

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (isScubaMaskOn(player, chest)) {
            ItemScubaTank tank = (ItemScubaTank) chest.getItem();
            final int max = player.getMaxAirSupply();
            SimpleSingleStackStorage storage = new SimpleSingleStackStorage(chest);
            tank.useGas(ContainerItemContext.ofSingleSlot(storage), 1);
            GasStack received = tank.useGas(ContainerItemContext.ofSingleSlot(storage), max - player.getAirSupply());
            player.setItemSlot(EquipmentSlot.CHEST, storage.getStack());
            if (!received.isEmpty()) {
                player.setAirSupply(player.getAirSupply() + (int) received.getAmount());
            }
            if (player.getAirSupply() == max) {
                for (MobEffectInstance effect : player.getActiveEffects()) {
                    if (MekanismUtils.shouldSpeedUpEffect(effect)) {
                        for (int i = 0; i < 9; i++) {
                            MekanismUtils.speedUpEffectSafely(player, effect);
                        }
                    }
                }
            }
        }

        Mekanism.playerState.updateFlightInfo(player);
    }

    public static boolean isGravitationalModulationReady(Player player) {
        if (MekanismUtils.isPlayingMode(player)) {
            IModule<ModuleGravitationalModulatingUnit> module = IModuleHelper.INSTANCE.load(player.getItemBySlot(EquipmentSlot.CHEST), MekanismModules.GRAVITATIONAL_MODULATING_UNIT);
            return module != null && module.isEnabled() && module.hasEnoughEnergy(MekanismConfig.COMMON.gear.mekaSuitEnergyUsageGravitationalModulation);
        }
        return false;
    }

    public static boolean isGravitationalModulationOn(Player player) {
        return isGravitationalModulationReady(player) && player.getAbilities().flying;
    }

    public boolean onEntityAttacked(LivingEntity entity, DamageSource source, float amount) {
        if (amount <= 0 || !entity.isAlive()) {
            //If some mod does weird things and causes the damage value to be negative or zero then exit
            // as our logic assumes there is actually damage happening and can crash if someone tries to
            // use a negative number as the damage value. We also check to make sure that we don't do
            // anything if the entity is dead as living attack is still fired when the entity is dead
            // for things like fall damage if the entity dies before hitting the ground, and then energy
            // would be depleted regardless if keep inventory is on even if no damage was stopped as the
            // entity can't take damage while dead
            return false;
        }
        //Gas Mask checks
        if (source.is(MekanismTags.DamageTypes.IS_PREVENTABLE_MAGIC)) {
            ItemStack headStack = entity.getItemBySlot(EquipmentSlot.HEAD);
            if (!headStack.isEmpty() && headStack.getItem() instanceof ItemScubaMask) {
                ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);
                if (!chestStack.isEmpty() && chestStack.getItem() instanceof ItemScubaTank tank && tank.getFlowing(chestStack) && ChemicalUtil.hasGas(chestStack)) {
                    return true;
                }
            }
        }
        //Note: We have this here in addition to listening to LivingHurt, so as if we can fully block the damage
        // then we don't play the hurt effect/sound, as cancelling LivingHurtEvent still causes that to happen
        if (source.is(DamageTypeTags.IS_FALL)) {
            //Free runner checks
            FallEnergyInfo info = getFallAbsorptionEnergyInfo(entity);
            if (info != null && tryAbsorbAll(amount, info.container, info.damageRatio, info.energyCost)) {
                return true;
            }
        }
        if (entity instanceof Player player) {
            if (ItemMekaSuitArmor.tryAbsorbAll(player, source, amount)) {
                return true;
            }
        }
        return false;
    }

    public boolean onLivingHurt(LivingEntity entity, DamageSource source, float amount, Consumer<Float> setFloat) {
        if (amount <= 0 || !entity.isAlive()) {
            //If some mod does weird things and causes the damage value to be negative or zero then exit
            // as our logic assumes there is actually damage happening and can crash if someone tries to
            // use a negative number as the damage value. We also check to make sure that we don't do
            // anything if the entity is dead as living attack is still fired when the entity is dead
            // for things like fall damage if the entity dies before hitting the ground, and then energy
            // would be depleted regardless if keep inventory is on even if no damage was stopped as the
            // entity can't take damage while dead. While living hurt is not fired, we catch this case
            // just in case anyway because it is a simple boolean check and there is no guarantee that
            // other mods may not be firing the event manually even when the entity is dead
            return false;
        }
        if (source.is(DamageTypeTags.IS_FALL)) {
            FallEnergyInfo info = getFallAbsorptionEnergyInfo(entity);
            if (info != null && handleDamage(amount, setFloat, info.container, info.damageRatio, info.energyCost)) {
                return true;
            }
        }
        if (entity instanceof Player player) {
            float ratioAbsorbed = ItemMekaSuitArmor.getDamageAbsorbed(player, source, amount);
            if (ratioAbsorbed > 0) {
                float damageRemaining = amount * Math.max(0, 1 - ratioAbsorbed);
                if (damageRemaining <= 0) {
                    return true;
                } else {
                    setFloat.accept(damageRemaining);
                }
            }
        }
        return false;
    }

    private boolean tryAbsorbAll(float amount, @Nullable EnergyStorage energyContainer, FloatSupplier absorptionRatio, LongSupplier energyCost) {
        if (energyContainer != null && absorptionRatio.getAsFloat() == 1) {
            long energyRequirement = (long) (energyCost.getAsLong() * (double)amount);
            if (energyRequirement == 0) {
                //No energy is actually needed to absorb the damage, either because of the config
                // or how small the amount to absorb is
                return true;
            }
            try(Transaction t=Transaction.openOuter()) {
                long simulatedExtract = energyContainer.extract(energyRequirement, t);
                if (simulatedExtract == energyRequirement) {
                    //If we could fully negate the damage cancel the event and extract it
                    t.commit();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean handleDamage(float amountSource, Consumer<Float> setAmount, @Nullable EnergyStorage energyContainer, FloatSupplier absorptionRatio, LongSupplier energyCost) {
        if (energyContainer != null) {
            float absorption = absorptionRatio.getAsFloat();
            float amount = amountSource * absorption;
            long energyRequirement = (long) (energyCost.getAsLong() * (double)amount);
            float ratioAbsorbed;
            if (energyRequirement == 0) {
                //No energy is actually needed to absorb the damage, either because of the config
                // or how small the amount to absorb is
                ratioAbsorbed = absorption;
            } else {
                try(Transaction t=Transaction.openOuter()) {
                    ratioAbsorbed = absorption * ((float) energyContainer.extract(energyRequirement, t) / amount);
                    t.commit();
                }
            }
            if (ratioAbsorbed > 0) {
                float damageRemaining = amountSource * Math.max(0, 1 - ratioAbsorbed);
                if (damageRemaining <= 0) {
                    return true;
                } else {
                    setAmount.accept(damageRemaining);
                }
            }
        }
        return false;
    }

    public void onLivingJump(LivingEntity entity) {
        if (entity instanceof Player player) {
            IModule<ModuleHydraulicPropulsionUnit> module = IModuleHelper.INSTANCE.load(player.getItemBySlot(EquipmentSlot.FEET), MekanismModules.HYDRAULIC_PROPULSION_UNIT);
            if (module != null && module.isEnabled() && Mekanism.keyMap.has(player.getUUID(), KeySync.BOOST)) {
                float boost = module.getCustomInstance().getBoost();
                long usage = (long) (MekanismConfig.COMMON.gear.mekaSuitBaseJumpEnergyUsage * (boost / 0.1F));
                EnergyStorage energyContainer = module.getEnergyContainer();
                if (module.canUseEnergy(player, energyContainer, usage, false)) {
                    // if we're sprinting with the boost module, limit the height
                    IModule<ModuleLocomotiveBoostingUnit> boostModule = IModuleHelper.INSTANCE.load(player.getItemBySlot(EquipmentSlot.LEGS), MekanismModules.LOCOMOTIVE_BOOSTING_UNIT);
                    if (boostModule != null && boostModule.isEnabled() && boostModule.getCustomInstance().canFunction(boostModule, player)) {
                        boost = (float) Math.sqrt(boost);
                    }
                    player.setDeltaMovement(player.getDeltaMovement().add(0, boost, 0));
                    module.useEnergy(player, energyContainer, usage, true);
                }
            }
        }
    }

    /**
     * @return null if free runners are not being worn, or they don't have an energy container for some reason
     */
    @Nullable
    private FallEnergyInfo getFallAbsorptionEnergyInfo(LivingEntity base) {
        ItemStack feetStack = base.getItemBySlot(EquipmentSlot.FEET);
        if (!feetStack.isEmpty()) {
            SimpleSingleStackStorage storage = new SimpleSingleStackStorage(feetStack);
            ContainerItemContext cic = ContainerItemContext.ofSingleSlot(storage);
            if (feetStack.getItem() instanceof ItemFreeRunners boots) {
                if (boots.getMode(feetStack).preventsFallDamage()) {
                    return new FallEnergyInfo(cic.find(EnergyStorage.ITEM), () -> MekanismConfig.COMMON.gear.freeRunnerFallDamageRatio,
                            () -> MekanismConfig.COMMON.gear.freeRunnerFallEnergyCost);
                }
            } else if (feetStack.getItem() instanceof ItemMekaSuitArmor) {
                return new FallEnergyInfo(cic.find(EnergyStorage.ITEM), () ->MekanismConfig.COMMON.gear.mekaSuitFallDamageRatio,
                        () -> MekanismConfig.COMMON.gear.mekaSuitEnergyUsageFall);
            }
        }
        return null;
    }

    private record FallEnergyInfo(@Nullable EnergyStorage container, FloatSupplier damageRatio, LongSupplier energyCost) {
    }

    public float getBreakSpeed(Player player, float speed, BlockState state, Optional<BlockPos> position) {
        if (position.isPresent()) { // currently its always false
            BlockPos pos = position.get();
            // Blasting item speed check
            ItemStack mainHand = player.getMainHandItem();
            if (!mainHand.isEmpty() && mainHand.getItem() instanceof IBlastingItem tool) {
                Map<BlockPos, BlockState> blocks = tool.getBlastedBlocks(player.level(), player, mainHand, pos, state);
                if (!blocks.isEmpty()) {
                    // Scales mining speed based on hardest block
                    // Does not take into account the tool check for those blocks or other mining speed changes that don't apply to the target block.
                    float targetHardness = state.getDestroySpeed(player.level(), pos);
                    float maxHardness = blocks.entrySet().stream()
                          .map(entry -> entry.getValue().getDestroySpeed(player.level(), entry.getKey()))
                          .reduce(targetHardness, Float::max);
                    speed *= (targetHardness / maxHardness);
                }
            }
        }

        //Gyroscopic stabilization check
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        if (!legs.isEmpty() && IModuleHelper.INSTANCE.isEnabled(legs, MekanismModules.GYROSCOPIC_STABILIZATION_UNIT)) {
            if (player.isEyeInFluid(ConventionalFluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {
                speed *= 5.0F;
            }

            if (!player.onGround()) {
                speed *= 5.0F;
            }
        }

        return speed;
    }
}
