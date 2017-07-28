package net.blay09.mods.cookingforblockheads.registry.recipe;

import com.google.common.collect.Lists;
import net.blay09.mods.cookingforblockheads.registry.CookingRegistry;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class GeneralFoodRecipe extends FoodRecipe {

    public GeneralFoodRecipe(IRecipe recipe) {
        this.outputItem = recipe.getRecipeOutput();
        if(recipe instanceof ShapedRecipes) {
            this.recipeWidth = ((ShapedRecipes) recipe).getWidth();
        } else if(recipe instanceof ShapedOreRecipe) {
            this.recipeWidth = ((ShapedOreRecipe) recipe).getWidth();
        } else {
            this.recipeWidth = recipe.canFit(1, 1) ? 1 : recipe.canFit(2, 1) ? 2 : 3;
        }
        craftMatrix = Lists.newArrayList();
        for(Ingredient ingredient : recipe.getIngredients()) {
            if(ingredient != Ingredient.EMPTY) {
                boolean isToolItem = CookingRegistry.isToolItem(ingredient);
                craftMatrix.add(new FoodIngredient(ingredient, isToolItem));
            } else {
                craftMatrix.add(null);
            }
        }
    }

}
