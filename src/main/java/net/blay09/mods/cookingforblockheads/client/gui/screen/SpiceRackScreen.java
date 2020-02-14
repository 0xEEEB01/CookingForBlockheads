package net.blay09.mods.cookingforblockheads.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.blay09.mods.cookingforblockheads.container.SpiceRackContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SpiceRackScreen extends ContainerScreen<SpiceRackContainer> {

    private static final ResourceLocation texture = new ResourceLocation(CookingForBlockheads.MOD_ID, "textures/gui/spice_rack.png");

    public SpiceRackScreen(SpiceRackContainer container, PlayerInventory playerInventory, ITextComponent displayName) {
        super(container, playerInventory, displayName);
        ySize = 132;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        Minecraft minecraft = getMinecraft();
        minecraft.fontRenderer.drawString(title.getFormattedText(), 8, 6, 4210752);
        minecraft.fontRenderer.drawString(I18n.format("container.inventory"), 8, 38, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        getMinecraft().getTextureManager().bindTexture(texture);
        blit(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

}
