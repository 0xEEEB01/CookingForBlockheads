package net.blay09.mods.cookingbook.api;

import net.minecraft.item.ItemStack;

public interface IInternalMethods {

    void addSinkHandler(ItemStack itemStack, SinkHandler sinkHandler);
    void addOvenFuel(ItemStack fuelItem, int fuelTime);
    void addOvenRecipe(ItemStack sourceItem, ItemStack resultItem);
    void addToolItem(ItemStack toolItem);

}
