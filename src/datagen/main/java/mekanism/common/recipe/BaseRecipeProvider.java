package mekanism.common.recipe;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import mekanism.api.annotations.NothingNullByDefault;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.fabricmc.fabric.api.recipe.v1.ingredient.FabricIngredient;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.DifferenceIngredient;

@NothingNullByDefault
public abstract class BaseRecipeProvider extends RecipeProvider {
    protected BaseRecipeProvider(PackOutput output, String modid) {
        super(output);
    }

    @Override
    public final void buildRecipes(Consumer<FinishedRecipe> consumer) {
        addRecipes(consumer);
        getSubRecipeProviders().forEach(subRecipeProvider -> subRecipeProvider.addRecipes(consumer));
    }

    protected abstract void addRecipes(Consumer<FinishedRecipe> consumer);

    /**
     * Gets all the sub/offloaded recipe providers that this recipe provider has.
     *
     * @implNote This is only called once per provider so there is no need to bother caching the list that this returns
     */
    protected List<ISubRecipeProvider> getSubRecipeProviders() {
        return Collections.emptyList();
    }

    public static Ingredient createIngredient(TagKey<Item> itemTag, ItemLike... items) {
        return createIngredient(Collections.singleton(itemTag), items);
    }

    public static Ingredient createIngredient(Collection<TagKey<Item>> itemTags, ItemLike... items) {
        return Ingredient.fromValues(Stream.concat(
              itemTags.stream().map(Ingredient.TagValue::new),
              Arrays.stream(items).map(item -> new Ingredient.ItemValue(new ItemStack(item)))
        ));
    }

    @SafeVarargs
    public static Ingredient createIngredient(TagKey<Item>... tags) {
        return Ingredient.of(Arrays.stream(tags).map(Ingredient.TagValue::new));
    }

    public static Ingredient difference(TagKey<Item> base, ItemLike subtracted) {
        return DefaultCustomIngredients.difference(Ingredient.of(base), Ingredient.of(subtracted));
    }
}