package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.world.level.gameevent.GameEvent;

public class GameEventRegistryObject<GAME_EVENT extends GameEvent> extends WrappedRegistryObject<GAME_EVENT> {

    public GameEventRegistryObject(GAME_EVENT registryObject) {
        super(registryObject);
    }
}