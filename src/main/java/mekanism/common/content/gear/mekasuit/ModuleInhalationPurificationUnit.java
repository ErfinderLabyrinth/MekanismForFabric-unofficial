package mekanism.common.content.gear.mekasuit;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tags.MekanismTags;
import mekanism.common.util.MekanismUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

@ParametersAreNotNullByDefault
public class ModuleInhalationPurificationUnit implements ICustomModule<ModuleInhalationPurificationUnit> {

    private static final ModuleDamageAbsorbInfo INHALATION_ABSORB_INFO = new ModuleDamageAbsorbInfo(() -> MekanismConfig.COMMON.gear.mekaSuitMagicDamageRatio,
            () -> MekanismConfig.COMMON.gear.mekaSuitEnergyUsageMagicReduce);

    private IModuleConfigItem<Boolean> beneficialEffects;
    private IModuleConfigItem<Boolean> neutralEffects;
    private IModuleConfigItem<Boolean> harmfulEffects;

    @Override
    public void init(IModule<ModuleInhalationPurificationUnit> module, ModuleConfigItemCreator configItemCreator) {
        beneficialEffects = configItemCreator.createConfigItem("beneficial_effects", MekanismLang.MODULE_PURIFICATION_BENEFICIAL, new ModuleBooleanData(false));
        neutralEffects = configItemCreator.createConfigItem("neutral_effects", MekanismLang.MODULE_PURIFICATION_NEUTRAL, new ModuleBooleanData());
        harmfulEffects = configItemCreator.createConfigItem("harmful_effects", MekanismLang.MODULE_PURIFICATION_HARMFUL, new ModuleBooleanData());
    }

    @Override
    public void tickClient(IModule<ModuleInhalationPurificationUnit> module, Player player) {
        //Messy rough estimate version of tickServer so that the timer actually properly updates
        if (!player.isSpectator()) {
            long usage = MekanismConfig.COMMON.gear.mekaSuitEnergyUsagePotionTick;
            boolean free = usage == 0 || player.isCreative();
            long energy = free ? 0 : module.getContainerEnergy();
            if (free || energy >= usage) {
                //Gather all the active effects that we can handle, so that we have them in their own list and
                // don't run into any issues related to CMEs
                List<MobEffectInstance> effects = player.getActiveEffects().stream().filter(this::canHandle).toList();
                for (MobEffectInstance effect : effects) {
                    if (free) {
                        speedupEffect(player, effect);
                    } else {
                        energy = Math.max(energy - usage, 0);
                        speedupEffect(player, effect);
                        if (energy < usage) {
                            //If after using energy, our remaining energy is now smaller than how much we need to use, exit
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tickServer(IModule<ModuleInhalationPurificationUnit> module, Player player) {
        long usage = MekanismConfig.COMMON.gear.mekaSuitEnergyUsagePotionTick;
        boolean free = usage == 0 || player.isCreative();
        EnergyStorage energyContainer = free ? null : module.getEnergyContainer();
        if (free || (energyContainer != null && energyContainer.getAmount() >= usage)) {
            //Gather all the active effects that we can handle, so that we have them in their own list and
            // don't run into any issues related to CMEs
            List<MobEffectInstance> effects = player.getActiveEffects().stream().filter(this::canHandle).toList();
            for (MobEffectInstance effect : effects) {
                if (free) {
                    speedupEffect(player, effect);
                } else if (module.useEnergy(player, energyContainer, usage, true) == 0) {
                    //If we can't actually extract energy, exit
                    break;
                } else {
                    speedupEffect(player, effect);
                    if (energyContainer.getAmount() < usage) {
                        //If after using energy, our remaining energy is now smaller than how much we need to use, exit
                        break;
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public ModuleDamageAbsorbInfo getDamageAbsorbInfo(IModule<ModuleInhalationPurificationUnit> module, DamageSource damageSource) {
        return damageSource.is(MekanismTags.DamageTypes.IS_PREVENTABLE_MAGIC) ? INHALATION_ABSORB_INFO : null;
    }

    private void speedupEffect(Player player, MobEffectInstance effect) {
        for (int i = 0; i < 9; i++) {
            MekanismUtils.speedUpEffectSafely(player, effect);
        }
    }

    private boolean canHandle(MobEffectInstance effectInstance) {
        return MekanismUtils.shouldSpeedUpEffect(effectInstance) && switch (effectInstance.getEffect().getCategory()) {
            case BENEFICIAL -> beneficialEffects.get();
            case HARMFUL -> harmfulEffects.get();
            case NEUTRAL -> neutralEffects.get();
        };
    }
}