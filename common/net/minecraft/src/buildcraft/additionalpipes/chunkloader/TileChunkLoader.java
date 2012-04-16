package net.minecraft.src.buildcraft.additionalpipes.chunkloader;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.TileEntity;

public class TileChunkLoader extends TileEntity {

    public static List<TileChunkLoader> chunkLoaderList = Collections.synchronizedList(new LinkedList<TileChunkLoader>());
    
    public TileChunkLoader() {
    }
    
    private synchronized void addToList() {
    	if (!chunkLoaderList.contains(this)) {
            chunkLoaderList.add(this);
        }
    }
    
    private synchronized void removeFromList() {
    	if (chunkLoaderList.contains(this)) {
            chunkLoaderList.remove(this);
        }
    }
    
    /*
    * Returns a list of ChunkCoordPair that are the chunks around the block
    * in a 3x3 area.
    */
    public List getLoadArea() {

        List<ChunkCoordIntPair> loadArea = new LinkedList<ChunkCoordIntPair>();

        Chunk centerChunk = worldObj.getChunkFromBlockCoords(xCoord, zCoord);

        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(centerChunk.xPosition + x, centerChunk.zPosition + z);
                loadArea.add(chunkCoords);
            }
        }

        return loadArea;
    }
    
    @Override
    public void updateEntity() {
    	super.updateEntity();
    	addToList();
    }
    
    @Override
    public void invalidate() {
        super.invalidate();
        removeFromList();
    }
}

