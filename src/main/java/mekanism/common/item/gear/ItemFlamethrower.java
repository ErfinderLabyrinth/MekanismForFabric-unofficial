package mekanism.common.item.gear;

import mekanism.api.IIncrementalEnum;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.math.MathUtils;
import mekanism.api.text.EnumColor;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.ILangEntry;
import mekanism.client.render.RenderPropertiesProvider;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.interfaces.IGasItem;
import mekanism.common.item.interfaces.IItemHUDProvider;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.registration.impl.CreativeTabDeferredRegister.ICustomCreativeTabContents;
import mekanism.common.registries.MekanismGases;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.StorageUtils;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class ItemFlamethrower extends Item implements IItemHUDProvider, IModeItem, IGasItem, ICustomCreativeTabContents, RenderPropertiesProvider.MekRenderPropertiesGetter {

    public ItemFlamethrower(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.RARE));
    }

//    @Override
//    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
//        consumer.accept(RenderPropertiesProvider.flamethrower());
//    }


    @Override
    public RenderPropertiesProvider.MekRenderProperties getRenderProperties() {
        return RenderPropertiesProvider.flamethrower();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        StorageUtils.addStoredGas(stack, tooltip, true, false);
        tooltip.add(MekanismLang.MODE.translateColored(EnumColor.GRAY, getMode(stack)));
    }

    @Override
    public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
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
        tabOutput.accept(ChemicalUtil.getFilledVariant(new ItemStack(this), MekanismConfig.COMMON.gear.flamethrowerMaxGas, MekanismGases.HYDROGEN));
    }

    public FlamethrowerMode getMode(ItemStack stack) {
        return FlamethrowerMode.byIndexStatic(ItemDataUtils.getInt(stack, NBTConstants.MODE));
    }

    public void setMode(ItemStack stack, FlamethrowerMode mode) {
        ItemDataUtils.setInt(stack, NBTConstants.MODE, mode.ordinal());
    }

//    @Override
//    protected boolean areCapabilityConfigsLoaded() {
//        return super.areCapabilityConfigsLoaded() && MekanismConfig.gear.isLoaded();
//    }

//    @Override
//    protected void gatherCapabilities(List<ItemCapability> capabilities, ItemStack stack, CompoundTag nbt) {
//        capabilities.add(RateLimitGasHandler.create(MekanismConfig.gear.flamethrowerFillRate, MekanismConfig.gear.flamethrowerMaxGas,
//              ChemicalTankBuilder.GAS.notExternal, ChemicalTankBuilder.GAS.alwaysTrueBi, gas -> gas == MekanismGases.HYDROGEN.getChemical()));
//    }

    @Override
    public void addHUDStrings(List<Component> list, Player player, ItemStack stack, EquipmentSlot slotType) {
        boolean hasGas = false;
        if (stack.getItem() instanceof IGasHandler gasHandlerItem) {
            Iterator<StorageView<Gas>> iterator = gasHandlerItem.iterator();
            if (iterator.hasNext()) {
                //Validate something didn't go terribly wrong, and we actually do have the tank we expect to have
                StorageView<Gas> view = iterator.next();
                GasStack storedGas = view.getResource().getStack(view.getAmount());
                if (!storedGas.isEmpty()) {
                    list.add(MekanismLang.FLAMETHROWER_STORED.translateColored(EnumColor.GRAY, EnumColor.ORANGE, storedGas.getAmount()));
                    hasGas = true;
                }
            }
        }
        if (!hasGas) {
            list.add(MekanismLang.FLAMETHROWER_STORED.translateColored(EnumColor.GRAY, EnumColor.ORANGE, MekanismLang.NO_GAS));
        }
        list.add(MekanismLang.MODE.translate(getMode(stack)));
    }

    @Override
    public void changeMode(@NotNull Player player, @NotNull ItemStack stack, int shift, DisplayChange displayChange) {
        FlamethrowerMode mode = getMode(stack);
        FlamethrowerMode newMode = mode.adjust(shift);
        if (mode != newMode) {
            setMode(stack, newMode);
            displayChange.sendMessage(player, () -> MekanismLang.FLAMETHROWER_MODE_CHANGE.translate(newMode));
        }
    }

    @NotNull
    @Override
    public Component getScrollTextComponent(@NotNull ItemStack stack) {
        return getMode(stack).getTextComponent();
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

//    @Override
//    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
//        return false;
//    }
//
//    @Override
//    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
//        return false;
//    }

    @NothingNullByDefault
    public enum FlamethrowerMode implements IIncrementalEnum<FlamethrowerMode>, IHasTextComponent {
        COMBAT(MekanismLang.FLAMETHROWER_COMBAT, EnumColor.YELLOW),
        HEAT(MekanismLang.FLAMETHROWER_HEAT, EnumColor.ORANGE),
        INFERNO(MekanismLang.FLAMETHROWER_INFERNO, EnumColor.DARK_RED);

        private static final FlamethrowerMode[] MODES = values();
        private final ILangEntry langEntry;
        private final EnumColor color;

        FlamethrowerMode(ILangEntry langEntry, EnumColor color) {
            this.langEntry = langEntry;
            this.color = color;
        }

        @Override
        public Component getTextComponent() {
            return langEntry.translateColored(color);
        }

        @Override
        public FlamethrowerMode byIndex(int index) {
            return byIndexStatic(index);
        }

        public static FlamethrowerMode byIndexStatic(int index) {
            return MathUtils.getByIndexMod(MODES, index);
        }
    }
}