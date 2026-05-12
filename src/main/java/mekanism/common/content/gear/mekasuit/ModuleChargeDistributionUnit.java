package mekanism.common.content.gear.mekasuit;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.api.math.FloatingLong;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.network.distribution.EnergySaveTarget;
import mekanism.common.inventory.SimpleSingleStackStorage;
import mekanism.common.util.EmitUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNotNullByDefault
public class ModuleChargeDistributionUnit implements ICustomModule<ModuleChargeDistributionUnit> {

    private IModuleConfigItem<Boolean> chargeSuit;
    private IModuleConfigItem<Boolean> chargeInventory;

    @Override
    public void init(IModule<ModuleChargeDistributionUnit> module, ModuleConfigItemCreator configItemCreator) {
        chargeSuit = configItemCreator.createConfigItem("charge_suit", MekanismLang.MODULE_CHARGE_SUIT, new ModuleBooleanData());
        chargeInventory = configItemCreator.createConfigItem("charge_inventory", MekanismLang.MODULE_CHARGE_INVENTORY, new ModuleBooleanData(false));
    }

    @Override
    public void tickServer(IModule<ModuleChargeDistributionUnit> module, Player player) {
        // charge inventory first
        if (chargeInventory.get()) {
            chargeInventory(module, player);
        }
        // distribute suit charge next
        if (chargeSuit.get()) {
            chargeSuit(player);
        }
    }

    private void chargeSuit(Player player) {
        FloatingLong total = FloatingLong.ZERO;
        EnergySaveTarget saveTarget = new EnergySaveTarget(4);
        NonNullList<ItemStack> armor = player.getInventory().armor;
        List<SimpleSingleStackStorage> storages = new ArrayList<>();
        for (int i = 0; i < armor.size(); i++) {
            SimpleSingleStackStorage armorSlot = new SimpleSingleStackStorage(armor.get(i));
            storages.set(i, armorSlot);
            EnergyStorage energyStorage = ContainerItemContext.ofSingleSlot(armorSlot).find(EnergyStorage.ITEM);
            if (energyStorage != null) {
                saveTarget.addDelegate(energyStorage);
                total = total.plusEqual(energyStorage.getAmount());
            }
        }
        for (int i = 0; i < armor.size(); i++) {
            if (armor.get(i) != storages.get(i).getStack()) {
                armor.set(i, storages.get(i).getStack());
            }
        }
        EmitUtils.sendToAcceptors(saveTarget, total);
        saveTarget.save();
    }

    private void chargeInventory(IModule<ModuleChargeDistributionUnit> module, Player player) {
        long toCharge = MekanismConfig.gear.mekaSuitInventoryChargeRate;
        // first try to charge mainhand/offhand item
        toCharge = charge(module, player, ContainerItemContext.ofPlayerHand(player, InteractionHand.MAIN_HAND), toCharge);
        toCharge = charge(module, player, ContainerItemContext.ofPlayerHand(player, InteractionHand.OFF_HAND), toCharge);
        if (toCharge != 0) {
            PlayerInventoryStorage inventoryStorage = PlayerInventoryStorage.of(player);
            for (int i = 0; i < 36; i++) {
                if (i != player.getInventory().selected) {
                    toCharge = charge(module, player, ContainerItemContext.ofPlayerSlot(player, inventoryStorage.getSlot(i)), toCharge);
                    if (toCharge == 0) {
                        break;
                    }
                }
            }
            if (toCharge != 0 && Mekanism.hooks.CuriosLoaded) {
//                Optional<? extends IItemHandler> curiosInventory = CuriosIntegration.getCuriosInventory(player);
//                if (curiosInventory.isPresent()) {
//                    IItemHandler handler = curiosInventory.get();
//                    for (int slot = 0, slots = handler.getSlots(); slot < slots; slot++) {
//                        toCharge = charge(module, player, handler.getStackInSlot(slot), toCharge);
//                        if (toCharge.isZero()) {
//                            break;
//                        }
//                    }
//                }
            }
        }
    }

    /** return rejects */
    private long charge(IModule<ModuleChargeDistributionUnit> module, Player player, ContainerItemContext context, long amount) {
        if (!context.getItemVariant().isBlank() && context.getAmount() != 0 && amount != 0) {
            EnergyStorage storage = context.find(EnergyStorage.ITEM);
            if (storage != null) {
                long simulatedInserted;
                try(Transaction t=Transaction.openOuter()) {
                    simulatedInserted = storage.insert(amount, t);
                }
                if (simulatedInserted > 0) {
                    //If we can actually insert any energy into
                    long inserted;
                    try(Transaction t=Transaction.openOuter()) {
                        inserted = storage.insert(module.useEnergy(player, simulatedInserted, false), t);
                        t.commit();
                    }
                    return amount - inserted;
                }
            }
        }
        return amount;
    }
}