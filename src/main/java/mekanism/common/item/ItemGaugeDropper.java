package mekanism.common.item;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.chemical.variable.RateLimitChemicalTank;
import mekanism.common.capabilities.fluid.item.RateLimitFluidHandler;
import mekanism.common.storage.item.*;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.FluidUtils;
import mekanism.common.util.StorageUtils;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ItemGaugeDropper extends Item implements ItemStorageHandler {
    private static final long CAPACITY = 16 * FluidConstants.BUCKET;
    //TODO: Convert this to a long and make it a config option after making fluids be able to handle longs. Also make the gauge dropper override areCapabilityConfigsLoaded
    private static final long TRANSFER_RATE = 256;

    public ItemGaugeDropper(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return StorageUtils.getBarWidth(stack);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return FluidUtils.getRGBDurabilityForDisplay(stack).orElseGet(() -> ChemicalUtil.getRGBDurabilityForDisplay(stack));
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
        ContainerItemContext context = ContainerItemContext.forPlayerInteraction(player, hand);
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!world.isClientSide) {
                Storage<FluidVariant> handler = context.find(FluidStorage.ITEM);
                if (handler != null) {
                    for (StorageView<FluidVariant> view : handler) {
                        try(Transaction t=Transaction.openOuter()) {
                            view.extract(view.getResource(), view.getAmount(), t);
                            t.commit();
                        }
                        if (view.getAmount() != 0 && !view.isResourceBlank()) {
                            Mekanism.logger.warn("Couldn't clear the fluid tank. Class " + handler.getClass().getName() + ";" + view.getClass().getName());
                        }
                    }
                }
                clearChemicalTanks(context, Capabilities.GAS_HANDLER_ITEM);
                clearChemicalTanks(context, Capabilities.INFUSION_HANDLER_ITEM);
                clearChemicalTanks(context, Capabilities.PIGMENT_HANDLER_ITEM);
                clearChemicalTanks(context, Capabilities.SLURRY_HANDLER_ITEM);
            }
            return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
        }
        return InteractionResultHolder.pass(stack);
    }

    private static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> void clearChemicalTanks(ContainerItemContext context, ItemApiLookup<? extends Storage<CHEMICAL>, ContainerItemContext> lookup) {
        Storage<CHEMICAL> handler = context.find(lookup);
        if (handler != null) {
            for (StorageView<CHEMICAL> view : handler) {
                try(Transaction t=Transaction.openOuter()) {
                    view.extract(view.getResource(), view.getAmount(), t);
                    t.commit();
                }
                if (view.getAmount() != 0 && !view.isResourceBlank()) {
                    Mekanism.logger.warn("Couldn't clear the chemical tank. Class " + handler.getClass().getName() + ";" + view.getClass().getName());
                }
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        StorageUtils.addStoredSubstance(stack, tooltip, false);
    }

    @Override
    public Storage<FluidVariant> getFluidStorage(ContainerItemContext context) {
        return new FluidItemStorage(context, () -> Collections.singletonList(new RateLimitFluidHandler.RateLimitFluidTank(() -> TRANSFER_RATE, () -> CAPACITY, null)));
    }

    @Override
    public Storage<Gas> getGasStorage(ContainerItemContext context) {
        return new GasItemStorage(context, () -> Collections.singletonList(new RateLimitChemicalTank.RateLimitGasTank(() -> TRANSFER_RATE, () -> CAPACITY, ChemicalTankBuilder.GAS.alwaysTrueBi, ChemicalTankBuilder.GAS.alwaysTrueBi,
                    ChemicalTankBuilder.GAS.alwaysTrue, null, null)));
    }

    @Override
    public Storage<InfuseType> getInfusionStorage(ContainerItemContext context) {
        return new InfusionItemStorage(context, () -> Collections.singletonList(new RateLimitChemicalTank.RateLimitInfusionTank(() -> TRANSFER_RATE, () -> CAPACITY, ChemicalTankBuilder.INFUSION.alwaysTrueBi, ChemicalTankBuilder.INFUSION.alwaysTrueBi,
                ChemicalTankBuilder.INFUSION.alwaysTrue, null)));
    }

    @Override
    public Storage<Pigment> getPigmentStorage(ContainerItemContext context) {
        return new PigmentItemStorage(context, () -> Collections.singletonList(new RateLimitChemicalTank.RateLimitPigmentTank(() -> TRANSFER_RATE, () -> CAPACITY, ChemicalTankBuilder.PIGMENT.alwaysTrueBi, ChemicalTankBuilder.PIGMENT.alwaysTrueBi,
                ChemicalTankBuilder.PIGMENT.alwaysTrue, null)));
    }

    @Override
    public Storage<Slurry> getSlurryStorage(ContainerItemContext context) {
        return new SlurryItemStorage(context, () -> Collections.singletonList(new RateLimitChemicalTank.RateLimitSlurryTank(() -> TRANSFER_RATE, () -> CAPACITY, ChemicalTankBuilder.SLURRY.alwaysTrueBi, ChemicalTankBuilder.SLURRY.alwaysTrueBi,
                ChemicalTankBuilder.SLURRY.alwaysTrue, null)));
    }

    /*@Override
    protected void gatherCapabilities(List<ItemCapability> capabilities, ItemStack stack, CompoundTag nbt) {
        capabilities.add(GaugeDropperContentsHandler.create());
    }*/


}