package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class TileEntityTypeRegistryObject<BE extends BlockEntity> extends WrappedRegistryObject<BlockEntityType<BE>> {

    @Nullable
    private BlockEntityTicker<BE> clientTicker;
    @Nullable
    private BlockEntityTicker<BE> serverTicker;

    public TileEntityTypeRegistryObject(BlockEntityType<BE> object) {
        super(object);
    }

    //Internal use only, overwrite the registry object
    TileEntityTypeRegistryObject<BE> setRegistryObject(BlockEntityType<BE> object) {
        this.object = object;
        return this;
    }

    //Internal use only
    TileEntityTypeRegistryObject<BE> clientTicker(BlockEntityTicker<BE> ticker) {
        clientTicker = ticker;
        return this;
    }

    //Internal use only
    TileEntityTypeRegistryObject<BE> serverTicker(BlockEntityTicker<BE> ticker) {
        serverTicker = ticker;
        return this;
    }

    @Nullable
    public BlockEntityTicker<BE> getTicker(boolean isClient) {
        return isClient ? clientTicker : serverTicker;
    }
}