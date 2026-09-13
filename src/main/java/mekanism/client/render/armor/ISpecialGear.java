package mekanism.client.render.armor;

import net.minecraft.world.item.ArmorItem;
import org.jetbrains.annotations.NotNull;

public interface ISpecialGear {

    @NotNull
    ICustomArmor getGearModel(ArmorItem.Type type);
}