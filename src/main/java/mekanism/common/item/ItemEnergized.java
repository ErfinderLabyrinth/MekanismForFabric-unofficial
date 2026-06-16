package mekanism.common.item;

import mekanism.api.AutomationType;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.registration.impl.CreativeTabDeferredRegister.ICustomCreativeTabContents;
import mekanism.common.storage.item.EnergyItemStorage;
import mekanism.common.storage.item.ItemStorageHandler;
import mekanism.common.util.StorageUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Predicate;

public class ItemEnergized extends Item implements ICustomCreativeTabContents, ItemStorageHandler {

    private final LongSupplier chargeRateSupplier;
    private final LongSupplier maxEnergySupplier;
    private final Predicate<@NotNull AutomationType> canExtract;
    private final Predicate<@NotNull AutomationType> canInsert;

    public ItemEnergized(LongSupplier chargeRateSupplier, LongSupplier maxEnergySupplier, Properties properties) {
        this(chargeRateSupplier, maxEnergySupplier, BasicEnergyContainer.manualOnly, BasicEnergyContainer.alwaysTrue, properties);
    }

    public ItemEnergized(LongSupplier chargeRateSupplier, LongSupplier maxEnergySupplier, Predicate<@NotNull AutomationType> canExtract,
          Predicate<@NotNull AutomationType> canInsert, Properties properties) {
        super(properties.stacksTo(1));
        this.chargeRateSupplier = chargeRateSupplier;
        this.maxEnergySupplier = maxEnergySupplier;
        this.canExtract = canExtract;
        this.canInsert = canInsert;
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
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        StorageUtils.addStoredEnergy(stack, tooltip, true);
    }

    @Override
    public void addItems(CreativeModeTab.Output tabOutput) {
        tabOutput.accept(StorageUtils.getFilledEnergyVariant(new ItemStack(this)));
    }

    protected long getMaxEnergy(ItemStack stack) {
        return maxEnergySupplier.getAsLong();
    }

    protected long getChargeRate(ItemStack stack) {
        return chargeRateSupplier.getAsLong();
    }

    public static void register() {
        //Note: We interact with this capability using "manual" as the automation type, to ensure we can properly bypass the energy limit for extracting
        // Internal is used by the "null" side, which is what will get used for most items
        EnergyStorage.ITEM.registerFallback((stack, context) -> {
            if(stack.getItem() instanceof ItemEnergized item) {
                return RateLimitEnergyHandler.create(() -> item.getChargeRate(stack), () -> item.getMaxEnergy(stack), item.canExtract, item.canInsert);
            }
            return null;
        });
    }

    @Override
    public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean allowContinuingBlockBreaking(Player player, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() == newStack.getItem();
    }

    @Override
    public EnergyStorage getEnergyStorage(ContainerItemContext context) {
        ItemStack stack = context.getItemVariant().toStack();
        ItemEnergized item = (ItemEnergized) stack.getItem();
        return new EnergyItemStorage(context, () -> RateLimitEnergyHandler.create(() -> item.getChargeRate(stack), () -> item.getMaxEnergy(stack), item.canExtract, item.canInsert));
    }
}