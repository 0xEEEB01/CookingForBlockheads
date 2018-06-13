package net.blay09.mods.cookingforblockheads.compat;

import com.google.common.base.Function;
import mcjty.theoneprobe.api.*;
import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.blay09.mods.cookingforblockheads.block.BlockFridge;
import net.blay09.mods.cookingforblockheads.block.BlockMilkJar;
import net.blay09.mods.cookingforblockheads.block.BlockOven;
import net.blay09.mods.cookingforblockheads.block.BlockToaster;
import net.blay09.mods.cookingforblockheads.tile.TileFridge;
import net.blay09.mods.cookingforblockheads.tile.TileMilkJar;
import net.blay09.mods.cookingforblockheads.tile.TileOven;
import net.blay09.mods.cookingforblockheads.tile.TileToaster;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TheOneProbeAddon implements Function<ITheOneProbe, Void> {

    @Nullable
    @Override
    public Void apply(@Nullable ITheOneProbe top) {
        if (top != null) {
            top.registerProvider(new ProbeInfoProvider());
        }
        return null;
    }

    public static class ProbeInfoProvider implements IProbeInfoProvider {

        @Override
        public String getID() {
            return CookingForBlockheads.MOD_ID;
        }

        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
            // NOTE no lang support in The One Probe atm...
            if (state.getBlock() instanceof BlockMilkJar) {
                TileMilkJar tileEntity = tryGetTileEntity(world, data.getPos(), TileMilkJar.class);
                if (tileEntity != null) {
                    addMilkJarInfo(tileEntity, mode, info);
                }
            } else if (state.getBlock() instanceof BlockToaster) {
                TileToaster tileEntity = tryGetTileEntity(world, data.getPos(), TileToaster.class);
                if (tileEntity != null) {
                    addToasterInfo(tileEntity, mode, info);
                }
            } else if (state.getBlock() instanceof BlockOven) {
                TileOven tileEntity = tryGetTileEntity(world, data.getPos(), TileOven.class);
                if (tileEntity != null && tileEntity.hasPowerUpgrade()) {
                    info.text("Upgrade: Heating Unit");
                }
            } else if (state.getBlock() instanceof BlockFridge) {
                TileFridge tileEntity = tryGetTileEntity(world, data.getPos(), TileFridge.class);
                if (tileEntity != null && tileEntity.getBaseFridge().hasIceUpgrade()) {
                    info.text("Upgrade: Ice Unit");
                }

                if (tileEntity != null && tileEntity.getBaseFridge().hasPreservationUpgrade()) {
                    info.text("Upgrade: Preservation Chamber");
                }
            }
        }

        private void addMilkJarInfo(TileMilkJar tileEntity, ProbeMode mode, IProbeInfo info) {
            info.text(String.format("Milk Stored: %d/%d", (int) tileEntity.getMilkAmount(), (int) tileEntity.getMilkCapacity()));
        }

        private void addToasterInfo(TileToaster tileEntity, ProbeMode mode, IProbeInfo info) {
            if (tileEntity.isActive()) {
                info.text(String.format("Toasting... (%s)", (int) (tileEntity.getToastProgress() * 100)) + "%");
                info.progress((int) (tileEntity.getToastProgress() * 100), 100);
            }
        }

    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static <T extends TileEntity> T tryGetTileEntity(World world, BlockPos pos, Class<T> tileClass) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileClass.isAssignableFrom(tileEntity.getClass())) {
            return (T) tileEntity;
        }
        return null;
    }
}
