package net.blay09.mods.cookingforblockheads.compat;

import net.blay09.mods.balm.api.Balm;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

public class Compat {

    public static final String HARVESTCRAFT_FOOD_CORE = "pamhc2foodcore";
    public static final String THEONEPROBE = "theoneprobe";
    public static final String APPLECORE = "applecore";
    public static final String EX_COMPRESSUM = "excompressum";

    public static Fluid getMilkFluid() {
        return ForgeMod.MILK.get();
    }

    public static Item cuttingBoardItem = Items.AIR;

    private static Tag<Item> cookingOilTag; // TODO add to balm
    public static Tag<Item> getCookingOilTag() {
        if(cookingOilTag == null) {
            cookingOilTag = Balm.getRegistries().getItemTag(new ResourceLocation("balm", "cooking_oil"));
        }
        return cookingOilTag;
    }
}
