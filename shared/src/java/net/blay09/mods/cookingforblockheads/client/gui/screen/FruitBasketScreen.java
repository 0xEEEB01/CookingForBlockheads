package net.blay09.mods.cookingforblockheads.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.cookingforblockheads.menu.FruitBasketMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FruitBasketScreen extends AbstractContainerScreen<FruitBasketMenu> {

    private static final ResourceLocation texture = new ResourceLocation("textures/gui/container/generic_54.png");
    private final int inventoryRows;

    public FruitBasketScreen(FruitBasketMenu container, Inventory playerInventory, Component displayName) {
        super(container, playerInventory, displayName);
        this.inventoryRows = container.getNumRows();
        this.imageHeight = 114 + this.inventoryRows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, texture);
        blit(poseStack, leftPos, topPos, 0, 0, this.imageWidth, this.inventoryRows * 18 + 17);
        blit(poseStack, leftPos, topPos + this.inventoryRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }

}
