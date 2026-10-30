package mekanism.common.registration.impl;

import mekanism.api.providers.IItemProvider;
import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class ItemRegistryObject<ITEM extends Item> extends WrappedRegistryObject<ITEM> implements IItemProvider {

    public ItemRegistryObject(ITEM item) {
        super(item);
    }

    @NotNull
    @Override
    public ITEM asItem() {
        return get();
    }
}