package mekanism.common.item.block;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.chemical.item.ChemicalTankRateLimitChemicalTank;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.item.interfaces.IItemSustainedInventory;
import mekanism.common.storage.item.GasItemStorage;
import mekanism.common.storage.item.InfusionItemStorage;
import mekanism.common.storage.item.PigmentItemStorage;
import mekanism.common.storage.item.SlurryItemStorage;
import mekanism.common.tier.ChemicalTankTier;
import mekanism.common.tile.TileEntityChemicalTank;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.text.TextUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemBlockChemicalTank extends ItemBlockTooltip<BlockTileModel<TileEntityChemicalTank, Machine<TileEntityChemicalTank>>> implements IItemSustainedInventory {

    public ItemBlockChemicalTank(BlockTileModel<TileEntityChemicalTank, Machine<TileEntityChemicalTank>> block) {
        super(block);
    }

    @Override
    public ChemicalTankTier getTier() {
        return Attribute.getTier(getBlock(), ChemicalTankTier.class);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        ChemicalTankTier tier = getTier();
        StorageUtils.addStoredSubstance(stack, tooltip, tier == ChemicalTankTier.CREATIVE);
        if (tier == ChemicalTankTier.CREATIVE) {
            tooltip.add(MekanismLang.CAPACITY.translateColored(EnumColor.INDIGO, EnumColor.GRAY, MekanismLang.INFINITE));
        } else {
            tooltip.add(MekanismLang.CAPACITY_MB.translateColored(EnumColor.INDIGO, EnumColor.GRAY, TextUtils.format(tier.getStorage())));
        }
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        // No bar for empty containers as bars are drawn on top of stack count number
        return ChemicalUtil.hasGas(stack) ||
               ChemicalUtil.hasChemical(stack, ConstantPredicates.alwaysTrue(), Capabilities.INFUSION_HANDLER_ITEM) ||
               ChemicalUtil.hasChemical(stack, ConstantPredicates.alwaysTrue(), Capabilities.PIGMENT_HANDLER_ITEM) ||
               ChemicalUtil.hasChemical(stack, ConstantPredicates.alwaysTrue(), Capabilities.SLURRY_HANDLER_ITEM);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return StorageUtils.getBarWidth(stack);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return ChemicalUtil.getRGBDurabilityForDisplay(stack);
    }

//    @Override
//    protected void gatherCapabilities(List<ItemCapability> capabilities, ItemStack stack, CompoundTag nbt) {
//        super.gatherCapabilities(capabilities, stack, nbt);
//        capabilities.add(ChemicalTankContentsHandler.create(getTier()));
//    }

    @Override
    public Storage<Gas> getGasStorage(ContainerItemContext context) {
        return new GasItemStorage(context, () -> List.of(new ChemicalTankRateLimitChemicalTank.GasTankRateLimitChemicalTank(getTier(),
                () -> {})));
    }

    @Override
    public Storage<InfuseType> getInfusionStorage(ContainerItemContext context) {
        return new InfusionItemStorage(context, () -> List.of(new ChemicalTankRateLimitChemicalTank.InfusionTankRateLimitChemicalTank(getTier(),
                () -> {})));
    }

    @Override
    public Storage<Pigment> getPigmentStorage(ContainerItemContext context) {
        return new PigmentItemStorage(context, () -> List.of(new ChemicalTankRateLimitChemicalTank.PigmentTankRateLimitChemicalTank(getTier(),
                () -> {})));
    }

    @Override
    public Storage<Slurry> getSlurryStorage(ContainerItemContext context) {
        return new SlurryItemStorage(context, () -> List.of(new ChemicalTankRateLimitChemicalTank.SlurryTankRateLimitChemicalTank(getTier(),
                () -> {})));
    }
}