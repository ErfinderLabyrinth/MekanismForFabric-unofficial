package mekanism.common.registration.impl;

import mekanism.api.recipes.MekanismRecipe;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.IInputRecipeCache;
import mekanism.common.registration.WrappedRegistryObject;

public class RecipeTypeRegistryObject<RECIPE extends MekanismRecipe, INPUT_CACHE extends IInputRecipeCache> extends
      WrappedRegistryObject<MekanismRecipeType<RECIPE, INPUT_CACHE>> implements IMekanismRecipeTypeProvider<RECIPE, INPUT_CACHE> {

    public RecipeTypeRegistryObject(MekanismRecipeType<RECIPE, INPUT_CACHE> recipeType) {
        super(recipeType);
    }

    @Override
    public MekanismRecipeType<RECIPE, INPUT_CACHE> getRecipeType() {
        return get();
    }
}