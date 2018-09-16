package net.blay09.mods.cookingforblockheads.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.blay09.mods.cookingforblockheads.ItemUtils;
import net.blay09.mods.cookingforblockheads.KitchenMultiBlock;
import net.blay09.mods.cookingforblockheads.api.*;
import net.blay09.mods.cookingforblockheads.api.capability.IKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.api.capability.IngredientPredicate;
import net.blay09.mods.cookingforblockheads.api.capability.KitchenItemProvider;
import net.blay09.mods.cookingforblockheads.api.event.FoodRegistryInitEvent;
import net.blay09.mods.cookingforblockheads.compat.HarvestCraftAddon;
import net.blay09.mods.cookingforblockheads.container.inventory.InventoryCraftBook;
import net.blay09.mods.cookingforblockheads.registry.recipe.FoodIngredient;
import net.blay09.mods.cookingforblockheads.registry.recipe.FoodRecipe;
import net.blay09.mods.cookingforblockheads.registry.recipe.GeneralFoodRecipe;
import net.blay09.mods.cookingforblockheads.registry.recipe.SmeltingFood;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.*;

public class CookingRegistry {
    public static class ItemIdentifier {
        ResourceLocation location;
        int metadata;

        public ItemIdentifier(ResourceLocation location, int metadata) {
            this.location = location;
            this.metadata = metadata;
        }

        public ItemIdentifier(ItemStack object) {
            this.location = object.getItem().getRegistryName();
            this.metadata = object.getItemDamage();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof ItemIdentifier)) {
                return false;
            }
            ItemIdentifier identifier = (ItemIdentifier) o;
            return metadata == identifier.metadata && Objects.equals(location, identifier.location);
        }

        @Override
        public int hashCode() {
            return Objects.hash(location, metadata);
        }

        @Override
        public String toString() {
            return location.toString() + "@" + metadata;
        }
    }

    private static final List<IRecipe> recipeList = Lists.newArrayList();
    private static final ArrayListMultimap<ItemIdentifier, FoodRecipe> foodItems = ArrayListMultimap.create();
    private static final NonNullList<ItemStack> tools = NonNullList.create();
    private static final Map<ItemStack, Integer> ovenFuelItems = Maps.newHashMap();
    private static final Map<ItemStack, ItemStack> ovenRecipes = Maps.newHashMap();
    private static final Map<ItemStack, SinkHandler> sinkHandlers = Maps.newHashMap();
    private static final Map<ItemStack, ToastHandler> toastHandlers = Maps.newHashMap();
    private static final NonNullList<ItemStack> waterItems = NonNullList.create();
    private static final NonNullList<ItemStack> milkItems = NonNullList.create();
    private static final List<ISortButton> customSortButtons = Lists.newArrayList();

    private static Collection<ItemStack> nonFoodRecipes;

    public static void initFoodRegistry() {
        recipeList.clear();
        foodItems.clear();

        FoodRegistryInitEvent init = new FoodRegistryInitEvent();
        MinecraftForge.EVENT_BUS.post(init);

        nonFoodRecipes = init.getNonFoodRecipes();

        // Crafting Recipes of Food Items
        for (IRecipe recipe : CraftingManager.REGISTRY) {
            ItemStack output = recipe.getRecipeOutput();
            if (!output.isEmpty()) {
                if (output.getItem() instanceof ItemFood) {
                    if (HarvestCraftAddon.isWeirdConversionRecipe(recipe)) {
                        continue;
                    }
                    addFoodRecipe(recipe);
                } else {
                    for (ItemStack itemStack : nonFoodRecipes) {
                        if (ItemUtils.areItemStacksEqualWithWildcard(recipe.getRecipeOutput(), itemStack)) {
                            addFoodRecipe(recipe);
                            break;
                        }
                    }
                }
            }
        }

        // Smelting Recipes of Food Items
        for (Object obj : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
            Map.Entry entry = (Map.Entry) obj;
            ItemStack sourceStack = ItemStack.EMPTY;
            if (entry.getKey() instanceof Item) {
                sourceStack = new ItemStack((Item) entry.getKey());
            } else if (entry.getKey() instanceof Block) {
                sourceStack = new ItemStack((Block) entry.getKey());
            } else if (entry.getKey() instanceof ItemStack) {
                sourceStack = (ItemStack) entry.getKey();
            }
            ItemStack resultStack = (ItemStack) entry.getValue();
            if (resultStack.getItem() instanceof ItemFood) {
                foodItems.put(new ItemIdentifier(resultStack), new SmeltingFood(resultStack, sourceStack));
            } else {
                if (isNonFoodRecipe(resultStack)) {
                    foodItems.put(new ItemIdentifier(resultStack), new SmeltingFood(resultStack, sourceStack));
                }
            }
        }
    }

    public static boolean isNonFoodRecipe(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        for (ItemStack nonFoodStack : nonFoodRecipes) {
            if (ItemUtils.areItemStacksEqualWithWildcard(itemStack, nonFoodStack)) {
                return true;
            }
        }
        return false;
    }

    public static void addFoodRecipe(IRecipe recipe) {
        ItemStack output = recipe.getRecipeOutput();
        if (!output.isEmpty() && !recipe.getIngredients().isEmpty()) {
            recipeList.add(recipe);
            foodItems.put(new ItemIdentifier(output), new GeneralFoodRecipe(recipe));
//			if (recipe instanceof ShapedRecipes) {
//				foodItems.put(new ItemIdentifier(output), new ShapedCraftingFood((ShapedRecipes) recipe));
//			} else if (recipe instanceof ShapelessRecipes) {
//				foodItems.put(new ItemIdentifier(output), new ShapelessCraftingFood((ShapelessRecipes) recipe));
//			} else if (recipe instanceof ShapelessOreRecipe) {
//				foodItems.put(new ItemIdentifier(output), new ShapelessOreCraftingFood((ShapelessOreRecipe) recipe));
//			} else if (recipe instanceof ShapedOreRecipe) {
//				foodItems.put(new ItemIdentifier(output), new ShapedOreCraftingFood((ShapedOreRecipe) recipe));
//			}
        }
    }

    public static Multimap<ItemIdentifier, FoodRecipe> getFoodRecipes() {
        return foodItems;
    }

    public static Collection<FoodRecipe> getFoodRecipes(ItemStack outputItem) {
        return foodItems.get(new ItemIdentifier(outputItem));
    }

    public static Collection<FoodRecipe> getFoodRecipes(ItemIdentifier outputItem) {
        return foodItems.get(outputItem);
    }

    public static void addToolItem(ItemStack toolItem) {
        tools.add(toolItem);
    }

    public static boolean isToolItem(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        for (ItemStack toolItem : tools) {
            if (ItemUtils.areItemStacksEqualWithWildcardIgnoreDurability(toolItem, itemStack)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isToolItem(Ingredient ingredient) {
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            if (isToolItem(itemStack)) {
                return true;
            }
        }
        return false;
    }

    public static void addOvenFuel(ItemStack itemStack, int fuelTime) {
        ovenFuelItems.put(itemStack, fuelTime);
    }

    public static int getOvenFuelTime(ItemStack itemStack) {
        for (Map.Entry<ItemStack, Integer> entry : ovenFuelItems.entrySet()) {
            if (ItemUtils.areItemStacksEqualWithWildcard(entry.getKey(), itemStack)) {
                return entry.getValue();
            }
        }
        return 0;
    }

    public static void addSmeltingItem(ItemStack source, ItemStack result) {
        ovenRecipes.put(source, result);
    }

    public static ItemStack getSmeltingResult(ItemStack itemStack) {
        for (Map.Entry<ItemStack, ItemStack> entry : ovenRecipes.entrySet()) {
            if (ItemUtils.areItemStacksEqualWithWildcard(entry.getKey(), itemStack)) {
                return entry.getValue();
            }
        }
        return ItemStack.EMPTY;
    }

    public static void addToastHandler(ItemStack itemStack, ToastHandler toastHandler) {
        toastHandlers.put(itemStack, toastHandler);
    }

    @Nullable
    public static ToastHandler getToastHandler(ItemStack itemStack) {
        for (Map.Entry<ItemStack, ToastHandler> entry : toastHandlers.entrySet()) {
            if (ItemUtils.areItemStacksEqualWithWildcard(entry.getKey(), itemStack)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static void addSinkHandler(ItemStack itemStack, SinkHandler sinkHandler) {
        sinkHandlers.put(itemStack, sinkHandler);
    }

    public static ItemStack getSinkOutput(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        for (Map.Entry<ItemStack, SinkHandler> entry : sinkHandlers.entrySet()) {
            if (ItemUtils.areItemStacksEqualWithWildcard(entry.getKey(), itemStack)) {
                return entry.getValue().getSinkOutput(itemStack);
            }
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    private static SourceItem findAnyItemStack(ItemStack checkStack, List<IKitchenItemProvider> inventories, boolean requireBucket) {
        if (checkStack.isEmpty()) {
            return null;
        }

        for (int i = 0; i < inventories.size(); i++) {
            IKitchenItemProvider itemProvider = inventories.get(i);
            IngredientPredicate predicate = (it, count) -> ItemUtils.areItemStacksEqualWithWildcardIgnoreDurability(it, checkStack) && count > 0;
            SourceItem found = itemProvider.findSource(predicate, 1, inventories, requireBucket, true);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    public static List<SourceItem> findSourceCandidates(FoodIngredient ingredient, List<IKitchenItemProvider> inventories, boolean requireBucket, boolean isNoFilter) {
        List<SourceItem> sourceList = new ArrayList<>();

        ItemStack[] variants = ingredient.getItemStacks();
        for (ItemStack checkStack : variants) {
            SourceItem sourceItem = CookingRegistry.findAnyItemStack(checkStack, inventories, requireBucket);
            ItemStack foundStack = sourceItem != null ? sourceItem.getSourceStack() : ItemStack.EMPTY;
            if (foundStack.isEmpty()) {
                if (isNoFilter || ingredient.isToolItem()) {
                    sourceItem = new SourceItem(null, -1, checkStack);
                }
            }

            if (sourceItem != null) {
                sourceList.add(sourceItem);
            }
        }

        SourceItem sourceItem = !sourceList.isEmpty() ? sourceList.get(0) : null;
        if (sourceItem != null && sourceItem.getSourceProvider() != null) {
            sourceItem.getSourceProvider().markAsUsed(sourceItem, 1, inventories, requireBucket);
        }

        return sourceList;
    }

    public static boolean consumeBucket(List<IKitchenItemProvider> inventories, boolean simulate) {
        ItemStack bucketStack = new ItemStack(Items.BUCKET);
        for (int i = 0; i < inventories.size(); i++) {
            IKitchenItemProvider itemProvider = inventories.get(i);
            ItemStack found = itemProvider.findAndMarkAsUsed((it, count) -> ItemUtils.areItemStacksEqualWithWildcard(it, bucketStack) && count > 0, 1, inventories, false, simulate);
            if (!found.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public static RecipeStatus getRecipeStatus(FoodRecipe recipe, List<IKitchenItemProvider> inventories, boolean hasOven) {
        boolean requireBucket = doesItemRequireBucketForCrafting(recipe.getOutputItem());
        for (IKitchenItemProvider itemProvider : inventories) {
            itemProvider.resetSimulation();
        }

        List<FoodIngredient> craftMatrix = recipe.getCraftMatrix();
        boolean missingTools = false;
        for (int i = 0; i < craftMatrix.size(); i++) {
            FoodIngredient ingredient = craftMatrix.get(i);
            if (ingredient != null) {
                List<SourceItem> sourceList = findSourceCandidates(ingredient, inventories, requireBucket, false);
                if (sourceList.isEmpty()) {
                    return RecipeStatus.MISSING_INGREDIENTS;
                }

                if (sourceList.stream().allMatch(it -> it.getSourceProvider() == null)) {
                    missingTools = true;
                }
            }
        }

        // Do not mark smeltable recipes as available unless an oven is present.
        if (recipe.getType() == RecipeType.SMELTING && !hasOven) {
            return RecipeStatus.MISSING_TOOLS;
        }

        return missingTools ? RecipeStatus.MISSING_TOOLS : RecipeStatus.AVAILABLE;
    }

    public static List<IKitchenItemProvider> getItemProviders(@Nullable KitchenMultiBlock multiBlock, InventoryPlayer inventory) {
        return multiBlock != null ? multiBlock.getItemProviders(inventory) : Lists.newArrayList(new KitchenItemProvider(new InvWrapper(inventory)));
    }

    @Nullable
    public static IRecipe findFoodRecipe(InventoryCraftBook craftMatrix, World world) {
        for (IRecipe recipe : recipeList) {
            if (recipe.matches(craftMatrix, world)) {
                return recipe;
            }
        }
        return null;
    }

    public static void addWaterItem(ItemStack waterItem) {
        waterItems.add(waterItem);
    }

    public static void addMilkItem(ItemStack milkItem) {
        milkItems.add(milkItem);
    }

    public static void addSortButton(ISortButton button) {
        customSortButtons.add(button);
    }

    public static NonNullList<ItemStack> getWaterItems() {
        return waterItems;
    }

    public static NonNullList<ItemStack> getMilkItems() {
        return milkItems;
    }

    public static List<ISortButton> getSortButtons() {
        return customSortButtons;
    }

    public static boolean doesItemRequireBucketForCrafting(ItemStack outputItem) {
        ItemStack containerItem = ForgeHooks.getContainerItem(outputItem);
        if (!containerItem.isEmpty() && containerItem.getItem() == Items.BUCKET) {
            return true;
        }
        ResourceLocation registryName = outputItem.getItem().getRegistryName();
        return registryName != null && registryName.getResourcePath().contains("bucket");
    }
}
