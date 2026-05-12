package mekanism.common.item.gear;

import mekanism.api.FluidStack;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.capabilities.fluid.item.RateLimitFluidHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.registration.impl.CreativeTabDeferredRegister.ICustomCreativeTabContents;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.storage.item.FluidItemStorage;
import mekanism.common.storage.item.ItemStorageHandler;
import mekanism.common.util.FluidUtils;
import mekanism.common.util.StorageUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class ItemCanteen extends Item implements ICustomCreativeTabContents, ItemStorageHandler {

    public ItemCanteen(Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        StorageUtils.addStoredFluid(stack, tooltip, true, MekanismLang.EMPTY);
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
        return FluidUtils.getRGBDurabilityForDisplay(stack).orElse(0);
    }

    @Override
    public void addItems(CreativeModeTab.Output tabOutput) {
        tabOutput.accept(FluidUtils.getFilledVariant(new ItemStack(this), MekanismConfig.gear.canteenMaxStorage, MekanismFluids.NUTRITIONAL_PASTE));
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level world, @NotNull LivingEntity entityLiving) {
        if (!world.isClientSide && entityLiving instanceof Player player) {
            int needed = (int) Math.min(20 - player.getFoodData().getFoodLevel(), getFluid(stack).amount() / MekanismConfig.general.nutritionalPasteMBPerFood);
            if (needed > 0) {
                player.getFoodData().eat(needed, MekanismConfig.general.nutritionalPasteSaturation);
                Storage<FluidVariant> handler = ContainerItemContext.ofPlayerHand(player, player.getUsedItemHand()).find(FluidStorage.ITEM);
                if (handler != null) {
                    Iterator<StorageView<FluidVariant>> iterator = handler.iterator();
                    if (iterator.hasNext()) {
                        StorageView<FluidVariant> view = iterator.next();
                        try(Transaction t=Transaction.openOuter()) {
                            handler.extract(view.getResource(), needed * MekanismConfig.general.nutritionalPasteMBPerFood, t);
                            t.commit();
                        }
                    }
                }
                entityLiving.gameEvent(GameEvent.DRINK);
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 32;
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

//    @Override
//    protected boolean areCapabilityConfigsLoaded() {
//        return super.areCapabilityConfigsLoaded() && MekanismConfig.gear.isLoaded();
//    }

//    @Override
//    protected void gatherCapabilities(List<ItemCapability> capabilities, ItemStack stack, CompoundTag nbt) {
//        super.gatherCapabilities(capabilities, stack, nbt);
//        capabilities.add(RateLimitFluidHandler.create(() -> MekanismConfig.gear.canteenTransferRate, () -> MekanismConfig.gear.canteenMaxStorage,
//                BasicFluidTank.alwaysTrueBi, BasicFluidTank.alwaysTrueBi, fluid -> fluid.getFluid() == MekanismFluids.NUTRITIONAL_PASTE.getFluid()));
//    }

    @Override
    public Storage<FluidVariant> getFluidStorage(ContainerItemContext context) {
        return new FluidItemStorage(context, () -> RateLimitFluidHandler.create(() -> MekanismConfig.gear.canteenTransferRate, () -> MekanismConfig.gear.canteenMaxStorage,
                BasicFluidTank.alwaysTrueBi, BasicFluidTank.alwaysTrueBi, fluid -> fluid.getFluid() == MekanismFluids.NUTRITIONAL_PASTE.getFluid()).getTanks());
    }

    private FluidStack getFluid(ItemStack stack) {
        ContainerItemContext context = ContainerItemContext.withConstant(stack);
        Storage<FluidVariant> fluidStorage = context.find(FluidStorage.ITEM);
        if (fluidStorage != null) {
//            if (fluidStorage instanceof IMekanismFluidHandler fluidHandler) {
//                IExtendedFluidTank fluidTank = fluidHandler.getFluidTank(0, null);
//                if (fluidTank != null) {
//                    return fluidTank.getFluid();
//                }
//            }
            Iterator<StorageView<FluidVariant>> iterator = fluidStorage.iterator();
            if (iterator.hasNext()) {
                StorageView<FluidVariant> view = iterator.next();
            }
            return FluidStack.EMPTY;
        }
        return FluidStack.EMPTY;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand handIn) {
        if (!playerIn.isCreative() && playerIn.canEat(false) && getFluid(playerIn.getItemInHand(handIn)).amount() >= 50) {
            playerIn.startUsingItem(handIn);
        }
        return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
    }
}
