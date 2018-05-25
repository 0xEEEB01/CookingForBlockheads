package net.blay09.mods.cookingforblockheads.api.capability;

import net.blay09.mods.cookingforblockheads.api.SourceItem;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public abstract class DefaultKitchenItemProvider implements IKitchenItemProvider {

    @Override
    public ItemStack useItemStack(int slot, int amount, boolean simulate, List<IKitchenItemProvider> inventories, boolean requireBucket) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlots() {
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack returnItemStack(ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public void resetSimulation() {
    }

    @Override
    public int getSimulatedUseCount(int slot) {
        return 0;
    }

    @Override
    public ItemStack findAndMarkAsUsed(IngredientPredicate predicate, int maxAmount, List<IKitchenItemProvider> inventories, boolean requireBucket, boolean simulate) {
        SourceItem sourceItem = findSourceAndMarkAsUsed(predicate, maxAmount, inventories, requireBucket, simulate);
        if (sourceItem != null) {
            return sourceItem.getSourceStack();
        }

        return ItemStack.EMPTY;
    }

    @Override
    @Nullable
    public SourceItem findSourceAndMarkAsUsed(IngredientPredicate predicate, int maxAmount, List<IKitchenItemProvider> inventories, boolean requireBucket, boolean simulate) {
        for (int j = 0; j < getSlots(); j++) {
            ItemStack itemStack = getStackInSlot(j);
            int amount = Math.min(itemStack.getCount(), maxAmount);
            if (!itemStack.isEmpty() && predicate.test(itemStack, itemStack.getCount() - getSimulatedUseCount(j))) {
                itemStack = useItemStack(j, amount, simulate, inventories, requireBucket);
                if (!itemStack.isEmpty()) {
                    return new SourceItem(this, j, itemStack);
                }
            }
        }

        return null;
    }

    @Override
    public void consumeSourceItem(SourceItem sourceItem, int maxAmount, List<IKitchenItemProvider> inventories, boolean requireContainer) {
        if (sourceItem.getSourceSlot() < 0) {
            // Ignore negative source slots by default since they should only be used for items that are infinite.
            return;
        }

        useItemStack(sourceItem.getSourceSlot(), maxAmount, false, inventories, requireContainer);
    }

}
