package mekanism.common.registration.impl;

import mekanism.api.providers.IBlockProvider;
import mekanism.common.registration.DoubleWrappedRegistryObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class BlockRegistryObject<BLOCK extends Block, ITEM extends Item> extends DoubleWrappedRegistryObject<BLOCK, ITEM> implements IBlockProvider {

    public BlockRegistryObject(BLOCK block, ITEM item) {
        super(block, item);
    }

    @NotNull
    @Override
    public BLOCK getBlock() {
        return getPrimary();
    }

    @NotNull
    @Override
    public ITEM asItem() {
        return getSecondary();
    }
}