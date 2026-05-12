package mekanism.common.integration.energy;

import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.integration.energy.teamreborn.TeamRebornEnergyCompat;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergyCompatUtils {

    private EnergyCompatUtils() {
    }

    private static final List<IEnergyCompat> energyCompats = List.of(
          //We always have our own energy capability as the first one we check
          new StrictEnergyCompat(),
          //Note: We check the Flux Networks capability above Forge's so that we allow it to use the higher throughput amount supported by Flux Networks
          //new FNEnergyCompat(),
          //new ForgeEnergyCompat()
          new TeamRebornEnergyCompat()
    );

    /**
     * @apiNote For internal uses, only call this after mods have loaded so that we can properly assume all {@link IEnergyCompat#isUsable()} checks only depend on config
     * settings.
     */
    /*public static void initLoadedCache() {
        Set<CachedValue<?>> configs = new HashSet<>();
        for (IEnergyCompat energyCompat : energyCompats) {
            configs.addAll(energyCompat.getBackingConfigs());
        }
        ENABLED_ENERGY_CAPS = new ConfigBasedCachedSupplier<>(
              () -> energyCompats.stream().filter(IEnergyCompat::isUsable).<Capability<?>>map(IEnergyCompat::getCapability).toList(),
              configs.toArray(new CachedValue[0])
        );
    }*/

    public static List<IEnergyCompat> getCompats() {
        return energyCompats;
    }

    /**
     * Checks if it is a known and enabled energy capability
     */
    /*public static boolean isEnergyCapability(@NotNull Capability<?> capability) {
        //The capability may not be registered if the mod that adds it is not loaded. In which case we can just
        // short circuit and not check if
        if (capability.isRegistered()) {
            for (IEnergyCompat energyCompat : energyCompats) {
                //Note: We don't need to check if it is usable before checking if the capability matches as it is instance equality
                if (energyCompat.isMatchingCapability(capability)) {
                    return energyCompat.isUsable();
                }
            }
        }
        return false;
    }*/

    private static boolean isTileValid(@Nullable BlockEntity tile) {
        return tile != null && !tile.isRemoved() && tile.hasLevel();
    }

    public static boolean hasStrictEnergyHandler(@NotNull ItemStack stack) {
        if(stack.isEmpty()) {
            return false;
        }

        for (IEnergyCompat energyCompat : energyCompats) {
            if (energyCompat.isUsable() && energyCompat.isCapabilityPresent(stack)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasStrictEnergyHandler(Level level, BlockPos pos, Direction side) {
        for (IEnergyCompat energyCompat : energyCompats) {
            if (energyCompat.isUsable() && energyCompat.isCapabilityPresent(level, pos, side)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static IStrictEnergyHandler getLazyStrictEnergyHandler(@NotNull ItemStack stack, @NotNull ContainerItemContext context) {
        if(stack.isEmpty()) {
            return null;
        }

        for (IEnergyCompat energyCompat : energyCompats) {
            if (energyCompat.isUsable()) {
                IStrictEnergyHandler handler = energyCompat.getStrictEnergyHandler(stack, context);
                if(handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }

    @Nullable
    public static IStrictEnergyHandler getLazyStrictEnergyHandler(Level level, BlockPos pos, Direction side) {
        for (IEnergyCompat energyCompat : energyCompats) {
            if (energyCompat.isUsable()) {
                IStrictEnergyHandler handler = energyCompat.getStrictEnergyHandler(level, pos, side);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }

    /**
     * @apiNote It is expected that isEnergyCapability is called before calling this method
     */
    /*@NotNull
    public static EnergyStorage getEnergyCapability(@NotNull IStrictEnergyHandler handler) {
        //The capability may not be registered if the mod that adds it is not loaded. In which case we can just
        // short circuit and not check if
        if (capability.isRegistered()) {
            //Note: The methods that call this method cache the returned lazy optional properly
            for (IEnergyCompat energyCompat : energyCompats) {
                if (energyCompat.isUsable() && energyCompat.isMatchingCapability(capability)) {
                    //Note: This is a little ugly but this extra method ensures that the supplier's type does not get prematurely resolved
                    return energyCompat.getHandlerAs(handler).cast();
                }
            }
        }
        return LazyOptional.empty();
    }*/

    /**
     * Whether IC2 power should be used, taking into account whether it is installed or another mod is providing its API.
     *
     * @return if IC2 power should be used
     */
    public static boolean useIC2() {
        //TODO: IC2
        //Note: Use default value if called before configs are loaded. In general this should never happen, but third party mods may just call it regardless
        return false;//Mekanism.hooks.IC2Loaded/* && EnergyNet.instance != null*/ && !MekanismConfig.general.blacklistIC2.getOrDefault();
    }
}