package mekanism.common.item.gear;

import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.providers.IGasProvider;
import mekanism.common.capabilities.chemical.item.RateLimitGasHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.interfaces.IGasItem;
import mekanism.common.registration.impl.CreativeTabDeferredRegister.ICustomCreativeTabContents;
import mekanism.common.storage.item.GasItemStorage;
import mekanism.common.storage.item.ItemStorageHandler;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.StorageUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.LongSupplier;

public abstract class ItemGasArmor extends ItemSpecialArmor implements IGasItem, ICustomCreativeTabContents, ItemStorageHandler {

    protected ItemGasArmor(ArmorMaterial material, ArmorItem.Type armorType, Properties properties) {
        super(material, armorType, properties.rarity(Rarity.RARE).stacksTo(1));
    }

    protected abstract LongSupplier getMaxGas();

    protected abstract LongSupplier getFillRate();

    protected abstract IGasProvider getGasType();

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        StorageUtils.addStoredGas(stack, tooltip, true, false);
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
        tabOutput.accept(ChemicalUtil.getFilledVariant(new ItemStack(this), getMaxGas().getAsLong(), getGasType()));
    }

    @Override
    protected boolean areCapabilityConfigsLoaded() {
        return super.areCapabilityConfigsLoaded() && MekanismConfig.COMMON.gear.isLoaded();
    }

//    @Override
//    protected void gatherCapabilities(List<ItemCapability> capabilities, ItemStack stack, CompoundTag nbt) {
//        super.gatherCapabilities(capabilities, stack, nbt);
//        capabilities.add(RateLimitGasHandler.create(getFillRate(), getMaxGas(), ChemicalTankBuilder.GAS.notExternal, ChemicalTankBuilder.GAS.alwaysTrueBi,
//              gas -> gas == getGasType().getChemical()));
//    }

    @Override
    public Storage<Gas> getGasStorage(ContainerItemContext context) {
        return new GasItemStorage(context, () -> RateLimitGasHandler.create(getFillRate(), getMaxGas(), ChemicalTankBuilder.GAS.notExternal, ChemicalTankBuilder.GAS.alwaysTrueBi,
                gas -> gas == getGasType().getChemical()).getTanks());
    }
}