package net.blay09.mods.cookingforblockheads.client;

import net.blay09.mods.cookingforblockheads.CommonProxy;
import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ClientProxy extends CommonProxy {

    public static final TextureAtlasSprite[] ovenToolIcons = new TextureAtlasSprite[4];

    public ClientProxy() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void registerModels() {
        /*ModelLoader.setCustomStateMapper(ModBlocks.fridge, new DefaultStateMapper() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(BlockState state) {
                if (state.getValue(FridgeBlock.TYPE) == FridgeBlock.FridgeType.LARGE) {
                    return new ModelResourceLocation(CookingForBlockheads.MOD_ID + ":fridge_large", getPropertyString(state.getProperties()));
                } else if (state.getValue(FridgeBlock.TYPE) == FridgeBlock.FridgeType.INVISIBLE) {
                    return new ModelResourceLocation(CookingForBlockheads.MOD_ID + ":fridge_invisible", getPropertyString(state.getProperties()));
                }
                return super.getModelResourceLocation(state);
            }
        });

        DefaultStateMapper ignorePropertiesStateMapper = new DefaultStateMapper() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(BlockState state) {
                ResourceLocation location = state.getBlock().getRegistryName();
                return new ModelResourceLocation(location != null ? location.toString() : "", "normal");
            }
        };
        ModelLoader.setCustomStateMapper(ModBlocks.cuttingBoard, ignorePropertiesStateMapper);
        ModelLoader.setCustomStateMapper(ModBlocks.fruitBasket, ignorePropertiesStateMapper);*/
    }

    @Override
    public List<ITextComponent> getItemTooltip(ItemStack itemStack, PlayerEntity player) {
        return itemStack.getTooltip(player, ITooltipFlag.TooltipFlags.NORMAL);
    }

    @SubscribeEvent
    public void registerIconsPre(TextureStitchEvent.Pre event) {
        event.addSprite(new ResourceLocation(CookingForBlockheads.MOD_ID, "items/slot_bakeware"));
        event.addSprite(new ResourceLocation(CookingForBlockheads.MOD_ID, "items/slot_pot"));
        event.addSprite(new ResourceLocation(CookingForBlockheads.MOD_ID, "items/slot_saucepan"));
        event.addSprite(new ResourceLocation(CookingForBlockheads.MOD_ID, "items/slot_skillet"));
    }

    @SubscribeEvent
    public void registerIconsPost(TextureStitchEvent.Post event) {
        ovenToolIcons[0] = event.getMap().getSprite(new ResourceLocation(CookingForBlockheads.MOD_ID, "items/slot_bakeware"));
        ovenToolIcons[1] = event.getMap().getSprite(new ResourceLocation(CookingForBlockheads.MOD_ID, "items/slot_pot"));
        ovenToolIcons[2] = event.getMap().getSprite(new ResourceLocation(CookingForBlockheads.MOD_ID, "items/slot_saucepan"));
        ovenToolIcons[3] = event.getMap().getSprite(new ResourceLocation(CookingForBlockheads.MOD_ID, "items/slot_skillet"));
    }

}
