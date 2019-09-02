package net.blay09.mods.cookingforblockheads.item;

import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.blay09.mods.cookingforblockheads.network.NetworkHandler;
import net.blay09.mods.cookingforblockheads.network.message.MessageSyncedEffect;
import net.blay09.mods.cookingforblockheads.tile.TileOven;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHeatingUnit extends Item {

    public static final String name = "heating_unit";
    public static final ResourceLocation registryName = new ResourceLocation(CookingForBlockheads.MOD_ID, name);

    public ItemHeatingUnit() {
        super(new Item.Properties().group(CookingForBlockheads.itemGroup).maxStackSize(1));
    }

    @Override
    public ActionResultType onItemUseFirst(PlayerEntity player, World world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, EnumHand hand) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileOven && !((TileOven) tileEntity).hasPowerUpgrade()) {
            if (!player.abilities.isCreativeMode) {
                player.getHeldItem(hand).shrink(1);
            }

            ((TileOven) tileEntity).setHasPowerUpgrade(true);
            if (!world.isRemote) {
                NetworkHandler.instance.sendToAllAround(new MessageSyncedEffect(pos, MessageSyncedEffect.Type.OVEN_UPGRADE), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32));
            }

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    public void addInformation(ItemStack itemStack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(itemStack, world, tooltip, flag);

        tooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.cookingforblockheads:oven_upgrade"));
        for (String s : I18n.format("tooltip.cookingforblockheads:heating_unit.description").split("\\\\n")) {
            tooltip.add(TextFormatting.GRAY + s);
        }
    }

}
