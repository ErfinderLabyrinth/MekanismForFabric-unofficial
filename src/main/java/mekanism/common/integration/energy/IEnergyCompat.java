package mekanism.common.integration.energy;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IStrictEnergyHandler;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public interface IEnergyCompat<S> {

    /**
     * Whether this energy compat is actually enabled.
     *
     * @return if this energy compat is enabled.
     */
    boolean isUsable();

    /**
     * Gets the configs that back {@link #isUsable()} so that caching for usable and enabled energy types can be done.
     *
     * @implNote If this {@link IEnergyCompat} will never be usable due to missing required mods, this should just return an empty collection to allow the enabled caching
     * to skip listening to the corresponding config settings.
     */
    /*default Collection<CachedValue<?>> getBackingConfigs() {
        return Collections.emptySet();
    }*/

    /**
     * Checks if the given provider has this capability.
     *
     * @param provider Capability provider
     * @param side     Side
     *
     * @return {@code true} if the provider has this {@link IEnergyCompat}'s capability, {@code false} otherwise
     *
     * @implNote The capabilities should be kept lazy so that they are not resolved if they are not needed yet.
     */
    default boolean isCapabilityPresent(ItemStack stack) {
        return getStrictEnergyHandler(stack, ContainerItemContext.withConstant(stack)) != null;
    }

    default boolean isCapabilityPresent(Level level, BlockPos pos, @Nullable Direction side) {
        return getStrictEnergyHandler(level, pos, side) != null;
    }

    /**
     * Gets the {@link IStrictEnergyHandler} as a lazy optional for the capability this energy compat is for.
     *
     * @param handler The handler to wrap
     *
     * @return A lazy optional for this capability
     */
    S getHandlerAs(IStrictEnergyHandler handler);

    /**
     * Wraps the capability implemented in the provider into a lazy optional {@link IStrictEnergyHandler}, or returns {@code LazyOptional.empty()} if the capability is
     * not implemented.
     *
     * @param provider Capability provider
     * @param side     Side
     *
     * @return The capability implemented in the provider into an {@link IStrictEnergyHandler}, or {@code null} if the capability is not implemented.
     */
    @Nullable IStrictEnergyHandler getStrictEnergyHandler(Level level, BlockPos pos, @Nullable Direction side);

    @Nullable IStrictEnergyHandler getStrictEnergyHandler(ItemStack stack, ContainerItemContext context);
}