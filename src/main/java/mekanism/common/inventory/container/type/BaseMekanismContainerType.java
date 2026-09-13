package mekanism.common.inventory.container.type;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class BaseMekanismContainerType<T, CONTAINER extends AbstractContainerMenu, FACTORY> extends ExtendedScreenHandlerType<CONTAINER> {

    protected final FACTORY mekanismConstructor;
    protected final Class<T> type;

    protected BaseMekanismContainerType(Class<T> type, FACTORY mekanismConstructor, ExtendedFactory<CONTAINER> menuSupplier) {
        super(menuSupplier);
        this.type = type;
        this.mekanismConstructor = mekanismConstructor;
    }
}