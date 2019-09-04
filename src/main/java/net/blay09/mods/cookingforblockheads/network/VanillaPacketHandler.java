package net.blay09.mods.cookingforblockheads.network;

import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;

import java.util.Objects;

public class VanillaPacketHandler {

    public static void sendTileEntityUpdate(TileEntity tileEntity) {
        SUpdateTileEntityPacket updatePacket = tileEntity.getUpdatePacket();
        if (updatePacket != null) {
            World world = Objects.requireNonNull(tileEntity.getWorld());
            ((ServerChunkProvider) world.getChunkProvider()).chunkManager.getTrackingPlayers(new ChunkPos(tileEntity.getPos()), false).forEach(player -> player.connection.sendPacket(updatePacket));
        }
    }

}
