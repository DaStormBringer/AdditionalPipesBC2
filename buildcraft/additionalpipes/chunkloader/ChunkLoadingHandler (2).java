package net.minecraft.src.buildcraft.additionalpipes.chunkloader;

import java.util.List;
import java.util.Set;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import net.minecraft.src.buildcraft.additionalpipes.chunkloader.TileChunkLoader;
import net.minecraft.src.forge.IChunkLoadHandler;

public class ChunkLoadingHandler implements IChunkLoadHandler {

    @Override
    public void addActiveChunks(World world, Set<ChunkCoordIntPair> chunkList) {

        for (TileChunkLoader tile : TileChunkLoader.chunkLoaderList) {

            List<ChunkCoordIntPair> loadArea = tile.getLoadArea();
            for (ChunkCoordIntPair chunkCoords : loadArea) {

                if (!chunkList.contains(chunkCoords)) {
                    chunkList.add(chunkCoords);
                    //log("Adding chunk: " + chunkCoords, LOG_INFO);
                }
                else {
                    //log(chunkCoords + " already there.", LOG_INFO);
                }
            }
        }

    }

    @Override
    public boolean canUnloadChunk(Chunk chunk) {

        for (TileChunkLoader tile : TileChunkLoader.chunkLoaderList) {

            List<ChunkCoordIntPair> loadArea = tile.getLoadArea();
            for (ChunkCoordIntPair chunkCoords : loadArea) {

                if (chunk.worldObj.getChunkFromChunkCoords(chunkCoords.chunkXPos, chunkCoords.chunkZPosition).equals(chunk)) {
                    //log("Keeping chunk: " + chunk.getChunkCoordIntPair(), LOG_INFO);
                    return false;
                }
            }
        }

        //log("Unloading chunk: " + chunk.getChunkCoordIntPair(), LOG_INFO);
        return true;
    }

    @Override
    public boolean canUpdateEntity(Entity entity) {
        return true;
    }
}