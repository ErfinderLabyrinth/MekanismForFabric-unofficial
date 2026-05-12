package mekanism.common.registration.impl;

import mekanism.api.text.EnumColor;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.item.ItemModule;
import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemDeferredRegister extends WrappedDeferredRegister<Item> {

    private final List<ItemRegistryObject<? extends Item>> allItems = new ArrayList<>();

    public ItemDeferredRegister(String modid) {
        super(BuiltInRegistries.ITEM);
    }

    public ItemRegistryObject<Item> register(ResourceLocation id) {
        return register(id, Item::new);
    }

    public ItemRegistryObject<Item> registerUnburnable(ResourceLocation id) {
        return registerUnburnable(id, Item::new);
    }

    public ItemRegistryObject<Item> register(ResourceLocation id, Rarity rarity) {
        return register(id, properties -> new Item(properties.rarity(rarity)));
    }

    public ItemRegistryObject<Item> register(ResourceLocation id, EnumColor color) {
        return register(id, properties -> new Item(properties) {
            @NotNull
            @Override
            public Component getName(@NotNull ItemStack stack) {
                return TextComponentUtil.build(color, super.getName(stack));
            }
        });
    }

    public ItemRegistryObject<ItemModule> registerModule(ResourceLocation id, ModuleRegistryObject<?> moduleDataSupplier) {
        //Note: We use the internal helper just in case we end up needing to know it is an ItemModule instead of just an Item somewhere
        return register(id, () -> ModuleHelper.get().createModuleItem(moduleDataSupplier, new Item.Properties()));
    }

    public <ITEM extends Item> ItemRegistryObject<ITEM> register(ResourceLocation id, Function<Item.Properties, ITEM> sup) {
        return register(id, () -> sup.apply(new Item.Properties()));
    }

    public <ITEM extends Item> ItemRegistryObject<ITEM> registerUnburnable(ResourceLocation id, Function<Item.Properties, ITEM> sup) {
        return register(id, () -> sup.apply(new Item.Properties().fireResistant()));
    }

    public <ITEM extends Item> ItemRegistryObject<ITEM> register(ResourceLocation id, Supplier<ITEM> sup) {
        ItemRegistryObject<ITEM> registeredItem = register(id, sup, ItemRegistryObject::new);
        allItems.add(registeredItem);
        return registeredItem;
    }

    public <ENTITY extends Mob> ItemRegistryObject<SpawnEggItem> registerSpawnEgg(ResourceLocation id, EntityType<ENTITY> entityType,
                                                                                  int primaryColor, int secondaryColor) {
        return register(id, props -> new SpawnEggItem(entityType, primaryColor,
              secondaryColor, props));
    }

    public List<ItemRegistryObject<? extends Item>> getAllItems() {
        return Collections.unmodifiableList(allItems);
    }
}