package buildcraft.additionalpipes.pipes;

import logisticspipes.api.ILPPipeTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.api.transport.IPipeTile;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.Pipe;
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
	
	/**
	 * Get the number of pipes that are connected to a pipe tile.
	 * @param tile
	 * @return
	 */
	public static int getNumConnectedPipesExceptSide(IPipeTile tile, ForgeDirection sideToExclude)
	{
		int count = 0;
		
		for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
			if (sideToExclude != o && tile.isPipeConnected(o)) {
				++count;
			}
		}
		
		return count;
	}
	
	@Override
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side)
	{
		
		ForgeDirection currentlyConnectedSide = container.pipe.getOpenOrientation().getOpposite();
		if(!(currentlyConnectedSide.equals(side) || getNumConnectedPipes(container) == 0))
		{
			return false;
		}
		
		try
		{
			return pipelineEndsinLogisticsPipe(tile, side.getOpposite());
		}
		//can this actually happen?
		//I'm just trying to make sure that it doesn't corrupt your world if it doesn't.
		catch(StackOverflowError err)
		{
			Log.warn("Wow, that's a long, straight pipeline! Gave up looking for logistics pipes!");
		}
		
		return false;
	}
	
	public void switchSource() {
		int connectedSide = container.pipe.getOpenOrientation().getOpposite().ordinal();
		int newSide = 6;

		for(int i = connectedSide + 1; i <= connectedSide + 6; ++i) {
			ForgeDirection o = ForgeDirection.VALID_DIRECTIONS[i % 6];
			TileEntity tile = container.getTile(o);
			
			try
			{
				if(pipelineEndsinLogisticsPipe(tile, o.getOpposite()))
				{
					newSide = o.ordinal();
					break;
				}
			}
			//can this actually happen?
			//I'm just trying to make sure that it doesn't corrupt your world if it doesn't.
			catch(StackOverflowError err)
			{
				Log.warn("Wow, that's a long, straight pipeline! Gave up looking for logistics pipes!");
			}
		}

		if(newSide != ForgeDirection.UNKNOWN.ordinal() && connectedSide != ForgeDirection.UNKNOWN.ordinal() && connectedSide != newSide) {
			container.pipeConnectionsBuffer[connectedSide] = false;
			container.pipeConnectionsBuffer[newSide] = true;

			container.scheduleRenderUpdate();
			// worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	/**
	 * Recursive function for checking valid pipelines
	 * @param start
	 * @param sideConnectedToPrevPipe
	 * @return
	 */
	public boolean pipelineEndsinLogisticsPipe(TileEntity start, ForgeDirection sideConnectedToPrevPipe)
	{
		//found one!
		if(start instanceof ILPPipeTile)
		{
			return true;
		}
		
		//another BC pipe
		else if(start instanceof IPipeTile)
		{
			Pipe<?> pipe = (Pipe<?>) ((IPipeTile) start).getPipe();
			if(!BlockGenericPipe.isValid(pipe) || !(pipe.transport instanceof PipeTransportItems))
			{
				//or not?
				return false;
			}
			
			//if it branches off, then this pipeline isn't valid
			if(PipeTransportItemsLogistics.getNumConnectedPipesExceptSide(pipe.container, sideConnectedToPrevPipe) != 1)
			{
				return false;
			}
			
			//move to next pipe
			for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
				if (pipe.container.isPipeConnected(o) && o != sideConnectedToPrevPipe) {
					
					return pipelineEndsinLogisticsPipe(((IPipeTile) start).getNeighborTile(o), o.getOpposite());
				}
			}
		}
		//trying to connect to anything besides a BC or LP pipe
		return false;
	}
	
}
