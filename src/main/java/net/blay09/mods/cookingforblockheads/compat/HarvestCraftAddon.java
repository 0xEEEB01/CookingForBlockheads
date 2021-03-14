package net.blay09.mods.cookingforblockheads.compat;

import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.blay09.mods.cookingforblockheads.block.ModBlocks;
import net.blay09.mods.cookingforblockheads.util.TextUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class HarvestCraftAddon {

    private boolean cuttingBoardFound;

    public HarvestCraftAddon() {
        MinecraftForge.EVENT_BUS.register(this);
        Compat.cuttingBoardItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Compat.HARVESTCRAFT_FOOD_CORE, "cuttingboarditem"));
        if (Compat.cuttingBoardItem != null && Compat.cuttingBoardItem != Items.AIR) {
            CookingForBlockheads.extraItemGroupItems.add(new ItemStack(Compat.cuttingBoardItem));
            cuttingBoardFound = true;
        }
    }

    /**
     * TODO DANGER! Referenced directly from CookingRegistry, so any hard references to HC code in this class will crash if HC is not installed - this should be moved elsewhere / might not be needed anymore
     */
    public static boolean isWeirdConversionRecipe(IRecipe<?> recipe) {
        if (recipe.getIngredients().size() == 2 && recipe.getRecipeOutput().getCount() == 2) {
            Ingredient first = recipe.getIngredients().get(0);
            Ingredient second = recipe.getIngredients().get(1);
            return first.test(recipe.getRecipeOutput()) && second.test(recipe.getRecipeOutput());
        }

        return false;
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onItemTooltip(ItemTooltipEvent event) {
        if (!cuttingBoardFound) {
            return;
        }

        if (event.getItemStack().getItem() == Compat.cuttingBoardItem) {
            event.getToolTip().add(TextUtils.coloredTextComponent("tooltip.cookingforblockheads:multiblock_kitchen", TextFormatting.YELLOW));
            event.getToolTip().add(new TranslationTextComponent("tooltip.cookingforblockheads:can_be_placed_in_world"));
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!cuttingBoardFound) {
            return;
        }

        if (event.getItemStack().getItem() != Compat.cuttingBoardItem) {
            return;
        }

        if (event.getFace() != Direction.UP) {
            return;
        }

        World world = event.getWorld();
        PlayerEntity player = event.getPlayer();
        BlockState clickedBlock = world.getBlockState(event.getPos());
        if (clickedBlock.getBlock() == Blocks.CHEST || clickedBlock.getBlock() == Blocks.CRAFTING_TABLE || clickedBlock.getBlock() == ModBlocks.cuttingBoard) {
            return;
        }

        BlockPos pos = event.getPos().offset(event.getFace());
        if (canPlace(player, ModBlocks.cuttingBoard.getDefaultState(), world, pos)) {
            BlockItemUseContext useContext = new BlockItemUseContext(new ItemUseContext(player, event.getHand(), new BlockRayTraceResult(Vector3d.copy(pos), event.getFace(), pos, true)));
            BlockState placedState = ModBlocks.cuttingBoard.getStateForPlacement(useContext);
            world.setBlockState(pos, placedState);
            if (!player.abilities.isCreativeMode) {
                event.getItemStack().shrink(1);
            }

            player.swingArm(event.getHand());
            player.playSound(SoundEvents.BLOCK_WOOD_PLACE, 1f, 1f);
            event.setCancellationResult(ActionResultType.SUCCESS);
            event.setCanceled(true);
        }
    }

    private boolean canPlace(PlayerEntity player, BlockState state, World world, BlockPos pos) {
        ISelectionContext context = ISelectionContext.forEntity(player);
        return state.isValidPosition(world, pos) && world.placedBlockCollides(state, pos, context);
    }

}
