package buildcraft.additionalpipes.chunkloader;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import buildcraft.additionalpipes.AdditionalPipes;

public class TileChunkLoader extends TileEntity {

	private Ticket chunkTicket;
	private boolean uninitialized = true;

	public List<ChunkCoordIntPair> getLoadArea() {
		List<ChunkCoordIntPair> loadArea = new LinkedList<ChunkCoordIntPair>();

		Chunk centerChunk = worldObj.getChunkFromBlockCoords(xCoord, zCoord);

		for (int x = -1; x < 2; x++) {
			for (int z = -1; z < 2; z++) {
				ChunkCoordIntPair chunkCoords =
						new ChunkCoordIntPair(centerChunk.xPosition + x, centerChunk.zPosition + z);
				loadArea.add(chunkCoords);
			}
		}

		return loadArea;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!AdditionalPipes.proxy.isServer(worldObj)) return;
		if(uninitialized && chunkTicket == null) {
			chunkTicket = ForgeChunkManager
					.requestTicket(AdditionalPipes.instance, worldObj, Type.NORMAL);
			if(chunkTicket != null) {
				forceChunkLoading(chunkTicket);
			}
			uninitialized = false;
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		ForgeChunkManager.releaseTicket(chunkTicket);
	}

	public void forceChunkLoading(Ticket ticket) {
		chunkTicket = ticket;
		for(ChunkCoordIntPair coord : getLoadArea()) {
			ForgeChunkManager.forceChunk(ticket, coord);
		}
	}

	public void stopChunkLoading() {
		for(Object obj : chunkTicket.getChunkList()) {
			ChunkCoordIntPair coord = (ChunkCoordIntPair) obj;
			ForgeChunkManager.unforceChunk(chunkTicket, coord);
		}
	}
}

