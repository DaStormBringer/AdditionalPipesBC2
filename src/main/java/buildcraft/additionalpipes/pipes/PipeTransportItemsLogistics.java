package buildcraft.additionalpipes.pipes;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.transport.IPipeTile;
import buildcraft.transport.PipeTransportItems;

/**
 * Pipe transport for Logistics Teleport Pipes
 * @author Jamie
 *
 */
public class PipeTransportItemsLogistics extends PipeTransportItems
{
	/**
	 * Get the number of pipes that are connected to a pipe tile.
	 * @param tile
	 * @return
	 */
	public static int getNumConnectedPipes(IPipeTile tile)
	{
		int count = 0;
		
		for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
			if (tile.isPipeConnected(o)) {
				++count;
			}
		}
		
		return count;
	}
	
	@Override
	/**
	 * Special version which only connects to logistics pipes, or pipelines that end in them
	 */
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side)
	{
		
		ForgeDirection currentlyConnectedSide = container.pipe.getOpenOrientation().getOpposite();
		if(currentlyConnectedSide.equals(side))
		{
			return true;
		}
		else if(getNumConnectedPipes(container) == 0)
		{
			return true;
		}
		
		//Log.warn("Wow, that's a long, straight pipeline! Gave up looking for logistics pipes!");

		return false;
	}
	
}
