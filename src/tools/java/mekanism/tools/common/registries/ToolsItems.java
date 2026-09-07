package mekanism.tools.common.registries;

import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.tools.common.MekanismTools;
import mekanism.tools.common.config.MekanismToolsConfig;
import mekanism.tools.common.item.ItemMekanismArmor;
import mekanism.tools.common.item.ItemMekanismAxe;
import mekanism.tools.common.item.ItemMekanismHoe;
import mekanism.tools.common.item.ItemMekanismPaxel;
import mekanism.tools.common.item.ItemMekanismPickaxe;
import mekanism.tools.common.item.ItemMekanismShield;
import mekanism.tools.common.item.ItemMekanismShovel;
import mekanism.tools.common.item.ItemMekanismSword;
import mekanism.tools.common.item.ItemRefinedGlowstoneArmor;
import mekanism.tools.common.item.tier.MekanismTiers;
import mekanism.tools.common.material.BaseMekanismMaterial;
import mekanism.tools.common.material.MaterialCreator;
import mekanism.tools.common.material.VanillaPaxelMaterialCreator;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

import java.util.Locale;
import java.util.function.BiFunction;

public class ToolsItems {

    private ToolsItems() {
    }

    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(MekanismTools.MODID);

    public static final ItemRegistryObject<ItemMekanismPaxel> WOOD_PAXEL = registerPaxel(MekanismToolsConfig.tools.wood, Tiers.WOOD, false);
    public static final ItemRegistryObject<ItemMekanismPaxel> STONE_PAXEL = registerPaxel(MekanismToolsConfig.tools.stone, Tiers.STONE, false);
    public static final ItemRegistryObject<ItemMekanismPaxel> IRON_PAXEL = registerPaxel(MekanismToolsConfig.tools.iron, Tiers.IRON, false);
    public static final ItemRegistryObject<ItemMekanismPaxel> DIAMOND_PAXEL = registerPaxel(MekanismToolsConfig.tools.diamond, Tiers.DIAMOND, false);
    public static final ItemRegistryObject<ItemMekanismPaxel> GOLD_PAXEL = registerPaxel(MekanismToolsConfig.tools.gold, Tiers.GOLD, false);
    public static final ItemRegistryObject<ItemMekanismPaxel> NETHERITE_PAXEL = registerPaxel(MekanismToolsConfig.tools.netherite, Tiers.NETHERITE, true);

    public static final ItemRegistryObject<ItemMekanismPickaxe> BRONZE_PICKAXE = registerPickaxe(MekanismToolsConfig.tools.bronze);
    public static final ItemRegistryObject<ItemMekanismAxe> BRONZE_AXE = registerAxe(MekanismToolsConfig.tools.bronze);
    public static final ItemRegistryObject<ItemMekanismShovel> BRONZE_SHOVEL = registerShovel(MekanismToolsConfig.tools.bronze);
    public static final ItemRegistryObject<ItemMekanismHoe> BRONZE_HOE = registerHoe(MekanismToolsConfig.tools.bronze);
    public static final ItemRegistryObject<ItemMekanismSword> BRONZE_SWORD = registerSword(MekanismToolsConfig.tools.bronze);
    public static final ItemRegistryObject<ItemMekanismPaxel> BRONZE_PAXEL = registerPaxel(MekanismToolsConfig.tools.bronze);
    public static final ItemRegistryObject<ItemMekanismArmor> BRONZE_HELMET = registerArmor(MekanismToolsConfig.tools.bronze, ArmorItem.Type.HELMET);
    public static final ItemRegistryObject<ItemMekanismArmor> BRONZE_CHESTPLATE = registerArmor(MekanismToolsConfig.tools.bronze, ArmorItem.Type.CHESTPLATE);
    public static final ItemRegistryObject<ItemMekanismArmor> BRONZE_LEGGINGS = registerArmor(MekanismToolsConfig.tools.bronze, ArmorItem.Type.LEGGINGS);
    public static final ItemRegistryObject<ItemMekanismArmor> BRONZE_BOOTS = registerArmor(MekanismToolsConfig.tools.bronze, ArmorItem.Type.BOOTS);
    public static final ItemRegistryObject<ItemMekanismShield> BRONZE_SHIELD = registerShield(MekanismToolsConfig.tools.bronze);

    public static final ItemRegistryObject<ItemMekanismPickaxe> LAPIS_LAZULI_PICKAXE = registerPickaxe(MekanismToolsConfig.tools.lapisLazuli);
    public static final ItemRegistryObject<ItemMekanismAxe> LAPIS_LAZULI_AXE = registerAxe(MekanismToolsConfig.tools.lapisLazuli);
    public static final ItemRegistryObject<ItemMekanismShovel> LAPIS_LAZULI_SHOVEL = registerShovel(MekanismToolsConfig.tools.lapisLazuli);
    public static final ItemRegistryObject<ItemMekanismHoe> LAPIS_LAZULI_HOE = registerHoe(MekanismToolsConfig.tools.lapisLazuli);
    public static final ItemRegistryObject<ItemMekanismSword> LAPIS_LAZULI_SWORD = registerSword(MekanismToolsConfig.tools.lapisLazuli);
    public static final ItemRegistryObject<ItemMekanismPaxel> LAPIS_LAZULI_PAXEL = registerPaxel(MekanismToolsConfig.tools.lapisLazuli);
    public static final ItemRegistryObject<ItemMekanismArmor> LAPIS_LAZULI_HELMET = registerArmor(MekanismToolsConfig.tools.lapisLazuli, ArmorItem.Type.HELMET);
    public static final ItemRegistryObject<ItemMekanismArmor> LAPIS_LAZULI_CHESTPLATE = registerArmor(MekanismToolsConfig.tools.lapisLazuli, ArmorItem.Type.CHESTPLATE);
    public static final ItemRegistryObject<ItemMekanismArmor> LAPIS_LAZULI_LEGGINGS = registerArmor(MekanismToolsConfig.tools.lapisLazuli, ArmorItem.Type.LEGGINGS);
    public static final ItemRegistryObject<ItemMekanismArmor> LAPIS_LAZULI_BOOTS = registerArmor(MekanismToolsConfig.tools.lapisLazuli, ArmorItem.Type.BOOTS);
    public static final ItemRegistryObject<ItemMekanismShield> LAPIS_LAZULI_SHIELD = registerShield(MekanismToolsConfig.tools.lapisLazuli);

    public static final ItemRegistryObject<ItemMekanismPickaxe> OSMIUM_PICKAXE = registerPickaxe(MekanismToolsConfig.tools.osmium);
    public static final ItemRegistryObject<ItemMekanismAxe> OSMIUM_AXE = registerAxe(MekanismToolsConfig.tools.osmium);
    public static final ItemRegistryObject<ItemMekanismShovel> OSMIUM_SHOVEL = registerShovel(MekanismToolsConfig.tools.osmium);
    public static final ItemRegistryObject<ItemMekanismHoe> OSMIUM_HOE = registerHoe(MekanismToolsConfig.tools.osmium);
    public static final ItemRegistryObject<ItemMekanismSword> OSMIUM_SWORD = registerSword(MekanismToolsConfig.tools.osmium);
    public static final ItemRegistryObject<ItemMekanismPaxel> OSMIUM_PAXEL = registerPaxel(MekanismToolsConfig.tools.osmium);
    public static final ItemRegistryObject<ItemMekanismArmor> OSMIUM_HELMET = registerArmor(MekanismToolsConfig.tools.osmium, ArmorItem.Type.HELMET);
    public static final ItemRegistryObject<ItemMekanismArmor> OSMIUM_CHESTPLATE = registerArmor(MekanismToolsConfig.tools.osmium, ArmorItem.Type.CHESTPLATE);
    public static final ItemRegistryObject<ItemMekanismArmor> OSMIUM_LEGGINGS = registerArmor(MekanismToolsConfig.tools.osmium, ArmorItem.Type.LEGGINGS);
    public static final ItemRegistryObject<ItemMekanismArmor> OSMIUM_BOOTS = registerArmor(MekanismToolsConfig.tools.osmium, ArmorItem.Type.BOOTS);
    public static final ItemRegistryObject<ItemMekanismShield> OSMIUM_SHIELD = registerShield(MekanismToolsConfig.tools.osmium);

    public static final ItemRegistryObject<ItemMekanismPickaxe> REFINED_GLOWSTONE_PICKAXE = registerPickaxe(MekanismTiers.REFINED_GLOWSTONE);
    public static final ItemRegistryObject<ItemMekanismAxe> REFINED_GLOWSTONE_AXE = registerAxe(MekanismTiers.REFINED_GLOWSTONE);
    public static final ItemRegistryObject<ItemMekanismShovel> REFINED_GLOWSTONE_SHOVEL = registerShovel(MekanismTiers.REFINED_GLOWSTONE);
    public static final ItemRegistryObject<ItemMekanismHoe> REFINED_GLOWSTONE_HOE = registerHoe(MekanismTiers.REFINED_GLOWSTONE);
    public static final ItemRegistryObject<ItemMekanismSword> REFINED_GLOWSTONE_SWORD = registerSword(MekanismTiers.REFINED_GLOWSTONE);
    public static final ItemRegistryObject<ItemMekanismPaxel> REFINED_GLOWSTONE_PAXEL = registerPaxel(MekanismTiers.REFINED_GLOWSTONE);
    public static final ItemRegistryObject<ItemMekanismArmor> REFINED_GLOWSTONE_HELMET = registerArmor(MekanismTiers.REFINED_GLOWSTONE, ArmorItem.Type.HELMET, ItemRefinedGlowstoneArmor::new);
    public static final ItemRegistryObject<ItemMekanismArmor> REFINED_GLOWSTONE_CHESTPLATE = registerArmor(MekanismTiers.REFINED_GLOWSTONE, ArmorItem.Type.CHESTPLATE, ItemRefinedGlowstoneArmor::new);
    public static final ItemRegistryObject<ItemMekanismArmor> REFINED_GLOWSTONE_LEGGINGS = registerArmor(MekanismTiers.REFINED_GLOWSTONE, ArmorItem.Type.LEGGINGS, ItemRefinedGlowstoneArmor::new);
    public static final ItemRegistryObject<ItemMekanismArmor> REFINED_GLOWSTONE_BOOTS = registerArmor(MekanismTiers.REFINED_GLOWSTONE, ArmorItem.Type.BOOTS, ItemRefinedGlowstoneArmor::new);
    public static final ItemRegistryObject<ItemMekanismShield> REFINED_GLOWSTONE_SHIELD = registerShield(MekanismTiers.REFINED_GLOWSTONE);

    public static final ItemRegistryObject<ItemMekanismPickaxe> REFINED_OBSIDIAN_PICKAXE = registerPickaxe(MekanismTiers.REFINED_OBSIDIAN);
    public static final ItemRegistryObject<ItemMekanismAxe> REFINED_OBSIDIAN_AXE = registerAxe(MekanismTiers.REFINED_OBSIDIAN);
    public static final ItemRegistryObject<ItemMekanismShovel> REFINED_OBSIDIAN_SHOVEL = registerShovel(MekanismTiers.REFINED_OBSIDIAN);
    public static final ItemRegistryObject<ItemMekanismHoe> REFINED_OBSIDIAN_HOE = registerHoe(MekanismTiers.REFINED_OBSIDIAN);
    public static final ItemRegistryObject<ItemMekanismSword> REFINED_OBSIDIAN_SWORD = registerSword(MekanismTiers.REFINED_OBSIDIAN);
    public static final ItemRegistryObject<ItemMekanismPaxel> REFINED_OBSIDIAN_PAXEL = registerPaxel(MekanismTiers.REFINED_OBSIDIAN);
    public static final ItemRegistryObject<ItemMekanismArmor> REFINED_OBSIDIAN_HELMET = registerArmor(MekanismTiers.REFINED_OBSIDIAN, ArmorItem.Type.HELMET);
    public static final ItemRegistryObject<ItemMekanismArmor> REFINED_OBSIDIAN_CHESTPLATE = registerArmor(MekanismTiers.REFINED_OBSIDIAN, ArmorItem.Type.CHESTPLATE);
    public static final ItemRegistryObject<ItemMekanismArmor> REFINED_OBSIDIAN_LEGGINGS = registerArmor(MekanismTiers.REFINED_OBSIDIAN, ArmorItem.Type.LEGGINGS);
    public static final ItemRegistryObject<ItemMekanismArmor> REFINED_OBSIDIAN_BOOTS = registerArmor(MekanismTiers.REFINED_OBSIDIAN, ArmorItem.Type.BOOTS);
    public static final ItemRegistryObject<ItemMekanismShield> REFINED_OBSIDIAN_SHIELD = registerShield(MekanismTiers.REFINED_OBSIDIAN);

    public static final ItemRegistryObject<ItemMekanismPickaxe> STEEL_PICKAXE = registerPickaxe(MekanismTiers.STEEL);
    public static final ItemRegistryObject<ItemMekanismAxe> STEEL_AXE = registerAxe(MekanismTiers.STEEL);
    public static final ItemRegistryObject<ItemMekanismShovel> STEEL_SHOVEL = registerShovel(MekanismTiers.STEEL);
    public static final ItemRegistryObject<ItemMekanismHoe> STEEL_HOE = registerHoe(MekanismTiers.STEEL);
    public static final ItemRegistryObject<ItemMekanismSword> STEEL_SWORD = registerSword(MekanismTiers.STEEL);
    public static final ItemRegistryObject<ItemMekanismPaxel> STEEL_PAXEL = registerPaxel(MekanismTiers.STEEL);
    public static final ItemRegistryObject<ItemMekanismArmor> STEEL_HELMET = registerArmor(MekanismTiers.STEEL, ArmorItem.Type.HELMET);
    public static final ItemRegistryObject<ItemMekanismArmor> STEEL_CHESTPLATE = registerArmor(MekanismTiers.STEEL, ArmorItem.Type.CHESTPLATE);
    public static final ItemRegistryObject<ItemMekanismArmor> STEEL_LEGGINGS = registerArmor(MekanismTiers.STEEL, ArmorItem.Type.LEGGINGS);
    public static final ItemRegistryObject<ItemMekanismArmor> STEEL_BOOTS = registerArmor(MekanismTiers.STEEL, ArmorItem.Type.BOOTS);
    public static final ItemRegistryObject<ItemMekanismShield> STEEL_SHIELD = registerShield(MekanismTiers.STEEL);

    private static ItemRegistryObject<ItemMekanismShield> registerShield(MekanismTiers material) {
        return register(ItemMekanismShield::new, "_shield", material);
    }

    private static ItemRegistryObject<ItemMekanismPickaxe> registerPickaxe(MekanismTiers material) {
        return register(ItemMekanismPickaxe::new, "_pickaxe", material);
    }

    private static ItemRegistryObject<ItemMekanismAxe> registerAxe(MekanismTiers material) {
        return register(ItemMekanismAxe::new, "_axe", material);
    }

    private static ItemRegistryObject<ItemMekanismShovel> registerShovel(MekanismTiers material) {
        return register(ItemMekanismShovel::new, "_shovel", material);
    }

    private static ItemRegistryObject<ItemMekanismHoe> registerHoe(MekanismTiers material) {
        return register(ItemMekanismHoe::new, "_hoe", material);
    }

    private static ItemRegistryObject<ItemMekanismSword> registerSword(MekanismTiers material) {
        return register(ItemMekanismSword::new, "_sword", material);
    }

    private static ItemRegistryObject<ItemMekanismPaxel> registerPaxel(MekanismTiers material) {
        return register(ItemMekanismPaxel::new, "_paxel", material);
    }

    private static ItemRegistryObject<ItemMekanismPaxel> registerPaxel(VanillaPaxelMaterialCreator material, Tiers tier, boolean unburnable) {
        if (unburnable) {
            return ITEMS.registerUnburnable(MekanismTools.rl(tier.name().toLowerCase(Locale.ROOT) + "_paxel"), properties -> new ItemMekanismPaxel(material, tier, properties));
        }
        return ITEMS.register(MekanismTools.rl(tier.name().toLowerCase(Locale.ROOT) + "_paxel"), properties -> new ItemMekanismPaxel(material, tier, properties));
    }

    private static ItemRegistryObject<ItemMekanismArmor> registerArmor(MekanismTiers material, ArmorItem.Type armorType) {
        return registerArmor(material, armorType, ItemMekanismArmor::new);
    }

    private static ItemRegistryObject<ItemMekanismArmor> registerArmor(MekanismTiers material, ArmorItem.Type armorType, ArmorCreator armorCreator) {
        return ITEMS.register(MekanismTools.rl(material.getRegistryPrefix() + "_" + armorType.getName()), () -> armorCreator.create(material, armorType, getBaseProperties(material)));
    }

    private static <ITEM extends Item> ItemRegistryObject<ITEM> register(BiFunction<MekanismTiers, Item.Properties, ITEM> itemCreator, String suffix,
                                                                         MekanismTiers material) {
        return ITEMS.register(MekanismTools.rl(material.getRegistryPrefix() + suffix), () -> itemCreator.apply(material, getBaseProperties(material)));
    }

    private static Item.Properties getBaseProperties(BaseMekanismMaterial material) {
        Item.Properties properties = new Item.Properties();
        if (!material.burnsInFire()) {
            properties = properties.fireResistant();
        }
        return properties;
    }

    public static void register() {}

    @FunctionalInterface
    private interface ArmorCreator {

        ItemMekanismArmor create(MekanismTiers material, ArmorItem.Type armorType, Item.Properties properties);
    }
}