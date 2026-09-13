package mekanism.generators.common.item;

import java.util.Iterator;
import java.util.List;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.chemical.item.RateLimitGasHandler;
import mekanism.common.registration.impl.CreativeTabDeferredRegister.ICustomCreativeTabContents;
import mekanism.common.storage.item.GasItemStorage;
import mekanism.common.storage.item.ItemStorageHandler;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.StorageUtils;
import mekanism.generators.common.GeneratorTags;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.registries.GeneratorsGases;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemHohlraum extends Item implements ICustomCreativeTabContents, ItemStorageHandler {

    public ItemHohlraum(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        Storage<Gas> gasHandlerItem = Capabilities.GAS_HANDLER_ITEM.find(stack, ContainerItemContext.withConstant(stack));
        if (gasHandlerItem != null) {
            Iterator<StorageView<Gas>> iterator = gasHandlerItem.iterator();
            if (iterator.hasNext()) {
                //Validate something didn't go terribly wrong, and we actually do have the tank we expect to have
                StorageView<Gas> gasView = iterator.next();
                GasStack storedGas = gasView.getResource().getStack(gasView.getAmount());
                if (!storedGas.isEmpty()) {
                    tooltip.add(MekanismLang.STORED.translate(storedGas, storedGas.getAmount()));
                    if (storedGas.getAmount() == gasView.getCapacity()) {
                        tooltip.add(GeneratorsLang.READY_FOR_REACTION.translateColored(EnumColor.DARK_GREEN));
                    } else {
                        tooltip.add(GeneratorsLang.INSUFFICIENT_FUEL.translateColored(EnumColor.DARK_RED));
                    }
                    return;
                }
            }
        }
        tooltip.add(MekanismLang.NO_GAS.translate());
        tooltip.add(GeneratorsLang.INSUFFICIENT_FUEL.translateColored(EnumColor.DARK_RED));
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
        return ChemicalUtil.getRGBDurabilityForDisplay(stack);
    }

    @Override
    public void addItems(CreativeModeTab.Output tabOutput) {
        tabOutput.accept(ChemicalUtil.getFilledVariant(new ItemStack(this), MekanismGeneratorsConfig.generators.hohlraumMaxGas, GeneratorsGases.FUSION_FUEL));
    }

    @Override
    public Storage<Gas> getGasStorage(ContainerItemContext context) {
        return new GasItemStorage(context, () -> RateLimitGasHandler.create(() -> MekanismGeneratorsConfig.generators.hohlraumFillRate, () -> MekanismGeneratorsConfig.generators.hohlraumMaxGas,
                ChemicalTankBuilder.GAS.notExternal, ChemicalTankBuilder.GAS.alwaysTrueBi, GeneratorTags.Gases.FUSION_FUEL_LOOKUP::contains).getTanks());
    }
}