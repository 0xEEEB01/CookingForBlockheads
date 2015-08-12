package net.blay09.mods.cookingbook.container;

import net.blay09.mods.cookingbook.food.FoodIngredient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;

import java.util.ArrayList;
import java.util.List;

public class SlotCraftMatrix extends Slot {

    private static final int ITEM_SWITCH_TIME = 20;

    private final EntityPlayer player;
    private FoodIngredient ingredient;
    private boolean enabled = true;

    private IInventory[] sourceInventories;
    private ItemStack[] visibleStacks;
    private int visibleItemTime;
    private int visibleItemIndex;

    public SlotCraftMatrix(EntityPlayer player, IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.player = player;
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }

    public void setIngredient(FoodIngredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean canBeHovered() {
        return enabled;
    }

    /**
     * SERVER ONLY
     */
    public void updateVisibleStacks() {
        if(ingredient != null) {
            visibleStacks = ingredient.getItemStacks();
            if(ingredient.getItemStacks().length > 1 && !ingredient.isToolItem()) {
                List<ItemStack> visibleStackList = new ArrayList<ItemStack>();
                for(ItemStack visibleStack : visibleStacks) {
                    for(int i = 0; i < sourceInventories.length; i++) {
                        for(int j = 0; j < sourceInventories[i].getSizeInventory(); j++) {
                            ItemStack itemStack = sourceInventories[i].getStackInSlot(j);
                            if(itemStack != null) {
                                if(itemStack.getHasSubtypes() ? itemStack.isItemEqual(visibleStack) : itemStack.getItem() == visibleStack.getItem()) {
                                    visibleStackList.add(visibleStack);
                                }
                            }
                        }
                    }
                }
                visibleStacks = visibleStackList.toArray(new ItemStack[visibleStackList.size()]);
            }
            if(visibleStacks.length == 1) {
                putStack(visibleStacks[0]);
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(player.openContainer.windowId, slotNumber, visibleStacks[0]));
            }
            visibleItemTime = ITEM_SWITCH_TIME;
            visibleItemIndex = 0;
        } else {
            putStack(null);
            ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(player.openContainer.windowId, slotNumber, null));
            visibleStacks = null;
        }
        update();
    }

    /**
     * SERVER ONLY
     */
    public void update() {
        if(visibleStacks != null && visibleStacks.length > 1) {
            visibleItemTime++;
            if(visibleItemTime >= ITEM_SWITCH_TIME) {
                visibleItemIndex++;
                if(visibleItemIndex >= visibleStacks.length) {
                    visibleItemIndex = 0;
                }
                putStack(visibleStacks[visibleItemIndex]);
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(player.openContainer.windowId, slotNumber, visibleStacks[0]));
                visibleItemTime = 0;
            }
        }
    }

    /**
     * SERVER ONLY
     * @param sourceInventories
     */
    public void setSourceInventories(IInventory[] sourceInventories) {
        this.sourceInventories = sourceInventories;
    }
}
