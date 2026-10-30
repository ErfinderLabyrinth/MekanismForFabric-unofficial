package mekanism.generators.common.content.gear.mekasuit;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.math.FloatingLong;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.WorldUtils;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import team.reborn.energy.api.EnergyStorage;

@ParametersAreNotNullByDefault
public class ModuleSolarRechargingUnit implements ICustomModule<ModuleSolarRechargingUnit> {

    private static final double RAIN_MULTIPLIER = 0.2;

    @Override
    public void tickServer(IModule<ModuleSolarRechargingUnit> module, Player player) {
        EnergyStorage energyContainer = module.getEnergyContainer();
        if (energyContainer != null && energyContainer.getAmount() != energyContainer.getCapacity()) {
            //Use the position that is roughly where the solar panel is
            BlockPos pos = BlockPos.containing(player.getX(), player.getEyeY() + 0.2, player.getZ());
            //Based on how TileEntitySolarGenerator and the rest of our solar things do energy calculations
            if (WorldUtils.canSeeSun(player.level(), pos)) {
                Biome b = player.level().getBiomeManager().getBiome(pos).value();
                boolean needsRainCheck = b.getPrecipitationAt(pos) != Precipitation.NONE;
                // Consider the best temperature to be 0.8; biomes that are higher than that
                // will suffer an efficiency loss (semiconductors don't like heat); biomes that are cooler
                // get a boost. We scale the efficiency to around 30% so that it doesn't totally dominate
                float tempEff = 0.3F * (0.8F - b.getTemperature(pos));

                // Treat rainfall as a proxy for humidity; any humidity works as a drag on overall efficiency.
                // As with temperature, we scale it so that it doesn't overwhelm production. Note the signedness
                // on the scaling factor. Also note that we only use rainfall as a proxy if it CAN rain; some dimensions
                // (like the End) have rainfall set, but can't actually support rain.
                float humidityEff = needsRainCheck ? -0.3F * b.climateSettings.downfall() : 0.0F;
                double peakOutput = MekanismConfig.COMMON.gear.mekaSuitSolarRechargingRate * (1.0F + tempEff + humidityEff);

                //Get the brightness of the sun; note that there are some implementations that depend on the base
                // brightness function which doesn't take into account the fact that rain can't occur in some biomes.
                float brightness = WorldUtils.getSunBrightness(player.level(), 1.0F);

                //Production is a function of the peak possible output in this biome and sun's current brightness
                double production = peakOutput * brightness;
                //If the generator is in a biome where it can rain, and it's raining penalize production by 80%
                if (needsRainCheck && (player.level().isRaining() || player.level().isThundering())) {
                    production = production * RAIN_MULTIPLIER;
                }
                //Multiply actual production based on how many modules are installed
                try(Transaction t = Transaction.openOuter()) {
                    energyContainer.insert((long) (production * module.getInstalledCount()), t);
                    t.commit();
                }
            }
        }
    }
}