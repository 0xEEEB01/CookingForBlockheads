package net.blay09.mods.cookingforblockheads.client.render;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;

public class CabinetRenderer extends CounterRenderer {
    public static IBakedModel[][] models;
    public static IBakedModel[][] modelsFlipped;

    private static final float[] doorOriginsX = new float[]{
            (1 - 0.84375f) - 1 / 16f,
            0.09375f + 1 / 16f,
            0.84375f + 1 / 16f,
            1 - (0.09375f + 1 / 16f)
    };

    private static final float[] doorOriginsZ = new float[]{
            (1 - 0.09375f) - 1 / 16f,
            (1 - 0.84375f) - 1 / 16f,
            0.09375f + 1 / 16f,
            0.84375f + 1 / 16f
    };

    @Override
    protected float getDoorOriginX(EnumFacing facing) {
        return doorOriginsX[facing.getHorizontalIndex()];
    }

    @Override
    protected float getDoorOriginZ(EnumFacing facing) {
        return doorOriginsZ[facing.getHorizontalIndex()];
    }

    @Override
    protected IBakedModel getDoorModel(EnumFacing facing, EnumDyeColor blockColor, boolean isFlipped) {
        return isFlipped ? modelsFlipped[facing.getHorizontalIndex()][blockColor.getMetadata()] : models[facing.getHorizontalIndex()][blockColor.getMetadata()];
    }
}
