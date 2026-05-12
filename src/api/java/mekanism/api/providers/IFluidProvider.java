package mekanism.api.providers;

import mekanism.api.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

@MethodsReturnNonnullByDefault
public interface IFluidProvider extends IBaseProvider {

    /**
     * Gets the fluid this provider represents.
     */
    Fluid getFluid();

    /**
     * Creates a fluid stack of the given size using the fluid this provider represents.
     *
     * @param size Size of the stack.
     */
    default FluidStack getFluidStack(long size) {
        return new FluidStack(FluidVariant.of(getFluid()), size);
    }

    @Override
    default ResourceLocation getRegistryName() {
        return BuiltInRegistries.FLUID.getKey(getFluid());
    }

    @Override
    default Component getTextComponent() {
        //return getFluid().getFluidType().getDescription(getFluidStack(1));
        return Component.literal("COMING SOON (mekanism.api.providers.IFluidProvider)");
    }

    @Override
    default String getTranslationKey() {
        //return getFluid().getFluidType().getDescriptionId();
        return "COMING SOON (mekanism.api.providers.IFluidProvider)";
    }
}