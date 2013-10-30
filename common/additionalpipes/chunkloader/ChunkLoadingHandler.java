package additionalpipes.chunkloader;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class ChunkLoadingHandler implements LoadingCallback
{
	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world)
	{
		for (final Ticket ticket : tickets)
		{
			final int x = ticket.getModData().getInteger("xCoord");
			final int y = ticket.getModData().getInteger("yCoord");
			final int z = ticket.getModData().getInteger("zCoord");
			final TileEntity te = world.getBlockTileEntity(x, y, z);
			if (te instanceof TileChunkLoader)
			{
				((TileChunkLoader) te).forceChunkLoading(ticket);
			}
		}
	}
}