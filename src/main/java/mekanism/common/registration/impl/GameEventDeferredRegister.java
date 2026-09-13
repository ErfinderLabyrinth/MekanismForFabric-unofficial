package mekanism.common.registration.impl;

import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.function.Supplier;

public class GameEventDeferredRegister extends WrappedDeferredRegister<GameEvent> {

    private final String modid;

    public GameEventDeferredRegister(String modid) {
        super(BuiltInRegistries.GAME_EVENT);
        this.modid = modid;
    }

    public GameEventRegistryObject<GameEvent> register(String name) {
        return register(name, 16);
    }

    public GameEventRegistryObject<GameEvent> register(String name, int notificationRadius) {
        return register(name, () -> new GameEvent(modid + ":" + name, notificationRadius));
    }

    public <GAME_EVENT extends GameEvent> GameEventRegistryObject<GAME_EVENT> register(String name, Supplier<GAME_EVENT> sup) {
        return register(new ResourceLocation(modid, name), sup, GameEventRegistryObject::new);
    }
}