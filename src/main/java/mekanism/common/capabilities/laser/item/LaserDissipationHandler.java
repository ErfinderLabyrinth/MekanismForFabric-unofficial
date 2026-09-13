package mekanism.common.capabilities.laser.item;

import mekanism.api.lasers.ILaserDissipation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.ToDoubleFunction;

public class LaserDissipationHandler extends Item implements ILaserDissipation {

    public static LaserDissipationHandler create(Item.Properties properties, ToDoubleFunction<ItemStack> dissipationFunction, ToDoubleFunction<ItemStack> refractionFunction) {
        Objects.requireNonNull(dissipationFunction, "Dissipation function cannot be null");
        Objects.requireNonNull(refractionFunction, "Refraction function cannot be null");
        return new LaserDissipationHandler(properties, dissipationFunction, refractionFunction);
    }

    private final ToDoubleFunction<ItemStack> dissipationFunction;
    private final ToDoubleFunction<ItemStack> refractionFunction;

    private LaserDissipationHandler(Item.Properties properties, ToDoubleFunction<ItemStack> dissipationFunction, ToDoubleFunction<ItemStack> refractionFunction) {
        super(properties);
        this.dissipationFunction = dissipationFunction;
        this.refractionFunction = refractionFunction;
    }

    @Override
    public double getDissipationPercent(ItemStack stack) {
        return dissipationFunction.applyAsDouble(stack);
    }

    @Override
    public double getRefractionPercent(ItemStack stack) {
        return refractionFunction.applyAsDouble(stack);
    }
}