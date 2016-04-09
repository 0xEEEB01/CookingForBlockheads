package net.blay09.mods.cookingforblockheads.registry.recipe;

import com.google.common.collect.Lists;
import net.blay09.mods.cookingforblockheads.registry.CookingRegistry;
import net.minecraft.item.crafting.ShapelessRecipes;

import java.util.ArrayList;

public class ShapelessCraftingFood extends FoodRecipe {

    public ShapelessCraftingFood(int id, ShapelessRecipes recipe) {
        super(id);
        this.outputItem = recipe.getRecipeOutput();
        this.craftMatrix = Lists.newArrayList();
        for(int i = 0; i < recipe.recipeItems.size(); i++) {
            if (recipe.recipeItems.get(i) != null) {
                boolean isToolItem = CookingRegistry.isToolItem(recipe.recipeItems.get(i));
                craftMatrix.add(new FoodIngredient(recipe.recipeItems.get(i).copy(), isToolItem));
            }
        }
    }

}
