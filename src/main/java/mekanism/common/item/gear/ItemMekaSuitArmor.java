package mekanism.common.item.gear;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.math.LongMath;
import mekanism.api.FluidStack;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.ICustomModule.ModuleDamageAbsorbInfo;
import mekanism.api.gear.IModule;
import mekanism.api.gear.ModuleData.ExclusiveFlag;
import mekanism.api.lasers.ILaserDissipation;
import mekanism.api.radiation.capability.IRadiationShielding;
import mekanism.api.text.EnumColor;
import mekanism.client.key.MekKeyHandler;
import mekanism.client.key.MekanismKeyHandler;
import mekanism.client.render.RenderPropertiesProvider;
import mekanism.client.render.armor.ICustomArmor;
import mekanism.client.render.armor.ISpecialGear;
import mekanism.client.render.armor.ISpecialGearGetter;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.chemical.item.ChemicalTankSpec;
import mekanism.common.capabilities.chemical.variable.RateLimitChemicalTank;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.capabilities.fluid.item.RateLimitMultiTankFluidHandler;
import mekanism.common.capabilities.fluid.item.RateLimitMultiTankFluidHandler.FluidTankSpec;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.Module;
import mekanism.common.content.gear.mekasuit.ModuleElytraUnit;
import mekanism.common.content.gear.mekasuit.ModuleJetpackUnit;
import mekanism.common.content.gear.shared.ModuleEnergyUnit;
import mekanism.common.item.interfaces.IJetpackItem;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.lib.attribute.AttributeCache;
import mekanism.common.lib.attribute.IAttributeRefresher;
import mekanism.common.mixinhelper.CustomEnchantmentsItem;
import mekanism.common.mixinhelper.ElytraFlyable;
import mekanism.common.mixinhelper.WalkableOnPowderSnow;
import mekanism.common.registration.impl.CreativeTabDeferredRegister.ICustomCreativeTabContents;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismModules;
import mekanism.common.storage.item.EnergyItemStorage;
import mekanism.common.storage.item.FluidItemStorage;
import mekanism.common.storage.item.GasItemStorage;
import mekanism.common.storage.item.ItemStorageHandler;
import mekanism.common.tags.MekanismTags;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class ItemMekaSuitArmor extends ItemSpecialArmor implements IModuleContainerItem, IModeItem, IJetpackItem, IAttributeRefresher, ICustomCreativeTabContents, ISpecialGear, ISpecialGearGetter, WalkableOnPowderSnow, ItemStorageHandler, FabricElytraItem, IRadiationShielding, ILaserDissipation, CustomEnchantmentsItem {

    private static final MekaSuitMaterial MEKASUIT_MATERIAL = new MekaSuitMaterial();

    public static final List<ResourceKey<DamageType>> BASE_ALWAYS_SUPPORTED = List.of(DamageTypes.FALLING_ANVIL, DamageTypes.CACTUS, DamageTypes.CRAMMING,
          DamageTypes.DRAGON_BREATH, DamageTypes.DRY_OUT, DamageTypes.FALL, DamageTypes.FALLING_BLOCK, DamageTypes.FLY_INTO_WALL, DamageTypes.GENERIC,
          DamageTypes.HOT_FLOOR, DamageTypes.IN_FIRE, DamageTypes.IN_WALL, DamageTypes.LAVA, DamageTypes.LIGHTNING_BOLT, DamageTypes.ON_FIRE,
          DamageTypes.SWEET_BERRY_BUSH, DamageTypes.WITHER, DamageTypes.FREEZE, DamageTypes.FALLING_STALACTITE, DamageTypes.STALAGMITE, DamageTypes.SONIC_BOOM);

    public static float getBaseDamageRatio(ResourceKey<DamageType> damageType) {
        if (damageType == DamageTypes.SONIC_BOOM) {
            return 0.75F;
        }
        return 1F;
    }

    private final AttributeCache attributeCache;
    //TODO: Expand this system so that modules can maybe define needed tanks?
    private final List<ChemicalTankSpec<Gas>> gasTankSpecs = new ArrayList<>();
    private final List<ChemicalTankSpec<Gas>> gasTankSpecsView = Collections.unmodifiableList(gasTankSpecs);
    private final List<FluidTankSpec> fluidTankSpecs = new ArrayList<>();
    private final List<FluidTankSpec> fluidTankSpecsView = Collections.unmodifiableList(fluidTankSpecs);
    private final float absorption;
    //Full laser dissipation causes 3/4 of the energy to be dissipated and the remaining energy to be refracted
    private final double laserDissipation;
    private final double laserRefraction;

    public ItemMekaSuitArmor(ArmorItem.Type armorType, Properties properties) {
        super(MEKASUIT_MATERIAL, armorType, properties.rarity(Rarity.EPIC).stacksTo(1));
        Supplier armorConfig;
        switch (armorType) {
            case HELMET -> {
                fluidTankSpecs.add(FluidTankSpec.createFillOnly(() -> MekanismConfig.COMMON.gear.mekaSuitNutritionalTransferRate, () -> MekanismConfig.COMMON.gear.mekaSuitNutritionalMaxStorage,
                      fluid -> fluid.getFluid() == MekanismFluids.NUTRITIONAL_PASTE.getFluid(), stack -> hasModule(stack, MekanismModules.NUTRITIONAL_INJECTION_UNIT)));
                absorption = 0.15F;
                laserDissipation = 0.15;
                laserRefraction = 0.2;
                armorConfig = () -> MekanismConfig.COMMON.gear.mekaSuitHelmetArmor;
            }
            case CHESTPLATE -> {
                gasTankSpecs.add(ChemicalTankSpec.createFillOnly(() -> MekanismConfig.COMMON.gear.mekaSuitJetpackTransferRate, () -> MekanismConfig.COMMON.gear.mekaSuitJetpackMaxStorage,
                      gas -> gas == MekanismGases.HYDROGEN.get(), stack -> hasModule(stack, MekanismModules.JETPACK_UNIT)));
                absorption = 0.4F;
                laserDissipation = 0.3;
                laserRefraction = 0.4;
                armorConfig = () -> MekanismConfig.COMMON.gear.mekaSuitBodyArmorArmor;
            }
            case LEGGINGS -> {
                absorption = 0.3F;
                laserDissipation = 0.1875;
                laserRefraction = 0.25;
                armorConfig = () -> MekanismConfig.COMMON.gear.mekaSuitPantsArmor;
            }
            case BOOTS -> {
                absorption = 0.15F;
                laserDissipation = 0.1125;
                laserRefraction = 0.15;
                armorConfig = () -> MekanismConfig.COMMON.gear.mekaSuitBootsArmor;
            }
            default -> throw new IllegalArgumentException("Unknown Equipment Slot Type");
        }
        this.attributeCache = new AttributeCache(this, armorConfig, () -> MekanismConfig.COMMON.gear.mekaSuitToughness, () -> MekanismConfig.COMMON.gear.mekaSuitKnockbackResistance);
    }

//    @Override
//    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
//        // safety check
//        return 0;
//    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (MekKeyHandler.isKeyPressed(MekanismKeyHandler.detailsKey)) {
            addModuleDetails(stack, tooltip);
        } else {
            StorageUtils.addStoredEnergy(stack, tooltip, true);
            if (!gasTankSpecs.isEmpty()) {
                StorageUtils.addStoredGas(stack, tooltip, true, false);
            }
            if (!fluidTankSpecs.isEmpty()) {
                StorageUtils.addStoredFluid(stack, tooltip, true);
            }
            tooltip.add(MekanismLang.HOLD_FOR_MODULES.translateColored(EnumColor.GRAY, EnumColor.INDIGO, MekanismKeyHandler.detailsKey.getTranslatedKeyMessage()));
        }
    }

//    @Override
//    public boolean makesPiglinsNeutral(@NotNull ItemStack stack, @NotNull LivingEntity wearer) {
//        return true;
//    }

//    @Override
//    public boolean isEnderMask(@NotNull ItemStack stack, @NotNull Player player, @NotNull EnderMan enderman) {
//        return type == ArmorItem.Type.HELMET;
//    }

    @Override
    public boolean canWalkOnPowderedSnow(@NotNull ItemStack stack, @NotNull LivingEntity wearer) {
        return type == ArmorItem.Type.BOOTS;
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

//    @Override
//    public boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot) {
//        //Try to avoid replacing this item if there are any modules currently installed
//        return super.isNotReplaceableByPickAction(stack, player, inventorySlot) || ItemDataUtils.hasData(stack, NBTConstants.MODULES, Tag.TAG_COMPOUND);
//    }

    @Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment, int originalLevel) {
        if (stack.isEmpty()) {
            return 0;
        }
        //Enchantments in our data
        ListTag enchantments = ItemDataUtils.getList(stack, NBTConstants.ENCHANTMENTS);
        return Math.max(MekanismUtils.getEnchantmentLevel(enchantments, enchantment), originalLevel);
    }

    @Override
    public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack, Map<Enchantment, Integer> originalEnchantments) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.deserializeEnchantments(ItemDataUtils.getList(stack, NBTConstants.ENCHANTMENTS));
        originalEnchantments.forEach((enchantment, level) -> enchantments.merge(enchantment, level, Math::max));
        return enchantments;
    }

    @Override
    public void addItems(CreativeModeTab.Output tabOutput) {
        tabOutput.accept(StorageUtils.getFilledEnergyVariant(new ItemStack(this)));
    }

    public void onArmorTick(ItemStack stack, Level world, Player player) {
        for (Module<?> module : getModules(stack)) {
            module.tick(player);
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        super.inventoryTick(itemStack, level, entity, i, bl);
        if (i >= 36 && i < 40 && entity instanceof Player player) {
            onArmorTick(itemStack, level, player);
        }
    }

    @Override
    protected boolean areCapabilityConfigsLoaded() {
        return super.areCapabilityConfigsLoaded() && MekanismConfig.COMMON.gear.isLoaded();
    }

    protected void gatherCapabilities(List<Object> capabilities, ItemStack stack, CompoundTag nbt) {
//        super.gatherCapabilities(capabilities, stack, nbt);
        //Note: We interact with this capability using "manual" as the automation type, to ensure we can properly bypass the energy limit for extracting
        // Internal is used by the "null" side, which is what will get used for most items
//        capabilities.add(RateLimitEnergyHandler.create(() -> getChargeRate(stack), () -> getMaxEnergy(stack), BasicEnergyContainer.manualOnly,
//              BasicEnergyContainer.alwaysTrue));
//        capabilities.add(RadiationShieldingHandler.create(item -> isModuleEnabled(item, MekanismModules.RADIATION_SHIELDING_UNIT) ?
//                                                                  ItemHazmatSuitArmor.getShieldingByArmor(getType()) : 0));
//        capabilities.add(LaserDissipationHandler.create(item -> isModuleEnabled(item, MekanismModules.LASER_DISSIPATION_UNIT) ? laserDissipation : 0,
//              item -> isModuleEnabled(item, MekanismModules.LASER_DISSIPATION_UNIT) ? laserRefraction : 0));
//        if (!gasTankSpecs.isEmpty()) {
//            capabilities.add(RateLimitMultiTankGasHandler.create(gasTankSpecs));
//        }
//        if (!fluidTankSpecs.isEmpty()) {
//            capabilities.add(RateLimitMultiTankFluidHandler.create(fluidTankSpecs));
//        }
    }

    @Override
    public EnergyStorage getEnergyStorage(ContainerItemContext context) {
        return new EnergyItemStorage(context, () -> RateLimitEnergyHandler.create(() -> getChargeRate(context.getItemVariant().toStack((int)context.getAmount())), () -> getMaxEnergy(context.getItemVariant().toStack((int)context.getAmount())), BasicEnergyContainer.manualOnly,
                BasicEnergyContainer.alwaysTrue));
    }

    @Override
    public double getRadiationShielding(ItemStack stack) {
        return isModuleEnabled(stack, MekanismModules.RADIATION_SHIELDING_UNIT) ?
                ItemHazmatSuitArmor.getShieldingByArmor(getType()) : 0;
    }

    @Override
    public double getDissipationPercent(ItemStack stack) {
        return isModuleEnabled(stack, MekanismModules.LASER_DISSIPATION_UNIT) ? laserDissipation : 0;
    }

    @Override
    public double getRefractionPercent(ItemStack stack) {
        return isModuleEnabled(stack, MekanismModules.LASER_DISSIPATION_UNIT) ? laserRefraction : 0;
    }

    @Override
    public Storage<FluidVariant> getFluidStorage(ContainerItemContext context) {
        if (!fluidTankSpecs.isEmpty()) {
            return new FluidItemStorage(context, () -> RateLimitMultiTankFluidHandler.create(context.getItemVariant().toStack((int)context.getAmount()), fluidTankSpecs).getTanks());
        }
        return null;
    }

    @Override
    public Storage<Gas> getGasStorage(ContainerItemContext context) {
        if (gasTankSpecs.isEmpty()) {
            return null;
        }
        return new GasItemStorage(context, () -> {
            List<IGasTank> tanks = new ArrayList<>();
            for (ChemicalTankSpec<Gas> spec : gasTankSpecs) {
                tanks.add(new RateLimitChemicalTank.RateLimitGasTank(spec.rate, spec.capacity, spec.canExtract, (gas, automationType) -> spec.canInsert.test(gas, automationType, context.getItemVariant().toStack((int)context.getAmount())), spec.isValid, null, null));
            }
            return tanks;
        });
    }

    public List<ChemicalTankSpec<Gas>> getGasTankSpecs() {
        return gasTankSpecsView;
    }

    public List<FluidTankSpec> getFluidTankSpecs() {
        return fluidTankSpecsView;
    }

    @NotNull
    public GasStack useGas(ContainerItemContext context, Gas type, long amount) {
        Storage<Gas> gasHandlerItem = context.find(Capabilities.GAS_HANDLER_ITEM);
        if (gasHandlerItem != null) {
            try(Transaction t=Transaction.openOuter()) {
                long amountExtracted = gasHandlerItem.extract(type, amount, t);
                t.commit();
                return type.getStack(amountExtracted);
            }
        }
        return GasStack.EMPTY;
    }

    public GasStack getContainedGas(ItemStack stack, Gas type) {
        Storage<Gas> gasHandlerItem = ContainerItemContext.withConstant(stack).find(Capabilities.GAS_HANDLER_ITEM);
        if (gasHandlerItem != null) {
            for (StorageView<Gas> view:gasHandlerItem) {
                if (view.getResource() == type) {
                    return view.getResource().getStack(view.getAmount());
                }
            }
        }
        return GasStack.EMPTY;
    }

    public FluidStack getContainedFluid(ItemStack stack, FluidStack type) {
        Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
        if (storage != null) {
            for (StorageView<FluidVariant> view:storage) {
                FluidStack fluidInTank = new FluidStack(view.getResource(), view.getAmount());
                if (fluidInTank.equals(type)) {
                    return fluidInTank;
                }
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public void changeMode(@NotNull Player player, @NotNull ItemStack stack, int shift, DisplayChange displayChange) {
        for (Module<?> module : getModules(stack)) {
            if (module.handlesModeChange()) {
                module.changeMode(player, stack, shift, displayChange);
                return;
            }
        }
    }

    @Override
    public boolean supportsSlotType(ItemStack stack, @NotNull EquipmentSlot slotType) {
        //Note: We ignore radial modes as those are just for the Meka-Tool currently
        return slotType == getEquipmentSlot() && getModules(stack).stream().anyMatch(Module::handlesModeChange);
    }

    @Override
    public boolean useCustomElytra(LivingEntity entity, ItemStack chestStack, boolean tickElytra) {
        if(canElytraFly(chestStack, entity)) {
            if(tickElytra) {
                elytraFlightTick(chestStack, entity, entity.getFallFlyingTicks());
            }
            return true;
        }
        return false;
    }

    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        if (getType() == ArmorItem.Type.CHESTPLATE && !entity.isShiftKeyDown()) {
            //Don't allow elytra flight if the player is sneaking. This lets the player exit elytra flight early
            IModule<ModuleElytraUnit> module = getModule(stack, MekanismModules.ELYTRA_UNIT);
            if (module != null && module.isEnabled() && module.canUseEnergy(entity, MekanismConfig.COMMON.gear.mekaSuitElytraEnergyUsage)) {
                //If we can use the elytra, check if the jetpack unit is also installed, and if it is,
                // only mark that we can use the elytra if the jetpack is not set to hover or if it is if it has no hydrogen stored
                IModule<ModuleJetpackUnit> jetpack = getModule(stack, MekanismModules.JETPACK_UNIT);
                return jetpack == null || !jetpack.isEnabled() || jetpack.getCustomInstance().getMode() != JetpackMode.HOVER ||
                       getContainedGas(stack, MekanismGases.HYDROGEN.get()).isEmpty();
            }
        }
        return false;
    }

    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        //Note: As canElytraFly is checked just before this we don't bother validating ahead of time we have the energy
        // or that we are the correct slot
        if (!entity.level().isClientSide) {
            int nextFlightTicks = flightTicks + 1;
            if (nextFlightTicks % 10 == 0) {
                if (nextFlightTicks % 20 == 0) {
                    IModule<ModuleElytraUnit> module = getModule(stack, MekanismModules.ELYTRA_UNIT);
                    if (module != null && module.isEnabled()) {
                        module.useEnergy(entity, MekanismConfig.COMMON.gear.mekaSuitElytraEnergyUsage);
                    }
                }
                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
        }
        return true;
    }

    @Override
    public boolean canUseJetpack(ItemStack stack) {
        return type == ArmorItem.Type.CHESTPLATE && (isModuleEnabled(stack, MekanismModules.JETPACK_UNIT) ? ChemicalUtil.hasChemical(stack, MekanismGases.HYDROGEN.get()) :
                                                     getModules(stack).stream().anyMatch(module -> module.isEnabled() && module.getData().isExclusive(ExclusiveFlag.OVERRIDE_JUMP.getMask())));
    }

    @Override
    public JetpackMode getJetpackMode(ItemStack stack) {
        IModule<ModuleJetpackUnit> module = getModule(stack, MekanismModules.JETPACK_UNIT);
        if (module != null && module.isEnabled()) {
            return module.getCustomInstance().getMode();
        }
        return JetpackMode.DISABLED;
    }

    @Override
    public void useJetpackFuel(ContainerItemContext context) {
        useGas(context, MekanismGases.HYDROGEN.get(), 1);
    }

    private long getMaxEnergy(ItemStack stack) {
        IModule<ModuleEnergyUnit> module = getModule(stack, MekanismModules.ENERGY_UNIT);
        return module == null ? MekanismConfig.COMMON.gear.mekaSuitBaseEnergyCapacity : module.getCustomInstance().getEnergyCapacity(module);
    }

    private long getChargeRate(ItemStack stack) {
        IModule<ModuleEnergyUnit> module = getModule(stack, MekanismModules.ENERGY_UNIT);
        return module == null ? MekanismConfig.COMMON.gear.mekaSuitBaseChargeRate : module.getCustomInstance().getChargeRate(module);
    }

    @NotNull
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull ItemStack stack, @NotNull EquipmentSlot slot) {
        return slot == getEquipmentSlot() ? attributeCache.get() : ImmutableMultimap.of();
    }

    @Override
    public void addToBuilder(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
        UUID modifier = ARMOR_MODIFIER_UUID_PER_TYPE.get(getType());
        builder.put(Attributes.ARMOR, new AttributeModifier(modifier, "Armor modifier", getDefense(), Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(modifier, "Armor toughness", getToughness(), Operation.ADDITION));
        builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(modifier, "Armor knockback resistance", getMaterial().getKnockbackResistance(),
              Operation.ADDITION));
    }

    @Override
    public int getDefense() {
        return getMaterial().getDefenseForType(getType());
    }

    @Override
    public float getToughness() {
        return getMaterial().getToughness();
    }

    @Override
    public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean allowContinuingBlockBreaking(Player player, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() == newStack.getItem();
    }

    public static float getDamageAbsorbed(Player player, DamageSource source, float amount) {
        return getDamageAbsorbed(player, source, amount, null);
    }

    public static boolean tryAbsorbAll(Player player, DamageSource source, float amount) {
        List<Runnable> energyUsageCallbacks = new ArrayList<>(4);
        if (getDamageAbsorbed(player, source, amount, energyUsageCallbacks) >= 1) {
            //If we can fully absorb it, actually use the energy from the various pieces and then return that we absorbed it all
            for (Runnable energyUsageCallback : energyUsageCallbacks) {
                energyUsageCallback.run();
            }
            return true;
        }
        return false;
    }

    private static float getDamageAbsorbed(Player player, DamageSource source, float amount, @Nullable List<Runnable> energyUseCallbacks) {
        if (amount <= 0) {
            return 0;
        }
        float ratioAbsorbed = 0;
        List<FoundArmorDetails> armorDetails = new ArrayList<>();
        //Start by looping the armor, allowing modules to absorb damage if they can
        for (int i = 0; i < player.getInventory().armor.size(); i++) {
            ItemStack stack = player.getInventory().getArmor(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemMekaSuitArmor armor) {
                EnergyStorage energyContainer = ContainerItemContext.ofPlayerSlot(player, PlayerInventoryStorage.of(player).getSlot(36 + i)).find(EnergyStorage.ITEM);
                if (energyContainer != null) {
                    FoundArmorDetails details = new FoundArmorDetails(energyContainer, armor);
                    armorDetails.add(details);
                    for (Module<?> module : details.armor.getModules(stack)) {
                        if (module.isEnabled()) {
                            ModuleDamageAbsorbInfo damageAbsorbInfo = getModuleDamageAbsorbInfo(module, source);
                            if (damageAbsorbInfo != null) {
                                float absorption = damageAbsorbInfo.absorptionRatio().getAsFloat();
                                ratioAbsorbed += absorbDamage(details.usageInfo, amount, absorption, ratioAbsorbed, damageAbsorbInfo.energyCost());
                                if (ratioAbsorbed >= 1) {
                                    //If we have fully absorbed the damage, stop checking/trying to absorb more
                                    break;
                                }
                            }
                        }
                    }
                    if (ratioAbsorbed >= 1) {
                        //If we have fully absorbed the damage, stop checking/trying to absorb more
                        break;
                    }
                }
            }
        }
        if (ratioAbsorbed < 1) {
            //If we haven't fully absorbed it check the individual pieces of armor for if they can absorb any
            Float absorbRatio = null;
            for (FoundArmorDetails details : armorDetails) {
                if (absorbRatio == null) {
                    //If we haven't looked up yet if we can absorb the damage type and if we can't
                    // stop checking if the armor is able to
                    if (!source.is(MekanismTags.DamageTypes.MEKASUIT_ALWAYS_SUPPORTED) && source.is(DamageTypeTags.BYPASSES_ARMOR)) {
                        break;
                    }
                    // Next lookup the ratio at which we can absorb the given damage type from the config
                    ResourceLocation damageTypeName = source.typeHolder().unwrapKey()
                          .map(ResourceKey::location)
                          //Note: In theory the above path should always be done as vanilla only makes damage sources with reference holders
                          // but just in case have the fallback to look up the name from the registry
                          .orElseGet(() -> player.level().registryAccess().registry(Registries.DAMAGE_TYPE)
                                .map(registry -> registry.getKey(source.type()))
                                .orElse(null)
                          );
                    if (damageTypeName != null) {//Note: This should not be null unless something went wrong and the damage source is for a damage type that is not registered
                        absorbRatio = MekanismConfig.COMMON.gear.mekaSuitDamageRatios.get(damageTypeName.toString());
                    }
                    if (absorbRatio == null) {
                        absorbRatio = MekanismConfig.COMMON.gear.mekaSuitUnspecifiedDamageRatio;
                    }
                    if (absorbRatio == 0) {
                        //If the config specifies that the damage type shouldn't be blocked at all
                        // stop checking if the armor is able to
                        break;
                    }
                }
                float absorption = details.armor.absorption * absorbRatio;
                ratioAbsorbed += absorbDamage(details.usageInfo, amount, absorption, ratioAbsorbed, () -> MekanismConfig.COMMON.gear.mekaSuitEnergyUsageDamage);
                if (ratioAbsorbed >= 1) {
                    //If we have fully absorbed the damage, stop checking/trying to absorb more
                    break;
                }
            }
        }
        for (FoundArmorDetails details : armorDetails) {
            //Use energy/or enqueue usage for each piece as needed
            if (details.usageInfo.energyUsed != 0) {
                if (energyUseCallbacks == null) {
                    try(Transaction t=Transaction.openOuter()) {
                        details.energyContainer.extract(details.usageInfo.energyUsed, t);
                        t.commit();
                    }
                } else {
                    energyUseCallbacks.add(() -> {
                        try(Transaction t=Transaction.openOuter()) {
                            details.energyContainer.extract(details.usageInfo.energyUsed, t);
                            t.commit();
                        }
                    });
                }
            }
        }
        return Math.min(ratioAbsorbed, 1);
    }

    @Nullable
    private static <MODULE extends ICustomModule<MODULE>> ModuleDamageAbsorbInfo getModuleDamageAbsorbInfo(IModule<MODULE> module, DamageSource damageSource) {
        return module.getCustomInstance().getDamageAbsorbInfo(module, damageSource);
    }

    private static float absorbDamage(EnergyUsageInfo usageInfo, float amount, float absorption, float currentAbsorbed, LongSupplier energyCost) {
        //Cap the amount that we can absorb to how much we have left to absorb
        absorption = Math.min(1 - currentAbsorbed, absorption);
        float toAbsorb = amount * absorption;
        if (toAbsorb > 0) {
            long usage = (long) (energyCost.getAsLong() * toAbsorb);
            if (usage == 0) {
                //No energy is actually needed to absorb the damage, either because of the config
                // or how small the amount to absorb is
                return absorption;
            } else if (usageInfo.energyAvailable >= usage) {
                //If we have more energy available than we need, increase how much energy we "used"
                // and decrease how much we have available.
                usageInfo.energyUsed = LongMath.saturatedAdd(usageInfo.energyUsed, usage);
                usageInfo.energyAvailable = Math.max(usageInfo.energyAvailable - usage, 0);
                return absorption;
            } else if (usageInfo.energyAvailable != 0) {
                //Otherwise, if we have energy available but not as much as needed to fully absorb it
                // then we calculate what ratio we are able to block
                float absorbedPercent = (float) (usageInfo.energyAvailable / (double)usage);
                usageInfo.energyUsed = LongMath.saturatedAdd(usageInfo.energyUsed, usageInfo.energyAvailable);
                usageInfo.energyAvailable = 0;
                return absorption * absorbedPercent;
            }
        }
        return 0;
    }

    @Override
    public @NotNull ICustomArmor getGearModel(Type type) {
        return RenderPropertiesProvider.mekaSuit().getGearModel(type);
    }

    @Override
    public ISpecialGear getSpecialGear() {
        return RenderPropertiesProvider.mekaSuit();
    }

    private static class FoundArmorDetails {

        private final EnergyStorage energyContainer;
        private final EnergyUsageInfo usageInfo;
        private final ItemMekaSuitArmor armor;

        public FoundArmorDetails(EnergyStorage energyContainer, ItemMekaSuitArmor armor) {
            this.energyContainer = energyContainer;
            this.usageInfo = new EnergyUsageInfo(energyContainer.getAmount());
            this.armor = armor;
        }
    }

    private static class EnergyUsageInfo {

        private long energyAvailable;
        private long energyUsed = 0;

        public EnergyUsageInfo(long energyAvailable) {
            //Copy it so we can just use minusEquals without worry
            this.energyAvailable = energyAvailable;
        }
    }

    // This is unused for the most part; toughness / damage reduction is handled manually, though it can fall back to netherite values
    protected static class MekaSuitMaterial extends BaseSpecialArmorMaterial {

        @Override
        public int getDefenseForType(@NotNull ArmorItem.Type armorType) {
            return switch (armorType) {
                case BOOTS -> MekanismConfig.COMMON.gear.mekaSuitBootsArmor;
                case LEGGINGS -> MekanismConfig.COMMON.gear.mekaSuitPantsArmor;
                case CHESTPLATE -> MekanismConfig.COMMON.gear.mekaSuitBodyArmorArmor;
                case HELMET -> MekanismConfig.COMMON.gear.mekaSuitHelmetArmor;
            };
        }

        @Override
        public float getToughness() {
            return MekanismConfig.COMMON.gear.mekaSuitToughness;
        }

        @Override
        public float getKnockbackResistance() {
            return MekanismConfig.COMMON.gear.mekaSuitKnockbackResistance;
        }

        @NotNull
        @Override
        public String getName() {
            return Mekanism.MODID + ":mekasuit";
        }
    }
}