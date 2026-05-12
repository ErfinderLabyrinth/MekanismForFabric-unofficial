package mekanism.common.content.gear.mekasuit;

import mekanism.api.FluidStack;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IHUDElement;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.StorageUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

@ParametersAreNotNullByDefault
public class ModuleNutritionalInjectionUnit implements ICustomModule<ModuleNutritionalInjectionUnit> {

    private static final ResourceLocation icon = MekanismUtils.getResource(ResourceType.GUI_HUD, "nutritional_injection_unit.png");

    @Override
    public void tickServer(IModule<ModuleNutritionalInjectionUnit> module, Player player) {
        long usage = MekanismConfig.gear.mekaSuitEnergyUsageNutritionalInjection;
        if (MekanismUtils.isPlayingMode(player) && player.canEat(false)) {
            //Check if we can use a single iteration of it
            ItemStack container = module.getContainer();
            ItemMekaSuitArmor item = (ItemMekaSuitArmor) container.getItem();
            long needed = Math.min(20 - player.getFoodData().getFoodLevel(),
                  item.getContainedFluid(container, MekanismFluids.NUTRITIONAL_PASTE.getFluidStack(1)).amount() / MekanismConfig.general.nutritionalPasteMBPerFood);
            long toFeed = Math.min(module.getContainerEnergy() / usage, needed);
            if (toFeed > 0) {
                module.useEnergy(player, usage * toFeed);
                ContainerItemContext context = ContainerItemContext.ofSingleSlot(module.getContainerStorage());
                Storage<FluidVariant> fluidStorage = context.find(FluidStorage.ITEM);
                if (fluidStorage != null) {
                    try(Transaction t = Transaction.openOuter()) {
                        fluidStorage.extract(FluidVariant.of(MekanismFluids.NUTRITIONAL_PASTE.getFluid()), toFeed * MekanismConfig.general.nutritionalPasteMBPerFood, t);
                        t.commit();
                    }
                }
                player.getFoodData().eat((int) needed, MekanismConfig.general.nutritionalPasteSaturation);
            }
        }
    }

    @Override
    public void addHUDElements(IModule<ModuleNutritionalInjectionUnit> module, Player player, Consumer<IHUDElement> hudElementAdder) {
        if (module.isEnabled()) {
            ItemStack container = module.getContainer();
            Storage<FluidVariant> capability = ContainerItemContext.withConstant(container).find(FluidStorage.ITEM);
            if (capability != null) {
                int max = MekanismConfig.gear.mekaSuitNutritionalMaxStorage;
                try(Transaction t=Transaction.openOuter()) {
                    capability.extract(FluidVariant.of(MekanismFluids.NUTRITIONAL_PASTE.getFluid()), max, t); //Why?
                }
            }
            FluidStack stored = ((ItemMekaSuitArmor) container.getItem()).getContainedFluid(container, MekanismFluids.NUTRITIONAL_PASTE.getFluidStack(1));
            double ratio = StorageUtils.getRatio(stored.amount(), MekanismConfig.gear.mekaSuitNutritionalMaxStorage);
            hudElementAdder.accept(IModuleHelper.INSTANCE.hudElementPercent(icon, ratio));
        }
    }
}
