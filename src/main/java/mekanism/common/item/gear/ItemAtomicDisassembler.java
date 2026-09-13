package mekanism.common.item.gear;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMaps;
import mekanism.api.IDisableableEnum;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.math.MathUtils;
import mekanism.api.radial.IRadialDataHelper;
import mekanism.api.radial.RadialData;
import mekanism.api.radial.mode.IRadialMode;
import mekanism.api.text.EnumColor;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.ILangEntry;
import mekanism.client.render.RenderPropertiesProvider;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.mekatool.ModuleExcavationEscalationUnit.ExcavationMode;
import mekanism.common.content.gear.mekatool.ModuleVeinMiningUnit;
import mekanism.common.inventory.SimpleSingleStackStorage;
import mekanism.common.item.gear.ItemAtomicDisassembler.DisassemblerMode;
import mekanism.common.item.interfaces.IItemHUDProvider;
import mekanism.common.lib.attribute.AttributeCache;
import mekanism.common.lib.attribute.IAttributeRefresher;
import mekanism.common.lib.radial.IRadialEnumModeItem;
import mekanism.common.registration.impl.CreativeTabDeferredRegister;
import mekanism.common.registries.MekanismItems;
import mekanism.common.storage.item.EnergyItemStorage;
import mekanism.common.storage.item.ItemStorageHandler;
import mekanism.common.tags.MekanismTags;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class ItemAtomicDisassembler extends DiggerItem implements CreativeTabDeferredRegister.ICustomCreativeTabContents, IItemHUDProvider, IRadialEnumModeItem<DisassemblerMode>, IAttributeRefresher, RenderPropertiesProvider.MekRenderPropertiesGetter, ItemStorageHandler {

    //All basic dig actions except shears
//    public static final Set<ToolAction> ALWAYS_SUPPORTED_ACTIONS = Set.of(ToolActions.AXE_DIG, ToolActions.HOE_DIG, ToolActions.SHOVEL_DIG, ToolActions.PICKAXE_DIG,
//          ToolActions.SWORD_DIG);
    private static final RadialData<DisassemblerMode> RADIAL_DATA = IRadialDataHelper.INSTANCE.dataForEnum(Mekanism.rl("disassembler_mode"), DisassemblerMode.NORMAL);

    /**
     * @apiNote For use in calculating drops of given blocks. Given mods may do checks relating to tool actions we need to make sure that this stack is full energy.
     */
    public static ItemStack fullyChargedStack() {
        ItemAtomicDisassembler disassembler = MekanismItems.ATOMIC_DISASSEMBLER.get();
        ItemStack stack = new ItemStack(disassembler);
        return StorageUtils.getFilledEnergyVariant(stack);
    }

    private final AttributeCache attributeCache;

    public ItemAtomicDisassembler(Properties properties) {
        super(1, -2.8F, Tiers.NETHERITE, null, properties.rarity(Rarity.RARE));
        //super(() -> MekanismConfig.gear.disassemblerChargeRate, () -> MekanismConfig.gear.disassemblerMaxEnergy, properties.rarity(Rarity.RARE));
        this.attributeCache = new AttributeCache(this, () -> MekanismConfig.COMMON.gear.disassemblerMaxDamage, () -> MekanismConfig.COMMON.gear.disassemblerAttackSpeed);
    }

    @Override
    public RenderPropertiesProvider.MekRenderProperties getRenderProperties() {
        return RenderPropertiesProvider.disassembler();
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState blockState) {
        return blockState.is(BlockTags.SWORD_EFFICIENT) || blockState.is(BlockTags.MINEABLE_WITH_PICKAXE) || blockState.is(BlockTags.MINEABLE_WITH_AXE) || blockState.is(BlockTags.MINEABLE_WITH_SHOVEL) || blockState.is(BlockTags.MINEABLE_WITH_HOE);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        StorageUtils.addStoredEnergy(stack, tooltip, true);
        DisassemblerMode mode = getMode(stack);
        tooltip.add(MekanismLang.MODE.translateColored(EnumColor.INDIGO, mode));
        tooltip.add(MekanismLang.DISASSEMBLER_EFFICIENCY.translateColored(EnumColor.INDIGO, mode.getEfficiency()));
    }

//    @Override
//    public boolean canPerformAction(ItemStack stack, ToolAction action) {
//        if (ALWAYS_SUPPORTED_ACTIONS.contains(action)) {
//            IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
//            if (energyContainer != null) {
//                //Note: We use a hardness of zero here as that will get the minimum potential destroy energy required
//                // as that is the best guess we can currently give whether the corresponding dig action is supported
//                FloatingLong energyRequired = getDestroyEnergy(stack, 0);
//                FloatingLong energyAvailable = energyContainer.getEnergy();
//                //If we don't have enough energy to break at full speed check if the reduced speed could actually mine
//                return energyRequired.smallerOrEqual(energyAvailable) || !energyAvailable.divide(energyRequired).isZero();
//            }
//        }
//        return false;
//    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if(attacker.getItemInHand(InteractionHand.MAIN_HAND) == stack) {
            SimpleSingleStackStorage storage = new SimpleSingleStackStorage(stack);
            EnergyStorage energyStorage = ContainerItemContext.ofSingleSlot(storage).find(EnergyStorage.ITEM);
            if (energyStorage != null && energyStorage.getAmount() != 0) {
                //Try to extract full energy, even if we have a lower damage amount this is fine as that just means
                // we don't have enough energy, but we will remove as much as we can, which is how much corresponds
                // to the amount of damage we will actually do
                try(Transaction t=Transaction.openOuter()) {
                    energyStorage.extract(MekanismConfig.COMMON.gear.disassemblerEnergyUsageWeapon, t);
                    t.commit();
                }
            }
            attacker.setItemInHand(InteractionHand.MAIN_HAND, storage.getStack());
        }
        return true;
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        EnergyStorage energyStorage = ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM);
        if (energyStorage == null) {
            return 0;
        }
        //Use raw hardness to get the best guess of if it is zero or not
        long energyRequired = getDestroyEnergy(stack, state.destroySpeed);
        long energyAvailable;
        try(Transaction t=Transaction.openOuter()) {
            energyAvailable = energyStorage.extract(energyRequired, t);
        }
        if (energyAvailable < energyRequired) {
            //If we can't extract all the energy we need to break it go at base speed reduced by how much we actually have available
            return DisassemblerMode.NORMAL.getEfficiency() * energyAvailable / (float)energyRequired;
        }
        return getMode(stack).getEfficiency();
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level world, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity entityliving) {
        SimpleSingleStackStorage storage = new SimpleSingleStackStorage(stack);
        EnergyStorage energyStorage = ContainerItemContext.ofSingleSlot(storage).find(EnergyStorage.ITEM);
        if (energyStorage != null) {
            try(Transaction t=Transaction.openOuter()) {
                energyStorage.extract(getDestroyEnergy(stack, state.getDestroySpeed(world, pos)), t);
                t.commit();
            }
        }
        entityliving.setItemInHand(InteractionHand.MAIN_HAND, storage.getStack());
        return true;
    }

    @Override
    public boolean allowContinuingBlockBreaking(Player player, ItemStack oldStack, ItemStack newStack) {
        if (player.level().isClientSide || player.isCreative()) {
            return super.allowContinuingBlockBreaking(player, oldStack, newStack);
        }

        EnergyStorage energyStorage = ContainerItemContext.forPlayerInteraction(player, InteractionHand.MAIN_HAND).find(EnergyStorage.ITEM);
        if (energyStorage != null && getMode(newStack) == DisassemblerMode.VEIN) {
            BlockPos pos = ((ServerPlayer)player).gameMode.destroyPos;
            Level world = player.level();
            BlockState state = world.getBlockState(pos);
            long baseDestroyEnergy = getDestroyEnergy(newStack);
            long energyRequired = getDestroyEnergy(baseDestroyEnergy, state.getDestroySpeed(world, pos));
            long canExtracted;
            try(Transaction t=Transaction.openOuter()) {
                canExtracted = energyStorage.extract(energyRequired, t);
            }
            if (canExtracted >= energyRequired) {
                // Only allow mining things that are considered an ore
                if (ModuleVeinMiningUnit.canVeinBlock(state) && state.is(MekanismTags.Blocks.ATOMIC_DISASSEMBLER_ORE)) {
                    Object2IntMap<BlockPos> found = ModuleVeinMiningUnit.findPositions(world, Map.of(pos, state), 0, Reference2BooleanMaps.singleton(state.getBlock(), true));
                    MekanismUtils.veinMineArea(energyStorage, energyRequired, world, pos, (ServerPlayer) player, newStack, this, found, hardness -> 0,
                            (hardness, distance, bs) -> (long) (getDestroyEnergy(baseDestroyEnergy, hardness) * 0.5 * Math.pow(distance, 1.5)));
                }
            }
        }
        return super.allowContinuingBlockBreaking(player, oldStack, newStack);
    }

    private long getDestroyEnergy(ItemStack itemStack, float hardness) {
        return getDestroyEnergy(getDestroyEnergy(itemStack), hardness);
    }

    private long getDestroyEnergy(long baseDestroyEnergy, float hardness) {
        return hardness == 0 ? baseDestroyEnergy / 2 : baseDestroyEnergy;
    }

    private long getDestroyEnergy(ItemStack itemStack) {
        return MekanismConfig.COMMON.gear.disassemblerEnergyUsage * getMode(itemStack).getEfficiency();
    }

    @Override
    public String getModeSaveKey() {
        return NBTConstants.MODE;
    }

    @Override
    public DisassemblerMode getModeByIndex(int ordinal) {
        return DisassemblerMode.byIndexStatic(ordinal);
    }

    @NotNull
    @Override
    public RadialData<DisassemblerMode> getRadialData(ItemStack stack) {
        return RADIAL_DATA;
    }

    @NotNull
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull ItemStack stack, @NotNull EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            EnergyStorage energyStorage = ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM);
            long energy = energyStorage == null ? 0 : energyStorage.getAmount();
            long energyCost = MekanismConfig.COMMON.gear.disassemblerEnergyUsageWeapon;
            if (energy > energyCost) {
                //If we have enough energy to act at full damage, use the cached multimap rather than creating a new one
                // This will be the case the vast majority of the time
                return attributeCache.get();
            }
            //If we don't have enough power use it at a reduced power level
            int minDamage = MekanismConfig.COMMON.gear.disassemblerMinDamage;
            int damageDifference = MekanismConfig.COMMON.gear.disassemblerMaxDamage - minDamage;
            double damage = minDamage + (double) (damageDifference * energy) / energyCost;
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", damage, Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", MekanismConfig.COMMON.gear.disassemblerAttackSpeed, Operation.ADDITION));
            return builder.build();
        }
        return super.getAttributeModifiers(stack, slot);
    }

    @Override
    public void addToBuilder(Builder<Attribute, AttributeModifier> builder) {
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", MekanismConfig.COMMON.gear.disassemblerMaxDamage, Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", MekanismConfig.COMMON.gear.disassemblerAttackSpeed, Operation.ADDITION));
    }

    @Override
    public void addHUDStrings(List<Component> list, Player player, ItemStack stack, EquipmentSlot slotType) {
        DisassemblerMode mode = getMode(stack);
        list.add(MekanismLang.MODE.translateColored(EnumColor.GRAY, EnumColor.INDIGO, mode));
        list.add(MekanismLang.DISASSEMBLER_EFFICIENCY.translateColored(EnumColor.GRAY, EnumColor.INDIGO, mode.getEfficiency()));
    }

    @Override
    public void changeMode(@NotNull Player player, @NotNull ItemStack stack, int shift, DisplayChange displayChange) {
        DisassemblerMode mode = getMode(stack);
        DisassemblerMode newMode = mode.adjust(shift);
        if (mode != newMode) {
            setMode(stack, player, newMode);
            displayChange.sendMessage(player, () -> MekanismLang.DISASSEMBLER_MODE_CHANGE.translate(EnumColor.INDIGO, newMode, EnumColor.AQUA, newMode.getEfficiency()));
        }
    }

    @NotNull
    @Override
    public Component getScrollTextComponent(@NotNull ItemStack stack) {
        DisassemblerMode mode = getMode(stack);
        return MekanismLang.GENERIC_WITH_PARENTHESIS.translateColored(EnumColor.INDIGO, mode, EnumColor.AQUA, mode.getEfficiency());
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
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
    public void addItems(CreativeModeTab.Output tabOutput) {
        tabOutput.accept(StorageUtils.getFilledEnergyVariant(new ItemStack(this)));
    }

    protected long getMaxEnergy(ItemStack stack) {
        return MekanismConfig.COMMON.gear.disassemblerMaxEnergy;
    }

    protected long getChargeRate(ItemStack stack) {
        return MekanismConfig.COMMON.gear.disassemblerChargeRate;
    }

    @Override
    public EnergyStorage getEnergyStorage(ContainerItemContext context) {
        return new EnergyItemStorage(context, () -> RateLimitEnergyHandler.create(() -> MekanismConfig.COMMON.gear.disassemblerChargeRate, () -> MekanismConfig.COMMON.gear.disassemblerMaxEnergy,
                BasicEnergyContainer.manualOnly, BasicEnergyContainer.alwaysTrue));
    }

    @NothingNullByDefault
    public enum DisassemblerMode implements IDisableableEnum<DisassemblerMode>, IHasTextComponent, IRadialMode {
        NORMAL(MekanismLang.RADIAL_EXCAVATION_SPEED_NORMAL, 20, () -> true, EnumColor.BRIGHT_GREEN, ExcavationMode.NORMAL.icon()),
        SLOW(MekanismLang.RADIAL_EXCAVATION_SPEED_SLOW, 8, () -> MekanismConfig.COMMON.gear.disassemblerSlowMode, EnumColor.PINK, ExcavationMode.SLOW.icon()),
        //Note: Uses extreme icon as both are efficiency 128
        FAST(MekanismLang.RADIAL_EXCAVATION_SPEED_FAST, 128, () -> MekanismConfig.COMMON.gear.disassemblerFastMode, EnumColor.RED, ExcavationMode.EXTREME.icon()),
        VEIN(MekanismLang.RADIAL_VEIN_NORMAL, 20, () -> MekanismConfig.COMMON.gear.disassemblerVeinMining, EnumColor.AQUA, MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_RADIAL, "vein_normal.png")),
        OFF(MekanismLang.RADIAL_EXCAVATION_SPEED_OFF, 0, () -> true, EnumColor.WHITE, ExcavationMode.OFF.icon());

        private static final DisassemblerMode[] MODES = values();

        private final BooleanSupplier checkEnabled;
        private final ILangEntry langEntry;
        private final int efficiency;
        private final EnumColor color;
        private final ResourceLocation icon;

        DisassemblerMode(ILangEntry langEntry, int efficiency, BooleanSupplier checkEnabled, EnumColor color, ResourceLocation icon) {
            this.langEntry = langEntry;
            this.efficiency = efficiency;
            this.checkEnabled = checkEnabled;
            this.color = color;
            this.icon = icon;
        }

        /**
         * Gets a Mode from its ordinal. NOTE: if this mode is not enabled then it will reset to NORMAL
         */
        public static DisassemblerMode byIndexStatic(int index) {
            DisassemblerMode mode = MathUtils.getByIndexMod(MODES, index);
            return mode.isEnabled() ? mode : NORMAL;
        }

        @Override
        public DisassemblerMode byIndex(int index) {
            //Note: We can't just use byIndexStatic, as we want to be able to return disabled modes
            return MathUtils.getByIndexMod(MODES, index);
        }

        @Override
        public Component getTextComponent() {
            return langEntry.translate(color);
        }

        @NotNull
        @Override
        public Component sliceName() {
            return getTextComponent();
        }

        public int getEfficiency() {
            return efficiency;
        }

        @Override
        public boolean isEnabled() {
            return checkEnabled.getAsBoolean();
        }

        @NotNull
        @Override
        public ResourceLocation icon() {
            return icon;
        }

        @Override
        public EnumColor color() {
            return color;
        }
    }
}