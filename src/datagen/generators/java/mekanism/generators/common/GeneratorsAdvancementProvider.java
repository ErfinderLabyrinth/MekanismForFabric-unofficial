package mekanism.generators.common;

import java.util.function.Consumer;
import mekanism.common.advancements.BaseAdvancementProvider;
import mekanism.common.advancements.MekanismAdvancements;
import mekanism.generators.common.advancements.GeneratorsAdvancements;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import org.jetbrains.annotations.NotNull;

public class GeneratorsAdvancementProvider extends BaseAdvancementProvider {

    public GeneratorsAdvancementProvider(FabricDataOutput output) {
        super(output, MekanismGenerators.MODID);
    }

    @Override
    protected void registerAdvancements(@NotNull Consumer<Advancement> consumer) {
        Advancement materials = createPlaceHolder(MekanismAdvancements.MATERIALS.name());
        Advancement heatGenerator = advancement(GeneratorsAdvancements.HEAT_GENERATOR)
              .parent(materials)
              .displayAndCriterion(GeneratorsBlocks.HEAT_GENERATOR, FrameType.TASK, true)
              .save(consumer);
        Advancement solarGenerator = advancement(GeneratorsAdvancements.SOLAR_GENERATOR)
              .parent(heatGenerator)
              .displayAndCriterion(GeneratorsBlocks.SOLAR_GENERATOR, FrameType.TASK, false)
              .save(consumer);
        Advancement windGenerator = advancement(GeneratorsAdvancements.WIND_GENERATOR)
              .parent(materials)
              .displayAndCriterion(GeneratorsBlocks.WIND_GENERATOR, FrameType.TASK, false)
              .save(consumer);
        Advancement burnTheGas = advancement(GeneratorsAdvancements.BURN_THE_GAS)
              .parent(windGenerator)
              .displayAndCriterion(GeneratorsBlocks.GAS_BURNING_GENERATOR, FrameType.GOAL, true)
              .save(consumer);
    }
}