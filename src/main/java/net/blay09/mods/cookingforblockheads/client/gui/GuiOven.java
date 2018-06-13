package net.blay09.mods.cookingforblockheads.client.gui;

import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.blay09.mods.cookingforblockheads.container.ContainerOven;
import net.blay09.mods.cookingforblockheads.tile.TileOven;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiOven extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(CookingForBlockheads.MOD_ID, "textures/gui/oven.png");
    private final TileOven tileEntity;

    public GuiOven(EntityPlayer player, TileOven tileEntity) {
        super(new ContainerOven(player, tileEntity));
        this.tileEntity = tileEntity;
        this.xSize += 22;
        this.ySize = 193;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        if (tileEntity.hasPowerUpgrade() && mouseX >= guiLeft + xSize - 25 && mouseY >= guiTop + 22 && mouseX < guiLeft + xSize - 25 + 35 + 18 && mouseY < guiTop + 22 + 72) {
            drawHoveringText(I18n.format("tooltip.cookingforblockheads:energy_stored", tileEntity.getEnergyStored(), tileEntity.getEnergyCapacity()), mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = I18n.format("container." + CookingForBlockheads.MOD_ID + ":oven");
        this.fontRenderer.drawString(s, (this.xSize + 22) / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8 + 22, this.ySize - 96 + 2, 4210752);

        for (int i = 0; i < 9; i++) {
            Slot slot = inventorySlots.inventorySlots.get(i + 7);
            if (slot.getHasStack()) {
                ItemStack itemStack = TileOven.getSmeltingResult(slot.getStack());
                if (!itemStack.isEmpty()) {
                    renderItemWithTint(itemStack, slot.xPos, slot.yPos + 16, 0xFFFFFF + ((int) (tileEntity.getCookProgress(i) * 255) << 24));
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(texture);

        // Draw background
        drawTexturedModalRect(guiLeft + 22, guiTop, 0, 0, xSize - 22, ySize);

        // Draw tool slots
        drawTexturedModalRect(guiLeft, guiTop + 10, 176, 30, 25, 87);

        int offsetX = tileEntity.hasPowerUpgrade() ? -5 : 0;

        // Draw main slots
        drawTexturedModalRect(guiLeft + 22 + 61 + offsetX, guiTop + 18, 176, 117, 76, 76);

        // Draw fuel slot
        drawTexturedModalRect(guiLeft + 22 + 38 + offsetX, guiTop + 43, 205, 84, 18, 33);

        // Draw fuel bar
        if (tileEntity.isBurning()) {
            int burnTime = (int) (12 * tileEntity.getBurnTimeProgress());
            drawTexturedModalRect(guiLeft + 22 + 40 + offsetX, guiTop + 43 + 12 - burnTime, 176, 12 - burnTime, 14, burnTime + 1);
        }

        // Draw power bar
        if (tileEntity.hasPowerUpgrade()) {
            //drawTexturedModalRect(guiLeft + 35, guiTop + 20, 205, 0, 18, 72);
            //drawTexturedModalRect(guiLeft + xSize - 30, guiTop + 22, 205, 0, 18, 72);
            drawTexturedModalRect(guiLeft + xSize - 25, guiTop + 22, 205, 0, 18, 72);
            float energyPercentage = tileEntity.getEnergyStored() / (float) tileEntity.getEnergyCapacity();
            drawTexturedModalRect(guiLeft + xSize - 25 + 1, guiTop + 22 + 1 + 70 - (int) (energyPercentage * 70), 223, 0, 16, (int) (energyPercentage * 70));
            //drawTexturedModalRect(guiLeft + 60, guiTop + 20, 205, 0, 18, 72);
        }
    }

    private void renderItemWithTint(ItemStack itemStack, int x, int y, int color) {
        RenderItem itemRenderer = mc.getRenderItem();
        IBakedModel model = itemRenderer.getItemModelWithOverrides(itemStack, null, null);
        GlStateManager.pushMatrix();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1f, 1f, 1f, 1f);

        GlStateManager.translate(x, y, 300f + zLevel);
        GlStateManager.scale(1f, -1f, 1f);
        GlStateManager.scale(16f, 16f, 16f);
        if (model.isGui3d()) {
            GlStateManager.enableLighting();
        } else {
            GlStateManager.disableLighting();
        }

        model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GUI, false);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        for (EnumFacing facing : EnumFacing.values()) {
            renderQuads(vertexBuffer, model.getQuads(null, facing, 0L), color, itemStack);
        }

        renderQuads(vertexBuffer, model.getQuads(null, null, 0), color, itemStack);
        tessellator.draw();

        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }

    private void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack) {
        boolean useItemTint = color == -1 && !stack.isEmpty();
        int i = 0;
        for (int j = quads.size(); i < j; ++i) {
            BakedQuad quad = quads.get(i);
            int k = color;
            if (useItemTint && quad.hasTintIndex()) {
                k = mc.getItemColors().colorMultiplier(stack, quad.getTintIndex());
                if (EntityRenderer.anaglyphEnable) {
                    k = TextureUtil.anaglyphColor(k);
                }
                k = k | -16777216;
            }
            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, quad, k);
        }
    }

}
