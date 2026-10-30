package mekanism.api.datagen.recipe;

import net.minecraft.advancements.CriterionTriggerInstance;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Helper class to declare named criteria for repeated use.
 *
 * @param name      Name of the Recipe Criterion.
 * @param criterion Criterion Instance.
 */
public record RecipeCriterion(@NotNull String name, @NotNull CriterionTriggerInstance criterion) {

    /**
     * @param name      Name of the Recipe Criterion.
     * @param criterion Criterion Instance.
     */
    public RecipeCriterion {
        Objects.requireNonNull(name, "Criterion must have a name.");
        Objects.requireNonNull(criterion, "Recipe criterion's must have a criterion to match.");
    }
}