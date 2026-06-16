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
import mekanism.common.item.interfaces.IJetpackItem;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.mixinhelper.LivingEntityExtension;
import mekanism.common.registries.MekanismGases;
import mekanism.common.util.ItemDataUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.function.LongSupplier;

public class ItemJetpack extends ItemGasArmor implements IItemHUDProvider, IModeItem, IJetpackItem, ISpecialGearGetter {

    private static final JetpackMaterial JETPACK_MATERIAL = new JetpackMaterial();

    public ItemJetpack(Properties properties) {
        this(JETPACK_MATERIAL, properties);
    }

    public ItemJetpack(ArmorMaterial material, Properties properties) {
        super(material, ArmorItem.Type.CHESTPLATE, properties);
    }

    @Override
    public ISpecialGear getSpecialGear() {
        return RenderPropertiesProvider.jetpack();
    }

    @Override
    protected LongSupplier getMaxGas() {
        return () -> MekanismConfig.COMMON.gear.jetpackMaxGas;
    }

    @Override
    protected LongSupplier getFillRate() {
        return () -> MekanismConfig.COMMON.gear.jetpackFillRate;
    }

    @Override
    protected IGasProvider getGasType() {
        return MekanismGases.HYDROGEN;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(MekanismLang.MODE.translateColored(EnumColor.GRAY, getJetpackMode(stack).getTextComponent()));
    }

    @Override
    public boolean canUseJetpack(ItemStack stack) {
        return hasGas(stack);
    }

    @Override
    public JetpackMode getJetpackMode(ItemStack stack) {
        return JetpackMode.byIndexStatic(ItemDataUtils.getInt(stack, NBTConstants.MODE));
    }

    @Override
    public void useJetpackFuel(ContainerItemContext context) {
        LivingEntityExtension.SUPPRESS_SOUND.set(true);
        try {
            useGas(context, 1);
        } finally {
            LivingEntityExtension.SUPPRESS_SOUND.set(false);
        }
    }

    public void setMode(ItemStack stack, JetpackMode mode) {
        ItemDataUtils.setInt(stack, NBTConstants.MODE, mode.ordinal());
    }

    @Override
    public void addHUDStrings(List<Component> list, Player player, ItemStack stack, EquipmentSlot slotType) {
        if (slotType == getEquipmentSlot()) {
            ItemJetpack jetpack = (ItemJetpack) stack.getItem();
            list.add(MekanismLang.JETPACK_MODE.translateColored(EnumColor.DARK_GRAY, jetpack.getJetpackMode(stack)));
            GasStack stored = GasStack.EMPTY;
            Storage<Gas> gasHandlerItem = ContainerItemContext.withConstant(stack).find(Capabilities.GAS_HANDLER_ITEM);
            if (gasHandlerItem != null) {
                Iterator<StorageView<Gas>> iterator = gasHandlerItem.iterator();
                if (iterator.hasNext()) {
                    StorageView<Gas> view = iterator.next();
                    stored = view.getResource().getStack(view.getAmount());
                }
            }
            list.add(MekanismLang.JETPACK_STORED.translateColored(EnumColor.DARK_GRAY, EnumColor.ORANGE, stored.getAmount()));
        }
    }

    @Override
    public void changeMode(@NotNull Player player, @NotNull ItemStack stack, int shift, DisplayChange displayChange) {
        JetpackMode mode = getJetpackMode(stack);
        JetpackMode newMode = mode.adjust(shift);
        if (mode != newMode) {
            setMode(stack, newMode);
            displayChange.sendMessage(player, () -> MekanismLang.JETPACK_MODE_CHANGE.translate(newMode));
        }
    }

    @Override
    public boolean supportsSlotType(ItemStack stack, @NotNull EquipmentSlot slotType) {
        return slotType == getEquipmentSlot();
    }

//    @Override
//    public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
//        if (this instanceof ItemArmoredJetpack) {
//            return super.getDefaultTooltipHideFlags(stack);
//        }
//        return super.getDefaultTooltipHideFlags(stack) | TooltipPart.MODIFIERS.getMask();
//    }

    @NothingNullByDefault
    protected static class JetpackMaterial extends BaseSpecialArmorMaterial {

        @Override
        public String getName() {
            return Mekanism.MODID + ":jetpack";
        }
    }
}
