package net.blay09.mods.cookingforblockheads.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.blay09.mods.cookingforblockheads.block.BlockKitchen;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.BasicState;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public class CachedDynamicModel implements IBakedModel {

    private final Map<String, IBakedModel> cache = Maps.newHashMap();

    private final ModelBakery modelBakery;
    private final IModel baseModel;
    private final List<Pair<Predicate<BlockState>, IBakedModel>> parts;
    private final Function<BlockState, ImmutableMap<String, String>> textureMapFunction;

    private TextureAtlasSprite particleTexture;

    public CachedDynamicModel(ModelBakery modelBakery, IModel baseModel, @Nullable List<Pair<Predicate<BlockState>, IBakedModel>> parts, @Nullable Function<BlockState, ImmutableMap<String, String>> textureMapFunction) {
        this.modelBakery = modelBakery;
        this.baseModel = baseModel;
        this.parts = parts;
        this.textureMapFunction = textureMapFunction;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (state != null) {
            String stateString = state.toString();
            IBakedModel bakedModel = cache.get(stateString);
            if (bakedModel == null) {
                TRSRTransformation transform = TRSRTransformation.from(state.get(BlockKitchen.FACING));
                if (state.has(BlockKitchen.LOWERED) && state.get(BlockKitchen.LOWERED)) {
                    transform = transform.compose(new TRSRTransformation(new Vector3f(0f, -0.05f, 0f), null, null, null));
                }

                BasicState modelState = new BasicState(transform, false);
                IModel retexturedBaseModel = textureMapFunction != null ? baseModel.retexture(textureMapFunction.apply(state)) : baseModel;
                bakedModel = retexturedBaseModel.bake(modelBakery, ModelLoader.defaultTextureGetter(), modelState, DefaultVertexFormats.BLOCK);
                cache.put(stateString, bakedModel);

                if (particleTexture == null) {
                    particleTexture = bakedModel.getParticleTexture();
                }
            }

            return bakedModel.getQuads(state, side, rand);
        }

        return Collections.emptyList();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return particleTexture != null ? particleTexture : ModelLoader.White.INSTANCE;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }

}
