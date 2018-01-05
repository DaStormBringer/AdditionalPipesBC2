package buildcraft.additionalpipes.chunkloader;

import java.util.LinkedList;
import java.util.List;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.utils.Log;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

public class TileTeleportTether extends TileEntity {

	private Ticket chunkTicket;
	private int loadDistance = 1;

	public List<ChunkPos> getLoadArea() {
		List<ChunkPos> loadArea = new LinkedList<ChunkPos>();

		for(int x = -loadDistance; x < loadDistance + 1; x++) {
			for(int z = -loadDistance; z < loadDistance + 1; z++) {
				ChunkPos chunkCoords = new ChunkPos((pos.getX() >> 4) + x, (pos.getZ() >> 4) + z);

				loadArea.add(chunkCoords);
			}
		}

		return loadArea;
	}

	@Override
	public void validate() {
		super.validate();
		if(!getWorld().isRemote && chunkTicket == null) {
			Ticket ticket = ForgeChunkManager.requestTicket(AdditionalPipes.instance, getWorld(), Type.NORMAL);
			if(ticket != null) {
				forceChunkLoading(ticket);
			}
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		stopChunkLoading();
	}

	public void setLoadDistance(int dist) {
		loadDistance = dist;
		forceChunkLoading(chunkTicket);
	}

	public void forceChunkLoading(Ticket ticket) {
		stopChunkLoading();
		chunkTicket = ticket;
		for(ChunkPos coord : getLoadArea()) {
			Log.info(String.format("Force loading chunk %s in %s", coord, getWorld().provider.getClass()));
			ForgeChunkManager.forceChunk(chunkTicket, coord);
		}
	}

	public void unforceChunkLoading() {
		for(Object obj : chunkTicket.getChunkList()) {
			ChunkPos coord = (ChunkPos) obj;
			ForgeChunkManager.unforceChunk(chunkTicket, coord);
		}
	}

	public void stopChunkLoading() {
		if(chunkTicket != null) {
			ForgeChunkManager.releaseTicket(chunkTicket);
			chunkTicket = null;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound) {
		return super.writeToNBT(par1NBTTagCompound);
	}
}
