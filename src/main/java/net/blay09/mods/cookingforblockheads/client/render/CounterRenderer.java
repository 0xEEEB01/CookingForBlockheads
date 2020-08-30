package net.blay09.mods.cookingforblockheads.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.blay09.mods.cookingforblockheads.block.BlockKitchen;
import net.blay09.mods.cookingforblockheads.client.ModModels;
import net.blay09.mods.cookingforblockheads.tile.CounterTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Random;

public class CounterRenderer extends TileEntityRenderer<CounterTileEntity> {

    private static final Random random = new Random();

    private static final float doorOriginX = 0.84375f;

    private static final float doorOriginZ = 0.09375f;

    public CounterRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    protected float getDoorOriginX() {
        return doorOriginX;
    }

    protected float getDoorOriginZ() {
        return doorOriginZ;
    }

    protected float getBottomShelfOffsetY() {
        return -0.85f;
    }

    protected float getTopShelfOffsetY() {
        return 0.35f;
    }

    protected IBakedModel getDoorModel(@Nullable DyeColor blockColor, boolean isFlipped) {
        int colorIndex = blockColor != null ? blockColor.getId() + 1 : 0;
        return isFlipped ? ModModels.counterDoorsFlipped[colorIndex] : ModModels.counterDoors[colorIndex];
    }

    @Override
    public void render(CounterTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        World world = tileEntity.getWorld();
        if (world == null) {
            return;
        }

        BlockState state = tileEntity.getBlockState();
        boolean hasColor = state.get(BlockKitchen.HAS_COLOR);
        DyeColor blockColor = hasColor ? state.get(BlockKitchen.COLOR) : null;
        float blockAngle = tileEntity.getFacing().getHorizontalAngle();
        float doorAngle = tileEntity.getDoorAnimator().getRenderAngle(partialTicks);
        boolean isFlipped = tileEntity.isFlipped();

        matrixStack.push();
        float doorOriginX = getDoorOriginX();
        float doorOriginZ = getDoorOriginZ();
        float doorDirection = -1f;
        if (isFlipped) {
            doorOriginX = 1 - doorOriginX;
            doorDirection = 1f;
        }

        RenderUtils.applyBlockAngle(matrixStack, tileEntity.getBlockState());
        matrixStack.translate(-0.5f, 0f, -0.5f);

        matrixStack.translate(doorOriginX, 0f, doorOriginZ);
        matrixStack.rotate(new Quaternion(0f, doorDirection * (float) Math.toDegrees(doorAngle), 0f, true));
        matrixStack.translate(-doorOriginX, 0f, -doorOriginZ);

        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        IBakedModel model = getDoorModel(blockColor, isFlipped);
        dispatcher.getBlockModelRenderer().renderModel(world, model, tileEntity.getBlockState(), tileEntity.getPos(), matrixStack, buffer.getBuffer(RenderType.getSolid()), false, random, 0, 0, EmptyModelData.INSTANCE);
        matrixStack.pop();

        // Render the content if the door is open
        if (doorAngle > 0f) {
            matrixStack.push();
            matrixStack.translate(0, 0.5, 0);
            RenderUtils.applyBlockAngle(matrixStack, tileEntity.getBlockState());
            matrixStack.scale(0.3f, 0.3f, 0.3f);
            IItemHandler itemHandler = tileEntity.getItemHandler();
            int itemsPerShelf = itemHandler.getSlots() / 2;
            int itemsPerRow = itemsPerShelf / 2;
            for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
                ItemStack itemStack = itemHandler.getStackInSlot(i);
                if (!itemStack.isEmpty()) {
                    float offsetX, offsetY, offsetZ;
                    int shelfIndex = i % itemsPerShelf;
                    int rowIndex = i % itemsPerRow;
                    float spacing = 2f / (float) itemsPerRow;
                    offsetX = (rowIndex - itemsPerRow / 2f) * -spacing + (shelfIndex >= itemsPerRow ? -0.2f : 0f);
                    offsetY = i < itemsPerShelf ? getTopShelfOffsetY() : getBottomShelfOffsetY();
                    offsetZ = shelfIndex < itemsPerRow ? 0.5f : -0.5f;
                    matrixStack.push();
                    matrixStack.translate(offsetX, offsetY, offsetZ);
                    matrixStack.rotate(new Quaternion(0f, 45f, 0f, true));
                    RenderUtils.renderItem(itemStack, combinedLight, matrixStack, buffer);
                    matrixStack.pop();
                }
            }

            matrixStack.pop();
        }
    }

}
