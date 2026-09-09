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
    private final String modid;

    public ItemDeferredRegister(String modid) {
        super(BuiltInRegistries.ITEM);
        this.modid = modid;
    }

    public ItemRegistryObject<Item> register(String name) {
        return register(new ResourceLocation(modid, name));
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

    public ItemRegistryObject<ItemModule> registerModule(ModuleRegistryObject<?> moduleDataSupplier) {
        //Note: We use the internal helper just in case we end up needing to know it is an ItemModule instead of just an Item somewhere
        return register(new ResourceLocation(modid, "module_" + moduleDataSupplier.getInternalRegistryName()), () -> ModuleHelper.get().createModuleItem(moduleDataSupplier, new Item.Properties()));
    }

    public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Function<Item.Properties, ITEM> sup) {
        return register(new ResourceLocation(modid, name), sup);
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

    public <ENTITY extends Mob> ItemRegistryObject<SpawnEggItem> registerSpawnEgg(EntityTypeRegistryObject<ENTITY> registryObject,
                                                                                  int primaryColor, int secondaryColor) {
        return register(registryObject.getRegistryName().withSuffix("_spawn_egg"), props -> new SpawnEggItem(registryObject.getEntityType(), primaryColor,
                secondaryColor, props));
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