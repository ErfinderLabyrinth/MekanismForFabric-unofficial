package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class PlacementModifierRegistryObject<PROVIDER extends PlacementModifier> extends WrappedRegistryObject<PlacementModifierType<PROVIDER>> {

    public PlacementModifierRegistryObject(PlacementModifierType<PROVIDER> registryObject) {
        super(registryObject);
    }
}