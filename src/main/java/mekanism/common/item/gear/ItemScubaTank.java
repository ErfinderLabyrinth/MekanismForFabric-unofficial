package mekanism.common.item.gear;

import mekanism.api.NBTConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.providers.IGasProvider;
import mekanism.api.text.EnumColor;
import mekanism.client.render.RenderPropertiesProvider;
import mekanism.client.render.armor.ISpecialGear;
import mekanism.client.render.armor.ISpecialGearGetter;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.interfaces.IItemHUDProvider;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.registries.MekanismGases;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.text.BooleanStateDisplay.OnOff;
import mekanism.common.util.text.BooleanStateDisplay.YesNo;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.function.LongSupplier;

public class ItemScubaTank extends ItemGasArmor implements IItemHUDProvider, IModeItem, ISpecialGearGetter {

    private static final ScubaTankMaterial SCUBA_TANK_MATERIAL = new ScubaTankMaterial();

    public ItemScubaTank(Properties properties) {
        super(SCUBA_TANK_MATERIAL, ArmorItem.Type.CHESTPLATE, properties);
    }

    @Override
    public ISpecialGear getSpecialGear() {
        return RenderPropertiesProvider.scubaTank();
    }

    @Override
    protected LongSupplier getMaxGas() {
        return () -> MekanismConfig.COMMON.gear.scubaMaxGas;
    }

    @Override
    protected LongSupplier getFillRate() {
        return () -> MekanismConfig.COMMON.gear.scubaFillRate;
    }

    @Override
    protected IGasProvider getGasType() {
        return MekanismGases.OXYGEN;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(MekanismLang.FLOWING.translateColored(EnumColor.GRAY, YesNo.of(getFlowing(stack), true)));
    }

    public boolean getFlowing(ItemStack stack) {
        return ItemDataUtils.getBoolean(stack, NBTConstants.RUNNING);
    }

    public void setFlowing(ItemStack stack, boolean flowing) {
        ItemDataUtils.setBoolean(stack, NBTConstants.RUNNING, flowing);
    }

    @Override
    public void addHUDStrings(List<Component> list, Player player, ItemStack stack, EquipmentSlot slotType) {
        if (slotType == getEquipmentSlot()) {
            ItemScubaTank scubaTank = (ItemScubaTank) stack.getItem();
            list.add(MekanismLang.SCUBA_TANK_MODE.translateColored(EnumColor.DARK_GRAY, OnOff.of(scubaTank.getFlowing(stack), true)));
            GasStack stored = GasStack.EMPTY;
            Storage<Gas> gasHandlerItem = ContainerItemContext.withConstant(stack).find(Capabilities.GAS_HANDLER_ITEM);
            if (gasHandlerItem != null) {
                Iterator<StorageView<Gas>> iterator = gasHandlerItem.iterator();
                if (iterator.hasNext()) {
                    StorageView<Gas> view = iterator.next();
                    stored = view.getResource().getStack(view.getAmount());
                }
            }
            list.add(MekanismLang.GENERIC_STORED.translateColored(EnumColor.DARK_GRAY, MekanismGases.OXYGEN, EnumColor.ORANGE, stored.getAmount()));
        }
    }

    @Override
    public void changeMode(@NotNull Player player, @NotNull ItemStack stack, int shift, DisplayChange displayChange) {
        if (Math.abs(shift) % 2 == 1) {
            //We are changing by an odd amount, so toggle the mode
            boolean newState = !getFlowing(stack);
            setFlowing(stack, newState);
            displayChange.sendMessage(player, () -> MekanismLang.FLOWING.translate(OnOff.of(newState, true)));
        }
    }

//    @Override
//    public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
//        return super.getDefaultTooltipHideFlags(stack) | TooltipPart.MODIFIERS.getMask();
//    }

    @Override
    public boolean supportsSlotType(ItemStack stack, @NotNull EquipmentSlot slotType) {
        return slotType == getEquipmentSlot();
    }

    @NothingNullByDefault
    protected static class ScubaTankMaterial extends BaseSpecialArmorMaterial {

        @Override
        public String getName() {
            return Mekanism.MODID + ":scuba_tank";
        }
    }
}